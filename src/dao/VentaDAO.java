package dao;

import factory.ConexionFactory;
import modelo.DetalleVenta;
import modelo.Venta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            String sqlDetalle = "INSERT INTO detalles_venta (id_venta, id_producto, descripcion, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
            String sqlRestarStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";
            
            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
                 PreparedStatement psStock = con.prepareStatement(sqlRestarStock)) {
                
                for (DetalleVenta detalle : listaDetalles) {
                    // Guardar Renglón
                    psDetalle.setInt(1, idVentaGenerado);
                    
                    if (detalle.getIdProducto() > 0) {
                        psDetalle.setInt(2, detalle.getIdProducto());
                        // Restar Stock solo si es un producto físico
                        psStock.setInt(1, detalle.getCantidad());
                        psStock.setInt(2, detalle.getIdProducto());
                        psStock.executeUpdate();
                    } else {
                        psDetalle.setNull(2, java.sql.Types.INTEGER); // Es un servicio
                    }
                    
                    psDetalle.setString(3, detalle.getDescripcion());
                    psDetalle.setInt(4, detalle.getCantidad());
                    psDetalle.setDouble(5, detalle.getPrecioUnitario());
                    psDetalle.setDouble(6, detalle.getSubtotal());
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
}