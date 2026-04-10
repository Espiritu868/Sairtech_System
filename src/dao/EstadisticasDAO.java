package dao;

import factory.ConexionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import modelo.Estadisticas;

public class EstadisticasDAO {
    
    private ConexionFactory factory;

    public EstadisticasDAO() {
        this.factory = new ConexionFactory();
    }

    public Estadisticas obtenerNumerosMagicos() {
        Estadisticas est = new Estadisticas();
        
        String sqlEntregados = "SELECT COUNT(*) AS total FROM ordenes_reparacion WHERE estado = 'Entregado'";
        String sqlPendientes = "SELECT COUNT(*) AS total FROM ordenes_reparacion WHERE estado != 'Entregado'";
        
        // --- CAMBIO AQUÍ: Sumamos Reparaciones + Ventas de Mostrador ---
        String sqlGanancias = "SELECT ( " +
                             "  (SELECT COALESCE(SUM(costo), 0) FROM ordenes_reparacion WHERE estado = 'Entregado') + " +
                             "  (SELECT COALESCE(SUM(total), 0) FROM ventas) " +
                             ") AS dinero";

        try (Connection conexion = factory.getConexion()) {
            
            try (PreparedStatement cmd1 = conexion.prepareStatement(sqlEntregados);
                 ResultSet rs1 = cmd1.executeQuery()) {
                if (rs1.next()) est.setEquiposEntregados(rs1.getInt("total"));
            }

            try (PreparedStatement cmd2 = conexion.prepareStatement(sqlPendientes);
                 ResultSet rs2 = cmd2.executeQuery()) {
                if (rs2.next()) est.setOrdenesPendientes(rs2.getInt("total"));
            }

            try (PreparedStatement cmd3 = conexion.prepareStatement(sqlGanancias);
                 ResultSet rs3 = cmd3.executeQuery()) {
                if (rs3.next()) est.setGananciasTotales(rs3.getDouble("dinero"));
            }

        } catch (SQLException e) {
            System.err.println("Error al calcular estadísticas: " + e.getMessage());
        }
        
        return est;
    }
    
    public Estadisticas obtenerNumerosMagicosMes(int mes, int anio) {
        Estadisticas est = new Estadisticas();
        
        String sqlEntregados = "SELECT COUNT(*) AS total FROM ordenes_reparacion WHERE estado = 'Entregado' AND MONTH(fecha_ingreso) = ? AND YEAR(fecha_ingreso) = ?";
        String sqlPendientes = "SELECT COUNT(*) AS total FROM ordenes_reparacion WHERE estado NOT IN ('Entregado', 'Sin Reparacion') AND MONTH(fecha_ingreso) = ? AND YEAR(fecha_ingreso) = ?";
        
        // --- CAMBIO AQUÍ: Sumamos Reparaciones del mes + Ventas del mes ---
        String sqlGanancias = "SELECT ( " +
                             "  (SELECT COALESCE(SUM(costo), 0) FROM ordenes_reparacion WHERE estado = 'Entregado' AND MONTH(fecha_ingreso) = ? AND YEAR(fecha_ingreso) = ?) + " +
                             "  (SELECT COALESCE(SUM(total), 0) FROM ventas WHERE MONTH(fecha_venta) = ? AND YEAR(fecha_venta) = ?) " +
                             ") AS dinero";

        try (Connection con = factory.getConexion()) {
            
            try (PreparedStatement ps = con.prepareStatement(sqlEntregados)) {
                ps.setInt(1, mes); ps.setInt(2, anio);
                try (ResultSet rs = ps.executeQuery()) { 
                    if (rs.next()) est.setEquiposEntregados(rs.getInt("total")); 
                }
            }
            
            try (PreparedStatement ps = con.prepareStatement(sqlPendientes)) {
                ps.setInt(1, mes); ps.setInt(2, anio);
                try (ResultSet rs = ps.executeQuery()) { 
                    if (rs.next()) est.setOrdenesPendientes(rs.getInt("total")); 
                }
            }
            
            try (PreparedStatement ps = con.prepareStatement(sqlGanancias)) {
                // Parámetros para la suma de reparaciones
                ps.setInt(1, mes); ps.setInt(2, anio);
                // Parámetros para la suma de ventas
                ps.setInt(3, mes); ps.setInt(4, anio);
                try (ResultSet rs = ps.executeQuery()) { 
                    if (rs.next()) est.setGananciasTotales(rs.getDouble("dinero")); 
                }
            }
            
        } catch (SQLException e) { 
            System.err.println("Error en estadísticas mensuales: " + e.getMessage()); 
        }
        
        return est;
    }

    // Los métodos de gráficos se mantienen igual porque se basan en volumen de equipos
    public Map<String, Integer> obtenerDatosGraficoEquipos() {
        Map<String, Integer> mapaDatos = new HashMap<>();
        String sql = "SELECT t.nombre_tipo, COUNT(e.id_equipo) AS total "
                   + "FROM equipos_registrados e "
                   + "INNER JOIN tipos_equipo t ON e.id_tipo = t.id_tipo "
                   + "GROUP BY t.nombre_tipo";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                mapaDatos.put(rs.getString("nombre_tipo"), rs.getInt("total"));
            }
        } catch (SQLException e) { System.err.println("Error en gráfico: " + e.getMessage()); }
        return mapaDatos;
    }

    public Map<String, Integer> obtenerDatosGraficoEquiposMes(int mes, int anio) {
        Map<String, Integer> mapa = new HashMap<>();
        String sql = "SELECT t.nombre_tipo, COUNT(o.id_orden) AS total " +
                     "FROM ordenes_reparacion o " +
                     "JOIN equipos_registrados e ON o.id_equipo = e.id_equipo " +
                     "JOIN tipos_equipo t ON e.id_tipo = t.id_tipo " +
                     "WHERE MONTH(o.fecha_ingreso) = ? AND YEAR(o.fecha_ingreso) = ? " +
                     "GROUP BY t.nombre_tipo";
        try (Connection con = factory.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mes); ps.setInt(2, anio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { 
                    mapa.put(rs.getString("nombre_tipo"), rs.getInt("total")); 
                }
            }
        } catch (SQLException e) { System.err.println("Error en gráfico mensual: " + e.getMessage()); }
        return mapa;
    }
}