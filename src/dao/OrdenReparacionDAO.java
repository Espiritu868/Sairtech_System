    package dao;

    import factory.ConexionFactory;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.sql.Statement;
    import java.util.ArrayList;
    import java.util.List;
    import modelo.OrdenReparacion;

    public class OrdenReparacionDAO {
        private ConexionFactory factory;

        public OrdenReparacionDAO() {
            this.factory = new ConexionFactory();
        }

        public int insertarConId(OrdenReparacion orden) {
            String sql = "INSERT INTO Ordenes_Reparacion (id_equipo, problema_reportado, trabajo_realizado, costo, estado, seguridad_dispositivo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = factory.getConexion();
             PreparedStatement comando = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, orden.getIdEquipo());
            comando.setString(2, orden.getProblemaReportado());
            comando.setString(3, orden.getTrabajoRealizado());
            comando.setDouble(4, orden.getCosto());
            comando.setString(5, orden.getEstado());
            comando.setString(6, orden.getSeguridadDispositivo()); // <--- NUEVO

                int filasAfectadas = comando.executeUpdate();

                if (filasAfectadas > 0) {
                    try (ResultSet rs = comando.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error al insertar orden: " + e.getMessage());
            }
            return -1;
        }

        public boolean insertar(OrdenReparacion orden) {
            return insertarConId(orden) != -1;
        }

        public boolean actualizar(OrdenReparacion orden) {
            String sql = "UPDATE Ordenes_Reparacion SET problema_reportado = ?, trabajo_realizado = ?, costo = ?, estado = ?, id_usuario_entrega = ? WHERE id_orden = ?";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setString(1, orden.getProblemaReportado());
                comando.setString(2, orden.getTrabajoRealizado());
                comando.setDouble(3, orden.getCosto());
                comando.setString(4, orden.getEstado());

                if (orden.getIdUsuarioEntrega() > 0) {
                    comando.setInt(5, orden.getIdUsuarioEntrega());
                } else {
                    comando.setNull(5, java.sql.Types.INTEGER);
                }

                comando.setInt(6, orden.getIdOrden());

                return comando.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al actualizar la orden: " + e.getMessage());
                return false;
            }
        }

        public boolean actualizarEstadoYCosto(int idOrden, String nuevoEstado, double nuevoCosto) {
            String sql = "UPDATE Ordenes_Reparacion SET estado = ?, costo = ? WHERE id_orden = ?";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setString(1, nuevoEstado);
                comando.setDouble(2, nuevoCosto);
                comando.setInt(3, idOrden);

                return comando.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al actualizar estado y costo: " + e.getMessage());
                return false;
            }
        }

        public List<Object[]> listarReporteCompleto() {
            List<Object[]> listaReporte = new ArrayList<>();
            // Añadimos t.nombre_tipo a la consulta
            String sql = "SELECT o.id_orden, CONCAT(c.nombre, ' ', c.apellido) AS nombre_completo, e.modelo, o.fecha_ingreso, o.problema_reportado, o.estado, o.costo, t.nombre_tipo " +
                         "FROM Ordenes_Reparacion o " +
                         "JOIN Equipos_Registrados e ON o.id_equipo = e.id_equipo " +
                         "JOIN Clientes c ON e.id_cliente = c.id_cliente " +
                         "JOIN Tipos_Equipo t ON e.id_tipo = t.id_tipo " + 
                         "ORDER BY o.fecha_ingreso DESC";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql);
                 ResultSet resultado = comando.executeQuery()) {

                while (resultado.next()) {
                    Object[] fila = new Object[7]; // Ampliamos a 7 columnas
                    fila[0] = resultado.getInt("id_orden");
                    fila[1] = resultado.getString("nombre_completo"); 
                    fila[2] = resultado.getString("modelo");
                    fila[3] = resultado.getString("problema_reportado");
                    fila[4] = resultado.getString("estado");
                    fila[5] = resultado.getDouble("costo");
                    fila[6] = resultado.getString("nombre_tipo"); // Nueva columna
                    listaReporte.add(fila);
                }
            } catch (SQLException e) {
                System.err.println("Error al listar reporte completo: " + e.getMessage());
            }
            return listaReporte;
        }

        public List<Object[]> buscarOrden(String texto) {
            List<Object[]> lista = new ArrayList<>();
            // Añadimos t.nombre_tipo a la consulta de búsqueda
            String sql = "SELECT o.id_orden, CONCAT(c.nombre, ' ', c.apellido) AS nombre_completo, " +
                         "e.modelo, o.problema_reportado, o.estado, o.costo, t.nombre_tipo " +
                         "FROM Ordenes_Reparacion o " +
                         "LEFT JOIN Equipos_Registrados e ON o.id_equipo = e.id_equipo " +
                         "LEFT JOIN Clientes c ON e.id_cliente = c.id_cliente " +
                         "LEFT JOIN Tipos_Equipo t ON e.id_tipo = t.id_tipo " +
                         "WHERE o.id_orden LIKE ? OR c.nombre LIKE ? OR c.apellido LIKE ? OR e.modelo LIKE ? " +
                         "ORDER BY o.id_orden DESC";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                String p = "%" + texto + "%";
                comando.setString(1, p);
                comando.setString(2, p);
                comando.setString(3, p);
                comando.setString(4, p);

                try (ResultSet resultado = comando.executeQuery()) {
                    while (resultado.next()) {
                        Object[] fila = new Object[7];
                        fila[0] = resultado.getInt("id_orden");
                        fila[1] = resultado.getString("nombre_completo");
                        fila[2] = resultado.getString("modelo");
                        fila[3] = resultado.getString("problema_reportado");
                        fila[4] = resultado.getString("estado");
                        fila[5] = resultado.getDouble("costo");
                        fila[6] = resultado.getString("nombre_tipo"); // Nueva columna
                        lista.add(fila);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error buscando orden: " + e.getMessage());
            }
            return lista;
        }

        public boolean eliminar(int idOrden) {
            String sql = "DELETE FROM Ordenes_Reparacion WHERE id_orden = ?";
            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setInt(1, idOrden);
                return comando.executeUpdate() > 0;

            } catch (SQLException e) {
                System.err.println("Error al eliminar orden: " + e.getMessage());
                return false;
            }
        }

        public String[] obtenerTextosOrden(int idOrden) {
            String[] textos = new String[3]; // <--- Ahora guarda 3 cosas
            // Asegúrate de que la columna se llama seguridad_dispositivo en tu BD
            String sql = "SELECT problema_reportado, trabajo_realizado, seguridad_dispositivo FROM Ordenes_Reparacion WHERE id_orden = ?";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setInt(1, idOrden);
                try (ResultSet rs = comando.executeQuery()) {
                    if (rs.next()) {
                        textos[0] = rs.getString("problema_reportado");
                        textos[1] = rs.getString("trabajo_realizado");
                        textos[2] = rs.getString("seguridad_dispositivo"); // <--- Extraemos la clave
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener textos de la orden: " + e.getMessage());
            }
            return textos;
        }

        public boolean actualizarTextosOrden(int idOrden, String problema, String trabajo, String seguridad) {
            String sql = "UPDATE ordenes_reparacion SET problema_reportado = ?, trabajo_realizado = ?, seguridad_dispositivo = ? WHERE id_orden = ?";
            try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, problema);
                ps.setString(2, trabajo);
                ps.setString(3, seguridad); // <--- Guardamos la nueva clave
                ps.setInt(4, idOrden);

                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                System.err.println("Error al actualizar detalles y clave: " + e.getMessage());
                return false;
            }
        }

        public boolean marcarComoEntregado(int idOrden, int idUsuarioEntrega) {
            String sql = "UPDATE Ordenes_Reparacion SET estado = 'Entregado', id_usuario_entrega = ? WHERE id_orden = ?";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setInt(1, idUsuarioEntrega);
                comando.setInt(2, idOrden);

                return comando.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al marcar equipo como entregado: " + e.getMessage());
                return false;
            }
        }

        public List<Object[]> filtrarPorEstado(String estadoFiltro) {
            List<Object[]> listaReporte = new ArrayList<>();
            // Añadimos t.nombre_tipo al filtro también
            String sql = "SELECT o.id_orden, CONCAT(c.nombre, ' ', c.apellido) AS nombre_completo, e.modelo, o.fecha_ingreso, o.problema_reportado, o.estado, o.costo, t.nombre_tipo " +
                         "FROM Ordenes_Reparacion o " +
                         "JOIN Equipos_Registrados e ON o.id_equipo = e.id_equipo " +
                         "JOIN Clientes c ON e.id_cliente = c.id_cliente " +
                         "JOIN Tipos_Equipo t ON e.id_tipo = t.id_tipo " +
                         "WHERE o.estado = ? " + 
                         "ORDER BY o.fecha_ingreso DESC";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setString(1, estadoFiltro);

                try (ResultSet resultado = comando.executeQuery()) {
                    while (resultado.next()) {
                        Object[] fila = new Object[7]; 
                        fila[0] = resultado.getInt("id_orden");
                        fila[1] = resultado.getString("nombre_completo"); 
                        fila[2] = resultado.getString("modelo");
                        fila[3] = resultado.getString("problema_reportado");
                        fila[4] = resultado.getString("estado");
                        fila[5] = resultado.getDouble("costo");
                        fila[6] = resultado.getString("nombre_tipo"); // Nueva columna
                        listaReporte.add(fila);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error al filtrar órdenes por estado: " + e.getMessage());
            }
            return listaReporte;
        }

        public String[] buscarDetallesEquipoParaOrden(int idEquipo) {
            String[] detalles = new String[2]; 
            String sql = "SELECT CONCAT(c.nombre, ' ', c.apellido) AS nombre_completo, e.modelo " +
                         "FROM Equipos_Registrados e " +
                         "JOIN Clientes c ON e.id_cliente = c.id_cliente " +
                         "WHERE e.id_equipo = ?";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setInt(1, idEquipo);

                try (ResultSet rs = comando.executeQuery()) {
                    if (rs.next()) {
                        detalles[0] = rs.getString("nombre_completo");
                        detalles[1] = rs.getString("modelo");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error al buscar detalles del equipo: " + e.getMessage());
            }
            return detalles;
        }

        public String obtenerNombreTecnicoEntrega(int idOrden) {
            String nombreTecnico = "Pendiente de entrega";
            String sql = "SELECT u.usuario FROM Ordenes_Reparacion o " +
                         "INNER JOIN usuarios u ON o.id_usuario_entrega = u.id_usuario " +
                         "WHERE o.id_orden = ?";

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setInt(1, idOrden);
                try (ResultSet rs = comando.executeQuery()) {
                    if (rs.next()) {
                        nombreTecnico = rs.getString("usuario");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error al obtener nombre del técnico: " + e.getMessage());
            }

            return nombreTecnico;
        }

       // --- VERSIÓN BLINDADA PARA OBTENER LA FECHA REAL ---
        public String obtenerFechaOrden(int idOrden) {
            String fecha = "";
            String sql = "SELECT fecha_ingreso FROM Ordenes_Reparacion WHERE id_orden = ?"; 

            try (Connection conexion = factory.getConexion();
                 PreparedStatement comando = conexion.prepareStatement(sql)) {

                comando.setInt(1, idOrden);
                try (ResultSet rs = comando.executeQuery()) {
                    if (rs.next()) {
                        try {
                            // Intento 1: Formato Fecha/Hora nativo de SQL
                            java.sql.Timestamp ts = rs.getTimestamp("fecha_ingreso");
                            if (ts != null) {
                                fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(ts);
                            }
                        } catch (Exception e) {
                            // Intento 2: Por si la columna en tu BD la creaste como texto (VARCHAR)
                            fecha = rs.getString("fecha_ingreso");
                        }
                    }
                }
            } catch (SQLException e) {
                // Si la consulta SQL falla por nombre incorrecto, nos lo gritará aquí:
                javax.swing.JOptionPane.showMessageDialog(null, "Error SQL al sacar fecha: " + e.getMessage());
            }

            // EL CHIVATO: Si después de todo esto la fecha sigue vacía, te avisará en pantalla
            if (fecha == null || fecha.trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "¡ALERTA!\nLa base de datos se conectó, pero 'fecha_ingreso' vino vacía para la orden #" + idOrden, 
                    "Fallo en Base de Datos", javax.swing.JOptionPane.WARNING_MESSAGE);
            }

            return fecha;
        }


    }