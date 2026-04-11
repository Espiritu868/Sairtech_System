
package gui;

import javax.swing.JButton;
import javax.swing.JLabel;


public class VentanaPrincipal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    private int idUsuarioActivo;
    
    public VentanaPrincipal() {
        initComponents();
        this.setLocationRelativeTo(null);
        aplicarDisenoPrincipal();
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        panelMenu.setVisible(false);

        PanelLogin login = new PanelLogin();
        mostrarPanel(login);
        new dao.UsuarioDAO().inicializarAdministradorDefecto();
    }
    
    public void habilitarSistema(String rol, String nombreUsuario) { 
        
        panelMenu.setVisible(true);
        lblPerfil.setText(nombreUsuario + " | " + rol);
        btnSalir.setVisible(true);
        
        if (rol.equals("Administrador")) {
            // El admin ve todo, incluyendo la carpeta oculta
            panelCategoriaAdmin.setVisible(true); 
            
            // --- CORRECCIÓN: Desbloquear explícitamente los botones de admin ---
            for(java.awt.Component c : panelCategoriaAdmin.getComponents()) {
                if(c instanceof javax.swing.JPanel) { // El panelSubBotones
                    for(java.awt.Component subC : ((javax.swing.JPanel)c).getComponents()) {
                        subC.setEnabled(true);
                    }
                }
            }

            seleccionarBotonMenu(null); // Desbloquea todos
            
            mostrarPanel(new PanelEstadisticas()); 
            seleccionarBotonMenu(btnDashboard); // Seleccionamos el Dashboard
        } else {
            // AL TÉCNICO LE DESAPARECE TODA LA CARPETA DE ADMINISTRACIÓN
            panelCategoriaAdmin.setVisible(false); 
            seleccionarBotonMenu(null); // Desbloquea los que sí puede ver
            
            // Lo mandamos directo al ingreso de equipos
            mostrarPanel(new PanelIngresoEquipos());
            seleccionarBotonMenu(btnIngresoEquipos); 
        }
    }
    
    private void mostrarPanel(javax.swing.JPanel p) {
        p.setSize(panelContenedor.getWidth(), panelContenedor.getHeight());
        p.setLocation(0,0);
        
        // Limpiamos el contenedor y le metemos el nuevo panel
        panelContenedor.removeAll();
        panelContenedor.add(p, java.awt.BorderLayout.CENTER);
        
        // Le decimos a Java que refresque la pantalla
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }
    private void seleccionarBotonMenu(javax.swing.JButton botonActivo) {
        javax.swing.JButton[] todosLosBotones = {
            btnIngresoEquipos, btnControlOrdenes, btnEntregaEquipos, 
            btnPuntoVenta, btnInventario, btnProveedores, 
            btnClientes, btnHistorialEquipos, 
            btnDashboard, btnGestionUsuarios,
            btnPanelKnijico
        };
        
        for (javax.swing.JButton b : todosLosBotones) {
            if (b != null) b.setEnabled(true);
        }
        
        // Apagamos (iluminamos) en el que estamos parados ahorita
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
    
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        // Ponemos el tema moderno
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
        // Usamos FlowLayout para apilar los menús colapsables
        panelMenu.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        // Logo y Perfil
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

        // --- INICIALIZAR TODOS LOS SUB-BOTONES ---
        btnIngresoEquipos = new javax.swing.JButton("Ingreso de Equipos");
        btnControlOrdenes = new javax.swing.JButton("Control de Órdenes");
        btnEntregaEquipos = new javax.swing.JButton("Entrega / Cobro");
        
        btnPuntoVenta = new javax.swing.JButton("Punto de Venta");
        btnInventario = new javax.swing.JButton("Inventario");
        btnProveedores = new javax.swing.JButton("Proveedores");
        
        btnClientes = new javax.swing.JButton("Directorio Clientes");
        btnHistorialEquipos = new javax.swing.JButton("Historial Equipos");
        
        btnDashboard = new javax.swing.JButton("Dashboard");
        btnGestionUsuarios = new javax.swing.JButton("Usuarios del Sistema");
        
        // --- INICIALIZAR BOTÓN KNIJICO CON DISEÑO DE MARCA ---
        btnPanelKnijico = new javax.swing.JButton("Admin. Pantallas Knijico");
        btnPanelKnijico.setPreferredSize(new java.awt.Dimension(250, 50)); 
        btnPanelKnijico.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
        btnPanelKnijico.setForeground(java.awt.Color.WHITE);
        btnPanelKnijico.setBackground(new java.awt.Color(243, 156, 18)); // Naranja Knijico
        btnPanelKnijico.setBorderPainted(false);
        btnPanelKnijico.setFocusPainted(false);
        btnPanelKnijico.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Hover personalizado para el botón naranja
        btnPanelKnijico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if(btnPanelKnijico.isEnabled()) btnPanelKnijico.setBackground(new java.awt.Color(211, 84, 0)); // Naranja oscuro hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPanelKnijico.setBackground(new java.awt.Color(243, 156, 18));
            }
        });

        // --- CREAR LAS CATEGORÍAS (ACORDEONES) ---
        // Usamos texto limpio en lugar de emojis problemáticos
        javax.swing.JPanel catTaller = crearMenuColapsable("[ Taller y Servicios ]", true, btnIngresoEquipos, btnControlOrdenes, btnEntregaEquipos);
        javax.swing.JPanel catVentas = crearMenuColapsable("[ Ventas e Inventario ]", false, btnPuntoVenta, btnInventario, btnProveedores);
        javax.swing.JPanel catDirectorios = crearMenuColapsable("[ Directorios ]", false, btnClientes, btnHistorialEquipos);
        
        
        panelCategoriaAdmin = crearMenuColapsable("[ Administración ]", false, btnDashboard, btnGestionUsuarios);
        panelMenu.add(panelCategoriaAdmin);
        panelMenu.add(catTaller);
        panelMenu.add(catVentas);
        // ... (código de los acordeones)
        panelMenu.add(catDirectorios);
        
        javax.swing.JLabel separadorKnijico = new javax.swing.JLabel();
        separadorKnijico.setPreferredSize(new java.awt.Dimension(250, 30)); 
        panelMenu.add(separadorKnijico);
        
        panelMenu.add(btnPanelKnijico);

        // Botón Salir (Al final)
        btnSalir = new javax.swing.JButton("Cerrar Sesión");
        btnSalir.setPreferredSize(new java.awt.Dimension(250, 50)); 
        btnSalir.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        btnSalir.setForeground(new java.awt.Color(231, 76, 60)); // Rojo
        btnSalir.setBackground(new java.awt.Color(44, 62, 80));
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalir.addActionListener(this::btnSalirActionPerformed);
        
        javax.swing.JLabel empujador = new javax.swing.JLabel();
        empujador.setPreferredSize(new java.awt.Dimension(250, 40)); 
        panelMenu.add(empujador);
        panelMenu.add(btnSalir);

        // Contenedor central
        panelContenedor.setLayout(new java.awt.BorderLayout());
        panelContenedor.setBackground(new java.awt.Color(240, 244, 248));

        this.add(panelMenu, java.awt.BorderLayout.WEST);       
        this.add(panelContenedor, java.awt.BorderLayout.CENTER); 

        this.revalidate();
        this.repaint();
        
        asignarEventosBotones(); // Llamamos al asignador de clics
    }

    // --- NUEVOS MÉTODOS PARA EL ACORDEÓN ---
    private javax.swing.JPanel crearMenuColapsable(String titulo, boolean abiertoPorDefecto, javax.swing.JButton... subBotones) {
        javax.swing.JPanel contenedor = new javax.swing.JPanel(new java.awt.BorderLayout());
        contenedor.setOpaque(false);

        // Usamos un estilo más limpio para el botón principal de categoría
        javax.swing.JButton btnCategoria = new javax.swing.JButton(titulo);
        btnCategoria.setPreferredSize(new java.awt.Dimension(250, 45)); // Ligeramente más delgado
        btnCategoria.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        // Color de texto sutil para la categoría
        btnCategoria.setForeground(new java.awt.Color(189, 195, 199)); 
        // Fondo que se funde con el menú lateral, no destaca tanto como antes
        btnCategoria.setBackground(new java.awt.Color(34, 49, 63)); 
        btnCategoria.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // Centrado se ve más como un separador
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
            altoTotal += 40; // Altura de cada sub-botón
        }
        panelSubBotones.setPreferredSize(new java.awt.Dimension(250, altoTotal));

        // Lógica de colapsar/expandir
        btnCategoria.addActionListener(e -> {
            boolean visible = panelSubBotones.isVisible();
            panelSubBotones.setVisible(!visible);
            // Ya no cambiamos texto, solo colapsa/expande visualmente
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
                if(btn.isEnabled()) btn.setBackground(new java.awt.Color(65, 85, 105)); // Hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new java.awt.Color(44, 62, 80));
            }
        });
    }
    // =========================================================
    // ACCIONES DE NAVEGACIÓN (LOS CLICS)
    // =========================================================
    private void btnIngresoActionPerformed(java.awt.event.ActionEvent evt) {                                             
        mostrarPanel(new PanelIngresoEquipos());
        seleccionarBotonMenu(btnIngresoEquipos);
    } 
    
    private void btnKnijicoActionPerformed(java.awt.event.ActionEvent evt) {                                            
        mostrarPanel(new PanelKnijico()); // Abrirá tu nuevo panel
        seleccionarBotonMenu(btnPanelKnijico);
    }
    
    private void btnEntregaCobroActionPerformed(java.awt.event.ActionEvent evt) {                                            
        mostrarPanel(new PanelPuntoVenta("TALLER")); // <--- Agregamos "TALLER"
        seleccionarBotonMenu(btnEntregaEquipos); 
    }

    private void btnListadoActionPerformed(java.awt.event.ActionEvent evt) {                                            
        mostrarPanel(new PanelListadoOrdenes());
        seleccionarBotonMenu(btnControlOrdenes);
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
    
    private void btnPuntoVentaActionPerformed(java.awt.event.ActionEvent evt) {                                            
        mostrarPanel(new PanelPuntoVenta("MOSTRADOR")); // <--- Agregamos "MOSTRADOR"
        seleccionarBotonMenu(btnPuntoVenta);        
    }

    private void btnUsuariosActionPerformed(java.awt.event.ActionEvent evt) {                                            
        javax.swing.JPasswordField pwd = new javax.swing.JPasswordField(15);
        pwd.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
        
        Object[] mensaje = {
            "Acceso Restringido.\nIngrese una contraseña de Administrador para continuar:\n\n", pwd
        };
        
        int opcion = javax.swing.JOptionPane.showConfirmDialog(this, mensaje, "Verificación de Seguridad", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        
        if (opcion == javax.swing.JOptionPane.OK_OPTION) {
            String passwordIngresada = new String(pwd.getPassword());
            
            // Verificamos en toda la BD si esa contraseña pertenece a algún Admin
            String nombreAdminEncontrado = verificarContrasenaAdminGlobal(passwordIngresada);
            
            if (nombreAdminEncontrado != null) {
                // ¡Éxito! Damos la bienvenida con su nombre
                javax.swing.JOptionPane.showMessageDialog(this, "¡Bienvenido al panel, " + nombreAdminEncontrado + "!", "Acceso Concedido", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                mostrarPanel(new PanelUsuarios());
                seleccionarBotonMenu(btnGestionUsuarios);
            } else {
                // Fallo
                javax.swing.JOptionPane.showMessageDialog(this, "Acceso denegado.\nLa contraseña es incorrecta o no pertenece a un Administrador.\n\nSolo los administradores pueden entrar a este módulo.", "Error de Seguridad", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // =========================================================
    // LÓGICA DE SEGURIDAD (LLAVE MAESTRA DE ADMINISTRADORES)
    // =========================================================
    private String verificarContrasenaAdminGlobal(String password) {
        String hashGenerado = "";
        try {
            // 1. Encriptar la contraseña ingresada a SHA-256
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
        
        // 2. Buscar en TODA la base de datos si ESA contraseña pertenece a un Admin
        String sql = "SELECT usuario FROM usuarios WHERE password_hash = ? AND rol = 'Administrador'";
        try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Solo pasamos el hash, no le decimos qué usuario buscar
            ps.setString(1, hashGenerado);
            
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si encuentra coincidencia, retornamos el nombre del Admin dueño de esa clave
                    return rs.getString("usuario"); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar BD por contraseña global: " + e.getMessage());
        }
        
        // Si llega aquí, es porque no encontró a ningún admin con esa contraseña
        return null;
    }
    
    private void asignarEventosBotones() {
        // Enlazar los botones con tus paneles existentes
        btnIngresoEquipos.addActionListener(this::btnIngresoActionPerformed);
        btnControlOrdenes.addActionListener(this::btnListadoActionPerformed);
        btnEntregaEquipos.addActionListener(this::btnEntregaCobroActionPerformed); // <-- Conectado al nuevo método
        
        btnPuntoVenta.addActionListener(this::btnPuntoVentaActionPerformed);
        btnInventario.addActionListener(this::btnInventarioActionPerformed);
        
        btnClientes.addActionListener(this::btnClientesActionPerformed);
        
        btnDashboard.addActionListener(this::btnEstadisticasActionPerformed);
        btnGestionUsuarios.addActionListener(this::btnUsuariosActionPerformed);
        
        btnPanelKnijico.addActionListener(this::btnKnijicoActionPerformed);

        // Función para los módulos que aún no existen
        java.awt.event.ActionListener accionProximamente = e -> 
            javax.swing.JOptionPane.showMessageDialog(this, "🛠️ Este módulo está en desarrollo. ¡Próximamente!", "Sairtech Dev", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        
        btnProveedores.addActionListener(accionProximamente);
        btnHistorialEquipos.addActionListener(accionProximamente);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel panelContenedor;
    // End of variables declaration//GEN-END:variables
    // --- VARIABLES DEL NUEVO MENÚ COLAPSABLE ---
    // Módulo 1
    private javax.swing.JButton btnIngresoEquipos;
    private javax.swing.JButton btnControlOrdenes;
    private javax.swing.JButton btnEntregaEquipos;
    // Módulo 2
    private javax.swing.JButton btnPuntoVenta;
    private javax.swing.JButton btnInventario;
    private javax.swing.JButton btnProveedores;
    // Módulo 3
    private javax.swing.JButton btnClientes;
    private javax.swing.JButton btnHistorialEquipos;
    private javax.swing.JButton btnPanelKnijico;
    // Módulo 4 (Admin)
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnGestionUsuarios;
    
    // Contenedor clave para la seguridad
    private javax.swing.JPanel panelCategoriaAdmin;
    
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {
        int respuesta = javax.swing.JOptionPane.showConfirmDialog(this, 
            "¿Desea cerrar la sesión actual?", "Cerrar Sesión", 
            javax.swing.JOptionPane.YES_NO_OPTION, 
            javax.swing.JOptionPane.QUESTION_MESSAGE);

        if (respuesta == javax.swing.JOptionPane.YES_OPTION) {
            // Ocultamos la ventana principal
            this.setVisible(false);
            
            // Creamos una nueva ventana desde cero
            VentanaPrincipal nuevaVentana = new VentanaPrincipal();
            nuevaVentana.setVisible(true);
            
            // Destruimos la ventana vieja para liberar memoria
            this.dispose();
        }
    }
    
    public String getNombreUsuarioActivo() {
        return lblPerfil.getText().split(" \\| ")[0];
    }

    public int getIdUsuarioActivo() {
        return idUsuarioActivo;
    }

    public void setIdUsuarioActivo(int idUsuarioActivo) {
        this.idUsuarioActivo = idUsuarioActivo;
    }
    
    public javax.swing.JButton getBtnEntregaEquipos() {
        return btnEntregaEquipos;
    }
        
    // --- Estas dos faltaban ---
    private javax.swing.JLabel lblPerfil;
    private javax.swing.JButton btnSalir;
    private javax.swing.JPanel panelMenu;
}
