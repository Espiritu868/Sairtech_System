package gui;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

public class PanelUsuarios extends javax.swing.JPanel {

    public PanelUsuarios() {
        initComponents();
        cargarTablaUsuarios();
        aplicarDisenoUsuarios();
        Set<AWTKeyStroke> teclas = new HashSet<>(
                getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS)
        );
        teclas.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, teclas);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();
        txtUsuario = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmbRol = new javax.swing.JComboBox<>();
        scrollUsuarios = new javax.swing.JScrollPane();
        tablaUsuarios = new javax.swing.JTable();
        btnEntrar1 = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("CREAR USUARIO");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 10, 520, -1));

        btnLimpiar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);
        jPanel3.add(btnLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 390, 130, -1));

        txtPassword.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });
        jPanel3.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 140, 210, -1));

        txtUsuario.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        txtUsuario.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel3.add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 200, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Contraseña");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 100, 130, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nombre de Usuario");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        cmbRol.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        cmbRol.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Administrador", "Tecnico" }));
        jPanel3.add(cmbRol, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 140, -1, -1));

        tablaUsuarios.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Usuario", "Rol"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaUsuariosMouseClicked(evt);
            }
        });
        scrollUsuarios.setViewportView(tablaUsuarios);

        jPanel3.add(scrollUsuarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, 210));

        btnEntrar1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnEntrar1.setText("Guardar");
        btnEntrar1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEntrar1.addActionListener(this::btnEntrar1ActionPerformed);
        jPanel3.add(btnEntrar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 210, 130, -1));

        btnModificar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnModificar.addActionListener(this::btnModificarActionPerformed);
        jPanel3.add(btnModificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 270, 130, -1));

        btnEliminar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
        jPanel3.add(btnEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 330, 130, -1));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 490));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1027, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 627, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

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
    
    private void aplicarDisenoUsuarios() {
        this.removeAll();
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        this.setBackground(new Color(240, 244, 248)); // Fondo gris claro web

        // TÍTULO SUPERIOR
        JLabel lblTitulo = new JLabel("Gestión de Usuarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        this.add(lblTitulo, BorderLayout.NORTH);

        // ZONA IZQUIERDA: ESTILIZAR LA TABLA
        tablaUsuarios.setRowHeight(35);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollUsuarios.getViewport().setBackground(Color.WHITE);
        scrollUsuarios.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // ZONA DERECHA: EL FORMULARIO MODERNO
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setPreferredSize(new Dimension(350, 0));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Título del formulario
        JLabel lblSub = new JLabel("Datos del Usuario");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSub.setForeground(Color.GRAY);
        gbc.gridy = 0;
        panelFormulario.add(lblSub, gbc);

        gbc.gridy++; panelFormulario.add(new JLabel("Nombre de Usuario:"), gbc);
        gbc.gridy++; panelFormulario.add(txtUsuario, gbc);

        gbc.gridy++; panelFormulario.add(new JLabel("Contraseña:"), gbc);
        gbc.gridy++; panelFormulario.add(txtPassword, gbc);

        gbc.gridy++; panelFormulario.add(new JLabel("Rol del Sistema:"), gbc);
        gbc.gridy++; panelFormulario.add(cmbRol, gbc);

        gbc.gridy++; gbc.insets = new Insets(25, 0, 0, 0);

        // Panel para agrupar botones en una cuadrícula 2x2
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setOpaque(false);

        // Estilizamos tus botones con colores FlatLaf modernos
        btnEntrar1.setText("Guardar"); 
        btnEntrar1.setBackground(new Color(46, 204, 113)); btnEntrar1.setForeground(Color.WHITE);
        
        btnModificar.setBackground(new Color(52, 152, 219)); btnModificar.setForeground(Color.WHITE);
        btnEliminar.setBackground(new Color(231, 76, 60)); btnEliminar.setForeground(Color.WHITE);
        btnLimpiar.setBackground(Color.GRAY); btnLimpiar.setForeground(Color.WHITE);

        panelBotones.add(btnEntrar1);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridy++; panelFormulario.add(panelBotones, gbc);

        // Empujar todo hacia arriba
        gbc.gridy++; gbc.weighty = 1.0;
        panelFormulario.add(Box.createVerticalGlue(), gbc);

        // ENSAMBLAR TODO EN EL PANEL PRINCIPAL
        this.add(scrollUsuarios, BorderLayout.CENTER);
        this.add(panelFormulario, BorderLayout.EAST); 

        this.revalidate();
        this.repaint();
    }
    
    private void cargarTablaUsuarios() {
        javax.swing.table.DefaultTableModel modeloTabla = (javax.swing.table.DefaultTableModel) tablaUsuarios.getModel();
        modeloTabla.setRowCount(0);

        dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
        java.util.List<modelo.Usuario> lista = daoUsuario.listarUsuarios();

        for (modelo.Usuario u : lista) {
            Object[] fila = new Object[3];
            fila[0] = u.getIdUsuario();
            fila[1] = u.getNombreUsuario();
            fila[2] = u.getRol();
            modeloTabla.addRow(fila);
        }
    }
    
    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        txtUsuario.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        tablaUsuarios.clearSelection();
        txtUsuario.requestFocus();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnLimpiar.doClick();
        }
    }//GEN-LAST:event_txtPasswordKeyPressed

    private void btnEntrar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrar1ActionPerformed

        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()); 
        String rol = cmbRol.getSelectedItem().toString();

        if (usuario.isEmpty() || password.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos (Usuario y Contraseña).", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
        
        // --- ESCUDO DE SEGURIDAD (MENSAJE AMBIGUO) ---
        if (daoUsuario.existeClave(password)) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Por políticas de seguridad del sistema, esta contraseña es considerada vulnerable o no está permitida.\nPor favor, asigne un PIN o contraseña diferente.", 
                "Contraseña No Válida", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtPassword.setText(""); 
            txtPassword.requestFocus();
            return; 
        }
        // ---------------------------------------------
        
        if (daoUsuario.registrarUsuario(usuario, password, rol)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente en el sistema.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            txtUsuario.setText("");
            txtPassword.setText("");
            cmbRol.setSelectedIndex(0);
            txtUsuario.requestFocus();
            
            cargarTablaUsuarios(); 
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al registrar. Es posible que este nombre de usuario ya exista.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEntrar1ActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
       int fila = tablaUsuarios.getSelectedRow();
        
        if (fila == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para modificar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = Integer.parseInt(tablaUsuarios.getValueAt(fila, 0).toString());
        String usuarioOriginal = tablaUsuarios.getValueAt(fila, 1).toString();
        String rolOriginal = tablaUsuarios.getValueAt(fila, 2).toString();

        String nuevoUsuario = txtUsuario.getText().trim();
        String nuevoPassword = new String(txtPassword.getPassword());
        String nuevoRol = cmbRol.getSelectedItem().toString();

        if (nuevoUsuario.equals(usuarioOriginal) && nuevoRol.equals(rolOriginal) && nuevoPassword.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No se detectó ningún cambio en los datos.", "Sin Cambios", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return; 
        }

        if (nuevoUsuario.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idUsuario == 1 && nuevoRol.equals("Tecnico")) {
            javax.swing.JOptionPane.showMessageDialog(this, "¡No puedes degradar al Administrador principal a Técnico!", "Acción Denegada", javax.swing.JOptionPane.ERROR_MESSAGE);
            cmbRol.setSelectedItem("Administrador");
            return;
        }

        dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
        
        // --- ESCUDO DE SEGURIDAD (MENSAJE AMBIGUO) ---
        if (!nuevoPassword.isEmpty()) {
            if (daoUsuario.existeClave(nuevoPassword)) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Por políticas de seguridad del sistema, esta contraseña es considerada vulnerable o no está permitida.\nPor favor, asigne un PIN o contraseña diferente.", 
                    "Contraseña No Válida", javax.swing.JOptionPane.WARNING_MESSAGE);
                txtPassword.setText(""); 
                txtPassword.requestFocus();
                return; 
            }
        }
        // ---------------------------------------------
        
        if (daoUsuario.modificarUsuario(idUsuario, nuevoUsuario, nuevoPassword, nuevoRol)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            cargarTablaUsuarios();
            txtUsuario.setText("");
            txtPassword.setText("");
            cmbRol.setSelectedIndex(0);
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al actualizar. Verifique que el nombre de usuario no esté repetido.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed

        int fila = tablaUsuarios.getSelectedRow();
        
        if (fila == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para eliminar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = Integer.parseInt(tablaUsuarios.getValueAt(fila, 0).toString());
        String nombreUsuario = tablaUsuarios.getValueAt(fila, 1).toString();

        if (idUsuario == 1) {
            javax.swing.JOptionPane.showMessageDialog(this, "¡ALERTA!\nNo puedes eliminar la cuenta del Administrador Principal del sistema por motivos de seguridad.", "Acción Denegada", javax.swing.JOptionPane.ERROR_MESSAGE);
            return; // Detenemos todo aquí mismo
        }

        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas revocar el acceso y eliminar al usuario: " + nombreUsuario + "?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE);

        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            
            dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
            
            if (daoUsuario.eliminarUsuario(idUsuario)) {
                javax.swing.JOptionPane.showMessageDialog(this, "Acceso revocado. Usuario eliminado correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                cargarTablaUsuarios(); // Refrescamos la tabla al instante
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al eliminar el usuario.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void tablaUsuariosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaUsuariosMouseClicked

        int fila = tablaUsuarios.getSelectedRow();
        
        if (fila != -1) {
            txtUsuario.setText(tablaUsuarios.getValueAt(fila, 1).toString());
            
            cmbRol.setSelectedItem(tablaUsuarios.getValueAt(fila, 2).toString());
            
            txtPassword.setText(""); 
        }
    }//GEN-LAST:event_tablaUsuariosMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEntrar1;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JComboBox<String> cmbRol;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane scrollUsuarios;
    private javax.swing.JTable tablaUsuarios;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
