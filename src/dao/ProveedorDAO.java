package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import modelo.Proveedor;

public class ProveedorDAO {

    public boolean guardar(Proveedor p) {
        String sql = "INSERT INTO proveedores (empresa, nombre_contacto, telefono, direccion, tipo_repuestos) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getEmpresa());
            ps.setString(2, p.getNombreContacto());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getDireccion());
            ps.setString(5, p.getTipoRepuestos());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al guardar proveedor: " + e.getMessage());
            return false;
        }
    }

    public List<Proveedor> listarActivos() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores WHERE estado = 'Activo' ORDER BY empresa ASC";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdProveedor(rs.getInt("id_proveedor"));
                p.setEmpresa(rs.getString("empresa"));
                p.setNombreContacto(rs.getString("nombre_contacto"));
                p.setTelefono(rs.getString("telefono"));
                p.setDireccion(rs.getString("direccion"));
                p.setTipoRepuestos(rs.getString("tipo_repuestos"));
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
        } catch (Exception e) {
            System.err.println("Error al listar proveedores: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizar(Proveedor p) {
        String sql = "UPDATE proveedores SET empresa=?, nombre_contacto=?, telefono=?, direccion=?, tipo_repuestos=? WHERE id_proveedor=?";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, p.getEmpresa());
            ps.setString(2, p.getNombreContacto());
            ps.setString(3, p.getTelefono());
            ps.setString(4, p.getDireccion());
            ps.setString(5, p.getTipoRepuestos());
            ps.setInt(6, p.getIdProveedor());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            return false;
        }
    }

    public boolean ocultar(int idProveedor) {
        String sql = "UPDATE proveedores SET estado = 'Oculto' WHERE id_proveedor = ?";
        try (Connection con = new factory.ConexionFactory().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}