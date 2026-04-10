package dao;

import factory.ConexionFactory;
import modelo.CategoriaProducto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaProductoDAO {
    private ConexionFactory factory;

    public CategoriaProductoDAO() {
        this.factory = new ConexionFactory();
    }

    public List<CategoriaProducto> listar() {
        List<CategoriaProducto> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias_productos ORDER BY nombre_categoria ASC";
        
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                CategoriaProducto c = new CategoriaProducto();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                c.setDescripcion(rs.getString("descripcion"));
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        return lista;
    }
}