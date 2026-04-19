package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KnijicoDAO {

    // =========================================================
    // GESTIÓN DE LOTES
    // =========================================================

    public boolean crearLote(String nombreLote) {
        String sql = "INSERT INTO lotes_knijico (nombre_lote) VALUES (?)";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreLote);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al crear lote Knijico: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> obtenerLotesActivos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_lote, nombre_lote, fecha_ingreso FROM lotes_knijico WHERE estado = 'Activo' ORDER BY id_lote DESC";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_lote"),
                    rs.getString("nombre_lote"),
                    rs.getString("fecha_ingreso")
                });
            }
        } catch (Exception e) {
            System.err.println("Error al listar lotes: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================
    // GESTIÓN DE PANTALLAS (REGISTRO Y ACTUALIZACIÓN)
    // =========================================================

public boolean registrarPantalla(int idLote, String modelo, double costo, double pCli, double pTec, int stock, int caja, String codigo) {
        String sql = "INSERT INTO pantallas_knijico (id_lote, modelo_equipo, precio_compra, precio_cliente, precio_tecnico, stock, numero_caja, codigo_barras) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, idLote);
            ps.setString(2, modelo);
            ps.setDouble(3, costo);
            ps.setDouble(4, pCli);
            ps.setDouble(5, pTec);
            ps.setInt(6, stock);
            ps.setInt(7, caja);
            
            // Verificamos si vino en blanco o con espacios
            boolean generarAutomatico = (codigo == null || codigo.trim().isEmpty());
            ps.setString(8, generarAutomatico ? null : codigo.trim());

            int filas = ps.executeUpdate();

            // GENERACIÓN AUTOMÁTICA DE CÓDIGO CON PREFIJO 7 (13 dígitos en total)
            if (filas > 0 && generarAutomatico) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        // El %012d rellena con ceros hasta tener 12 números, más el 7 inicial = 13 dígitos
                        String autoCodigo = "7" + String.format("%012d", idGenerado);
                        String updateSql = "UPDATE pantallas_knijico SET codigo_barras = ? WHERE id_pantalla = ?";
                        try (PreparedStatement psUpdate = con.prepareStatement(updateSql)) {
                            psUpdate.setString(1, autoCodigo);
                            psUpdate.setInt(2, idGenerado);
                            psUpdate.executeUpdate();
                        }
                    }
                }
            }
            return filas > 0;
        } catch (Exception e) {
            System.err.println("Error al registrar pantalla: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarPantalla(int id, int idLote, String modelo, double costo, double pCli, double pTec, int stock, int caja, String codigo) {
        // Generación automática si el campo viene vacío al actualizar
        boolean generarAutomatico = (codigo == null || codigo.trim().isEmpty());
        String codigoFinal = generarAutomatico ? ("7" + String.format("%012d", id)) : codigo.trim();

        String sql = "UPDATE pantallas_knijico SET id_lote=?, modelo_equipo=?, precio_compra=?, precio_cliente=?, precio_tecnico=?, stock=?, numero_caja=?, codigo_barras=? WHERE id_pantalla=?";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idLote);
            ps.setString(2, modelo);
            ps.setDouble(3, costo);
            ps.setDouble(4, pCli);
            ps.setDouble(5, pTec);
            ps.setInt(6, stock);
            ps.setInt(7, caja);
            ps.setString(8, codigoFinal);
            ps.setInt(9, id);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar pantalla: " + e.getMessage());
            return false;
        }
    }
    // =========================================================
    // LISTADO Y BÚSQUEDA
    // =========================================================

    public List<Object[]> listarPantallas(String textoBusqueda, boolean verOcultas, int idLoteFiltro) {
        List<Object[]> lista = new ArrayList<>();
        
        // Consulta corregida para incluir codigo_barras
        StringBuilder sql = new StringBuilder(
            "SELECT p.id_pantalla, p.id_lote, l.nombre_lote, p.modelo_equipo, p.precio_compra, p.precio_cliente, p.precio_tecnico, p.stock, p.estado, p.numero_caja, p.codigo_barras " +
            "FROM pantallas_knijico p " +
            "INNER JOIN lotes_knijico l ON p.id_lote = l.id_lote " +
            "WHERE (p.modelo_equipo LIKE ? OR p.codigo_barras LIKE ? OR CONCAT('L', p.id_lote, ' C', p.numero_caja) LIKE ?) "
        );

        sql.append(verOcultas ? "AND p.estado = 'Oculto' " : "AND p.estado = 'Activo' ");
        
        if (idLoteFiltro > 0) sql.append("AND p.id_lote = ? ");
        
        sql.append("ORDER BY p.id_pantalla DESC");

        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            String param = "%" + textoBusqueda + "%";
            ps.setString(1, param);
            ps.setString(2, param); // Busca por código de barras
            ps.setString(3, param); // Busca por ubicación L1 C1
            
            if (idLoteFiltro > 0) ps.setInt(4, idLoteFiltro);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idLote = rs.getInt("id_lote");
                    int caja = rs.getInt("numero_caja");
                    String modeloOriginal = rs.getString("modelo_equipo");
                    String modeloVisual = modeloOriginal + " [L" + idLote + " C" + caja + "]";

                    lista.add(new Object[]{
                        rs.getInt("id_pantalla"),      // 0
                        rs.getString("nombre_lote"),   // 1
                        modeloVisual,                  // 2
                        rs.getDouble("precio_compra"), // 3
                        rs.getDouble("precio_cliente"),// 4
                        rs.getDouble("precio_tecnico"),// 5
                        rs.getInt("stock"),            // 6
                        rs.getString("estado"),        // 7
                        modeloOriginal,                // 8: Oculto
                        caja,                          // 9: Oculto
                        idLote,                        // 10: Oculto
                        rs.getString("codigo_barras")  // 11: Oculto (Nuevo)
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar pantallas: " + e.getMessage());
        }
        return lista;
    }

    public Object[] buscarPorCodigoBarra(String codigo) {
        String sql = "SELECT id_pantalla, id_lote, modelo_equipo, precio_compra, precio_cliente, precio_tecnico, stock " +
                     "FROM pantallas_knijico WHERE codigo_barras = ? AND estado = 'Activo'";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getDouble(4), 
                        rs.getDouble(5), rs.getDouble(6), rs.getInt(7)
                    };
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar por código: " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    // ESTADOS (SOFT DELETE)
    // =========================================================

    public boolean ocultarPantalla(int idPantalla) {
        String sql = "UPDATE pantallas_knijico SET estado = 'Oculto' WHERE id_pantalla = ?";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPantalla);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean restaurarPantalla(int idPantalla) {
        String sql = "UPDATE pantallas_knijico SET estado = 'Activo' WHERE id_pantalla = ?";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPantalla);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}