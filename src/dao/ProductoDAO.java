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

    // --- GUARDAR UN PRODUCTO NUEVO ---
    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO productos (codigo_barras, nombre_producto, id_categoria, precio_compra, precio_venta, stock, stock_minimo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, producto.getCodigoBarras());
            ps.setString(2, producto.getNombreProducto());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStock());
            ps.setInt(7, producto.getStockMinimo());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            return false;
        }
    }
    
    // --- NUEVO: INSERTAR Y DEVOLVER EL ID GENERADO ---
    public int insertarConId(Producto producto) {
        String sql = "INSERT INTO productos (codigo_barras, nombre_producto, id_categoria, precio_compra, precio_venta, stock, stock_minimo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
             
            ps.setString(1, producto.getCodigoBarras());
            ps.setString(2, producto.getNombreProducto());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStock());
            ps.setInt(7, producto.getStockMinimo());
            
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar producto con ID: " + e.getMessage());
        }
        return -1;
    }

    // --- NUEVO: ACTUALIZAR SOLO EL CÓDIGO DE BARRAS ---
    public boolean actualizarCodigoBarras(int idProducto, String codigoBarras) {
        String sql = "UPDATE productos SET codigo_barras = ? WHERE id_producto = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar código de barras: " + e.getMessage());
            return false;
        }
    }

    // --- ACTUALIZAR UN PRODUCTO EXISTENTE ---
    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET codigo_barras = ?, nombre_producto = ?, id_categoria = ?, precio_compra = ?, precio_venta = ?, stock = ?, stock_minimo = ? WHERE id_producto = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, producto.getCodigoBarras());
            ps.setString(2, producto.getNombreProducto());
            ps.setInt(3, producto.getIdCategoria());
            ps.setDouble(4, producto.getPrecioCompra());
            ps.setDouble(5, producto.getPrecioVenta());
            ps.setInt(6, producto.getStock());
            ps.setInt(7, producto.getStockMinimo());
            ps.setInt(8, producto.getIdProducto());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    // --- BUSCAR PRODUCTOS PARA LA TABLA DEL INVENTARIO ---
    // Retorna Object[] porque combinamos datos del Producto y su Categoría
    public List<Object[]> buscarProductoCompleto(String textoBusqueda) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.codigo_barras, p.nombre_producto, c.nombre_categoria, p.precio_compra, p.precio_venta, p.stock, p.stock_minimo " +
                     "FROM productos p " +
                     "JOIN categorias_productos c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.nombre_producto LIKE ? OR p.codigo_barras LIKE ? OR c.nombre_categoria LIKE ?";
                     
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            String p = "%" + textoBusqueda + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[8];
                    fila[0] = rs.getInt("id_producto");
                    fila[1] = rs.getString("codigo_barras");
                    fila[2] = rs.getString("nombre_producto");
                    fila[3] = rs.getString("nombre_categoria");
                    fila[4] = rs.getDouble("precio_compra");
                    fila[5] = rs.getDouble("precio_venta");
                    fila[6] = rs.getInt("stock");
                    fila[7] = rs.getInt("stock_minimo");
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }
        return lista;
    }
    
    // --- ACTUALIZAR SOLO EL STOCK (Para cuando vendas algo) ---
    public boolean restarStock(int idProducto, int cantidadVendida) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id_producto = ? AND stock >= ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, cantidadVendida);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidadVendida); // Seguridad para que el stock no quede en negativo
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al restar stock: " + e.getMessage());
            return false;
        }
    }
    
    // --- NUEVO: BUSCAR PRODUCTO EXACTO POR CÓDIGO DE BARRAS ---
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
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar por código: " + e.getMessage());
        }
        return null; // Retorna null si el código no existe
    }
}