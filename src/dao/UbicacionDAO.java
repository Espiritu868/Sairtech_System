package dao;

import factory.ConexionFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UbicacionDAO {
    private ConexionFactory factory;

    public UbicacionDAO() {
        this.factory = new ConexionFactory();
    }

    public boolean insertar(String nombre) {
        String sql = "INSERT IGNORE INTO ubicaciones (nombre_ubicacion) VALUES (?)";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<String> listar() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT nombre_ubicacion FROM ubicaciones ORDER BY nombre_ubicacion ASC";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(rs.getString("nombre_ubicacion"));
            }
        } catch (SQLException e) { }
        return lista;
    }
    
    // --- VERSIÓN CON RASTREADORES PARA LA CONSOLA ---
    public boolean actualizar(String nombreAntiguo, String nombreNuevo) {
        System.out.println("=========================================");
        System.out.println("🔍 DAO INICIANDO: Cambiando '" + nombreAntiguo + "' por '" + nombreNuevo + "'");
        
        String sqlUbicacion = "UPDATE ubicaciones SET nombre_ubicacion = ? WHERE TRIM(nombre_ubicacion) = TRIM(?)";
        String sqlProductos = "UPDATE productos SET ubicacion = ? WHERE TRIM(ubicacion) = TRIM(?)";
        Connection con = null;
        
        try {
            con = factory.getConexion();
            con.setAutoCommit(false); 
            
            // 1. Actualizar catálogo
            int filasCat = 0;
            try (PreparedStatement ps1 = con.prepareStatement(sqlUbicacion)) {
                ps1.setString(1, nombreNuevo.trim());
                ps1.setString(2, nombreAntiguo.trim());
                filasCat = ps1.executeUpdate(); 
            }
            System.out.println("📊 Filas modificadas en catálogo (Ubicaciones): " + filasCat);
            
            if (filasCat == 0) {
                System.err.println("⚠️ ERROR LÓGICO: MySQL no encontró la vitrina exacta. Cancelando...");
                con.rollback(); 
                return false; 
            }
            
            // 2. Actualizar productos
            int filasProd = 0;
            try (PreparedStatement ps2 = con.prepareStatement(sqlProductos)) {
                ps2.setString(1, nombreNuevo.trim());
                ps2.setString(2, nombreAntiguo.trim());
                filasProd = ps2.executeUpdate(); 
            }
            System.out.println("📦 Productos movidos de lugar automáticamente: " + filasProd);
            
            con.commit(); 
            System.out.println("✅ COMMIT EXITOSO. TODO GUARDADO.");
            System.out.println("=========================================");
            return true;
            
        } catch (SQLException e) {
            System.err.println("💥 ERROR SQL CATASTRÓFICO: " + e.getMessage());
            if(con != null) try { con.rollback(); } catch(SQLException ex){}
            return false;
        } finally {
            if(con != null) try { con.setAutoCommit(true); con.close(); } catch(SQLException ex){}
        }
    }
}