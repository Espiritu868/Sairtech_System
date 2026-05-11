package dao;

import factory.ConexionFactory;
import modelo.Expediente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {
    
    private ConexionFactory factory;

    public HistorialDAO() {
        this.factory = new ConexionFactory();
    }

    public List<Expediente> buscarExpedienteCompleto(String textoBusqueda, String filtroEstado, boolean verPapelera) {
        List<Expediente> lista = new ArrayList<>();
        
        // 1. CORRECCIÓN: Manejo de la Papelera usando la columna 'eliminado' (1 = Papelera, 0 = Activo)
        String condicionPapelera = verPapelera ? "o.eliminado = 1" : "o.eliminado = 0";
        
        // 2. Manejo del Filtro de Estado ("Todos" no filtra)
        String condicionEstado = "";
        if (!filtroEstado.equals("Todos")) {
            condicionEstado = " AND o.estado = '" + filtroEstado + "' ";
        }

        // 3. CONSULTA CON 'eliminado' INCLUIDO EN EL WHERE
        String sql = "SELECT " +
                     "o.id_orden, " +
                     "o.fecha_ingreso, " +
                     "o.fecha_entrega, " +
                     "o.estado, " +
                     "CONCAT(c.nombre, ' ', c.apellido) AS nombre_cliente, " +
                     "c.telefono AS telefono_cliente, " +
                     "e.modelo AS modelo_equipo, " + 
                     "o.problema_reportado, " + 
                     "o.trabajo_realizado, " + 
                     "o.costo, " + 
                     "u.usuario AS tecnico_entrega " + 
                     "FROM ordenes_reparacion o " +
                     "LEFT JOIN equipos_registrados e ON o.id_equipo = e.id_equipo " +
                     "LEFT JOIN clientes c ON e.id_cliente = c.id_cliente " +
                     "LEFT JOIN usuarios u ON o.id_usuario_entrega = u.id_usuario " +
                     "WHERE (" + condicionPapelera + ")" + condicionEstado;

        // 4. Inyección dinámica del texto de búsqueda
        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            sql += " AND (e.modelo LIKE ? OR CONCAT(c.nombre, ' ', c.apellido) LIKE ? OR o.id_orden LIKE ?)";
        }
        
        // 5. Ordenamos por el más reciente
        sql += " ORDER BY o.id_orden DESC";

        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                String param = "%" + textoBusqueda.trim() + "%";
                ps.setString(1, param);
                ps.setString(2, param);
                ps.setString(3, param); 
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Expediente exp = new Expediente();
                    exp.setIdOrden(rs.getInt("id_orden"));
                    exp.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
                    exp.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
                    exp.setEstado(rs.getString("estado") != null ? rs.getString("estado") : "Pendiente");
                    exp.setNombreCliente(rs.getString("nombre_cliente") != null ? rs.getString("nombre_cliente") : "Desconocido");
                    exp.setTelefonoCliente(rs.getString("telefono_cliente") != null ? rs.getString("telefono_cliente") : "N/A");
                    exp.setModeloEquipo(rs.getString("modelo_equipo") != null ? rs.getString("modelo_equipo") : "Desconocido");
                    exp.setProblemaReportado(rs.getString("problema_reportado") != null ? rs.getString("problema_reportado") : "No detallado");
                    exp.setTrabajoRealizado(rs.getString("trabajo_realizado") != null ? rs.getString("trabajo_realizado") : "Sin diagnóstico registrado");
                    exp.setCosto(rs.getDouble("costo"));
                    exp.setTecnicoEntrega(rs.getString("tecnico_entrega") != null ? rs.getString("tecnico_entrega") : "N/A");
                    
                    lista.add(exp);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al buscar el historial maestro unificado: " + e.getMessage());
        }
        return lista;
    }
}