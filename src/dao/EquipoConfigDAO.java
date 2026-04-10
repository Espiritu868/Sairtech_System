package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipoConfigDAO {
    
    private ConexionFactory factory;

    public EquipoConfigDAO() {
        this.factory = new ConexionFactory();
    }

    public List<String> obtenerTiposEquipo() {
        List<String> tipos = new ArrayList<>();
        String sql = "SELECT nombre_tipo FROM tipos_equipo ORDER BY id_tipo ASC";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql);
             ResultSet rs = comando.executeQuery()) {
             
            while (rs.next()) {
                tipos.add(rs.getString("nombre_tipo"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar tipos de equipo: " + e.getMessage());
        }
        return tipos;
    }

    public List<String> obtenerMarcasPorTipo(String nombreTipo) {
        List<String> marcas = new ArrayList<>();
        String sql = "SELECT m.nombre_marca FROM marcas m " +
                     "JOIN tipos_equipo t ON m.id_tipo = t.id_tipo " +
                     "WHERE t.nombre_tipo = ? " +
                     "ORDER BY m.nombre_marca ASC";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
             
            comando.setString(1, nombreTipo);
            
            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    marcas.add(rs.getString("nombre_marca"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar marcas: " + e.getMessage());
        }
        return marcas;
    }
}