package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;

public class ClienteDAO {
    private ConexionFactory factory;

    public ClienteDAO() {
        this.factory = new ConexionFactory();
    }

    public int insertar(Cliente cliente) {
        String sql = "INSERT INTO Clientes (numero_identidad, nombre, apellido, telefono, correo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            comando.setString(1, cliente.getNumeroIdentidad());
            comando.setString(2, cliente.getNombre());
            comando.setString(3, cliente.getApellido());
            
            // --- MAGIA ANTI-NULL: Usamos el truco del SN-Identidad para evitar duplicados ---
            if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
                String rellenoTel = "SN-" + cliente.getNumeroIdentidad();
                if (rellenoTel.length() > 20) rellenoTel = rellenoTel.substring(0, 20);
                comando.setString(4, rellenoTel);
            } else {
                comando.setString(4, cliente.getTelefono().trim());
            }
            
            if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
                String rellenoCor = "SN-" + cliente.getNumeroIdentidad() + "@sairtech.com";
                if (rellenoCor.length() > 50) rellenoCor = rellenoCor.substring(0, 50);
                comando.setString(5, rellenoCor);
            } else {
                comando.setString(5, cliente.getCorreo().trim());
            }
            // --------------------------------------------------------------------------------
            
            if (comando.executeUpdate() > 0) {
                try (ResultSet rs = comando.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
        }
        return -1; 
    }

    public List<Cliente> listar() {
        List<Cliente> listaClientes = new ArrayList<>();
        String sql = "SELECT * FROM Clientes WHERE apellido != 'ELIMINADO'";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            
            while (resultado.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(resultado.getInt("id_cliente"));
                c.setNumeroIdentidad(resultado.getString("numero_identidad"));
                c.setNombre(resultado.getString("nombre"));
                c.setApellido(resultado.getString("apellido"));
                c.setTelefono(resultado.getString("telefono"));
                c.setCorreo(resultado.getString("correo"));
                listaClientes.add(c);
            }
        } catch (SQLException e) {}
        return listaClientes;
    }
    
    public List<Cliente> buscar(String texto) {
        List<Cliente> listaClientes = new ArrayList<>();
        String sql = "SELECT * FROM Clientes WHERE apellido != 'ELIMINADO' AND (numero_identidad LIKE ? OR nombre LIKE ? OR apellido LIKE ?)";        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            String parametro = "%" + texto + "%";
            comando.setString(1, parametro);
            comando.setString(2, parametro);
            comando.setString(3, parametro);
            
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(resultado.getInt("id_cliente"));
                    c.setNumeroIdentidad(resultado.getString("numero_identidad"));
                    c.setNombre(resultado.getString("nombre"));
                    c.setApellido(resultado.getString("apellido"));
                    c.setTelefono(resultado.getString("telefono"));
                    c.setCorreo(resultado.getString("correo"));
                    listaClientes.add(c);
                }
            }
        } catch (SQLException e) {}
        return listaClientes;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE Clientes SET numero_identidad = ?, nombre = ?, apellido = ?, telefono = ?, correo = ? WHERE id_cliente = ?";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setString(1, cliente.getNumeroIdentidad());
            comando.setString(2, cliente.getNombre());
            comando.setString(3, cliente.getApellido());
            
            // --- MAGIA ANTI-NULL EN ACTUALIZACIÓN ---
            if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
                String rellenoTel = "SN-" + cliente.getNumeroIdentidad();
                if (rellenoTel.length() > 20) rellenoTel = rellenoTel.substring(0, 20);
                comando.setString(4, rellenoTel);
            } else {
                comando.setString(4, cliente.getTelefono().trim());
            }
            
            if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
                String rellenoCor = "SN-" + cliente.getNumeroIdentidad() + "@sairtech.com";
                if (rellenoCor.length() > 50) rellenoCor = rellenoCor.substring(0, 50);
                comando.setString(5, rellenoCor);
            } else {
                comando.setString(5, cliente.getCorreo().trim());
            }
            // -----------------------------------------
            
            comando.setInt(6, cliente.getIdCliente());
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) { 
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false; 
        }
    }

    public boolean eliminar(int idCliente) {
        String sql = "UPDATE Clientes SET numero_identidad = CONCAT('0000-0000-', id_cliente), nombre = '***', apellido = 'ELIMINADO', telefono = '********', correo = '***' WHERE id_cliente = ?";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            comando.setInt(1, idCliente);
            return comando.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
    
    public boolean tieneHistorial(int idCliente) {
        String sql = "SELECT COUNT(*) FROM Equipos_Registrados WHERE id_cliente = ?";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            comando.setInt(1, idCliente);
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {}
        return false;
    }

    public boolean existeIdentidad(String identidad, int idClienteAIgnorar) {
        String sql = "SELECT COUNT(*) FROM Clientes WHERE numero_identidad = ? AND id_cliente != ? AND apellido != 'ELIMINADO'";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            comando.setString(1, identidad);
            comando.setInt(2, idClienteAIgnorar);
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {}
        return false;
    }

    public boolean existeTelefono(String telefono, int idClienteAIgnorar) {
        String sql = "SELECT COUNT(*) FROM Clientes WHERE telefono = ? AND id_cliente != ? AND apellido != 'ELIMINADO'";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            comando.setString(1, telefono);
            comando.setInt(2, idClienteAIgnorar);
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {}
        return false;
    }
}