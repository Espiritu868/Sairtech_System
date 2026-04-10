package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.EquipoRegistrado;

public class EquipoRegistradoDAO {
    private ConexionFactory factory;

    public EquipoRegistradoDAO() {
        this.factory = new ConexionFactory();
    }
    
    public List<Object[]> buscarEquipoCompleto(String texto) {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT e.id_equipo, e.modelo, e.imei_serie, c.nombre, c.apellido, t.nombre_tipo " +
                     "FROM Equipos_Registrados e " +
                     "JOIN Clientes c ON e.id_cliente = c.id_cliente " +
                     "JOIN tipos_equipo t ON e.id_tipo = t.id_tipo " + 
                     "WHERE e.modelo LIKE ? OR e.imei_serie LIKE ? " +
                     "OR c.nombre LIKE ? OR c.apellido LIKE ? OR c.numero_identidad LIKE ?";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            String p = "%" + texto + "%";
            
            comando.setString(1, p);
            comando.setString(2, p);
            comando.setString(3, p);
            comando.setString(4, p);
            comando.setString(5, p);
            
            try (ResultSet rs = comando.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[5]; 
                    fila[0] = rs.getInt("id_equipo");
                    fila[1] = rs.getString("modelo");
                    fila[2] = rs.getString("imei_serie");
                    fila[3] = rs.getString("nombre") + " " + rs.getString("apellido");
                    fila[4] = rs.getString("nombre_tipo"); 
                    
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscando equipo: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(EquipoRegistrado equipo) {
        String sql = "INSERT INTO Equipos_Registrados (id_cliente, id_tipo, id_marca, modelo, imei_serie) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setInt(1, equipo.getIdCliente());
            comando.setInt(2, equipo.getIdTipo());
            comando.setInt(3, equipo.getIdMarca());
            comando.setString(4, equipo.getModelo());
            comando.setString(5, equipo.getImeiSerie());
            
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar el equipo: " + e.getMessage());
            return false;
        }
    }

    public List<EquipoRegistrado> listar() {
        List<EquipoRegistrado> lista = new ArrayList<>();
        String sql = "SELECT * FROM Equipos_Registrados";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            
            while (resultado.next()) {
                EquipoRegistrado e = new EquipoRegistrado();
                e.setIdEquipo(resultado.getInt("id_equipo"));
                e.setIdCliente(resultado.getInt("id_cliente"));
                e.setIdTipo(resultado.getInt("id_tipo"));
                e.setIdMarca(resultado.getInt("id_marca"));
                e.setModelo(resultado.getString("modelo"));
                e.setImeiSerie(resultado.getString("imei_serie"));
                
                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar equipos: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(EquipoRegistrado equipo) {
        String sql = "UPDATE Equipos_Registrados SET id_cliente = ?, id_tipo = ?, id_marca = ?, modelo = ?, imei_serie = ? WHERE id_equipo = ?";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setInt(1, equipo.getIdCliente());
            comando.setInt(2, equipo.getIdTipo());
            comando.setInt(3, equipo.getIdMarca());
            comando.setString(4, equipo.getModelo());
            comando.setString(5, equipo.getImeiSerie());
            comando.setInt(6, equipo.getIdEquipo());
            
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar el equipo: " + e.getMessage());
            return false;
        }
    }
    
    public int insertarConId(EquipoRegistrado equipo) {
        String sql = "INSERT INTO Equipos_Registrados (id_cliente, id_tipo, id_marca, modelo, imei_serie) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            comando.setInt(1, equipo.getIdCliente());
            comando.setInt(2, equipo.getIdTipo());
            comando.setInt(3, equipo.getIdMarca());
            comando.setString(4, equipo.getModelo());
            comando.setString(5, equipo.getImeiSerie());
            
            int filasAfectadas = comando.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet rs = comando.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar equipo y obtener ID: " + e.getMessage());
        }
        return -1; // Retorna -1 si falló
    }
}