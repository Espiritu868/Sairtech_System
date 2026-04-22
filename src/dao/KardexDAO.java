package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KardexDAO {

    // 1. OBTENER TODO EL HISTORIAL DE UN PRODUCTO
    public List<Object[]> listarKardexPorProducto(int idProducto) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT k.id_kardex, DATE_FORMAT(k.fecha_movimiento, '%d/%m/%Y %H:%i') AS fecha, " +
                     "k.referencia, k.cantidad, k.stock_restante_despues, p.precio_compra, u.usuario " +
                     "FROM kardex k " +
                     "JOIN usuarios u ON k.id_usuario = u.id_usuario " +
                     "JOIN productos p ON k.id_producto = p.id_producto " +
                     "WHERE k.id_producto = ? " +
                     "ORDER BY k.id_kardex DESC"; // Los más recientes primero

        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cantidad = rs.getInt("cantidad");
                    // Formateamos para que se vea +5 o -3
                    String cantFormat = (cantidad > 0) ? "+" + cantidad : String.valueOf(cantidad);
                    
                    lista.add(new Object[]{
                        rs.getInt("id_kardex"),
                        rs.getString("fecha"),
                        rs.getString("referencia"),
                        cantFormat,
                        rs.getInt("stock_restante_despues"),
                        "L. " + rs.getDouble("precio_compra"),
                        rs.getString("usuario")
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar Kardex: " + e.getMessage());
        }
        return lista;
    }

    // 2. REGISTRAR UN AJUSTE MANUAL (Suma o Resta) USANDO TRANSACCIONES SEGURAS
    public boolean registrarAjusteManual(int idProducto, int idUsuario, String referencia, String tipoAjuste, int cantidadAjuste) {
        Connection con = null;
        try {
            con = new factory.ConexionFactory().getConexion();
            con.setAutoCommit(false); // Iniciamos transacción blindada

            // A) Consultamos el stock actual y bloqueamos la fila para evitar cruces
            int stockActual = 0;
            String sqlStock = "SELECT stock FROM productos WHERE id_producto = ? FOR UPDATE";
            try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                psStock.setInt(1, idProducto);
                try (ResultSet rs = psStock.executeQuery()) {
                    if (rs.next()) {
                        stockActual = rs.getInt("stock");
                    } else {
                        throw new SQLException("Producto no encontrado.");
                    }
                }
            }

            // B) Calculamos matemáticas
            int cantidadReal = tipoAjuste.equals("Incremento") ? cantidadAjuste : -cantidadAjuste;
            int nuevoStock = stockActual + cantidadReal;

            if (nuevoStock < 0) {
                throw new SQLException("El stock no puede quedar en negativo.");
            }

            // C) Actualizamos el stock en la tabla de productos
            String sqlUpdate = "UPDATE productos SET stock = ? WHERE id_producto = ?";
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, nuevoStock);
                psUpdate.setInt(2, idProducto);
                psUpdate.executeUpdate();
            }

            // D) Dejamos la huella en el Kardex
            String tipoMovDB = tipoAjuste.equals("Incremento") ? "AJUSTE_MANUAL_ENTRADA" : "AJUSTE_MANUAL_SALIDA";
            String sqlKardex = "INSERT INTO kardex (id_producto, id_usuario, fecha_movimiento, tipo_movimiento, cantidad, stock_restante_despues, referencia) VALUES (?, ?, NOW(), ?, ?, ?, ?)";
            try (PreparedStatement psKardex = con.prepareStatement(sqlKardex)) {
                psKardex.setInt(1, idProducto);
                psKardex.setInt(2, idUsuario);
                psKardex.setString(3, tipoMovDB);
                psKardex.setInt(4, cantidadReal);
                psKardex.setInt(5, nuevoStock);
                psKardex.setString(6, referencia);
                psKardex.executeUpdate();
            }

            con.commit(); // Confirmamos los cambios
            return true;

        } catch (Exception e) {
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            System.err.println("Error al ajustar Kardex: " + e.getMessage());
            return false;
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (SQLException ex) {}
        }
    }
}