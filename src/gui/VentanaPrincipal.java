package gui;

import javax.swing.JButton;
import javax.swing.JLabel;

public class VentanaPrincipal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    private int idUsuarioActivo;
    
    public VentanaPrincipal() {
        initComponents();
        setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("/image/logo.png")));
        this.setLocationRelativeTo(null);
        aplicarDisenoPrincipal();
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                // Quitamos el respaldo de "CIERRE" para que no moleste al salir
                System.exit(0);
            }
        });

        panelMenu.setVisible(false);

        PanelLogin login = new PanelLogin();
        mostrarPanel(login);
        new dao.UsuarioDAO().inicializarAdministradorDefecto();
        
        // --- ARRANQUE DEL PROGRAMADOR ---
        new Thread(() -> {
            // Eliminamos la línea de "INICIO" que hacía el backup al abrir.
            
            // MANTENEMOS esta línea: es la que enciende el reloj para 
            // que el sistema revise la hora cada minuto y haga los backups de ley.
            utilidades.BackupAutomatico.iniciarProgramadorRespaldo();
        }).start();
    }
    
    public void habilitarSistema(String rol, String nombreUsuario) { 
        
        panelMenu.setVisible(true);
        lblPerfil.setText(nombreUsuario + " | " + rol);
        btnSalir.setVisible(true);
        
        if (rol.equals("Administrador")) {
            panelCategoriaAdmin.setVisible(true); 
            
            for(java.awt.Component c : panelCategoriaAdmin.getComponents()) {
                if(c instanceof javax.swing.JPanel) { 
                    for(java.awt.Component subC : ((javax.swing.JPanel)c).getComponents()) {
                        subC.setEnabled(true);
                    }
                }
            }

            seleccionarBotonMenu(null); 
            
            mostrarPanel(new PanelEstadisticas()); 
            seleccionarBotonMenu(btnDashboard); 
        } else {
            panelCategoriaAdmin.setVisible(false); 
            seleccionarBotonMenu(null); 
            
            mostrarPanel(new PanelIngresoEquipos());
            seleccionarBotonMenu(btnIngresoEquipos); 
        }
    }
    
    public void mostrarPanel(javax.swing.JPanel p) {
        p.setSize(panelContenedor.getWidth(), panelContenedor.getHeight());
        p.setLocation(0,0);
        
        panelContenedor.removeAll();
        panelContenedor.add(p, java.awt.BorderLayout.CENTER);
        
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }
    
    private void seleccionarBotonMenu(javax.swing.JButton botonActivo) {
        // --- SE AGREGÓ btnBancoDespiece AL ARREGLO DE BOTONES ---
        javax.swing.JButton[] todosLosBotones = {
            btnIngresoEquipos, btnControlOrdenes, btnEntregaEquipos, btnBancoDespiece, 
            btnPuntoVenta, btnInventario, btnProveedores, btnHistorialVentas, 
            btnClientes, btnHistorialEquipos, btnHistorialGarantias,
            btnDashboard, btnGestionUsuarios,
            btnPanelKnijico
        };
        
        for (javax.swing.JButton b : todosLosBotones) {
            if (b != null) b.setEnabled(true);
        }
        
        if (botonActivo != null) {
            botonActivo.setEnabled(false);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        panelContenedor = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SAIRTECH SYSTEMS");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1037, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        panelContenedor.setBackground(new java.awt.Color(255, 255, 255));
        panelContenedor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout panelContenedorLayout = new javax.swing.GroupLayout(panelContenedor);
        panelContenedor.setLayout(panelContenedorLayout);
        panelContenedorLayout.setHorizontalGroup(
            panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelContenedorLayout.setVerticalGroup(
            panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 612, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelContenedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel panelContenedor;
    // End of variables declaration//GEN-END:variables
    // --- VARIABLES DEL NUEVO MENÚ COLAPSABLE ---
    private javax.swing.JButton btnIngresoEquipos;
    private javax.swing.JButton btnBancoDespiece; // <--- NUEVO BOTON PARA HUESERA
    private javax.swing.JButton btnControlOrdenes;
    private javax.swing.JButton btnEntregaEquipos;
    private javax.swing.JButton btnPuntoVenta;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnProveedores;
    private javax.swing.JButton btnHistorialVentas;
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnHistorialEquipos;
    private javax.swing.JButton btnHistorialGarantias; 
    private javax.swing.JButton btnPanelKnijico;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnGestionUsuarios;
    
    private javax.swing.JPanel panelCategoriaAdmin;
    private javax.swing.JLabel lblPerfil;
    private javax.swing.JButton btnSalir;
    private javax.swing.JPanel panelMenu;

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Error al iniciar FlatLaf: " + ex.getMessage());
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaPrincipal().setVisible(true);
            }
        });
    }
    
    private void aplicarDisenoPrincipal() {
        this.getContentPane().removeAll();
        this.setLayout(new java.awt.BorderLayout());
        panelMenu = new javax.swing.JPanel();
        panelMenu.setBackground(new java.awt.Color(44, 62, 80)); 
        panelMenu.setPreferredSize(new java.awt.Dimension(250, 0)); 
        panelMenu.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        javax.swing.JLabel lblLogo = new javax.swing.JLabel("SAIRTECH");
        lblLogo.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 26));
        lblLogo.setForeground(java.awt.Color.WHITE);
        lblLogo.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 0, 5, 0)); 
        panelMenu.add(lblLogo);

        lblPerfil = new javax.swing.JLabel("Iniciando...");
        lblPerfil.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 14));
        lblPerfil.setForeground(new java.awt.Color(189, 195, 199));
        lblPerfil.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0)); 
        panelMenu.add(lblPerfil);

        btnIngresoEquipos = new javax.swing.JButton("Ingreso de Equipos");
        btnBancoDespiece = new javax.swing.JButton("Banco de Despiece"); // <--- INICIALIZAR EL NUEVO BOTON
        btnControlOrdenes = new javax.swing.JButton("Control de Ordenes");
        btnPuntoVenta = new javax.swing.JButton("Punto de Venta");
        btnInventario = new javax.swing.JButton("Inventario");
        btnProveedores = new javax.swing.JButton("Proveedores");
        btnHistorialVentas = new javax.swing.JButton("Historial de Recibos");
        btnClientes = new javax.swing.JButton("Directorio Clientes");
        btnHistorialEquipos = new javax.swing.JButton("Historial Equipos");
        btnHistorialGarantias = new javax.swing.JButton("Historial Garantías"); 
        
        btnDashboard = new javax.swing.JButton("Dashboard");
        btnGestionUsuarios = new javax.swing.JButton("Usuarios del Sistema");
        
        btnPanelKnijico = new javax.swing.JButton("Admin. Pantallas Knijico");
        btnPanelKnijico.setPreferredSize(new java.awt.Dimension(250, 50)); 
        btnPanelKnijico.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
        btnPanelKnijico.setForeground(java.awt.Color.WHITE);
        btnPanelKnijico.setBackground(new java.awt.Color(243, 156, 18));
        btnPanelKnijico.setBorderPainted(false);
        btnPanelKnijico.setFocusPainted(false);
        btnPanelKnijico.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPanelKnijico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if(btnPanelKnijico.isEnabled()) btnPanelKnijico.setBackground(new java.awt.Color(211, 84, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPanelKnijico.setBackground(new java.awt.Color(243, 156, 18));
            }
        });

        // --- AGREGAMOS btnBancoDespiece AL MENÚ "Taller y Servicios" ---
        javax.swing.JPanel catTaller = crearMenuColapsable("[ Taller y Servicios ]", true, btnIngresoEquipos, btnBancoDespiece);

        javax.swing.JPanel catDirectorios = crearMenuColapsable("[ Directorios ]", false, btnClientes, btnHistorialEquipos, btnHistorialVentas, btnHistorialGarantias);
        javax.swing.JPanel catVentas = crearMenuColapsable("[ Ventas e Inventario ]", false, btnPuntoVenta, btnInventario, btnProveedores);
        
        panelCategoriaAdmin = crearMenuColapsable("[ Administración ]", false, btnDashboard, btnGestionUsuarios);
        
        panelMenu.add(panelCategoriaAdmin);
        panelMenu.add(catTaller);
        panelMenu.add(catVentas);
        panelMenu.add(catDirectorios);
        
        javax.swing.JLabel separadorKnijico = new javax.swing.JLabel();
        separadorKnijico.setPreferredSize(new java.awt.Dimension(250, 30)); 
        panelMenu.add(separadorKnijico);
        panelMenu.add(btnPanelKnijico);

        btnSalir = new javax.swing.JButton("Cerrar Sesión");
        btnSalir.setPreferredSize(new java.awt.Dimension(250, 50)); 
        btnSalir.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        btnSalir.setForeground(new java.awt.Color(231, 76, 60)); 
        btnSalir.setBackground(new java.awt.Color(44, 62, 80));
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalir.addActionListener(this::btnSalirActionPerformed);
        
        javax.swing.JLabel empujador = new javax.swing.JLabel();
        empujador.setPreferredSize(new java.awt.Dimension(250, 40)); 
        panelMenu.add(empujador);
        panelMenu.add(btnSalir);

        panelContenedor.setLayout(new java.awt.BorderLayout());
        panelContenedor.setBackground(new java.awt.Color(240, 244, 248));

        this.add(panelMenu, java.awt.BorderLayout.WEST);       
        this.add(panelContenedor, java.awt.BorderLayout.CENTER); 

        this.revalidate();
        this.repaint();
        
        asignarEventosBotones(); 
    }

    private javax.swing.JPanel crearMenuColapsable(String titulo, boolean abiertoPorDefecto, javax.swing.JButton... subBotones) {
        javax.swing.JPanel contenedor = new javax.swing.JPanel(new java.awt.BorderLayout());
        contenedor.setOpaque(false);

        javax.swing.JButton btnCategoria = new javax.swing.JButton(titulo);
        btnCategoria.setPreferredSize(new java.awt.Dimension(250, 45)); 
        btnCategoria.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        btnCategoria.setForeground(new java.awt.Color(189, 195, 199)); 
        btnCategoria.setBackground(new java.awt.Color(34, 49, 63)); 
        btnCategoria.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); 
        btnCategoria.setBorderPainted(false);
        btnCategoria.setFocusPainted(false);
        btnCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.JPanel panelSubBotones = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        panelSubBotones.setOpaque(false);
        panelSubBotones.setVisible(abiertoPorDefecto); 

        int altoTotal = 0;
        for (javax.swing.JButton subBtn : subBotones) {
            estilizarSubBoton(subBtn);
            panelSubBotones.add(subBtn);
            altoTotal += 40; 
        }
        panelSubBotones.setPreferredSize(new java.awt.Dimension(250, altoTotal));

        btnCategoria.addActionListener(e -> {
            boolean visible = panelSubBotones.isVisible();
            panelSubBotones.setVisible(!visible);
        });

        contenedor.add(btnCategoria, java.awt.BorderLayout.NORTH);
        contenedor.add(panelSubBotones, java.awt.BorderLayout.CENTER);
        return contenedor;
    }

    private void estilizarSubBoton(javax.swing.JButton btn) {
        btn.setPreferredSize(new java.awt.Dimension(250, 40));
        btn.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        btn.setForeground(java.awt.Color.WHITE);
        btn.setBackground(new java.awt.Color(44, 62, 80));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if(btn.isEnabled()) btn.setBackground(new java.awt.Color(65, 85, 105)); 
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new java.awt.Color(44, 62, 80));
            }
        });
    }
    
    private void btnIngresoActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelIngresoEquipos());
        seleccionarBotonMenu(btnIngresoEquipos);
    } 
    
    // --- ACCIÓN DEL NUEVO BOTÓN BANCO DE DESPIECE ---
    private void btnBancoDespieceActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelDespiece());
        seleccionarBotonMenu(btnBancoDespiece);
    }
    
    private void btnProveedoresActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelProveedores());
        seleccionarBotonMenu(btnProveedores);
    }
    
    private void btnKnijicoActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelKnijico()); 
        seleccionarBotonMenu(btnPanelKnijico);
    }
    
    private void btnEntregaCobroActionPerformed(java.awt.event.ActionEvent evt) {                                              
        mostrarPanel(PanelPuntoVenta.getInstancia()); 
        seleccionarBotonMenu(btnEntregaEquipos); 
    }

    private void btnListadoActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelListadoOrdenes());
        
    } 

    private void btnClientesActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelClientes());
        seleccionarBotonMenu(btnClientes);
    }

    private void btnEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelEstadisticas());
        seleccionarBotonMenu(btnDashboard);
    }
    
    private void btnInventarioActionPerformed(java.awt.event.ActionEvent evt) {                                              
        mostrarPanel(new PanelInventario());
        seleccionarBotonMenu(btnInventario);
    }
    
    private void btnHistorialVentasActionPerformed(java.awt.event.ActionEvent evt) {                                              
        mostrarPanel(new PanelHistorialRecibos());
        seleccionarBotonMenu(btnHistorialVentas);
    }
    
    private void btnHistorialGarantiasActionPerformed(java.awt.event.ActionEvent evt) {                                              
        mostrarPanel(new PanelHistorialGarantias());
        seleccionarBotonMenu(btnHistorialGarantias);
    }
    
    private void btnPuntoVentaActionPerformed(java.awt.event.ActionEvent evt) {                                              
        mostrarPanel(PanelPuntoVenta.getInstancia()); 
        seleccionarBotonMenu(btnPuntoVenta);        
    }
    
    private void btnHistorialEquiposActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelHistorialEquipos());
        seleccionarBotonMenu(btnHistorialEquipos);
    }

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {                                             
        javax.swing.JPasswordField pwd = new javax.swing.JPasswordField(15);
        pwd.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
        
        Object[] mensaje = { "Acceso Restringido.\nIngrese una contraseña de Administrador para continuar:\n\n", pwd };
        
        int opcion = javax.swing.JOptionPane.showConfirmDialog(this, mensaje, "Verificación de Seguridad", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        
        if (opcion == javax.swing.JOptionPane.OK_OPTION) {
            String passwordIngresada = new String(pwd.getPassword());
            String nombreAdminEncontrado = verificarContrasenaAdminGlobal(passwordIngresada);
            
            if (nombreAdminEncontrado != null) {
                javax.swing.JOptionPane.showMessageDialog(this, "¡Bienvenido al panel, " + nombreAdminEncontrado + "!", "Acceso Concedido", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                mostrarPanel(new PanelUsuarios());
                seleccionarBotonMenu(btnGestionUsuarios);
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Acceso denegado.\nLa contraseña es incorrecta o no pertenece a un Administrador.\n\nSolo los administradores pueden entrar a este módulo.", "Error de Seguridad", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String verificarContrasenaAdminGlobal(String password) {
        String hashGenerado = "";
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            hashGenerado = hexString.toString();
        } catch (Exception e) {
            System.err.println("Error al encriptar: " + e.getMessage());
            return null;
        }
        
        String sql = "SELECT usuario FROM usuarios WHERE password_hash = ? AND rol = 'Administrador'";
        try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, hashGenerado);
            
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("usuario"); 
            }
        } catch (Exception e) {
            System.err.println("Error al consultar BD por contraseña global: " + e.getMessage());
        }
        return null;
    }
    
    private void asignarEventosBotones() {
        btnIngresoEquipos.addActionListener(this::btnIngresoActionPerformed);
        
        // --- ASIGNAR EVENTO DEL BOTÓN BANCO DE DESPIECE ---
        btnBancoDespiece.addActionListener(this::btnBancoDespieceActionPerformed);
        
        btnControlOrdenes.addActionListener(this::btnListadoActionPerformed);
        btnPuntoVenta.addActionListener(this::btnPuntoVentaActionPerformed);
        btnInventario.addActionListener(this::btnInventarioActionPerformed);
        btnProveedores.addActionListener(this::btnProveedoresActionPerformed);
        btnHistorialVentas.addActionListener(this::btnHistorialVentasActionPerformed);
        btnHistorialGarantias.addActionListener(this::btnHistorialGarantiasActionPerformed); 
        btnClientes.addActionListener(this::btnClientesActionPerformed);
        btnDashboard.addActionListener(this::btnEstadisticasActionPerformed);
        btnGestionUsuarios.addActionListener(this::btnUsuariosActionPerformed);
        btnPanelKnijico.addActionListener(this::btnKnijicoActionPerformed);
        btnHistorialEquipos.addActionListener(this::btnHistorialEquiposActionPerformed);
    }
    
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {
        int respuesta = javax.swing.JOptionPane.showConfirmDialog(this, 
            "¿Desea cerrar la sesión actual?", "Cerrar Sesión", 
            javax.swing.JOptionPane.YES_NO_OPTION, 
            javax.swing.JOptionPane.QUESTION_MESSAGE);

        if (respuesta == javax.swing.JOptionPane.YES_OPTION) {
            this.setVisible(false);
            VentanaPrincipal nuevaVentana = new VentanaPrincipal();
            nuevaVentana.setVisible(true);
            this.dispose();
        }
    }
    
    public String getNombreUsuarioActivo() { return lblPerfil.getText().split(" \\| ")[0]; }
    public int getIdUsuarioActivo() { return idUsuarioActivo; }
    public void setIdUsuarioActivo(int idUsuarioActivo) { this.idUsuarioActivo = idUsuarioActivo; }
    public javax.swing.JButton getBtnEntregaEquipos() { return btnEntregaEquipos; }
}