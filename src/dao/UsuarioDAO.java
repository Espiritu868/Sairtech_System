package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;

public class UsuarioDAO {
    
    private ConexionFactory factory;

    public UsuarioDAO() {
        this.factory = new ConexionFactory();
    }

    public String encriptarContraseña(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error al encriptar la contraseña: " + ex.getMessage());
        }
    }

    public String validarLogin(String usuario, String passwordReal) {
        String sql = "SELECT rol FROM Usuarios WHERE usuario = ? AND password_hash = ?";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
             
            String passwordEncriptado = encriptarContraseña(passwordReal);
            
            comando.setString(1, usuario);
            comando.setString(2, passwordEncriptado);
            
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("rol"); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL en la validación de login: " + e.getMessage());
        }
        
        return "ERROR";
    }
    
    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, usuario, rol FROM Usuarios";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql);
             ResultSet rs = comando.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("usuario"));
                u.setRol(rs.getString("rol"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrarUsuario(String usuario, String passwordReal, String rol) {
        String sql = "INSERT INTO Usuarios (usuario, password_hash, rol) VALUES (?, ?, ?)";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {

            String passwordEncriptado = encriptarContraseña(passwordReal);

            comando.setString(1, usuario);
            comando.setString(2, passwordEncriptado);
            comando.setString(3, rol);

            return comando.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminarUsuario(int idUsuario) {
        if (idUsuario == 1) {
            System.err.println("Operación denegada: No se puede eliminar al administrador principal del sistema.");
            return false; 
        }

        String sql = "DELETE FROM Usuarios WHERE id_usuario = ?";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);
            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public boolean modificarUsuario(int idUsuario, String nuevoUsuario, String passwordReal, String nuevoRol) {
        String sql;
        boolean cambiarPassword = !passwordReal.isEmpty();

        if (cambiarPassword) {
            sql = "UPDATE Usuarios SET usuario = ?, password_hash = ?, rol = ? WHERE id_usuario = ?";
        } else {
            sql = "UPDATE Usuarios SET usuario = ?, rol = ? WHERE id_usuario = ?";
        }

        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {

            comando.setString(1, nuevoUsuario);

            if (cambiarPassword) {
                comando.setString(2, encriptarContraseña(passwordReal));
                comando.setString(3, nuevoRol);
                comando.setInt(4, idUsuario);
            } else {
                comando.setString(2, nuevoRol);
                comando.setInt(3, idUsuario);
            }

            return comando.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al modificar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public int obtenerIdPorNombre(String nombreUsuario) {
        String sql = "SELECT id_usuario FROM usuarios WHERE usuario = ?";
        
        try (Connection conexion = factory.getConexion(); 
             PreparedStatement comando = conexion.prepareStatement(sql)) {
             
            comando.setString(1, nombreUsuario);
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ID del usuario: " + e.getMessage());
        }
        return 0; 
    }

    // ==============================================================================
    // NUEVOS MÉTODOS PARA LA AUDITORÍA Y FIRMA RÁPIDA (PIN)
    // ==============================================================================

    // 1. Busca el nombre del usuario usando SOLO su contraseña (encriptándola primero)
    public String obtenerUsuarioPorClave(String passwordReal) {
        String usuario = null;
        String sql = "SELECT usuario FROM Usuarios WHERE password_hash = ?"; 
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            String passwordEncriptado = encriptarContraseña(passwordReal);
            comando.setString(1, passwordEncriptado);
            
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    usuario = rs.getString("usuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por clave: " + e.getMessage());
        }
        return usuario;
    }

    // 2. Verifica si una contraseña ya existe (encriptándola primero para buscarla)
    public boolean existeClave(String passwordReal) {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE password_hash = ?";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql)) {
            
            String passwordEncriptado = encriptarContraseña(passwordReal);
            comando.setString(1, passwordEncriptado);
            
            try (ResultSet rs = comando.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar clave duplicada: " + e.getMessage());
        }
        return false;
    }
    
    public void inicializarAdministradorDefecto() {
        String sqlCheck = "SELECT COUNT(*) FROM Usuarios";
        
        try (Connection conexion = factory.getConexion();
             PreparedStatement cmdCheck = conexion.prepareStatement(sqlCheck);
             ResultSet rs = cmdCheck.executeQuery()) {
            
            // Si el conteo es 0, la tabla está completamente vacía (instalación limpia)
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Base de datos sin usuarios. Instalando administrador principal...");
                
                // Generamos el primer usuario administrador
                registrarUsuario("SairTech", "d3jam33ntrar", "Administrador");
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar/crear el usuario inicial: " + e.getMessage());
        }
    }
}