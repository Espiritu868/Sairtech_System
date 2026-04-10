package gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
public class PanelIngresoEquipos extends javax.swing.JPanel {

    // Variables de control
    private CardLayout cardLayout;
    private JPanel panelTarjetas;
    private int idClienteSeleccionado = -1;
    private java.util.List<Integer> listaIdTipos = new java.util.ArrayList<>();
    private java.util.List<Integer> listaIdMarcas = new java.util.ArrayList<>();

    // Componentes Paso 1 (Cliente y Equipo)
    private JTextField txtBuscarCliente;
    private JTable tablaClientes;
    private JComboBox<String> cmbTipo;
    private JComboBox<String> cmbMarca;
    private JTextField txtModelo;
    private JTextField txtImei;
    private JLabel lblIdentificador;
    private JButton btnSiguiente;

    // Componentes Paso 2 (Orden)
    private JTextArea txtProblema;
    private JTextArea txtTrabajo;
    private JComboBox<String> cmbEstado;
    private JRadioButton rbtnPin;
    private JRadioButton rbtnPatron;
    private JPanel panelSeguridadContainer;
    private JTextField txtSeguridad;
    private JLabel lblPatronVisual;
    private JButton btnAtras;
    private JButton btnGuardarTodo;
    
    public PanelIngresoEquipos() {
        setLayout(new java.awt.BorderLayout());
        setBackground(new Color(240, 244, 248)); // Gris claro web

        // Título Superior (Se mantiene fijo)
        JLabel lblTitulo = new JLabel(" Ingreso Integral de Equipos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        add(lblTitulo, java.awt.BorderLayout.NORTH);

        // Configuración del CardLayout (El "Wizard")
        cardLayout = new CardLayout();
        panelTarjetas = new JPanel(cardLayout);
        panelTarjetas.setOpaque(false);
        panelTarjetas.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Construir las dos pantallas
        panelTarjetas.add(construirPaso1(), "PASO1");
        panelTarjetas.add(construirPaso2(), "PASO2");

        add(panelTarjetas, java.awt.BorderLayout.CENTER);

        // Inicializar datos
        cargarTipos();
        cargarTablaClientes("");
    }

   // =========================================================================
    // CONSTRUCCIÓN DE LA INTERFAZ (PASO 1)
    // =========================================================================
    private JPanel construirPaso1() {
        JPanel panelPaso1 = new JPanel(new java.awt.BorderLayout(20, 0));
        panelPaso1.setOpaque(false);

        // IZQUIERDA: Buscador de Clientes
        JPanel panelIzquierdo = new JPanel(new java.awt.BorderLayout(0, 10));
        panelIzquierdo.setOpaque(false);

        JLabel lblBuscar = new JLabel("1. Buscar y Seleccionar Cliente:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBuscar.setForeground(new Color(44, 62, 80));

        txtBuscarCliente = new JTextField();
        txtBuscarCliente.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtBuscarCliente.setPreferredSize(new Dimension(0, 40));
        txtBuscarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarTablaClientes(txtBuscarCliente.getText().trim());
            }
        });

        JPanel panelTopIzq = new JPanel(new java.awt.BorderLayout());
        panelTopIzq.setOpaque(false);
        panelTopIzq.add(lblBuscar, java.awt.BorderLayout.NORTH);
        panelTopIzq.add(txtBuscarCliente, java.awt.BorderLayout.CENTER);

        tablaClientes = new JTable();
        tablaClientes.setRowHeight(35);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaClientes.getSelectedRow();
                if (fila >= 0) {
                    idClienteSeleccionado = Integer.parseInt(tablaClientes.getValueAt(fila, 0).toString());
                    txtBuscarCliente.setText(tablaClientes.getValueAt(fila, 2).toString());
                }
            }
        });
        
        JScrollPane scrollBusqueda = new JScrollPane(tablaClientes);
        scrollBusqueda.getViewport().setBackground(Color.WHITE);

        panelIzquierdo.add(panelTopIzq, java.awt.BorderLayout.NORTH);
        panelIzquierdo.add(scrollBusqueda, java.awt.BorderLayout.CENTER);

        // DERECHA: Formulario del Equipo
        JPanel panelDerecho = new JPanel(new java.awt.GridBagLayout());
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setPreferredSize(new Dimension(380, 0));
        panelDerecho.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(10, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblSub = new JLabel("2. Datos del Equipo");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSub.setForeground(Color.GRAY);
        gbc.gridy = 0; gbc.insets = new java.awt.Insets(0, 0, 15, 0);
        panelDerecho.add(lblSub, gbc);

        cmbTipo = new JComboBox<>();
        cmbMarca = new JComboBox<>();
        txtModelo = new JTextField();
        txtImei = new JTextField();
        lblIdentificador = new JLabel("IMEI / Serie:");
        
        cmbTipo.setPreferredSize(new Dimension(0, 35));
        cmbMarca.setPreferredSize(new Dimension(0, 35));
        txtModelo.setPreferredSize(new Dimension(0, 35));
        txtImei.setPreferredSize(new Dimension(0, 35));
        lblIdentificador.setForeground(new Color(44, 62, 80));
        
        cmbTipo.addActionListener(e -> actualizarMarcas());

        gbc.insets = new java.awt.Insets(5, 0, 2, 0);
        gbc.gridy++; panelDerecho.add(new JLabel("Tipo de Equipo:"), gbc);
        gbc.gridy++; panelDerecho.add(cmbTipo, gbc);
        gbc.gridy++; panelDerecho.add(new JLabel("Marca:"), gbc);
        gbc.gridy++; panelDerecho.add(cmbMarca, gbc);
        gbc.gridy++; panelDerecho.add(new JLabel("Modelo:"), gbc);
        gbc.gridy++; panelDerecho.add(txtModelo, gbc);
        gbc.gridy++; panelDerecho.add(lblIdentificador, gbc);
        gbc.gridy++; panelDerecho.add(txtImei, gbc);

        // Botón Siguiente
        btnSiguiente = new JButton("Siguiente ➔");
        btnSiguiente.setBackground(new Color(52, 152, 219)); // Azul
        btnSiguiente.setForeground(Color.WHITE);
        btnSiguiente.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
        btnSiguiente.setPreferredSize(new Dimension(0, 45));
        btnSiguiente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSiguiente.addActionListener(e -> avanzarPaso2());

        gbc.gridy++; gbc.insets = new java.awt.Insets(30, 0, 0, 0);
        panelDerecho.add(btnSiguiente, gbc);
        gbc.gridy++; gbc.weighty = 1.0;
        panelDerecho.add(Box.createVerticalGlue(), gbc);

        panelPaso1.add(panelIzquierdo, java.awt.BorderLayout.CENTER);
        panelPaso1.add(panelDerecho, java.awt.BorderLayout.EAST);

        return panelPaso1;
    }

    // =========================================================================
    // CONSTRUCCIÓN DE LA INTERFAZ (PASO 2)
    // =========================================================================
    private JPanel construirPaso2() {
        JPanel panelPaso2 = new JPanel(new java.awt.GridBagLayout());
        panelPaso2.setBackground(Color.WHITE);
        panelPaso2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(10, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblSub = new JLabel("3. Detalles de la Orden");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSub.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0; gbc.insets = new java.awt.Insets(0, 0, 20, 0);
        panelPaso2.add(lblSub, gbc);

        // Problema Reportado con Fantasma
        gbc.insets = new java.awt.Insets(5, 0, 2, 0);
        gbc.gridy++; panelPaso2.add(new JLabel("Problema Reportado:"), gbc);
        txtProblema = new JTextArea("Describa aquí la falla del equipo...");
        configurarTextArea(txtProblema, "Describa aquí la falla del equipo...");
        JScrollPane scrollProblema = new JScrollPane(txtProblema);
        gbc.gridy++; gbc.weighty = 0.4; panelPaso2.add(scrollProblema, gbc);

        // Trabajo Realizado con Fantasma
        gbc.weighty = 0.0;
        gbc.gridy++; panelPaso2.add(new JLabel("Trabajo a Realizar / Realizado:"), gbc);
        txtTrabajo = new JTextArea("Escriba la reparación que se realizó...");
        configurarTextArea(txtTrabajo, "Escriba la reparación que se realizó...");
        JScrollPane scrollTrabajo = new JScrollPane(txtTrabajo);
        gbc.gridy++; gbc.weighty = 0.4; panelPaso2.add(scrollTrabajo, gbc);

        // Estado
        gbc.weighty = 0.0;
        gbc.gridy++; panelPaso2.add(new JLabel("Estado Inicial:"), gbc);
        cmbEstado = new JComboBox<>(new String[]{"Recibido", "En Revision", "Reparado", "Entregado", "Sin Reparacion"});
        cmbEstado.setPreferredSize(new Dimension(0, 35));
        gbc.gridy++; panelPaso2.add(cmbEstado, gbc);

        // Seguridad
        gbc.gridy++; panelPaso2.add(new JLabel("Seguridad del Dispositivo:"), gbc);
        
        rbtnPin = new JRadioButton("PIN / Texto", true);
        rbtnPatron = new JRadioButton("Patrón visual");
        rbtnPin.setBackground(Color.WHITE);
        rbtnPatron.setBackground(Color.WHITE);
        ButtonGroup bg = new ButtonGroup(); bg.add(rbtnPin); bg.add(rbtnPatron);
        JPanel panelRadios = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        panelRadios.setBackground(Color.WHITE);
        panelRadios.add(rbtnPin); panelRadios.add(new JLabel("   ")); panelRadios.add(rbtnPatron);
        
        gbc.gridy++; panelPaso2.add(panelRadios, gbc);

        panelSeguridadContainer = new JPanel(new CardLayout());
        panelSeguridadContainer.setBackground(Color.WHITE);
        txtSeguridad = new JTextField();
        txtSeguridad.setPreferredSize(new Dimension(0, 35));
        lblPatronVisual = new JLabel("<html><div style='text-align: center; letter-spacing: 12px; line-height: 0.8; font-size: 14px; color: #bdc3c7;'>O &nbsp; O &nbsp; O<br>O &nbsp; O &nbsp; O<br>O &nbsp; O &nbsp; O</div></html>");
        lblPatronVisual.setHorizontalAlignment(SwingConstants.CENTER);
        lblPatronVisual.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        lblPatronVisual.setPreferredSize(new Dimension(0, 85));
        
        panelSeguridadContainer.add(txtSeguridad, "PIN");
        panelSeguridadContainer.add(lblPatronVisual, "PATRON");

        rbtnPin.addActionListener(e -> ((CardLayout) panelSeguridadContainer.getLayout()).show(panelSeguridadContainer, "PIN"));
        rbtnPatron.addActionListener(e -> ((CardLayout) panelSeguridadContainer.getLayout()).show(panelSeguridadContainer, "PATRON"));

        gbc.gridy++; panelPaso2.add(panelSeguridadContainer, gbc);

        // Botones Finales
        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 15, 0));
        panelBotones.setBackground(Color.WHITE);
        
        btnAtras = new JButton("⬅ Volver");
        btnAtras.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
        btnAtras.setBackground(new Color(149, 165, 166)); // Gris
        btnAtras.setForeground(Color.WHITE);
        btnAtras.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAtras.addActionListener(e -> cardLayout.show(panelTarjetas, "PASO1"));

        btnGuardarTodo = new JButton("Guardar e Imprimir Ticket");
        btnGuardarTodo.setBackground(new Color(46, 204, 113)); // Verde
        btnGuardarTodo.setForeground(Color.WHITE);
        btnGuardarTodo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnGuardarTodo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardarTodo.addActionListener(e -> procesarGuardadoFinal());

        panelBotones.add(btnAtras);
        panelBotones.add(btnGuardarTodo);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.insets = new java.awt.Insets(25, 0, 0, 0);
        panelPaso2.add(panelBotones, gbc);

        return panelPaso2;
    }

    // =========================================================================
    // LÓGICA Y EVENTOS
    // =========================================================================
    
    private void configurarTextArea(JTextArea txt, String fantasma) {
        txt.setForeground(Color.GRAY);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txt.getText().equals(fantasma)) {
                    txt.setText("");
                    txt.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txt.getText().trim().isEmpty()) {
                    txt.setText(fantasma);
                    txt.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void avanzarPaso2() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, busque y seleccione un cliente primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmbTipo.getSelectedIndex() <= 0 || cmbMarca.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un Tipo y una Marca válidos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtModelo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El modelo es obligatorio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Transición visual al Paso 2
        cardLayout.show(panelTarjetas, "PASO2");
    }

    private void procesarGuardadoFinal() {
        String problema = txtProblema.getText().trim();
        String fantasmaPro = "Describa aquí la falla del equipo...";
        if (problema.isEmpty() || problema.equals(fantasmaPro)) {
            JOptionPane.showMessageDialog(this, "Por favor, describa el problema del equipo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // BLOQUEAMOS BOTONES PARA EVITAR DOBLE CLIC
        btnGuardarTodo.setEnabled(false);
        btnAtras.setEnabled(false);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        // Hilo en segundo plano para no congelar la UI
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. GUARDAR EQUIPO (o generar Imei si falta)
                String imeiSerie = txtImei.getText().trim();
                if (imeiSerie.isEmpty() || imeiSerie.equalsIgnoreCase("N/A")) {
                    imeiSerie = "SN-" + new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
                }

                modelo.EquipoRegistrado nuevoEquipo = new modelo.EquipoRegistrado(
                        idClienteSeleccionado,
                        listaIdTipos.get(cmbTipo.getSelectedIndex()),
                        listaIdMarcas.get(cmbMarca.getSelectedIndex()),
                        txtModelo.getText().trim(),
                        imeiSerie
                );

                dao.EquipoRegistradoDAO daoEquipo = new dao.EquipoRegistradoDAO();
                int idEquipoGenerado = daoEquipo.insertarConId(nuevoEquipo);

                if (idEquipoGenerado == -1) {
                    JOptionPane.showMessageDialog(this, "Error al registrar el equipo.", "Error BD", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. GUARDAR ORDEN CON EL ID DEL EQUIPO
                String trabajoReal = txtTrabajo.getText().trim();
                if (trabajoReal.equals("Escriba la reparación que se realizó...")) trabajoReal = "";
                
                String seguridad = rbtnPatron.isSelected() ? "Patrón" : (txtSeguridad.getText().trim().isEmpty() ? "Sin Clave" : txtSeguridad.getText().trim());

                modelo.OrdenReparacion nuevaOrden = new modelo.OrdenReparacion();
                nuevaOrden.setIdEquipo(idEquipoGenerado);
                nuevaOrden.setProblemaReportado(problema);
                nuevaOrden.setTrabajoRealizado(trabajoReal);
                nuevaOrden.setEstado(cmbEstado.getSelectedItem().toString());
                nuevaOrden.setCosto(0.0);
                nuevaOrden.setSeguridadDispositivo(seguridad);

                dao.OrdenReparacionDAO daoOrden = new dao.OrdenReparacionDAO();
                int idOrdenGenerada = daoOrden.insertarConId(nuevaOrden);

                if (idOrdenGenerada != -1) {
                    // 3. GENERAR PDF TICKET
                    generarTicketPDF(idOrdenGenerada, trabajoReal, problema, seguridad, imeiSerie);
                    
                    JOptionPane.showMessageDialog(this, "¡Orden #" + idOrdenGenerada + " creada exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarTodo();
                    cardLayout.show(panelTarjetas, "PASO1"); // Volvemos al inicio
                } else {
                    JOptionPane.showMessageDialog(this, "Error al crear la orden de reparación.", "Error BD", JOptionPane.ERROR_MESSAGE);
                }

            } finally {
                // RESTAURAR UI
                btnGuardarTodo.setEnabled(true);
                btnAtras.setEnabled(true);
                setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void generarTicketPDF(int idOrden, String trabajoReal, String problema, String seguridad, String imei) {
        try {
            VentanaPrincipal v = (VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
            String tecnicoActivo = v.getNombreUsuarioActivo();
            String tipoAImprimir = cmbTipo.getSelectedItem().toString();
            String equipoConClave = txtModelo.getText().trim() + " | S/N: " + imei + " | Clave: " + seguridad;
            String clienteName = txtBuscarCliente.getText();
            dao.OrdenReparacionDAO daoOrden = new dao.OrdenReparacionDAO();
            String fechaRealBD = daoOrden.obtenerFechaOrden(idOrden);

            utilidades.GeneradorPDF gen = new utilidades.GeneradorPDF();
            gen.crearTicket(
                String.valueOf(idOrden), fechaRealBD, clienteName, equipoConClave, problema, "0.00", 
                "SAIRTECH", "Santa Barbara, Barrio La Soledad", "8951-8040", 
                "Garantía de 30 días en mano de obra.", tecnicoActivo, trabajoReal, true, tipoAImprimir, true
            );
        } catch (Exception ex) {
            System.err.println("Aviso: No se pudo generar PDF: " + ex.getMessage());
        }
    }

    private void limpiarTodo() {
        txtBuscarCliente.setText("");
        idClienteSeleccionado = -1;
        cargarTablaClientes("");
        cmbTipo.setSelectedIndex(0);
        txtModelo.setText("");
        txtImei.setText("");
        
        txtProblema.setText("Describa aquí la falla del equipo...");
        txtProblema.setForeground(Color.GRAY);
        txtTrabajo.setText("Escriba la reparación que se realizó...");
        txtTrabajo.setForeground(Color.GRAY);
        cmbEstado.setSelectedIndex(0);
        rbtnPin.setSelected(true);
        txtSeguridad.setText("");
        ((CardLayout) panelSeguridadContainer.getLayout()).show(panelSeguridadContainer, "PIN");
    }

    // =========================================================================
    // MÉTODOS DE BASE DE DATOS LOCALES
    // =========================================================================
    private void cargarTipos() {
        cmbTipo.removeAllItems();
        listaIdTipos.clear();
        cmbTipo.addItem("--- Seleccione Tipo ---");
        listaIdTipos.add(-1);

        for (modelo.TipoEquipo t : new dao.TipoEquipoDAO().listar()) {
            cmbTipo.addItem(t.getNombreTipo());
            listaIdTipos.add(t.getIdTipo());
        }
    }

    private void actualizarMarcas() {
        cmbMarca.removeAllItems();
        listaIdMarcas.clear();

        if (cmbTipo.getSelectedIndex() <= 0) { 
            cmbMarca.addItem("--- Espere ---");
            listaIdMarcas.add(-1);
            lblIdentificador.setText("IMEI / Serie:");
            return;
        }

        cmbMarca.addItem("--- Seleccione Marca ---");
        listaIdMarcas.add(-1);
        int idTipo = listaIdTipos.get(cmbTipo.getSelectedIndex());
        String tipoNom = cmbTipo.getSelectedItem().toString();

        if (tipoNom.equals("Smartphones") || tipoNom.equals("Tablets")) lblIdentificador.setText("IMEI:");
        else if (tipoNom.equals("Computadoras")) lblIdentificador.setText("Service Tag / S/N:");
        else lblIdentificador.setText("N° de Serie:");

        String sql = "SELECT id_marca, nombre_marca FROM marcas WHERE id_tipo = ? ORDER BY nombre_marca ASC";
        try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTipo);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cmbMarca.addItem(rs.getString("nombre_marca"));
                    listaIdMarcas.add(rs.getInt("id_marca")); 
                }
            }
        } catch (Exception e) {}
    }

    private void cargarTablaClientes(String filtro) {
        dao.ClienteDAO daoCliente = new dao.ClienteDAO();
        java.util.List<modelo.Cliente> lista = filtro.isEmpty() ? daoCliente.listar() : daoCliente.buscar(filtro);
        java.util.Collections.reverse(lista);

        DefaultTableModel modeloTabla = new DefaultTableModel(new Object[]{"ID", "Identidad", "Cliente"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (modelo.Cliente c : lista) {
            modeloTabla.addRow(new Object[]{c.getIdCliente(), c.getNumeroIdentidad(), c.getNombre() + " " + c.getApellido()});
        }
        tablaClientes.setModel(modeloTabla);
        if (tablaClientes.getColumnModel().getColumnCount() > 0) {
            tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(40);
            tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(120);
            tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(250);
        }
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
