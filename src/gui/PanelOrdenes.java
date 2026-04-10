package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PanelOrdenes extends javax.swing.JPanel {
    
    
    private int idEquipoSeleccionado = -1;
    private String tipoEquipoSeleccionado = "";
    
    public PanelOrdenes() {
        initComponents();      
        aplicarDisenoOrdenes();
        cargarTablaBuscador("");
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        scrollBusquedaEquipo = new javax.swing.JScrollPane();
        tablaBusquedaEquipo = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtBuscarEquipo = new javax.swing.JTextField();
        panelTrabajo = new javax.swing.JScrollPane();
        txtTrabajo = new javax.swing.JTextArea();
        panelProblema = new javax.swing.JScrollPane();
        txtProblema = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        cmbEstado = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtEquipo = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaBusquedaEquipo.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaBusquedaEquipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaBusquedaEquipoMouseClicked(evt);
            }
        });
        scrollBusquedaEquipo.setViewportView(tablaBusquedaEquipo);

        jPanel1.add(scrollBusquedaEquipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 370, 700, 70));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("CREAR ORDEN");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, -1, -1));

        btnGuardar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        jPanel1.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 440, -1, 44));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Estado:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, -1, -1));

        txtBuscarEquipo.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtBuscarEquipo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarEquipoKeyReleased(evt);
            }
        });
        jPanel1.add(txtBuscarEquipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 529, -1));

        txtTrabajo.setColumns(20);
        txtTrabajo.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtTrabajo.setRows(5);
        txtTrabajo.setText("Escribe aquí la reparación realizada.");
        txtTrabajo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTrabajoFocusLost(evt);
            }
        });
        txtTrabajo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTrabajoMouseClicked(evt);
            }
        });
        panelTrabajo.setViewportView(txtTrabajo);

        jPanel1.add(panelTrabajo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 700, 60));

        txtProblema.setColumns(20);
        txtProblema.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtProblema.setRows(5);
        txtProblema.setText("Escribe aquí el problema de tu equipo.");
        txtProblema.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtProblemaFocusLost(evt);
            }
        });
        txtProblema.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtProblemaMouseClicked(evt);
            }
        });
        panelProblema.setViewportView(txtProblema);

        jPanel1.add(panelProblema, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 700, 60));

        jLabel8.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Buscar Equipo");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        cmbEstado.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cmbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Recibido", "En Revision", "Reparado", "Entregado", "Sin Reparacion" }));
        cmbEstado.addActionListener(this::cmbEstadoActionPerformed);
        jPanel1.add(cmbEstado, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 340, 120, 30));

        jLabel9.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Equipo:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 130, -1, -1));

        txtEquipo.setEditable(false);
        txtEquipo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtEquipo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEquipoKeyReleased(evt);
            }
        });
        jPanel1.add(txtEquipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 130, 200, -1));

        txtNombre.setEditable(false);
        txtNombre.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreKeyReleased(evt);
            }
        });
        jPanel1.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 130, 290, -1));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Cliente:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 810, 490));
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
       // 1. Validar que se haya seleccionado un equipo
        if (idEquipoSeleccionado == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Debe seleccionar un equipo de la lista.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Capturar datos de los campos
        String problema = txtProblema.getText().trim();
        String trabajoReal = txtTrabajo.getText().trim();
        // Evitamos guardar el texto fantasma en la BD
        if (trabajoReal.equals("Escriba la reparación que se realizó...")) {
            trabajoReal = "";
        }
        
        String estado = cmbEstado.getSelectedItem().toString();
        String cliente = txtNombre.getText(); 
        String equipo = txtEquipo.getText();  
        
        // Capturar seguridad basada en los botones
        String seguridad = "Sin Clave";
        if (rbtnPatron.isSelected()) {
            seguridad = "Patrón";
        } else {
            seguridad = txtSeguridad.getText().trim().isEmpty() ? "Sin Clave" : txtSeguridad.getText().trim();
        }
        
        // 3. Validar que el problema no esté vacío ni tenga el texto fantasma
        if (problema.isEmpty() || problema.equals("Describa aquí la falla del equipo...")) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, describa el problema del equipo.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. CREAR EL OBJETO E INYECTAR DATOS
        modelo.OrdenReparacion nuevaOrden = new modelo.OrdenReparacion();
        nuevaOrden.setIdEquipo(idEquipoSeleccionado);
        nuevaOrden.setProblemaReportado(problema);
        nuevaOrden.setTrabajoRealizado(trabajoReal);
        nuevaOrden.setEstado(estado);
        nuevaOrden.setCosto(0.0);
        nuevaOrden.setSeguridadDispositivo(seguridad); 

        // 5. Guardar en Base de Datos
        dao.OrdenReparacionDAO daoOrden = new dao.OrdenReparacionDAO();
        int idGenerado = daoOrden.insertarConId(nuevaOrden);
        
        if (idGenerado != -1) {
            try {
                gui.VentanaPrincipal v = (gui.VentanaPrincipal) javax.swing.SwingUtilities.getWindowAncestor(this);
                String tecnicoActivo = v.getNombreUsuarioActivo();
                utilidades.GeneradorPDF gen = new utilidades.GeneradorPDF();
                String nroOrdenStr = String.valueOf(idGenerado);
                
                // Preparar datos para el Ticket
                String tipoAImprimir = (tipoEquipoSeleccionado != null && !tipoEquipoSeleccionado.isEmpty()) ? tipoEquipoSeleccionado : "Generico";
                String equipoConClave = equipo + "  |  Clave: " + seguridad;

                // Generar el ticket PDF
                boolean ok = gen.crearTicket(
                    nroOrdenStr, 
                    "", 
                    cliente, 
                    equipoConClave, 
                    problema, 
                    "0.00", 
                    "SAIRTECH", 
                    "Santa Barbara, Santa Barbara, Barrio La Soledad, Frente a Sastreria La Elegancia", 
                    "8951-8040", 
                    "Garantía de 30 días en mano de obra.", 
                    tecnicoActivo, 
                    trabajoReal, 
                    true, 
                    tipoAImprimir,
                    true
                );

                if (ok) {
                    javax.swing.JOptionPane.showMessageDialog(this, "¡Orden #" + nroOrdenStr + " Guardada exitosamente!", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                System.err.println("Error al procesar ticket: " + ex.getMessage());
            }

            // 6. Limpieza de campos y retorno de textos fantasma
            txtBuscarEquipo.setText("");
            txtNombre.setText("");
            txtEquipo.setText("");
            
            // Limpieza de Seguridad
            txtSeguridad.setText("");
            rbtnPin.setSelected(true);
            ((java.awt.CardLayout) panelSeguridadContainer.getLayout()).show(panelSeguridadContainer, "PIN");
            
            // Retorno de textos fantasma
            txtProblema.setText("Describa aquí la falla del equipo...");
            txtProblema.setForeground(Color.GRAY);
            txtTrabajo.setText("Escriba la reparación que se realizó...");
            txtTrabajo.setForeground(Color.GRAY);
            
            cmbEstado.setSelectedIndex(0);
            idEquipoSeleccionado = -1;
            tipoEquipoSeleccionado = "";
            cargarTablaBuscador("");
            
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "No se pudo crear la orden en la base de datos.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtBuscarEquipoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarEquipoKeyReleased
       String texto = txtBuscarEquipo.getText().trim();
       cargarTablaBuscador(texto);
    
    }//GEN-LAST:event_txtBuscarEquipoKeyReleased
  
    private void tablaBusquedaEquipoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaBusquedaEquipoMouseClicked
        int fila = tablaBusquedaEquipo.getSelectedRow();
        
        if (fila >= 0) {
            try {
                this.idEquipoSeleccionado = Integer.parseInt(tablaBusquedaEquipo.getValueAt(fila, 0).toString());

                String modelo = tablaBusquedaEquipo.getValueAt(fila, 1).toString();

                String dueno = tablaBusquedaEquipo.getValueAt(fila, 2).toString(); 
                this.tipoEquipoSeleccionado = tablaBusquedaEquipo.getValueAt(fila, 3).toString(); 

                txtEquipo.setText(modelo);
                txtNombre.setText(dueno);

            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al seleccionar equipo: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_tablaBusquedaEquipoMouseClicked

    private void txtProblemaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProblemaMouseClicked

    }//GEN-LAST:event_txtProblemaMouseClicked

    private void txtTrabajoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTrabajoMouseClicked
      
    }//GEN-LAST:event_txtTrabajoMouseClicked

    private void txtTrabajoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTrabajoFocusLost

    }//GEN-LAST:event_txtTrabajoFocusLost

    private void txtProblemaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtProblemaFocusLost

    }//GEN-LAST:event_txtProblemaFocusLost

    private void cmbEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbEstadoActionPerformed

    }//GEN-LAST:event_cmbEstadoActionPerformed

    private void txtEquipoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEquipoKeyReleased

    }//GEN-LAST:event_txtEquipoKeyReleased

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased

    }//GEN-LAST:event_txtNombreKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbEstado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane panelProblema;
    private javax.swing.JScrollPane panelTrabajo;
    private javax.swing.JScrollPane scrollBusquedaEquipo;
    private javax.swing.JTable tablaBusquedaEquipo;
    private javax.swing.JTextField txtBuscarEquipo;
    private javax.swing.JTextField txtEquipo;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextArea txtProblema;
    private javax.swing.JTextArea txtTrabajo;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JTextField txtSeguridad; 
    private javax.swing.JRadioButton rbtnPin;
    private javax.swing.JRadioButton rbtnPatron;
    private javax.swing.JPanel panelSeguridadContainer;
    private javax.swing.JLabel lblPatronVisual;
    
    private void cargarTablaBuscador(String texto) {
        dao.EquipoRegistradoDAO daoEquipo = new dao.EquipoRegistradoDAO();
        
        java.util.List<Object[]> lista = daoEquipo.buscarEquipoCompleto(texto);
        
        // --- NUEVO: Le damos la vuelta a la lista para mostrar primero los últimos ---
        // --- NUEVO: Ordenamiento estricto por ID de Mayor a Menor ---
        lista.sort((Object[] a, Object[] b) -> {
            Integer idA = Integer.parseInt(a[0].toString());
            Integer idB = Integer.parseInt(b[0].toString());
            return idB.compareTo(idA); 
        });
        
        javax.swing.table.DefaultTableModel modeloTabla = new javax.swing.table.DefaultTableModel(
            new Object[]{"ID", "Modelo", "Dueño", "Tipo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        for (Object[] filaAnterior : lista) {
            Object[] filaNueva = new Object[4];
            filaNueva[0] = filaAnterior[0]; // ID
            filaNueva[1] = filaAnterior[1]; // Modelo
            filaNueva[2] = filaAnterior[3]; // Dueño
            filaNueva[3] = filaAnterior[4]; // Tipo
            
            modeloTabla.addRow(filaNueva);
        }

        tablaBusquedaEquipo.setModel(modeloTabla);

        // 3. Ajustamos los anchos a las nuevas 4 columnas
        if (tablaBusquedaEquipo.getColumnModel().getColumnCount() > 0) {
            tablaBusquedaEquipo.getColumnModel().getColumn(0).setPreferredWidth(40);
            tablaBusquedaEquipo.getColumnModel().getColumn(1).setPreferredWidth(200);
            tablaBusquedaEquipo.getColumnModel().getColumn(2).setPreferredWidth(250);
            tablaBusquedaEquipo.getColumnModel().getColumn(3).setPreferredWidth(100);
        }
    }
    
   private void aplicarDisenoOrdenes() {
        // Limpieza
        this.removeAll();
        this.setLayout(new java.awt.BorderLayout(20, 20));
        this.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));
        this.setBackground(new java.awt.Color(240, 244, 248));

        // TÍTULO SUPERIOR
        javax.swing.JLabel lblTitulo = new javax.swing.JLabel("Crear Nueva Orden de Reparación");
        lblTitulo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 26));
        lblTitulo.setForeground(new java.awt.Color(44, 62, 80));
        this.add(lblTitulo, java.awt.BorderLayout.NORTH);

        // SECCIÓN IZQUIERDA: BUSCADOR Y TABLA
        javax.swing.JPanel panelCentro = new javax.swing.JPanel(new java.awt.BorderLayout(0, 15));
        panelCentro.setOpaque(false);

        javax.swing.JPanel panelBusqueda = new javax.swing.JPanel(new java.awt.BorderLayout(0, 10));
        panelBusqueda.setOpaque(false);
        javax.swing.JLabel lblBuscar = new javax.swing.JLabel("1. Buscar y Seleccionar Equipo:");
        lblBuscar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblBuscar.setForeground(new java.awt.Color(44, 62, 80));

        txtBuscarEquipo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
        txtBuscarEquipo.setPreferredSize(new java.awt.Dimension(0, 40));

        panelBusqueda.add(lblBuscar, java.awt.BorderLayout.NORTH);
        panelBusqueda.add(txtBuscarEquipo, java.awt.BorderLayout.CENTER);

        // Estilizar tabla
        tablaBusquedaEquipo.setRowHeight(35);
        tablaBusquedaEquipo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        tablaBusquedaEquipo.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        scrollBusquedaEquipo.getViewport().setBackground(java.awt.Color.WHITE);
        scrollBusquedaEquipo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)));

        panelCentro.add(panelBusqueda, java.awt.BorderLayout.NORTH);
        panelCentro.add(scrollBusquedaEquipo, java.awt.BorderLayout.CENTER);
        this.add(panelCentro, java.awt.BorderLayout.CENTER);

        // SECCIÓN DERECHA: FORMULARIO
        javax.swing.JPanel panelDerecho = new javax.swing.JPanel(new java.awt.GridBagLayout());
        panelDerecho.setBackground(java.awt.Color.WHITE);
        panelDerecho.setPreferredSize(new java.awt.Dimension(380, 0)); 
        panelDerecho.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 220, 220)),
                javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.BOTH; 
        gbc.insets = new java.awt.Insets(8, 0, 2, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        javax.swing.JLabel lblSub = new javax.swing.JLabel("2. Detalles de la Orden");
        lblSub.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblSub.setForeground(java.awt.Color.GRAY);
        gbc.gridy = 0; gbc.insets = new java.awt.Insets(0, 0, 15, 0);
        panelDerecho.add(lblSub, gbc);

        // Cajas de texto (Cliente y Equipo)
        gbc.insets = new java.awt.Insets(5, 0, 2, 0);
        gbc.gridy++; panelDerecho.add(new javax.swing.JLabel("Cliente:"), gbc);
        txtNombre.setPreferredSize(new java.awt.Dimension(0, 30));
        gbc.gridy++; panelDerecho.add(txtNombre, gbc);

        gbc.gridy++; panelDerecho.add(new javax.swing.JLabel("Equipo:"), gbc);
        txtEquipo.setPreferredSize(new java.awt.Dimension(0, 30));
        gbc.gridy++; panelDerecho.add(txtEquipo, gbc);

        // --- CONFIGURACIÓN DE TEXTO FANTASMA (FOCUS LISTENER) ---
        
        gbc.gridy++; panelDerecho.add(new javax.swing.JLabel("Problema Reportado:"), gbc);
        gbc.weighty = 0.3;  
        panelProblema.setPreferredSize(new java.awt.Dimension(0, 80));
        
        // 1. Para el Problema Reportado
        txtProblema.setText("Describa aquí la falla del equipo...");
        txtProblema.setForeground(Color.GRAY); // Letra gris de inicio
        txtProblema.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtProblema.setLineWrap(true);
        txtProblema.setWrapStyleWord(true);
        
        txtProblema.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtProblema.getText().equals("Describa aquí la falla del equipo...")) {
                    txtProblema.setText(""); // Borra el fantasma
                    txtProblema.setForeground(Color.BLACK); // Letra normal para escribir
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtProblema.getText().trim().isEmpty()) {
                    txtProblema.setText("Describa aquí la falla del equipo..."); // Vuelve el fantasma
                    txtProblema.setForeground(Color.GRAY);
                }
            }
        });
        gbc.gridy++; panelDerecho.add(panelProblema, gbc);

        gbc.weighty = 0.0; 
        gbc.gridy++; panelDerecho.add(new javax.swing.JLabel("Trabajo a Realizar / Realizado:"), gbc);
        gbc.weighty = 0.3;
        panelTrabajo.setPreferredSize(new java.awt.Dimension(0, 80));
        
        // 2. Para el Trabajo Realizado
        txtTrabajo.setText("Escriba la reparación que se realizó...");
        txtTrabajo.setForeground(Color.GRAY); // Letra gris de inicio
        txtTrabajo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTrabajo.setLineWrap(true);
        txtTrabajo.setWrapStyleWord(true);
        
        txtTrabajo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtTrabajo.getText().equals("Escriba la reparación que se realizó...")) {
                    txtTrabajo.setText("");
                    txtTrabajo.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtTrabajo.getText().trim().isEmpty()) {
                    txtTrabajo.setText("Escriba la reparación que se realizó...");
                    txtTrabajo.setForeground(Color.GRAY);
                }
            }
        });
        gbc.gridy++; panelDerecho.add(panelTrabajo, gbc);

        // Estado inicial
        gbc.weighty = 0.0; 
        gbc.gridy++; panelDerecho.add(new javax.swing.JLabel("Estado Inicial:"), gbc);
        cmbEstado.setPreferredSize(new java.awt.Dimension(0, 35));
        gbc.gridy++; panelDerecho.add(cmbEstado, gbc);
        
        // --- NUEVA SECCIÓN DE SEGURIDAD DINÁMICA ---
        JLabel lblSeguridad = new JLabel("Seguridad del Dispositivo:");
        lblSeguridad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeguridad.setForeground(Color.GRAY);

        // 1. Creamos los botones de opción
        rbtnPin = new javax.swing.JRadioButton("PIN / Texto", true); // True para que inicie seleccionado
        rbtnPatron = new javax.swing.JRadioButton("Patrón visual");
        rbtnPin.setBackground(Color.WHITE);
        rbtnPatron.setBackground(Color.WHITE);

        // 2. Los agrupamos para que actúen como un switch
        javax.swing.ButtonGroup grupoSeguridad = new javax.swing.ButtonGroup();
        grupoSeguridad.add(rbtnPin);
        grupoSeguridad.add(rbtnPatron);

        // 3. Panel horizontal
        JPanel panelRadios = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        panelRadios.setBackground(Color.WHITE);
        panelRadios.add(rbtnPin);
        panelRadios.add(new JLabel("   "));
        panelRadios.add(rbtnPatron);

        // 4. Contenedor CardLayout
        panelSeguridadContainer = new JPanel(new java.awt.CardLayout());
        panelSeguridadContainer.setBackground(Color.WHITE);

        // CARTA 1: PIN
        txtSeguridad = new JTextField();
        txtSeguridad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSeguridad.setPreferredSize(new Dimension(0, 30));

        // CARTA 2: Patrón
        lblPatronVisual = new JLabel("<html><div style='text-align: center; letter-spacing: 12px; line-height: 0.8; font-size: 14px; color: #bdc3c7;'>O &nbsp; O &nbsp; O<br>O &nbsp; O &nbsp; O<br>O &nbsp; O &nbsp; O</div></html>");
        lblPatronVisual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPatronVisual.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(230, 230, 230)));
        lblPatronVisual.setPreferredSize(new Dimension(0, 85)); 

        panelSeguridadContainer.add(txtSeguridad, "PIN");
        panelSeguridadContainer.add(lblPatronVisual, "PATRON");

        // 5. Lógica de los botones
        rbtnPin.addActionListener(e -> {
            ((java.awt.CardLayout) panelSeguridadContainer.getLayout()).show(panelSeguridadContainer, "PIN");
        });
        rbtnPatron.addActionListener(e -> {
            ((java.awt.CardLayout) panelSeguridadContainer.getLayout()).show(panelSeguridadContainer, "PATRON");
        });

        // 6. Agregamos al panel principal derecho
        gbc.gridy++; panelDerecho.add(lblSeguridad, gbc);
        gbc.gridy++; panelDerecho.add(panelRadios, gbc);
        gbc.gridy++; gbc.insets = new java.awt.Insets(5, 0, 0, 0); 
        panelDerecho.add(panelSeguridadContainer, gbc);

        // Botón Guardar
        gbc.gridy++; gbc.insets = new java.awt.Insets(25, 0, 0, 0);
        btnGuardar.setBackground(new java.awt.Color(46, 204, 113));
        btnGuardar.setForeground(java.awt.Color.WHITE);
        btnGuardar.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        btnGuardar.setPreferredSize(new java.awt.Dimension(0, 45));
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelDerecho.add(btnGuardar, gbc);

        // Empujador
        gbc.gridy++; gbc.weighty = 1.0;
        panelDerecho.add(javax.swing.Box.createVerticalGlue(), gbc);

        this.add(panelDerecho, java.awt.BorderLayout.EAST);

        this.revalidate();
        this.repaint();
    }
}
