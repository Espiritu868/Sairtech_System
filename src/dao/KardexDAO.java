package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KardexDAO {

    // 1. OBTENER TODO EL HISTORIAL (BILINGÜE: Normal y Knijico)
    public List<Object[]> listarKardexPorProducto(int idProducto) {
        List<Object[]> lista = new ArrayList<>();
        // Quitamos el JOIN con 'productos' para que no falle al buscar pantallas Knijico
        String sql = "SELECT k.id_kardex, DATE_FORMAT(k.fecha_movimiento, '%d/%m/%Y %H:%i') AS fecha, " +
                     "k.referencia, k.cantidad, k.stock_restante_despues, u.usuario " +
                     "FROM kardex k " +
                     "JOIN usuarios u ON k.id_usuario = u.id_usuario " +
                     "WHERE k.id_producto = ? " +
                     "ORDER BY k.id_kardex DESC";

        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setInt(1, idProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cantidad = rs.getInt("cantidad");
                    String cantFormat = (cantidad > 0) ? "+" + cantidad : String.valueOf(cantidad);
                    
                    lista.add(new Object[]{
                        rs.getInt("id_kardex"),
                        rs.getString("fecha"),
                        rs.getString("referencia"),
                        cantFormat,
                        rs.getInt("stock_restante_despues"),
                        "0.00", // Relleno vacío (ya que quitamos la columna de costo en la interfaz)
                        rs.getString("usuario")
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar Kardex: " + e.getMessage());
        }
        return lista;
    }

    // 2. REGISTRAR UN AJUSTE MANUAL (BILINGÜE: Normal y Knijico)
    public boolean registrarAjusteManual(int idProductoOriginal, int idUsuario, String referencia, String tipoAjuste, int cantidadAjuste) {
        Connection con = null;
        try {
            con = new factory.ConexionFactory().getConexion();
            con.setAutoCommit(false); 

            // --- DETECCIÓN DE KNIJICO (Magia de los 70000) ---
            boolean esKnijico = idProductoOriginal >= 70000;
            int idReal = esKnijico ? (idProductoOriginal - 70000) : idProductoOriginal;
            String tablaStock = esKnijico ? "pantallas_knijico" : "productos";
            String columnaId = esKnijico ? "id_pantalla" : "id_producto";

            // A) Consultamos el stock actual en la tabla correcta
            int stockActual = 0;
            String sqlStock = "SELECT stock FROM " + tablaStock + " WHERE " + columnaId + " = ? FOR UPDATE";
            try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                psStock.setInt(1, idReal);
                try (ResultSet rs = psStock.executeQuery()) {
                    if (rs.next()) {
                        stockActual = rs.getInt("stock");
                    } else {
                        throw new SQLException("Producto no encontrado en " + tablaStock);
                    }
                }
            }

            // B) Calculamos matemáticas
            int cantidadReal = tipoAjuste.equals("Incremento") ? cantidadAjuste : -cantidadAjuste;
            int nuevoStock = stockActual + cantidadReal;

            if (nuevoStock < 0) throw new SQLException("El stock no puede quedar en negativo.");

            // C) Actualizamos el stock en la tabla correcta
            String sqlUpdate = "UPDATE " + tablaStock + " SET stock = ? WHERE " + columnaId + " = ?";
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, nuevoStock);
                psUpdate.setInt(2, idReal);
                psUpdate.executeUpdate();
            }

            // D) Dejamos la huella en el Kardex usando el ID Original (Ej: 70005)
            String tipoMovDB = tipoAjuste.equals("Incremento") ? "AJUSTE_MANUAL_ENTRADA" : "AJUSTE_MANUAL_SALIDA";
            String sqlKardex = "INSERT INTO kardex (id_producto, id_usuario, fecha_movimiento, tipo_movimiento, cantidad, stock_restante_despues, referencia) VALUES (?, ?, NOW(), ?, ?, ?, ?)";
            try (PreparedStatement psKardex = con.prepareStatement(sqlKardex)) {
                psKardex.setInt(1, idProductoOriginal);
                psKardex.setInt(2, idUsuario);
                psKardex.setString(3, tipoMovDB);
                psKardex.setInt(4, cantidadReal);
                psKardex.setInt(5, nuevoStock);
                psKardex.setString(6, referencia);
                psKardex.executeUpdate();
            }

            con.commit(); 
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