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
    
    public int insertarConId(Producto producto) {
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
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {}
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

    public List<Object[]> buscarProductoCompleto(String textoBusqueda) {
        List<Object[]> lista = new ArrayList<>();
        // NOTA: Se insertó 'p.ubicacion' en el índice 4 del ResultSet
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, c.nombre_categoria, p.ubicacion, p.precio_compra, p.precio_venta, p.stock, p.stock_minimo, p.id_proveedor, p.aplica_precio_tecnico, p.precio_tecnico " +
                     "FROM productos p " +
                     "JOIN categorias_productos c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.nombre_producto LIKE ? OR p.codigo_barras LIKE ? OR c.nombre_categoria LIKE ?";
                     
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            String param = "%" + textoBusqueda + "%";
            ps.setString(1, param); ps.setString(2, param); ps.setString(3, param);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[12]; // Aumentamos a 12
                    fila[0] = rs.getInt("id_producto");
                    fila[1] = rs.getString("codigo_barras");
                    fila[2] = rs.getString("nombre_producto");
                    fila[3] = rs.getString("nombre_categoria");
                    fila[4] = rs.getString("ubicacion"); // <--- NUEVO EN EL ARREGLO
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
    
    public Producto buscarPorCodigo(String codigoBarras) {
        String sql = "SELECT * FROM productos WHERE codigo_barras = ?";
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
}