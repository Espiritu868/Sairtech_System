package dao;

import factory.ConexionFactory;
import modelo.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private ConexionFactory factory;

    public ProductoDAO() {
        this.factory = new ConexionFactory();
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (codigo_barras, nombre_producto, id_categoria, precio_compra, precio_venta, stock, stock_minimo, id_proveedor, aplica_precio_tecnico, precio_tecnico, ubicacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, producto.getCodigoBarras());
            ps.setString(2, producto.getNombreProducto());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStock());
            ps.setInt(7, producto.getStockMinimo());
            
            if (producto.getIdProveedor() > 0) ps.setInt(8, producto.getIdProveedor());
            else ps.setNull(8, java.sql.Types.INTEGER);
            
            ps.setBoolean(9, producto.isAplicaPrecioTecnico());
            ps.setDouble(10, producto.getPrecioTecnico());
            ps.setString(11, producto.getUbicacion());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    public int insertarConId(modelo.Producto producto) {
        String sql = "INSERT INTO productos (codigo_barras, nombre_producto, id_categoria, precio_compra, precio_venta, stock, stock_minimo, id_proveedor, aplica_precio_tecnico, precio_tecnico, ubicacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
             
            ps.setString(1, producto.getCodigoBarras());
            ps.setString(2, producto.getNombreProducto());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStock());
            ps.setInt(7, producto.getStockMinimo());
            
            if (producto.getIdProveedor() > 0) ps.setInt(8, producto.getIdProveedor());
            else ps.setNull(8, java.sql.Types.INTEGER);
            
            ps.setBoolean(9, producto.isAplicaPrecioTecnico());
            ps.setDouble(10, producto.getPrecioTecnico());
            ps.setString(11, producto.getUbicacion());
            
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        
                        // --- MAGIA DEL KARDEX: INVENTARIO INICIAL ---
                        // Registramos el nacimiento del producto en el Kardex. 
                        // Usamos id_usuario 1 por defecto al ser creación inicial del sistema.
                        String sqlKardex = "INSERT INTO kardex (id_producto, id_usuario, fecha_movimiento, tipo_movimiento, cantidad, stock_restante_despues, referencia) VALUES (?, 1, NOW(), 'INVENTARIO_INICIAL', ?, ?, 'Ingreso de Nuevo Producto')";
                        try (PreparedStatement psKardex = con.prepareStatement(sqlKardex)) {
                            psKardex.setInt(1, idGenerado);
                            psKardex.setInt(2, producto.getStock());
                            psKardex.setInt(3, producto.getStock());
                            psKardex.executeUpdate();
                        }
                        // --------------------------------------------
                        
                        return idGenerado;
                    }
                }
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.err.println("¡Intento de código duplicado bloqueado por la BD!");
            javax.swing.JOptionPane.showMessageDialog(null, 
                "El Código de Barras que ingresó ya le pertenece a otro producto.\nPor favor, asigne uno distinto o déjelo en blanco para que el sistema lo genere automáticamente.", 
                "Código Duplicado", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return -1;
        } catch (java.sql.SQLException e) {
            System.err.println("Error general al insertar producto: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizarCodigoBarras(int idProducto, String codigoBarras) {
        String sql = "UPDATE productos SET codigo_barras = ? WHERE id_producto = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET codigo_barras = ?, nombre_producto = ?, id_categoria = ?, precio_compra = ?, precio_venta = ?, stock = ?, stock_minimo = ?, id_proveedor = ?, aplica_precio_tecnico = ?, precio_tecnico = ?, ubicacion = ? WHERE id_producto = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, producto.getCodigoBarras());
            ps.setString(2, producto.getNombreProducto());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStock());
            ps.setInt(7, producto.getStockMinimo());
            
            if (producto.getIdProveedor() > 0) ps.setInt(8, producto.getIdProveedor());
            else ps.setNull(8, java.sql.Types.INTEGER);
            
            ps.setBoolean(9, producto.isAplicaPrecioTecnico());
            ps.setDouble(10, producto.getPrecioTecnico());
            ps.setString(11, producto.getUbicacion());
            ps.setInt(12, producto.getIdProducto());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- MODIFICADO: Agregado el filtro booleano para papelera ---
    public List<Object[]> buscarProductoCompleto(String textoBusqueda, boolean verEliminados) {
        List<Object[]> lista = new ArrayList<>();
        int filtroEliminado = verEliminados ? 1 : 0;
        
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, c.nombre_categoria, p.ubicacion, p.precio_compra, p.precio_venta, p.stock, p.stock_minimo, p.id_proveedor, p.aplica_precio_tecnico, p.precio_tecnico " +
                     "FROM productos p " +
                     "JOIN categorias_productos c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.eliminado = ? AND (p.nombre_producto LIKE ? OR p.codigo_barras LIKE ? OR c.nombre_categoria LIKE ?)";
                     
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            String param = "%" + textoBusqueda + "%";
            ps.setInt(1, filtroEliminado);
            ps.setString(2, param); ps.setString(3, param); ps.setString(4, param);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[12]; 
                    fila[0] = rs.getInt("id_producto");
                    fila[1] = rs.getString("codigo_barras");
                    fila[2] = rs.getString("nombre_producto");
                    fila[3] = rs.getString("nombre_categoria");
                    fila[4] = rs.getString("ubicacion"); 
                    fila[5] = rs.getDouble("precio_compra");
                    fila[6] = rs.getDouble("precio_venta");
                    fila[7] = rs.getInt("stock");
                    fila[8] = rs.getInt("stock_minimo");
                    fila[9] = rs.getInt("id_proveedor");
                    fila[10] = rs.getBoolean("aplica_precio_tecnico"); 
                    fila[11] = rs.getDouble("precio_tecnico");
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {}
        return lista;
    }

    // --- NUEVO: Exclusivo para Punto de Venta (Oculta stock 0 y eliminados) ---
    public List<Object[]> buscarProductoParaVenta(String textoBusqueda) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, c.nombre_categoria, p.ubicacion, p.precio_compra, p.precio_venta, p.stock, p.stock_minimo, p.id_proveedor, p.aplica_precio_tecnico, p.precio_tecnico " +
                     "FROM productos p " +
                     "JOIN categorias_productos c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.eliminado = 0 AND p.stock > 0 AND (p.nombre_producto LIKE ? OR p.codigo_barras LIKE ? OR c.nombre_categoria LIKE ?)";
                     
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            String param = "%" + textoBusqueda + "%";
            ps.setString(1, param); ps.setString(2, param); ps.setString(3, param);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[12]; 
                    fila[0] = rs.getInt("id_producto");
                    fila[1] = rs.getString("codigo_barras");
                    fila[2] = rs.getString("nombre_producto");
                    fila[3] = rs.getString("nombre_categoria");
                    fila[4] = rs.getString("ubicacion"); 
                    fila[5] = rs.getDouble("precio_compra");
                    fila[6] = rs.getDouble("precio_venta");
                    fila[7] = rs.getInt("stock");
                    fila[8] = rs.getInt("stock_minimo");
                    fila[9] = rs.getInt("id_proveedor");
                    fila[10] = rs.getBoolean("aplica_precio_tecnico"); 
                    fila[11] = rs.getDouble("precio_tecnico");
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {}
        return lista;
    }
    
    public boolean restarStock(int idProducto, int cantidadVendida) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidadVendida);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadVendida); 
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    // --- MODIFICADO: Solo trae productos si no están eliminados ---
    public Producto buscarPorCodigo(String codigoBarras) {
        String sql = "SELECT * FROM productos WHERE codigo_barras = ? AND eliminado = 0";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, codigoBarras);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setCodigoBarras(rs.getString("codigo_barras"));
                    p.setNombreProducto(rs.getString("nombre_producto"));
                    p.setPrecioVenta(rs.getDouble("precio_venta"));
                    p.setStock(rs.getInt("stock"));
                    p.setIdProveedor(rs.getInt("id_proveedor"));
                    p.setAplicaPrecioTecnico(rs.getBoolean("aplica_precio_tecnico"));
                    p.setPrecioTecnico(rs.getDouble("precio_tecnico"));
                    p.setUbicacion(rs.getString("ubicacion"));
                    return p;
                }
            }
        } catch (SQLException e) {}
        return null; 
    }

    // --- NUEVO: Borrado Lógico ---
    public boolean eliminar(int idProducto) {
        String sql = "UPDATE productos SET eliminado = 1 WHERE id_producto = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    // --- NUEVO: Restaurar Producto ---
    public boolean restaurar(int idProducto) {
        String sql = "UPDATE productos SET eliminado = 0 WHERE id_producto = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    // --- NUEVO: Consulta rápida para saber si pide IMEI ---
    public int obtenerDiasGarantia(int idProducto) {
        String sql = "SELECT c.dias_garantia FROM productos p JOIN categorias_productos c ON p.id_categoria = c.id_categoria WHERE p.id_producto = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("dias_garantia");
            }
        } catch (SQLException e) { System.err.println("Error garantía: " + e.getMessage()); }
        return 0; // Si falla o no tiene, devuelve 0
    }
    
    // --- MODIFICADO: Ahora acepta un filtro de Categoría ---
    public List<Object[]> buscarProductoCompleto(String textoBusqueda, int idCategoriaFiltro, boolean verEliminados) {
        List<Object[]> lista = new ArrayList<>();
        int filtroEliminado = verEliminados ? 1 : 0;
        
        // Usamos StringBuilder para armar la consulta dinámicamente
        StringBuilder sql = new StringBuilder(
            "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, c.nombre_categoria, p.ubicacion, p.precio_compra, p.precio_venta, p.stock, p.stock_minimo, p.id_proveedor, p.aplica_precio_tecnico, p.precio_tecnico " +
            "FROM productos p " +
            "JOIN categorias_productos c ON p.id_categoria = c.id_categoria " +
            "WHERE p.eliminado = ? AND (p.nombre_producto LIKE ? OR p.codigo_barras LIKE ? OR c.nombre_categoria LIKE ?)"
        );

        // Si se seleccionó una categoría específica en el ComboBox, agregamos la condición
        if (idCategoriaFiltro > 0) {
            sql.append(" AND p.id_categoria = ?");
        }

        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
             
            String param = "%" + textoBusqueda + "%";
            ps.setInt(1, filtroEliminado);
            ps.setString(2, param); 
            ps.setString(3, param); 
            ps.setString(4, param);
            
            // Si hay categoría, le pasamos el parámetro extra a SQL
            if (idCategoriaFiltro > 0) {
                ps.setInt(5, idCategoriaFiltro);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[12]; 
                    fila[0] = rs.getInt("id_producto");
                    fila[1] = rs.getString("codigo_barras");
                    fila[2] = rs.getString("nombre_producto");
                    fila[3] = rs.getString("nombre_categoria");
                    fila[4] = rs.getString("ubicacion"); 
                    fila[5] = rs.getDouble("precio_compra");
                    fila[6] = rs.getDouble("precio_venta");
                    fila[7] = rs.getInt("stock");
                    fila[8] = rs.getInt("stock_minimo");
                    fila[9] = rs.getInt("id_proveedor");
                    fila[10] = rs.getBoolean("aplica_precio_tecnico"); 
                    fila[11] = rs.getDouble("precio_tecnico");
                    lista.add(fila);
                }
            }
        } catch (SQLException e) { 
            System.err.println("Error en buscarProductoCompleto: " + e.getMessage()); 
        }
        return lista;
    }
    
    
    // --- NUEVO: Trae todas las ubicaciones únicas registradas para llenar el ComboBox ---
    public List<String> obtenerUbicaciones() {
        List<String> ubicaciones = new ArrayList<>();
        String sql = "SELECT DISTINCT ubicacion FROM productos WHERE ubicacion IS NOT NULL AND ubicacion != '' ORDER BY ubicacion ASC";
        
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                ubicaciones.add(rs.getString("ubicacion"));
            }
        } catch (SQLException e) { 
            System.err.println("Error al obtener ubicaciones: " + e.getMessage()); 
        }
        return ubicaciones;
    }
}