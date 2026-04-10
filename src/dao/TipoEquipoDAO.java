package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.TipoEquipo;

public class TipoEquipoDAO {
    
    private ConexionFactory factory;

    public TipoEquipoDAO() {
        this.factory = new ConexionFactory();
    }

    public boolean insertar(TipoEquipo tipo) {
        String sql = "INSERT INTO Tipos_Equipo (nombre_tipo) VALUES (?)";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setString(1, tipo.getNombreTipo());
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar tipo de equipo: " + e.getMessage());
            return false;
        }
    }

    public List<TipoEquipo> listar() {
        List<TipoEquipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tipos_Equipo";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            
            while (resultado.next()) {
                lista.add(new TipoEquipo(
                    resultado.getInt("id_tipo"), 
                    resultado.getString("nombre_tipo")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tipos de equipo: " + e.getMessage());
        }
        return lista;
    }
}