package dao;

import factory.ConexionFactory;
import modelo.DetalleVenta;
import modelo.Venta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    private ConexionFactory factory;

    public VentaDAO() {
        this.factory = new ConexionFactory();
    }

    // =========================================================================
    // REGISTRAR VENTA COMPLETA (Transacción Segura)
    // =========================================================================
    public int registrarVentaCompleta(Venta venta, List<DetalleVenta> listaDetalles) {
        Connection con = null;
        int idVentaGenerado = -1;

        try {
            con = factory.getConexion();
            // 1. APAGAMOS EL AUTO-GUARDADO (Iniciamos la Transacción)
            con.setAutoCommit(false); 

            // 2. GUARDAR LA CABECERA (La tabla ventas)
            String sqlVenta = "INSERT INTO ventas (id_cliente, id_usuario, id_orden, total, metodo_pago) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                
                if (venta.getIdCliente() > 0) psVenta.setInt(1, venta.getIdCliente());
                else psVenta.setNull(1, java.sql.Types.INTEGER); // Consumidor Final
                
                psVenta.setInt(2, venta.getIdUsuario());
                
                if (venta.getIdOrden() > 0) psVenta.setInt(3, venta.getIdOrden());
                else psVenta.setNull(3, java.sql.Types.INTEGER); // Venta de mostrador directa
                
                psVenta.setDouble(4, venta.getTotal());
                psVenta.setString(5, venta.getMetodoPago());
                
                psVenta.executeUpdate();
                
                // Rescatar el ID del nuevo recibo
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) idVentaGenerado = rs.getInt(1);
                    else throw new SQLException("No se pudo obtener el ID de la venta.");
                }
            }

            // 3. GUARDAR LOS DETALLES Y RESTAR INVENTARIO
            String sqlDetalle = "INSERT INTO detalles_venta (id_venta, id_producto, descripcion, cantidad, precio_unitario, subtotal, imei, dias_garantia) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            // --- PREPARAMOS LAS DOS RUTAS DE INVENTARIO ---
            String sqlRestarStockNormal = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
            String sqlRestarStockKnijico = "UPDATE pantallas_knijico SET stock = stock - ? WHERE id_pantalla = ?";
            
            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
                 PreparedStatement psStockNormal = con.prepareStatement(sqlRestarStockNormal);
                 PreparedStatement psStockKnijico = con.prepareStatement(sqlRestarStockKnijico)) {
                
                for (DetalleVenta detalle : listaDetalles) {
                    // Guardar Renglón
                    psDetalle.setInt(1, idVentaGenerado);
                    
                    int idProductoVenta = detalle.getIdProducto();
                    
                    if (idProductoVenta > 0) {
                        psDetalle.setInt(2, idProductoVenta); // Guardamos el ID tal cual
                        
                        // --- LA MAGIA MATEMÁTICA PARA SEPARAR EL STOCK ---
                        if (idProductoVenta >= 70000) {
                            // ES PANTALLA KNIJICO
                            int idRealKnijico = idProductoVenta - 70000;
                            psStockKnijico.setInt(1, detalle.getCantidad());
                            psStockKnijico.setInt(2, idRealKnijico);
                            psStockKnijico.executeUpdate();
                        } else {
                            // ES PRODUCTO NORMAL
                            psStockNormal.setInt(1, detalle.getCantidad());
                            psStockNormal.setInt(2, idProductoVenta);
                            psStockNormal.executeUpdate();
                        }
                    } else {
                        psDetalle.setNull(2, java.sql.Types.INTEGER); // Es un servicio (ID 0)
                    }
                    
                    psDetalle.setString(3, detalle.getDescripcion());
                    psDetalle.setInt(4, detalle.getCantidad());
                    psDetalle.setDouble(5, detalle.getPrecioUnitario());
                    psDetalle.setDouble(6, detalle.getSubtotal());
                    
                    // Guardamos IMEI y Días de Garantía (Si están vacíos, se guardan en blanco/cero)
                    psDetalle.setString(7, detalle.getImei() != null ? detalle.getImei() : "");
                    psDetalle.setInt(8, detalle.getDiasGarantia());
                    
                    psDetalle.executeUpdate();
                }
            }

            // 4. SI HAY UNA ORDEN ASOCIADA, LA MARCAMOS COMO ENTREGADA
            if (venta.getIdOrden() > 0) {
                String sqlOrden = "UPDATE ordenes_reparacion SET estado = 'Entregado', id_usuario_entrega = ? WHERE id_orden = ?";
                try (PreparedStatement psOrden = con.prepareStatement(sqlOrden)) {
                    psOrden.setInt(1, venta.getIdUsuario());
                    psOrden.setInt(2, venta.getIdOrden());
                    psOrden.executeUpdate();
                }
            }

            // 5. SI TODO SALIÓ BIEN, GUARDAMOS DEFINITIVAMENTE (Commit)
            con.commit();
            return idVentaGenerado;

        } catch (SQLException e) {
            // SI ALGO FALLÓ, ECHAMOS TODO PARA ATRÁS (Rollback)
            System.err.println("Error en la transacción de venta. Haciendo Rollback: " + e.getMessage());
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                System.err.println("Error fatal al hacer rollback: " + ex.getMessage());
            }
            return -1;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true); // Restauramos el comportamiento normal
                    con.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    // =========================================================================
    // LISTAR HISTORIAL DE VENTAS (SOLO MOSTRADOR, EXCLUYE TALLER)
    // =========================================================================
    public List<Object[]> listarHistorialVentas(String busqueda) {
        List<Object[]> lista = new ArrayList<>();
        
        String sql = "SELECT v.id_venta, v.fecha_venta, " +
                     "IFNULL(CONCAT(c.nombre, ' ', c.apellido), 'Consumidor Final') AS cliente, " +
                     "v.total, v.metodo_pago, u.usuario " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "JOIN usuarios u ON v.id_usuario = u.id_usuario " +
                     "WHERE v.id_orden IS NULL AND (" +
                     "CAST(v.id_venta AS CHAR) LIKE ? " +
                     "OR v.fecha_venta LIKE ? " +
                     "OR IFNULL(CONCAT(c.nombre, ' ', c.apellido), 'Consumidor Final') LIKE ? " +
                     "OR CAST(v.total AS CHAR) LIKE ? " +
                     "OR v.metodo_pago LIKE ? " +
                     "OR u.usuario LIKE ?) " +
                     "ORDER BY v.id_venta DESC";

        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            String param = "%" + busqueda + "%";
            
            for (int i = 1; i <= 6; i++) {
                ps.setString(i, param);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_venta"),
                        rs.getString("fecha_venta"),
                        rs.getString("cliente"),
                        rs.getDouble("total"),
                        rs.getString("metodo_pago"),
                        rs.getString("usuario")
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar historial de ventas: " + e.getMessage());
        }
        return lista;
    }
    
    // =========================================================================
    // LISTAR HISTORIAL DE GARANTIAS (Trae Categoría y Teléfono del Cliente)
    // =========================================================================
    public List<Object[]> listarGarantias(String busqueda) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, DATE_FORMAT(v.fecha_venta, '%d/%m/%Y %H:%i') as fecha_compra, " +
                     "DATE_FORMAT(DATE_ADD(v.fecha_venta, INTERVAL dv.dias_garantia DAY), '%d/%m/%Y') as fecha_vence, " +
                     "IFNULL(CONCAT(cl.nombre, ' ', cl.apellido), 'Consumidor Final') AS cliente, " +
                     "dv.descripcion, dv.imei, " +
                     "CASE WHEN DATE_ADD(v.fecha_venta, INTERVAL dv.dias_garantia DAY) >= CURRENT_DATE() THEN 'VIGENTE' ELSE 'VENCIDA' END as estado, " +
                     "dv.precio_unitario, IFNULL(cat.nombre_categoria, 'ARTICULO') as categoria, IFNULL(cl.telefono, 'N/D') as telefono_cliente, dv.dias_garantia " +
                     "FROM detalles_venta dv " +
                     "JOIN ventas v ON dv.id_venta = v.id_venta " +
                     "LEFT JOIN clientes cl ON v.id_cliente = cl.id_cliente " +
                     "LEFT JOIN productos p ON dv.id_producto = p.id_producto " +
                     "LEFT JOIN categorias_productos cat ON p.id_categoria = cat.id_categoria " +
                     "WHERE dv.dias_garantia > 0 AND (" +
                     "dv.imei LIKE ? OR CAST(v.id_venta AS CHAR) LIKE ? OR IFNULL(CONCAT(cl.nombre, ' ', cl.apellido), 'Consumidor Final') LIKE ?) " +
                     "ORDER BY v.id_venta DESC";

        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            String param = "%" + busqueda + "%";
            ps.setString(1, param);
            ps.setString(2, param);
            ps.setString(3, param);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_venta"),
                        rs.getString("fecha_compra"),
                        rs.getString("fecha_vence"),
                        rs.getString("cliente"),
                        rs.getString("descripcion"),
                        rs.getString("imei"),
                        rs.getString("estado"),
                        rs.getDouble("precio_unitario"),
                        rs.getString("categoria"),
                        rs.getString("telefono_cliente"),
                        rs.getInt("dias_garantia")
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar garantias: " + e.getMessage());
        }
        return lista;
    }
}