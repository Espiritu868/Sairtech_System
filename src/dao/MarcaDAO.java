package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Marca;

public class MarcaDAO {
    
    private ConexionFactory factory;

    public MarcaDAO() {
        this.factory = new ConexionFactory();
    }

    public boolean insertar(Marca marca) {
        String sql = "INSERT INTO Marcas (nombre_marca) VALUES (?)";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setString(1, marca.getNombreMarca());
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar marca: " + e.getMessage());
            return false;
        }
    }

    public List<Marca> listar() {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT * FROM Marcas";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            
            while (resultado.next()) {
                lista.add(new Marca(
                    resultado.getInt("id_marca"), 
                    resultado.getString("nombre_marca")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar marcas: " + e.getMessage());
        }
        return lista;
    }
}