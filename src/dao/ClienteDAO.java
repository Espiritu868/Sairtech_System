package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;

public class ClienteDAO {
    private ConexionFactory factory;

    public ClienteDAO() {
        this.factory = new ConexionFactory();
    }

    public boolean insertar(Cliente cliente) {
        String sql = "INSERT INTO Clientes (numero_identidad, nombre, apellido, telefono, correo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setString(1, cliente.getNumeroIdentidad());
            comando.setString(2, cliente.getNombre());
            comando.setString(3, cliente.getApellido());
            
            // Envío seguro de nulos para campos opcionales
            if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
                comando.setNull(4, java.sql.Types.VARCHAR);
            } else {
                comando.setString(4, cliente.getTelefono().trim());
            }
            
            if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
                comando.setNull(5, java.sql.Types.VARCHAR);
            } else {
                comando.setString(5, cliente.getCorreo().trim());
            }
            
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar cliente: " + e.getMessage());
            return false;
        }
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
        } catch (SQLException e) {
            System.err.println("Error al listar clientes: " + e.getMessage());
        }
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
                    listaClientes.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en la búsqueda en vivo: " + e.getMessage());
        }
        return listaClientes;
    }

    public boolean actualizar(Cliente cliente) {
        String sql = "UPDATE Clientes SET numero_identidad = ?, nombre = ?, apellido = ?, telefono = ?, correo = ? WHERE id_cliente = ?";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setString(1, cliente.getNumeroIdentidad());
            comando.setString(2, cliente.getNombre());
            comando.setString(3, cliente.getApellido());
            
            // Envío seguro de nulos en actualización
            if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
                comando.setNull(4, java.sql.Types.VARCHAR);
            } else {
                comando.setString(4, cliente.getTelefono().trim());
            }
            
            if (cliente.getCorreo() == null || cliente.getCorreo().trim().isEmpty()) {
                comando.setNull(5, java.sql.Types.VARCHAR);
            } else {
                comando.setString(5, cliente.getCorreo().trim());
            }
            
            comando.setInt(6, cliente.getIdCliente());
            
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int idCliente) {
        // Soft delete: Ocultamos los datos sensibles pero conservamos el registro para el historial contable
        String sql = "UPDATE Clientes SET numero_identidad = CONCAT('0000-0000-', id_cliente), nombre = '***', apellido = 'ELIMINADO', telefono = '********', correo = '***' WHERE id_cliente = ?";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setInt(1, idCliente);
            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
            return false;
        }
    }
    
    public boolean tieneHistorial(int idCliente) {
        String sql = "SELECT COUNT(*) FROM Equipos_Registrados WHERE id_cliente = ?";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
             
            comando.setInt(1, idCliente);
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al comprobar historial del cliente: " + e.getMessage());
        }
        return false;
    }

    // --- MÉTODOS DE VALIDACIÓN DE DUPLICADOS ---

    public boolean existeIdentidad(String identidad, int idClienteAIgnorar) {
        String sql = "SELECT COUNT(*) FROM Clientes WHERE numero_identidad = ? AND id_cliente != ? AND apellido != 'ELIMINADO'";
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            comando.setString(1, identidad);
            comando.setInt(2, idClienteAIgnorar);
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al validar identidad duplicada: " + e.getMessage());
        }
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
        } catch (SQLException e) {
            System.err.println("Error al validar teléfono duplicado: " + e.getMessage());
        }
        return false;
    }
}