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
    // GESTIÓN DE PANTALLAS
    // =========================================================

    public boolean registrarPantalla(int idLote, String modeloEquipo, double precioCompra, double precioCliente, double precioTecnico, int stock) {
        String sql = "INSERT INTO pantallas_knijico (id_lote, modelo_equipo, precio_compra, precio_cliente, precio_tecnico, stock) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idLote);
            ps.setString(2, modeloEquipo);
            ps.setDouble(3, precioCompra);
            ps.setDouble(4, precioCliente);
            ps.setDouble(5, precioTecnico);
            ps.setInt(6, stock);
            
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.err.println("Error al registrar pantalla Knijico: " + e.getMessage());
            return false;
        }
    }

    public List<Object[]> listarPantallas(String textoBusqueda, boolean verOcultas, int idLoteFiltro) {
        List<Object[]> lista = new ArrayList<>();
        
        // Construimos la consulta dinámicamente
        StringBuilder sql = new StringBuilder(
            "SELECT p.id_pantalla, l.nombre_lote, p.modelo_equipo, p.precio_compra, p.precio_cliente, p.precio_tecnico, p.stock, p.estado " +
            "FROM pantallas_knijico p " +
            "INNER JOIN lotes_knijico l ON p.id_lote = l.id_lote " +
            "WHERE p.modelo_equipo LIKE ? "
        );

        if (verOcultas) {
            sql.append("AND p.estado = 'Oculto' "); // Solo las ocultas
        } else {
            sql.append("AND p.estado = 'Activo' "); // Solo las activas
        }
        if (idLoteFiltro > 0) {
            sql.append("AND p.id_lote = ? ");
        }
        sql.append("ORDER BY p.id_pantalla DESC");

        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            ps.setString(1, "%" + textoBusqueda + "%");
            
            if (idLoteFiltro > 0) {
                ps.setInt(2, idLoteFiltro);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_pantalla"),
                        rs.getString("nombre_lote"),
                        rs.getString("modelo_equipo"),
                        rs.getDouble("precio_compra"),
                        rs.getDouble("precio_cliente"),
                        rs.getDouble("precio_tecnico"),
                        rs.getInt("stock"),
                        rs.getString("estado")
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar pantallas Knijico: " + e.getMessage());
        }
        return lista;
    }

    // =========================================================
    // SOFT DELETE Y ACTUALIZACIONES
    // =========================================================

    public boolean ocultarPantalla(int idPantalla) {
        String sql = "UPDATE pantallas_knijico SET estado = 'Oculto' WHERE id_pantalla = ?";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPantalla);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.err.println("Error al ocultar pantalla: " + e.getMessage());
            return false;
        }
    }

    public boolean restaurarPantalla(int idPantalla) {
        String sql = "UPDATE pantallas_knijico SET estado = 'Activo' WHERE id_pantalla = ?";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPantalla);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.err.println("Error al restaurar pantalla: " + e.getMessage());
            return false;
        }
    }
}