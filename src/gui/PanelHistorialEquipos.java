package gui;

import dao.HistorialDAO;
import dao.OrdenReparacionDAO;
import modelo.Expediente;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PanelHistorialEquipos extends JPanel {

    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltroEstado;
    private JCheckBox chkVerEliminados;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    // Elementos de la Ficha Clínica
    private JLabel lblOrdenFicha;
    private JLabel lblEstadoFicha;
    private JLabel lblFechas;
    private JLabel lblCliente;
    private JLabel lblModelo;
    private JTextArea txtFalla;
    private JTextArea txtDiagnostico;
    private JLabel lblTecnicos;
    private JLabel lblTotal;
    
    // Herramientas de Edición Inyectadas
    private JComboBox<String> cmbNuevoEstado;
    private JTextField txtCostoFinal;
    private JButton btnActualizarOrden;
    private JButton btnEditarDetalles;
    private JButton btnReimprimir;
    private JButton btnEliminar;

    private HistorialDAO daoHistorial;
    private OrdenReparacionDAO daoOrden; 
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
    
    private List<Expediente> listaExpedientesActual; 
    private int idOrdenSeleccionada = -1;
    private String firmaCambioEstado = "";

    public PanelHistorialEquipos() {
        daoHistorial = new HistorialDAO();
        daoOrden = new OrdenReparacionDAO();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel(" Historial y Control de Órdenes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(construirPanelIzquierdo());
        splitPane.setRightComponent(construirFichaClinicaYEdicion());
        splitPane.setDividerLocation(550); 
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        add(splitPane, BorderLayout.CENTER);
        
        cargarTabla(); 
    }

    private JPanel construirPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel panelBuscador = new JPanel(new BorderLayout(10, 0));
        panelBuscador.setOpaque(false);
        
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtBuscar.setPreferredSize(new Dimension(0, 40));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarTabla();
            }
        });
        
        JPanel pnlFiltros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFiltros.setOpaque(false);
        
        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Recibido", "En Revision", "Reparado", "Sin Reparacion", "Entregado"});
        cmbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbFiltroEstado.setPreferredSize(new Dimension(130, 40));
        cmbFiltroEstado.addActionListener(e -> cargarTabla());
        
        chkVerEliminados = new JCheckBox("Ver Papelera");
        chkVerEliminados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkVerEliminados.setOpaque(false);
        chkVerEliminados.addActionListener(e -> cargarTabla());

        pnlFiltros.add(cmbFiltroEstado);
        pnlFiltros.add(chkVerEliminados);
        
        panelBuscador.add(new JLabel(" Buscar: "), BorderLayout.WEST);
        panelBuscador.add(txtBuscar, BorderLayout.CENTER);
        panelBuscador.add(pnlFiltros, BorderLayout.EAST);

        String[] columnas = {"Orden", "Fecha", "Cliente", "Equipo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(35);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(50); 
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(90); 
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(150); 
        
        tablaHistorial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mostrarDetallesOrden();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(panelBuscador, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirFichaClinicaYEdicion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; 
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(5, 0, 5, 0);

        lblOrdenFicha = new JLabel("ORDEN #---");
        lblOrdenFicha.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblOrdenFicha.setForeground(new Color(41, 128, 185));
        
        lblEstadoFicha = new JLabel("ESTADO");
        lblEstadoFicha.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEstadoFicha.setForeground(Color.GRAY);
        lblEstadoFicha.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel pnlCabecera = new JPanel(new BorderLayout()); pnlCabecera.setOpaque(false);
        pnlCabecera.add(lblOrdenFicha, BorderLayout.WEST);
        pnlCabecera.add(lblEstadoFicha, BorderLayout.EAST);
        panel.add(pnlCabecera, gbc);
        
        panel.add(new JSeparator(), ++gbc.gridy);

        lblFechas = new JLabel("Ingreso: --/--/---- | Estatus: ---");
        lblFechas.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblFechas.setForeground(Color.DARK_GRAY);
        gbc.gridy++; panel.add(lblFechas, gbc);

        lblCliente = new JLabel("Cliente: Seleccione una orden...");
        lblCliente.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gbc.gridy++; gbc.insets = new Insets(15, 0, 2, 0); panel.add(lblCliente, gbc);

        lblModelo = new JLabel("Equipo: ---");
        lblModelo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        gbc.gridy++; gbc.insets = new Insets(0, 0, 15, 0); panel.add(lblModelo, gbc);

        txtFalla = crearTextAreaVisual("Problema Reportado:");
        gbc.gridy++; gbc.weighty = 0.3; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(txtFalla), gbc);

        txtDiagnostico = crearTextAreaVisual("Trabajo Realizado:");
        gbc.gridy++; gbc.insets = new Insets(15, 0, 15, 0);
        panel.add(new JScrollPane(txtDiagnostico), gbc);

        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0);
        panel.add(new JSeparator(), ++gbc.gridy);
        
        lblTecnicos = new JLabel("Técnico que Entregó: ---");
        lblTecnicos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTecnicos.setForeground(Color.GRAY);
        gbc.gridy++; panel.add(lblTecnicos, gbc);

        lblTotal = new JLabel("TOTAL COBRADO: L. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(new Color(39, 174, 96));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridy++; gbc.insets = new Insets(10, 0, 15, 0); panel.add(lblTotal, gbc);

        panel.add(new JSeparator(), ++gbc.gridy);

        // --- HERRAMIENTAS DE CONTROL ---
        JPanel pnlHerramientas = new JPanel(new GridBagLayout());
        pnlHerramientas.setOpaque(false);
        GridBagConstraints gbcH = new GridBagConstraints();
        gbcH.fill = GridBagConstraints.HORIZONTAL; gbcH.weightx = 1.0; gbcH.insets = new Insets(5, 5, 5, 5);
        gbcH.gridx = 0; gbcH.gridy = 0;

        cmbNuevoEstado = new JComboBox<>(new String[] { "Recibido", "En Revision", "Reparado", "Sin Reparacion" });
        cmbNuevoEstado.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNuevoEstado.setEnabled(false);

        JButton btnDesbloquear = new JButton("🔓");
        btnDesbloquear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDesbloquear.setToolTipText("Forzar cambio de estado (Requiere Firma)");
        btnDesbloquear.addActionListener(e -> desbloquearEstado());
        
        JPanel pnlEstado = new JPanel(new BorderLayout(5, 0)); pnlEstado.setOpaque(false);
        pnlEstado.add(cmbNuevoEstado, BorderLayout.CENTER); pnlEstado.add(btnDesbloquear, BorderLayout.EAST);

        txtCostoFinal = new JTextField("0.0");
        txtCostoFinal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        pnlHerramientas.add(new JLabel("Nuevo Estado:"), gbcH);
        gbcH.gridx++; pnlHerramientas.add(pnlEstado, gbcH);
        gbcH.gridx++; pnlHerramientas.add(new JLabel("Costo Final (L.):"), gbcH);
        gbcH.gridx++; pnlHerramientas.add(txtCostoFinal, gbcH);

        gbcH.gridx = 0; gbcH.gridy++; gbcH.gridwidth = 2;
        btnActualizarOrden = new JButton("Actualizar Estado y Costo");
        estilizarBoton(btnActualizarOrden, new Color(52, 152, 219));
        btnActualizarOrden.addActionListener(e -> actualizarOrden());
        pnlHerramientas.add(btnActualizarOrden, gbcH);

        gbcH.gridx = 2; gbcH.gridwidth = 2;
        btnEditarDetalles = new JButton("Editar Detalles / Clave");
        estilizarBoton(btnEditarDetalles, new Color(142, 68, 173));
        btnEditarDetalles.addActionListener(e -> editarDetallesOrden());
        pnlHerramientas.add(btnEditarDetalles, gbcH);

        // Fila 3: Reimprimir y Eliminar (Simétricos)
        gbcH.gridy++; 
        gbcH.gridx = 0; 
        gbcH.gridwidth = 2; 
        btnReimprimir = new JButton("Reimprimir");
        estilizarBoton(btnReimprimir, new Color(243, 156, 18));
        btnReimprimir.addActionListener(e -> reimprimirTickets());
        pnlHerramientas.add(btnReimprimir, gbcH);

        gbcH.gridx = 2; 
        gbcH.gridwidth = 2; 
        btnEliminar = new JButton("Eliminar");
        estilizarBoton(btnEliminar, new Color(231, 76, 60));
        btnEliminar.addActionListener(e -> eliminarO_Restaurar());
        pnlHerramientas.add(btnEliminar, gbcH);

        gbc.gridy++; panel.add(pnlHerramientas, gbc);

        estadoBotones(false);

        return panel;
    }

    private void estilizarBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 35));
    }

    private JTextArea crearTextAreaVisual(String titulo) {
        JTextArea txt = new JTextArea();
        txt.setLineWrap(true); txt.setWrapStyleWord(true);
        txt.setEditable(false); txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBackground(new Color(250, 250, 250));
        txt.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                titulo, TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 12), Color.GRAY
        ));
        return txt;
    }

    private void cargarTabla() {
        String texto = txtBuscar.getText().trim();
        String estadoSeleccionado = cmbFiltroEstado.getSelectedItem().toString();
        boolean verEliminados = chkVerEliminados.isSelected();
        
        modeloTabla.setRowCount(0);
        listaExpedientesActual = daoHistorial.buscarExpedienteCompleto(texto, estadoSeleccionado, verEliminados);
        
        for (Expediente exp : listaExpedientesActual) {
            String fechaCorta = exp.getFechaIngreso() != null ? new SimpleDateFormat("dd/MM/yy").format(exp.getFechaIngreso()) : "N/A";
            modeloTabla.addRow(new Object[]{
                "#" + exp.getIdOrden(), 
                fechaCorta,    
                exp.getNombreCliente(),        
                exp.getModeloEquipo(),        
                exp.getEstado()        
            });
        }
        
        btnEliminar.setText(verEliminados ? "Restaurar" : "Eliminar");
        btnEliminar.setBackground(verEliminados ? new Color(46, 204, 113) : new Color(231, 76, 60));
        
        limpiarFicha();
    }

    private void mostrarDetallesOrden() {
        int fila = tablaHistorial.getSelectedRow();
        if (fila >= 0) {
            int filaReal = tablaHistorial.convertRowIndexToModel(fila);
            Expediente exp = listaExpedientesActual.get(filaReal);
            
            idOrdenSeleccionada = exp.getIdOrden();
            firmaCambioEstado = "";
            cmbNuevoEstado.setEnabled(false);
            
            lblOrdenFicha.setText("ORDEN #" + exp.getIdOrden());
            lblEstadoFicha.setText(exp.getEstado().toUpperCase());
            
            String fEntregaTexto = "Aún en taller";
            if(exp.getEstado().equalsIgnoreCase("Entregado")) {
                lblEstadoFicha.setForeground(new Color(39, 174, 96));
                fEntregaTexto = "Entregado al cliente";
            } else if(exp.getEstado().equalsIgnoreCase("Cancelado") || exp.getEstado().equalsIgnoreCase("Sin Reparacion") || exp.getEstado().equalsIgnoreCase("Eliminado")) {
                lblEstadoFicha.setForeground(new Color(231, 76, 60));
                fEntregaTexto = exp.getEstado();
            } else {
                lblEstadoFicha.setForeground(new Color(243, 156, 18)); 
            }

            String fIngreso = exp.getFechaIngreso() != null ? sdf.format(exp.getFechaIngreso()) : "N/A";
            lblFechas.setText("Ingresó: " + fIngreso + "   |   Estatus: " + fEntregaTexto);
            lblCliente.setText("Cliente: " + exp.getNombreCliente() + " (Tel: " + exp.getTelefonoCliente() + ")");
            lblModelo.setText("Equipo: " + exp.getModeloEquipo());
            txtFalla.setText(exp.getProblemaReportado());
            txtDiagnostico.setText(exp.getTrabajoRealizado());
            lblTecnicos.setText("Técnico que Entregó: " + exp.getTecnicoEntrega());
            lblTotal.setText(String.format("TOTAL COBRADO: L. %.2f", exp.getCosto()));
            
            try { cmbNuevoEstado.setSelectedItem(exp.getEstado()); } catch (Exception e) {}
            txtCostoFinal.setText(String.valueOf(exp.getCosto()));
            
            estadoBotones(true);
        }
    }

    private void limpiarFicha() {
        idOrdenSeleccionada = -1;
        lblOrdenFicha.setText("ORDEN #---");
        lblEstadoFicha.setText("ESTADO");
        lblEstadoFicha.setForeground(Color.GRAY);
        lblFechas.setText("Ingreso: --/--/---- | Estatus: ---");
        lblCliente.setText("Cliente: Seleccione una orden...");
        lblModelo.setText("Equipo: ---");
        txtFalla.setText("");
        txtDiagnostico.setText("");
        lblTecnicos.setText("Técnico que Entregó: ---");
        lblTotal.setText("TOTAL COBRADO: L. 0.00");
        
        txtCostoFinal.setText("0.0");
        cmbNuevoEstado.setEnabled(false);
        estadoBotones(false);
    }
    
    private void estadoBotones(boolean estado) {
        btnActualizarOrden.setEnabled(estado);
        btnEditarDetalles.setEnabled(estado);
        btnReimprimir.setEnabled(estado);
        btnEliminar.setEnabled(estado);
    }

    private void desbloquearEstado() {
        if (idOrdenSeleccionada == -1) return;
        String tecnico = solicitarFirmaUsuario();
        if (tecnico != null) {
            cmbNuevoEstado.setEnabled(true); 
            firmaCambioEstado = tecnico;
            JOptionPane.showMessageDialog(this, "Estado desbloqueado por: " + tecnico.toUpperCase() + ".\nElija el nuevo estado y presione 'Actualizar Estado y Costo'.", "Firma Aceptada", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actualizarOrden() {
        if (idOrdenSeleccionada == -1) return;
        try {
            String estado = cmbNuevoEstado.getSelectedItem().toString();
            double costo = Double.parseDouble(txtCostoFinal.getText().trim());

            if (daoOrden.actualizarEstadoYCosto(idOrdenSeleccionada, estado, costo)) {
                
                if (!firmaCambioEstado.isEmpty()) {
                    String[] textos = daoOrden.obtenerTextosOrden(idOrdenSeleccionada);
                    String problema = textos[0] != null ? textos[0] : "";
                    String trabajo = textos[1] != null ? textos[1] : "";
                    String claveActual = textos.length > 2 && textos[2] != null ? textos[2] : "Sin Clave";
                    
                    String fechaCambio = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
                    String notaHistorial = "\n\n[" + fechaCambio + " - Estado cambiado a '" + estado.toUpperCase() + "' por: " + firmaCambioEstado.toUpperCase() + "]";
                    
                    daoOrden.actualizarTextosOrden(idOrdenSeleccionada, problema, trabajo + notaHistorial, claveActual);
                }
                
                JOptionPane.showMessageDialog(this, "¡Orden actualizada correctamente!");
                cargarTabla(); 
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un costo numérico válido (ej: 150.00)", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarDetallesOrden() {
        if (idOrdenSeleccionada == -1) return;

        String[] textosActuales = daoOrden.obtenerTextosOrden(idOrdenSeleccionada);

        JTextArea txtProblemaNuevo = new JTextArea(4, 30);
        txtProblemaNuevo.setText(textosActuales[0] != null ? textosActuales[0] : "");
        txtProblemaNuevo.setLineWrap(true); txtProblemaNuevo.setWrapStyleWord(true);

        JTextArea txtTrabajoNuevo = new JTextArea(4, 30);
        txtTrabajoNuevo.setText(textosActuales[1] != null ? textosActuales[1] : "");
        txtTrabajoNuevo.setLineWrap(true); txtTrabajoNuevo.setWrapStyleWord(true);

        JTextField txtClave = new JTextField();
        txtClave.setText(textosActuales.length > 2 && textosActuales[2] != null ? textosActuales[2] : "Sin Clave");
        txtClave.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel panelEdicion = new JPanel(new GridLayout(6, 1, 5, 5));
        panelEdicion.add(new JLabel("Problema Reportado:"));
        panelEdicion.add(new JScrollPane(txtProblemaNuevo));
        panelEdicion.add(new JLabel("Trabajo Realizado / Diagnóstico:"));
        panelEdicion.add(new JScrollPane(txtTrabajoNuevo));
        panelEdicion.add(new JLabel("Seguridad / Clave del Dispositivo:"));
        panelEdicion.add(txtClave); 

        if (JOptionPane.showConfirmDialog(this, panelEdicion, "Editando Orden N° " + idOrdenSeleccionada, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (daoOrden.actualizarTextosOrden(idOrdenSeleccionada, txtProblemaNuevo.getText().trim(), txtTrabajoNuevo.getText().trim(), txtClave.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Detalles y clave de seguridad actualizados.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar los detalles.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void reimprimirTickets() {
        if (idOrdenSeleccionada == -1) return;
        
        int fila = tablaHistorial.getSelectedRow();
        String estadoActual = tablaHistorial.getValueAt(fila, 4).toString().trim();
        String cliente = tablaHistorial.getValueAt(fila, 2).toString().trim();

        Object[] opciones;
        if (estadoActual.equals("Entregado")) {
            opciones = new Object[]{"Ticket Recepción", "Ticket Técnico", "Ticket Entrega", "Cancelar"};
        } else {
            opciones = new Object[]{"Ticket Recepción", "Ticket Técnico", "Cancelar"};
        }

        int seleccion = JOptionPane.showOptionDialog(
            this, "¿Qué ticket desea reimprimir para " + cliente + " (Orden #" + idOrdenSeleccionada + ")?",
            "Reimpresión - SairTech", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opciones, opciones[0]
        );

        if (seleccion < 0 || seleccion == opciones.length - 1) return;

        if (seleccion == 1) {
            String[] detallesBD = daoOrden.obtenerTextosOrden(idOrdenSeleccionada);
            String problemaReal = (detallesBD[0] != null && !detallesBD[0].isEmpty()) ? detallesBD[0] : txtFalla.getText();
            String claveBD = (detallesBD.length > 2 && detallesBD[2] != null) ? detallesBD[2] : "Sin Clave";
            
            String equipo = lblModelo.getText().replace("Equipo: ", "").trim();
            boolean esCelular = true;

            // --- MAGIA: Búsqueda exacta del técnico original ---
            String tecnico = "SairTech";
            String sqlTecnico = "SELECT u.usuario FROM ordenes_reparacion o JOIN usuarios u ON o.id_usuario = u.id_usuario WHERE o.id_orden = ?";
            try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
                 java.sql.PreparedStatement psT = con.prepareStatement(sqlTecnico)) {
                psT.setInt(1, idOrdenSeleccionada);
                try (java.sql.ResultSet rsT = psT.executeQuery()) {
                    if(rsT.next()) tecnico = rsT.getString("usuario");
                }
            } catch (Exception ex) {}
            // --------------------------------------------------
            
            utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta();
            boolean impreso = impresora.imprimirTicketTecnicoDirecto(String.valueOf(idOrdenSeleccionada), cliente, equipo, problemaReal, esCelular, tecnico, claveBD);
            
            if (impreso) JOptionPane.showMessageDialog(this, "Sticker enviado a la impresora.", "Impresión", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }

        utilidades.GeneradorPDF generador = new utilidades.GeneradorPDF();
        boolean exito = generador.reimprimirTicketExistente(String.valueOf(idOrdenSeleccionada), cliente, seleccion);
        
        if (!exito) {
            int respuesta = JOptionPane.showConfirmDialog(this, "El archivo PDF ya no existe. ¿Desea regenerarlo con los datos actuales?", "Regenerar Ticket", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    String equipo = lblModelo.getText().replace("Equipo: ", "").trim();
                    String problema = txtFalla.getText();
                    String costoTotal = txtCostoFinal.getText();
                    
                    String[] detallesBD = daoOrden.obtenerTextosOrden(idOrdenSeleccionada);
                    String trabajoRealizado = (detallesBD[1] != null && !detallesBD[1].isEmpty()) ? detallesBD[1] : "Revisión técnica general.";
                    String claveBD = (detallesBD.length > 2 && detallesBD[2] != null && !detallesBD[2].isEmpty()) ? detallesBD[2] : "N/A";
                    String equipoConClave = equipo + "  |  Clave: " + claveBD;
                    
                    boolean esRecepcion = (seleccion == 0);
                    gui.VentanaPrincipal v = (gui.VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
                    String tecnico = (v != null) ? v.getNombreUsuarioActivo() : "SairTech";
                    String fechaOriginal = daoOrden.obtenerFechaOrden(idOrdenSeleccionada);
                    
                    boolean regenerado = generador.crearTicket(
                        String.valueOf(idOrdenSeleccionada), fechaOriginal, cliente, equipoConClave, problema, costoTotal, 
                        "SAIRTECH - TECNOLOGIA", "Santa Barbara, Barrio La Soledad", "8951-8040", 
                        "OJO no aplica garantia en equipos mojados.", 
                        tecnico, trabajoRealizado, esRecepcion, "Equipo", false 
                    );
                    
                    if (regenerado) generador.reimprimirTicketExistente(String.valueOf(idOrdenSeleccionada), cliente, seleccion);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al regenerar el ticket: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarO_Restaurar() {
        if (idOrdenSeleccionada == -1) return;
        boolean esModoPapelera = chkVerEliminados.isSelected();

        if (esModoPapelera) {
            if (daoOrden.restaurar(idOrdenSeleccionada)) {
                JOptionPane.showMessageDialog(this, "Orden restaurada correctamente.");
                cargarTabla();
            }
        } else {
            int resp = JOptionPane.showConfirmDialog(this, "¿Ocultar esta orden de los listados activos?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                if (daoOrden.eliminar(idOrdenSeleccionada)) {
                    JOptionPane.showMessageDialog(this, "Orden enviada a la papelera.");
                    cargarTabla();
                }
            }
        }
    }

    private String solicitarFirmaUsuario() {
        javax.swing.JPasswordField txtPass = new javax.swing.JPasswordField();
        Object[] mensaje = {"Ingrese su PIN / Contraseña para firmar:", txtPass};
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Firma Rápida - SairTech", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                             
        if (opcion == JOptionPane.OK_OPTION) {
            String clave = new String(txtPass.getPassword());
            dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
            String tecnico = daoUsuario.obtenerUsuarioPorClave(clave);
            if (tecnico != null) {
                return tecnico; 
            } else {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta o no registrada.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
