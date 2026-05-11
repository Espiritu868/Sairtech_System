package dao;

import factory.ConexionFactory;
import modelo.Despiece;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DespieceDAO {
    private ConexionFactory factory = new ConexionFactory();

    public boolean insertar(Despiece d) {
        String sql = "INSERT INTO despieces (modelo_dispositivo, placa_base, pantalla_lcd, bateria, marco_chasis, modulo_carga, camaras, comentarios) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getModeloDispositivo());
            ps.setString(2, d.getPlacaBase());
            ps.setString(3, d.getPantallaLcd());
            ps.setString(4, d.getBateria());
            ps.setString(5, d.getMarcoChasis());
            ps.setString(6, d.getModuloCarga());
            ps.setString(7, d.getCamaras());
            ps.setString(8, d.getComentarios());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al insertar despiece: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Despiece d) {
        String sql = "UPDATE despieces SET modelo_dispositivo=?, placa_base=?, pantalla_lcd=?, bateria=?, marco_chasis=?, modulo_carga=?, camaras=?, comentarios=? WHERE id_despiece=?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getModeloDispositivo());
            ps.setString(2, d.getPlacaBase());
            ps.setString(3, d.getPantallaLcd());
            ps.setString(4, d.getBateria());
            ps.setString(5, d.getMarcoChasis());
            ps.setString(6, d.getModuloCarga());
            ps.setString(7, d.getCamaras());
            ps.setString(8, d.getComentarios());
            ps.setInt(9, d.getIdDespiece());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar despiece: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM despieces WHERE id_despiece = ?";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public List<Despiece> buscarPorModelo(String busqueda) {
        List<Despiece> lista = new ArrayList<>();
        String sql = "SELECT * FROM despieces WHERE modelo_dispositivo LIKE ? ORDER BY id_despiece DESC";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + busqueda + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Despiece d = new Despiece();
                    d.setIdDespiece(rs.getInt("id_despiece"));
                    d.setModeloDispositivo(rs.getString("modelo_dispositivo"));
                    d.setPlacaBase(rs.getString("placa_base"));
                    d.setPantallaLcd(rs.getString("pantalla_lcd"));
                    d.setBateria(rs.getString("bateria"));
                    d.setMarcoChasis(rs.getString("marco_chasis"));
                    d.setModuloCarga(rs.getString("modulo_carga"));
                    d.setCamaras(rs.getString("camaras"));
                    d.setComentarios(rs.getString("comentarios"));
                    // Imágenes pendientes para la siguiente fase
                    lista.add(d);
                }
            }
        } catch (Exception e) { System.err.println("Error buscar despiece: " + e.getMessage()); }
        return lista;
    }
}