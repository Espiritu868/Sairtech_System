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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class PanelUsuarios extends javax.swing.JPanel {

    private JButton btnEliminar;
    private JButton btnEntrar1;
    private JButton btnLimpiar;
    private JButton btnModificar;
    private JButton btnRespaldoManual; // <-- NUEVO BOTÓN
    private JComboBox<String> cmbRol;
    private JScrollPane scrollUsuarios;
    private JTable tablaUsuarios;
    private JPasswordField txtPassword;
    private JTextField txtUsuario;

    public PanelUsuarios() {
        initComponents(); 
        inicializarComponentesManualmente(); 
        aplicarDisenoUsuarios(); 
        cargarTablaUsuarios(); 
        
        Set<AWTKeyStroke> teclas = new HashSet<>(
                getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS)
        );
        teclas.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, teclas);
    }
    
    private void inicializarComponentesManualmente() {
        txtUsuario = new JTextField();
        txtUsuario.setPreferredSize(new Dimension(0, 35));
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(0, 35));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });
        
        cmbRol = new JComboBox<>(new String[] { "Administrador", "Tecnico" });
        cmbRol.setPreferredSize(new Dimension(0, 35));
        cmbRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tablaUsuarios = new JTable();
        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] { "ID", "Usuario", "Rol" }
        ) {
            boolean[] canEdit = new boolean [] { false, false, false };
            public boolean isCellEditable(int rowIndex, int columnIndex) { return canEdit [columnIndex]; }
        });
        
        tablaUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaUsuariosMouseClicked(evt);
            }
        });
        
        scrollUsuarios = new JScrollPane();
        scrollUsuarios.setViewportView(tablaUsuarios);
        
        btnEntrar1 = new JButton();
        btnEntrar1.addActionListener(this::btnEntrar1ActionPerformed);
        
        btnModificar = new JButton();
        btnModificar.addActionListener(this::btnModificarActionPerformed);
        
        btnEliminar = new JButton();
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
        
        btnLimpiar = new JButton();
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);
        
        // --- INICIALIZACIÓN NUEVO BOTÓN ---
        btnRespaldoManual = new JButton("Generar Respaldo Manual Ahora");
        btnRespaldoManual.addActionListener(e -> solicitarRespaldoManual());
    }

    private void aplicarDisenoUsuarios() {
        this.removeAll();
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        this.setBackground(new Color(240, 244, 248));

        JLabel lblTitulo = new JLabel("Gestión de Usuarios y Sistema");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        this.add(lblTitulo, BorderLayout.NORTH);

        tablaUsuarios.setRowHeight(35);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollUsuarios.getViewport().setBackground(Color.WHITE);
        scrollUsuarios.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

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

        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setOpaque(false);

        btnEntrar1.setText("Guardar"); 
        btnEntrar1.setBackground(new Color(46, 204, 113)); btnEntrar1.setForeground(Color.WHITE);
        btnEntrar1.setFocusPainted(false); btnEntrar1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnModificar.setText("Modificar");
        btnModificar.setBackground(new Color(52, 152, 219)); btnModificar.setForeground(Color.WHITE);
        btnModificar.setFocusPainted(false); btnModificar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnEliminar.setText("Eliminar");
        btnEliminar.setBackground(new Color(231, 76, 60)); btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false); btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnLimpiar.setText("Limpiar");
        btnLimpiar.setBackground(Color.GRAY); btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false); btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panelBotones.add(btnEntrar1);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridy++; panelFormulario.add(panelBotones, gbc);

        // --- SECCIÓN: RESPALDOS DEL SISTEMA ---
        gbc.gridy++; gbc.insets = new Insets(40, 0, 5, 0);
        JLabel lblSys = new JLabel("Configuración de Seguridad");
        lblSys.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSys.setForeground(Color.GRAY);
        panelFormulario.add(lblSys, gbc);

        JButton btnConfigurarBackup = new JButton("Cambiar Ruta de Respaldo");
        btnConfigurarBackup.setBackground(new Color(142, 68, 173)); // Morado elegante
        btnConfigurarBackup.setForeground(Color.WHITE);
        btnConfigurarBackup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfigurarBackup.setPreferredSize(new Dimension(0, 40));
        btnConfigurarBackup.setFocusPainted(false);
        btnConfigurarBackup.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnConfigurarBackup.addActionListener(e -> configurarRutaBackup());

        gbc.gridy++; gbc.insets = new Insets(5, 0, 0, 0);
        panelFormulario.add(btnConfigurarBackup, gbc);
        
        // BOTÓN RESPALDO MANUAL
        btnRespaldoManual.setBackground(new Color(230, 126, 34)); // Naranja de advertencia/acción
        btnRespaldoManual.setForeground(Color.WHITE);
        btnRespaldoManual.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRespaldoManual.setPreferredSize(new Dimension(0, 40));
        btnRespaldoManual.setFocusPainted(false);
        btnRespaldoManual.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        gbc.gridy++; gbc.insets = new Insets(10, 0, 0, 0);
        panelFormulario.add(btnRespaldoManual, gbc);
        // ----------------------------------------------

        gbc.gridy++; gbc.weighty = 1.0;
        panelFormulario.add(Box.createVerticalGlue(), gbc);

        this.add(scrollUsuarios, BorderLayout.CENTER);
        this.add(panelFormulario, BorderLayout.EAST); 

        this.revalidate();
        this.repaint();
    }
    
    // --- LÓGICA DE RESPALDO MANUAL CON DETECTOR DE ERRORES ---
    private void solicitarRespaldoManual() {
        javax.swing.JPasswordField pf = new javax.swing.JPasswordField();
        int okCxl = javax.swing.JOptionPane.showConfirmDialog(this, pf, "Autorización Requerida\nIngrese contraseña de Administrador o Técnico:", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE);

        if (okCxl == javax.swing.JOptionPane.OK_OPTION) {
            String password = new String(pf.getPassword());
            if (password.isEmpty()) return;

            boolean autorizado = false;
            
            // 1. INTENTAMOS VALIDAR LA CONTRASEÑA
            try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
                 java.sql.PreparedStatement ps = con.prepareStatement("SELECT id_usuario FROM usuarios WHERE password_hash = ? AND (rol = 'Administrador' OR rol = 'Tecnico')")) {
                
                String passwordEncriptado = new dao.UsuarioDAO().encriptarContraseña(password);
                ps.setString(1, passwordEncriptado);

                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) autorizado = true;
                }
            } catch (Exception ex) {
                // AHORA SÍ: El servidor nos dirá exactamente por qué falla
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Fallo Técnico en el Servidor:\n" + ex.toString(), 
                    "Diagnóstico de Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!autorizado) {
                javax.swing.JOptionPane.showMessageDialog(this, "Contraseña incorrecta o sin privilegios.", "Acceso Denegado", javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. INTENTAMOS DEJAR EL MENSAJE EN EL BUZÓN
            try (java.sql.Connection con = new factory.ConexionFactory().getConexion()) {
                try (java.sql.PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS peticiones_backup (id INT PRIMARY KEY AUTO_INCREMENT, fecha DATETIME);")) {
                    ps.execute(); 
                }
                
                try (java.sql.PreparedStatement psInsert = con.prepareStatement("INSERT INTO peticiones_backup (fecha) VALUES (NOW())")) {
                    psInsert.executeUpdate();
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "¡Autorización Exitosa!\n\nSe ha enviado la orden silenciosa al Servidor.\nEl respaldo se guardará localmente en el disco F: en un máximo de 60 segundos.", 
                        "Respaldo en Proceso", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                 javax.swing.JOptionPane.showMessageDialog(this, "Error al crear la petición:\n" + ex.toString(), "Diagnóstico de Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void configurarRutaBackup() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Seleccione la carpeta destino para los respaldos");
        chooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        String rutaActual = utilidades.BackupAutomatico.obtenerRutaBackup();
        java.io.File archivoActual = new java.io.File(rutaActual);
        if (archivoActual.exists()) {
            chooser.setCurrentDirectory(archivoActual);
        }

        int seleccion = chooser.showOpenDialog(this);

        if (seleccion == javax.swing.JFileChooser.APPROVE_OPTION) {
            String nuevaRuta = chooser.getSelectedFile().getAbsolutePath();

            if (utilidades.BackupAutomatico.guardarNuevaRutaBackup(nuevaRuta)) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Ruta de respaldos actualizada correctamente a:\n" + nuevaRuta + "\n\nEl sistema guardará sus próximas copias de seguridad en esta nueva carpeta.",
                    "Configuración Guardada", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Hubo un error al intentar guardar la configuración.\nCompruebe que el archivo config.properties no esté bloqueado.",
                    "Error de Escritura", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
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
    
    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                           
        txtUsuario.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        tablaUsuarios.clearSelection();
        txtUsuario.requestFocus();
        
        btnEntrar1.setEnabled(true);   
        btnModificar.setEnabled(false); 
        btnEliminar.setEnabled(false);  
    }                                          

    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {                                       
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnLimpiar.doClick();
        }
    }                                      

    private void btnEntrar1ActionPerformed(java.awt.event.ActionEvent evt) {                                           
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()); 
        String rol = cmbRol.getSelectedItem().toString();

        if (usuario.isEmpty() || password.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, llene todos los campos (Usuario y Contraseña).", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
        
        if (daoUsuario.existeClave(password)) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Por políticas de seguridad del sistema, esta contraseña es considerada vulnerable o no está permitida.\nPor favor, asigne un PIN o contraseña diferente.", 
                "Contraseña No Válida", javax.swing.JOptionPane.WARNING_MESSAGE);
            txtPassword.setText(""); 
            txtPassword.requestFocus();
            return; 
        }
        
        if (daoUsuario.registrarUsuario(usuario, password, rol)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente en el sistema.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            cargarTablaUsuarios(); 
            btnLimpiar.doClick(); 
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al registrar. Es posible que este nombre de usuario ya exista.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }                                          

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {                                             
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
        
        if (daoUsuario.modificarUsuario(idUsuario, nuevoUsuario, nuevoPassword, nuevoRol)) {
            javax.swing.JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            cargarTablaUsuarios();
            btnLimpiar.doClick(); 
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al actualizar. Verifique que el nombre de usuario no esté repetido.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }                                            

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        int fila = tablaUsuarios.getSelectedRow();
        
        if (fila == -1) {
            javax.swing.JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para eliminar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = Integer.parseInt(tablaUsuarios.getValueAt(fila, 0).toString());
        String nombreUsuario = tablaUsuarios.getValueAt(fila, 1).toString();

        if (idUsuario == 1) {
            javax.swing.JOptionPane.showMessageDialog(this, "¡ALERTA!\nNo puedes eliminar la cuenta del Administrador Principal del sistema por motivos de seguridad.", "Acción Denegada", javax.swing.JOptionPane.ERROR_MESSAGE);
            return; 
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
                cargarTablaUsuarios(); 
                btnLimpiar.doClick(); 
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Error al eliminar el usuario.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                           

    private void tablaUsuariosMouseClicked(java.awt.event.MouseEvent evt) {                                           
        int fila = tablaUsuarios.getSelectedRow();
        
        if (fila != -1) {
            txtUsuario.setText(tablaUsuarios.getValueAt(fila, 1).toString());
            cmbRol.setSelectedItem(tablaUsuarios.getValueAt(fila, 2).toString());
            txtPassword.setText(""); 
            
            btnEntrar1.setEnabled(false);  
            btnModificar.setEnabled(true); 
            btnEliminar.setEnabled(true);  
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
