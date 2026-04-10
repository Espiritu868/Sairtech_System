
package gui;

import dao.OrdenReparacionDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelListadoOrdenes extends javax.swing.JPanel {

    
    private OrdenReparacionDAO daoOrden = new OrdenReparacionDAO();
    private String firmaCambioEstado = ""; // <--- NUEVA VARIABLE PARA GUARDAR QUIEN FIRMA

    public PanelListadoOrdenes() {
        initComponents();
        aplicarDisenoModerno();
        refrescarTabla();
        cmbFiltroEstado.setEditable(false);
        
        // Bloqueamos el ComboBox para que el usuario no pueda cambiarlo manualmente
        cmbNuevoEstado.setEnabled(false);
    }
    
    private void cargarTabla(List<Object[]> datos) {
        DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"N°", "Cliente", "Equipo", "Problema", "Estado", "Costo", "Tipo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Object[] fila : datos) {
            modelo.addRow(fila);
        }

        tablaGeneral.setModel(modelo);
        configurarColumnas();
        
        tablaGeneral.getColumnModel().removeColumn(tablaGeneral.getColumnModel().getColumn(6));
    }

    public void refrescarTabla() {
        cargarTabla(daoOrden.listarReporteCompleto());
    }

    private void configurarColumnas() {
        tablaGeneral.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] anchos = {50, 150, 120, 250, 100, 80};
        for (int i = 0; i < anchos.length; i++) {
            if (i < tablaGeneral.getColumnCount()) {
                tablaGeneral.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
            }
        }
    }

   private void aplicarDisenoModerno() {
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        this.setBackground(new Color(240, 244, 248));
        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));
        panelNorte.setOpaque(false);
        
        JPanel panelTitulos = new JPanel();
        panelTitulos.setLayout(new GridLayout(2, 1, 0, 0));
        panelTitulos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Listado de Órdenes");
        JLabel lblTitulo2 = new JLabel("SairTech - Tecnología");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(44, 62, 80));
        lblTitulo2.setForeground(new Color(127, 140, 141));
        
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblTitulo2);

        JPanel panelHerramientas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelHerramientas.setOpaque(false);
        txtBuscarOrden.setPreferredSize(new Dimension(200, 30));
        cmbFiltroEstado.setPreferredSize(new Dimension(150, 30));
        panelHerramientas.add(new JLabel("🔍"));
        panelHerramientas.add(txtBuscarOrden);
        panelHerramientas.add(new JLabel("Estado:"));
        panelHerramientas.add(cmbFiltroEstado);

        panelNorte.add(panelTitulos, BorderLayout.WEST);
        panelNorte.add(panelHerramientas, BorderLayout.EAST);

        JPanel panelGestion = new JPanel(new GridBagLayout());
        panelGestion.setBackground(Color.WHITE);
        panelGestion.setPreferredSize(new Dimension(320, 0));
        panelGestion.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblTituloInfo = new JLabel("ORDEN SELECCIONADA");
        lblTituloInfo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTituloInfo.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 5, 0);
        panelGestion.add(lblTituloInfo, gbc);

        JLabel lblNombreCliente = new JLabel("Ninguna seleccionada");
        lblNombreCliente.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNombreCliente.setForeground(new Color(41, 128, 185)); 
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 2, 0);
        panelGestion.add(lblNombreCliente, gbc);

        JLabel lblTipoEquipo = new JLabel(" ");
        lblTipoEquipo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTipoEquipo.setForeground(new Color(127, 140, 141));
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 2, 0);
        panelGestion.add(lblTipoEquipo, gbc);

        JLabel lblEquipoDetalle = new JLabel(" ");
        lblEquipoDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEquipoDetalle.setForeground(Color.DARK_GRAY);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 15, 0); 
        panelGestion.add(lblEquipoDetalle, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 15, 0);
        panelGestion.add(new JSeparator(), gbc);

        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 4; panelGestion.add(new JLabel("ID de Orden:"), gbc);
        gbc.gridy = 5; panelGestion.add(txtIdOrden, gbc);
        
        gbc.gridy = 6; gbc.insets = new Insets(10, 0, 5, 0);
        panelGestion.add(new JLabel("Estado:"), gbc);
        
        // Creamos un mini-panel para poner el ComboBox y el candado juntos
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelEstado.setBackground(Color.WHITE);
        panelEstado.add(cmbNuevoEstado);
        
        JButton btnDesbloquear = new JButton("🔓");
        btnDesbloquear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDesbloquear.setToolTipText("Forzar cambio de estado (Solo Admin)");
        btnDesbloquear.addActionListener(evt -> btnDesbloquearActionPerformed(evt));
        panelEstado.add(btnDesbloquear);

        gbc.gridy = 7; panelGestion.add(panelEstado, gbc);

        gbc.gridy = 8; gbc.insets = new Insets(10, 0, 5, 0);
        panelGestion.add(new JLabel("Costo Final (L.):"), gbc);
        gbc.gridy = 9; panelGestion.add(txtCostoFinal, gbc);

        // BOTONES
        estilizarBoton(btnActualizarOrden, new Color(52, 152, 219));
        estilizarBoton(btnEditarDetalles, new Color(155, 89, 182));
        estilizarBoton(btnEntregar, new Color(46, 204, 113));
        estilizarBoton(btnEliminar, new Color(231, 76, 60));

        // 1. Instanciamos, estilizamos y escuchamos el nuevo botón de Reimprimir
        JButton btnReimprimir = new JButton("Reimprimir [T]");
        estilizarBoton(btnReimprimir, new Color(243, 156, 18)); // Color Naranja/Amarillo
        btnReimprimir.addActionListener(evt -> btnReimprimirActionPerformed(evt));

        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridy = 10; panelGestion.add(btnActualizarOrden, gbc);
        
        gbc.insets = new Insets(8, 0, 0, 0);
        gbc.gridy = 11; panelGestion.add(btnEditarDetalles, gbc);
        
        gbc.gridy = 12; panelGestion.add(btnEntregar, gbc);
        
        // 2. Agregamos el botón de Reimprimir debajo de Entregar
        gbc.gridy = 13; panelGestion.add(btnReimprimir, gbc);
        
        gbc.insets = new Insets(30, 0, 0, 0);
        // 3. Movemos el botón Eliminar a la posición 14
        gbc.gridy = 14; panelGestion.add(btnEliminar, gbc);

        // ENSAMBLADO
        this.add(panelNorte, BorderLayout.NORTH);
        this.add(scrollTablaGeneral, BorderLayout.CENTER);
        this.add(panelGestion, BorderLayout.EAST);
        
        this.revalidate();
        this.repaint();
    }

    private void estilizarBoton(JButton btn, Color fondo) {
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelContenedor = new javax.swing.JPanel();
        btnEliminar = new javax.swing.JButton();
        btnActualizarOrden = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();
        scrollTablaGeneral = new javax.swing.JScrollPane();
        tablaGeneral = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtIdOrden = new javax.swing.JTextField();
        cmbNuevoEstado = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        txtCostoFinal = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtBuscarOrden = new javax.swing.JTextField();
        btnEditarDetalles = new javax.swing.JButton();
        btnEntregar = new javax.swing.JButton();
        cmbFiltroEstado = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelContenedor.setBackground(new java.awt.Color(255, 255, 255));
        panelContenedor.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnEliminar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
        panelContenedor.add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 440, -1, 44));

        btnActualizarOrden.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnActualizarOrden.setText("Actualizar Orden");
        btnActualizarOrden.addActionListener(this::btnActualizarOrdenActionPerformed);
        panelContenedor.add(btnActualizarOrden, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 20, -1, 90));

        btnGuardar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        panelContenedor.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 440, -1, 44));

        tablaGeneral.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        tablaGeneral.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaGeneralMouseClicked(evt);
            }
        });
        scrollTablaGeneral.setViewportView(tablaGeneral);

        panelContenedor.add(scrollTablaGeneral, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 790, 310));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Buscar Orden");
        panelContenedor.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        txtIdOrden.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtIdOrden.setEnabled(false);
        panelContenedor.add(txtIdOrden, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 90, 30));

        cmbNuevoEstado.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cmbNuevoEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Recibido", "En Revision", "Reparado", "Entregado", "Sin Reparacion" }));
        panelContenedor.add(cmbNuevoEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, 150, 30));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Orden N°");
        panelContenedor.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        txtCostoFinal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        panelContenedor.add(txtCostoFinal, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 80, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Costo Final");
        panelContenedor.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, -1, -1));

        txtBuscarOrden.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtBuscarOrden.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarOrdenKeyReleased(evt);
            }
        });
        panelContenedor.add(txtBuscarOrden, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 70, 290, 30));

        btnEditarDetalles.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnEditarDetalles.setText("Editar Detalles");
        btnEditarDetalles.addActionListener(this::btnEditarDetallesActionPerformed);
        panelContenedor.add(btnEditarDetalles, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 440, 150, 40));

        btnEntregar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnEntregar.setText("Entregar");
        btnEntregar.addActionListener(this::btnEntregarActionPerformed);
        panelContenedor.add(btnEntregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 440, -1, 44));

        cmbFiltroEstado.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cmbFiltroEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Recibido", "En Reparacion", "Listo para Entrega", "Entregado" }));
        cmbFiltroEstado.addItemListener(this::cmbFiltroEstadoItemStateChanged);
        panelContenedor.add(cmbFiltroEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 70, 130, 30));

        jPanel1.add(panelContenedor, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 490));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1027, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 627, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbFiltroEstadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbFiltroEstadoItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String filtro = cmbFiltroEstado.getSelectedItem().toString();
            cargarTabla(filtro.equals("Todos") ? daoOrden.listarReporteCompleto() : daoOrden.filtrarPorEstado(filtro));
        }
    }//GEN-LAST:event_cmbFiltroEstadoItemStateChanged

    private void btnEntregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntregarActionPerformed
                                           
        int fila = tablaGeneral.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idOrden = tablaGeneral.getValueAt(fila, 0).toString().trim();
        String estadoActual = tablaGeneral.getValueAt(fila, 4).toString().trim();
        String costoTotal = tablaGeneral.getValueAt(fila, 5).toString().trim();

        if (estadoActual.equals("Entregado")) {
            JOptionPane.showMessageDialog(this, "Esta orden ya fue entregada anteriormente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "¿Confirmar entrega y cobro de L. " + costoTotal + "?", "SairTech", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            // --- NUEVO: Pide firma para poder entregar ---
            String tecnicoFirma = solicitarFirmaUsuario();
            if (tecnicoFirma == null) return; // Si cancela o falla, no se entrega nada
            
            try {
                // Buscamos el ID del usuario que está firmando para registrarlo en la BD
                dao.UsuarioDAO daoUser = new dao.UsuarioDAO();
                int idTecnico = daoUser.obtenerIdPorNombre(tecnicoFirma);

                if (daoOrden.marcarComoEntregado(Integer.parseInt(idOrden), idTecnico)) {
                    
                    // Al registrar el entregado, también dejamos la huella en el historial de trabajo
                    String fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
                    String nota = "\n\n[" + fecha + " - EQUIPO ENTREGADO Y COBRADO POR: " + tecnicoFirma.toUpperCase() + "]";
                    String[] textos = daoOrden.obtenerTextosOrden(Integer.parseInt(idOrden));
                    daoOrden.actualizarTextosOrden(Integer.parseInt(idOrden), textos[0], textos[1] + nota);

                   // Generar PDF
                    utilidades.GeneradorPDF generador = new utilidades.GeneradorPDF();
                    String[] detalles = daoOrden.obtenerTextosOrden(Integer.parseInt(idOrden));
                    String trabajoRealizado = (detalles[1] != null && !detalles[1].isEmpty()) ? detalles[1] + nota : "Revisión técnica general.";
                    
                    // --- NUEVO: Extraemos la clave y la pegamos al equipo ---
                    String claveBD = (detalles[2] != null) ? detalles[2] : "Sin Clave";
                    String equipoConClave = tablaGeneral.getValueAt(fila, 2).toString().trim() + "  |  Clave: " + claveBD;
                    // --------------------------------------------------------

                    int modelRow = tablaGeneral.convertRowIndexToModel(fila);
                    String tipoEquipo = tablaGeneral.getModel().getValueAt(modelRow, 6).toString();
                    String fechaOriginal = daoOrden.obtenerFechaOrden(Integer.parseInt(idOrden));
                    // Cuando llames a crearTicket, pásale 'fechaOriginal' en lugar de las comillas vacías
                    boolean ticketCreado = generador.crearTicket(
                        idOrden, fechaOriginal, 
                        tablaGeneral.getValueAt(fila, 1).toString().trim(), 
                        equipoConClave, // <--- ¡AQUÍ ESTÁ!
                        tablaGeneral.getValueAt(fila, 3).toString().trim(), 
                        costoTotal, "SAIRTECH - TECNOLOGIA", 
                        "Santa Barbara, Barrio La Soledad, Frente a Sastreria La Elegancia", 
                        "8951-8040", "OJO no aplica garantia en equipos mojados, pantallas no cuentan con garantía.", 
                        tecnicoFirma, trabajoRealizado, false, tipoEquipo, true);

                    JOptionPane.showMessageDialog(this, "¡Entrega exitosa! Ticket generado.");
                    refrescarTabla();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error crítico al entregar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEntregarActionPerformed
    
    private void btnReimprimirActionPerformed(java.awt.event.ActionEvent evt) {
        int fila = tablaGeneral.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extraemos los datos necesarios de la tabla
        String idOrden = tablaGeneral.getValueAt(fila, 0).toString().trim();
        String cliente = tablaGeneral.getValueAt(fila, 1).toString().trim();
        String estadoActual = tablaGeneral.getValueAt(fila, 4).toString().trim();

        // Armamos el menú dependiendo del estado
        Object[] opciones;
        if (estadoActual.equals("Entregado")) {
            opciones = new Object[]{"Ticket Recepción", "Ticket Técnico", "Ticket Entrega", "Cancelar"};
        } else {
            opciones = new Object[]{"Ticket Recepción", "Ticket Técnico", "Cancelar"};
        }

        int seleccion = JOptionPane.showOptionDialog(
            this,
            "¿Qué ticket desea reimprimir para " + cliente + " (Orden #" + idOrden + ")?",
            "Reimpresión - SairTech",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, 
            opciones, 
            opciones[0]
        );

        // Si el usuario presiona "Cancelar" o cierra la ventana, la selección es igual al último elemento o -1
        if (seleccion >= 0 && seleccion < opciones.length - 1) {
            utilidades.GeneradorPDF generador = new utilidades.GeneradorPDF();
            
            // 1. Intentamos la forma rápida (buscar el PDF que ya existe)
            boolean exito = generador.reimprimirTicketExistente(idOrden, cliente, seleccion);
            
            // 2. EL PLAN B: Si no existe, lo regeneramos de cero
            if (!exito) {
                int respuesta = JOptionPane.showConfirmDialog(this,
                    "El archivo físico ya no existe (quizás es antiguo o fue eliminado).\n¿Desea regenerarlo con los datos actuales del sistema?",
                    "Regenerar Ticket", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    
                if (respuesta == JOptionPane.YES_OPTION) {
                    try {
                        String equipo = tablaGeneral.getValueAt(fila, 2).toString().trim();
                        String problema = tablaGeneral.getValueAt(fila, 3).toString().trim();
                        String costoTotal = tablaGeneral.getValueAt(fila, 5).toString().trim();
                        int modelRow = tablaGeneral.convertRowIndexToModel(fila);
                        String tipoEquipo = tablaGeneral.getModel().getValueAt(modelRow, 6).toString();
                        
                       String[] detallesBD = daoOrden.obtenerTextosOrden(Integer.parseInt(idOrden));
                        String trabajoRealizado = (detallesBD[1] != null && !detallesBD[1].isEmpty()) ? detallesBD[1] : "Revisión técnica general.";
                        String problemaReal = (detallesBD[0] != null && !detallesBD[0].isEmpty()) ? detallesBD[0] : problema;
                        
                        // --- NUEVO: Extraemos la clave y la pegamos al equipo ---
                        String claveBD = (detallesBD[2] != null) ? detallesBD[2] : "Sin Clave";
                        String equipoConClave = equipo + "  |  Clave: " + claveBD;
                        // --------------------------------------------------------
                        
                        boolean esRecepcion = (seleccion == 0 || seleccion == 1);
                        
                        gui.VentanaPrincipal v = (gui.VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
                        String tecnico = (v != null) ? v.getNombreUsuarioActivo() : "SairTech";

                        String fechaOriginal = daoOrden.obtenerFechaOrden(Integer.parseInt(idOrden));
                        // Llamamos al creador de tickets en MODO SILENCIOSO
                       boolean regenerado = generador.crearTicket(
                            idOrden, 
                            fechaOriginal, 
                            cliente, equipoConClave, problemaReal, costoTotal, // <--- Mandamos equipoConClave
                            "SAIRTECH - TECNOLOGIA", 
                            "Santa Barbara, Barrio La Soledad, Frente a Sastreria La Elegancia", 
                            "8951-8040", 
                            "OJO no aplica garantia en equipos mojados, pantallas no cuentan con garantía.", 
                            tecnico, trabajoRealizado, esRecepcion, tipoEquipo, 
                            false // <--- FALSE: Solo guarda el archivo en la carpeta, NO ABRE NADA AÚN
                        );
                        
                        if (regenerado) {
                            // 3. AHORA SÍ: Abrimos SOLAMENTE el ticket que el usuario seleccionó
                            generador.reimprimirTicketExistente(idOrden, cliente, seleccion);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error al intentar regenerar el ticket: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    private void btnDesbloquearActionPerformed(java.awt.event.ActionEvent evt) {                                               
        if (txtIdOrden.getText().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla primero.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String tecnico = solicitarFirmaUsuario();
        
        if (tecnico != null) {
            cmbNuevoEstado.setEnabled(true); 
            firmaCambioEstado = tecnico; // Guardamos el nombre en la memoria temporal
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Estado desbloqueado por: " + tecnico.toUpperCase() + ".\nElija el nuevo estado y presione 'Actualizar Orden'.", 
                "Firma Aceptada", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void btnEditarDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarDetallesActionPerformed
        if (txtIdOrden.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idOrden = Integer.parseInt(txtIdOrden.getText());
        String[] textosActuales = daoOrden.obtenerTextosOrden(idOrden);

        JTextArea txtProblema = new JTextArea(5, 30);
        txtProblema.setText(textosActuales[0] != null ? textosActuales[0] : "");
        txtProblema.setLineWrap(true); txtProblema.setWrapStyleWord(true);

        JTextArea txtTrabajo = new JTextArea(5, 30);
        txtTrabajo.setText(textosActuales[1] != null ? textosActuales[1] : "");
        txtTrabajo.setLineWrap(true); txtTrabajo.setWrapStyleWord(true);

        JPanel panelEdicion = new JPanel(new GridLayout(4, 1, 5, 5));
        panelEdicion.add(new JLabel("Problema Reportado:"));
        panelEdicion.add(new JScrollPane(txtProblema));
        panelEdicion.add(new JLabel("Trabajo Realizado:"));
        panelEdicion.add(new JScrollPane(txtTrabajo));

        if (JOptionPane.showConfirmDialog(this, panelEdicion, "Editando Orden N° " + idOrden, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            if (daoOrden.actualizarTextosOrden(idOrden, txtProblema.getText().trim(), txtTrabajo.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Detalles actualizados correctamente.");
                refrescarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar los detalles.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEditarDetallesActionPerformed

    private void txtBuscarOrdenKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarOrdenKeyReleased
        String texto = txtBuscarOrden.getText().trim();
        cargarTabla(texto.isEmpty() ? daoOrden.listarReporteCompleto() : daoOrden.buscarOrden(texto));
    }//GEN-LAST:event_txtBuscarOrdenKeyReleased

    private void tablaGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaGeneralMouseClicked
        int fila = tablaGeneral.getSelectedRow();
        if (fila >= 0) {
            int modelRow = tablaGeneral.convertRowIndexToModel(fila);
            DefaultTableModel model = (DefaultTableModel) tablaGeneral.getModel();

            txtIdOrden.setText(model.getValueAt(modelRow, 0).toString());
            String cliente = model.getValueAt(modelRow, 1).toString();
            String equipo = model.getValueAt(modelRow, 2).toString();
            String tipo = model.getValueAt(modelRow, 6).toString();

            Object valorEstado = model.getValueAt(modelRow, 4);
            if (valorEstado != null) cmbNuevoEstado.setSelectedItem(valorEstado.toString().trim());
            Object valorCosto = model.getValueAt(modelRow, 5);
            txtCostoFinal.setText(valorCosto != null ? valorCosto.toString() : "0.0");
            cmbNuevoEstado.setEnabled(false);
            firmaCambioEstado = "";

            try {
                BorderLayout bl = (BorderLayout) this.getLayout();
                JPanel pGestion = (JPanel) bl.getLayoutComponent(BorderLayout.EAST);

                JLabel lblNom = (JLabel) pGestion.getComponent(1);
                JLabel lblTipo = (JLabel) pGestion.getComponent(2);
                JLabel lblEq = (JLabel) pGestion.getComponent(3);

                lblNom.setText(cliente.toUpperCase());
                lblTipo.setText(tipo.toUpperCase());
                lblEq.setText(equipo);

                pGestion.repaint();
            } catch (Exception e) { }
        }
    }//GEN-LAST:event_tablaGeneralMouseClicked

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnActualizarOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarOrdenActionPerformed
        if (txtIdOrden.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla.");
            return;
        }
        try {
            int id = Integer.parseInt(txtIdOrden.getText());
            String estado = cmbNuevoEstado.getSelectedItem().toString();
            double costo = Double.parseDouble(txtCostoFinal.getText().trim());

            if (daoOrden.actualizarEstadoYCosto(id, estado, costo)) {
                
                // MODO AUDITORÍA: Si alguien firmó para cambiar esto, dejamos la huella
                if (!firmaCambioEstado.isEmpty()) {
                    String[] textos = daoOrden.obtenerTextosOrden(id);
                    String problema = textos[0] != null ? textos[0] : "";
                    String trabajo = textos[1] != null ? textos[1] : "";
                    
                    // Creamos la etiqueta de texto con fecha y autor
                    String fechaCambio = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());
                    String notaHistorial = "\n\n[" + fechaCambio + " - Estado cambiado a '" + estado.toUpperCase() + "' por: " + firmaCambioEstado.toUpperCase() + "]";
                    
                    // Inyectamos la nota en la base de datos
                    daoOrden.actualizarTextosOrden(id, problema, trabajo + notaHistorial);
                }
                
                JOptionPane.showMessageDialog(this, "¡Orden actualizada correctamente!");
                
                // Reseteamos todo por seguridad
                cmbNuevoEstado.setEnabled(false);
                firmaCambioEstado = ""; 
                refrescarTabla();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un costo válido (ej: 150.00)");
        }
    }//GEN-LAST:event_btnActualizarOrdenActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (txtIdOrden.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden de la tabla para eliminarla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar la Orden N° " + txtIdOrden.getText() + "?\nEsta acción no se puede deshacer.", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtIdOrden.getText());
            if (daoOrden.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Orden eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtIdOrden.setText("");
                cmbNuevoEstado.setSelectedIndex(0);
                txtCostoFinal.setText("");
                refrescarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la orden.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizarOrden;
    private javax.swing.JButton btnEditarDetalles;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEntregar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbFiltroEstado;
    private javax.swing.JComboBox<String> cmbNuevoEstado;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panelContenedor;
    private javax.swing.JScrollPane scrollTablaGeneral;
    private javax.swing.JTable tablaGeneral;
    private javax.swing.JTextField txtBuscarOrden;
    private javax.swing.JTextField txtCostoFinal;
    private javax.swing.JTextField txtIdOrden;
    // End of variables declaration//GEN-END:variables
    
    private String solicitarFirmaUsuario() {
        javax.swing.JPasswordField txtPass = new javax.swing.JPasswordField();
        Object[] mensaje = {"Ingrese su PIN / Contraseña para firmar:", txtPass};

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Firma Rápida - SairTech", 
                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                     
        if (opcion == JOptionPane.OK_OPTION) {
            String clave = new String(txtPass.getPassword());
            dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
            
            // Buscamos quién es el dueño de esa contraseña
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
}
