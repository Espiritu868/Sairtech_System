package gui;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class PanelLogin extends javax.swing.JPanel {

    private List<Burbuja> burbujas = new ArrayList<>();
    private Timer timerAnimacion;
    private int mouseX = -1000; // Lo empezamos fuera de la pantalla
    private int mouseY = -1000;
    
    public PanelLogin() {
        initComponents();
        aplicarDisenoLogin();
        iniciarMotorBurbujas();
        Set<AWTKeyStroke> teclas = new HashSet<>(
                getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS)
        );
        teclas.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, teclas);
    }
                   

    @SuppressWarnings("unchecked")
    private class Burbuja {
        double x, y;
        double radio;
        double dx, dy;
        Color color;

        public Burbuja() {
            radio = Math.random() * 30 + 10;
            color = new Color(52, 152, 219, (int)(Math.random() * 40 + 20));
            
            x = Math.random() * 1920;
            y = Math.random() * 1080;
            
            dx = (Math.random() - 0.5) * 1.5;
            dy = (Math.random() - 0.5) * 1.5;
        }

        public void mover(int anchoPantalla, int altoPantalla, int mX, int mY) {
            if (anchoPantalla == 0) return;

            x += dx;
            y += dy;

            // Rebotar contra las paredes
            if (x - radio < 0 || x + radio > anchoPantalla) dx *= -1;
            if (y - radio < 0 || y + radio > altoPantalla) dy *= -1;

            // Calculamos qué tan cerca está la burbuja del cursor
            double distanciaAlMouse = Math.hypot(x - mX, y - mY);
            
            if (distanciaAlMouse < 150) { // Si el mouse se acerca a menos de 150px
                double fuerzaRepulsion = (150 - distanciaAlMouse) / 100.0;
                dx += (x - mX) / distanciaAlMouse * fuerzaRepulsion;
                dy += (y - mY) / distanciaAlMouse * fuerzaRepulsion;
                
                double velocidad = Math.hypot(dx, dy);
                if (velocidad > 6) {
                    dx = (dx / velocidad) * 6;
                    dy = (dy / velocidad) * 6;
                }
            } else {
                double velocidad = Math.hypot(dx, dy);
                if (velocidad > 1.5) {
                    dx *= 0.95;
                    dy *= 0.95;
                }
            }
        }
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnEntrar = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();
        txtUsuario = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("BIENVENIDO");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 10, 520, -1));

        btnEntrar.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btnEntrar.setText("Entrar");
        btnEntrar.addActionListener(this::btnEntrarActionPerformed);
        jPanel2.add(btnEntrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 380, 120, 50));

        txtPassword.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtPassword.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPasswordKeyPressed(evt);
            }
        });
        jPanel2.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 280, 410, -1));

        txtUsuario.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtUsuario.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel2.add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 190, 410, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Contraseña");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 250, 410, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Nombre de Usuario");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 160, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 490));

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

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrarActionPerformed
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword()); 

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dao.UsuarioDAO daoAcceso = new dao.UsuarioDAO();
        
        String rolUsuario = daoAcceso.validarLogin(usuario, password);
        
        if (!rolUsuario.equals("ERROR")) {
            
            int idDelTecnico = daoAcceso.obtenerIdPorNombre(usuario);
            
            JOptionPane.showMessageDialog(this, "¡Bienvenido al sistema, " + rolUsuario + "!", "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);

            Window ventana = SwingUtilities.getWindowAncestor(this);
            
            if (ventana instanceof VentanaPrincipal) {
                VentanaPrincipal vp = (VentanaPrincipal) ventana;
              
                vp.setIdUsuarioActivo(idDelTecnico); 
                
                vp.habilitarSistema(rolUsuario, usuario);
            }
            
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText(""); 
            txtUsuario.requestFocus(); 
        }
    }//GEN-LAST:event_btnEntrarActionPerformed
    
    private void aplicarDisenoLogin() {
        this.removeAll();
        this.setLayout(new GridBagLayout()); 
        this.setOpaque(false);

        JPanel cajaLogin = new JPanel(new GridBagLayout());
        cajaLogin.setBackground(Color.WHITE);
        cajaLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(40, 50, 40, 50) 
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);

        // Título
        JLabel lblTitulo = new JLabel("BIENVENIDO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(44, 62, 80));
        cajaLogin.add(lblTitulo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 30, 0); 
        JLabel lblSub = new JLabel("Sistema Operativo Sairtech", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.GRAY);
        cajaLogin.add(lblSub, gbc);

        // Usuario
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        JLabel lblUser = new JLabel("Nombre de Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(new Color(44, 62, 80));
        cajaLogin.add(lblUser, gbc);

        gbc.gridy++;
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsuario.setPreferredSize(new Dimension(300, 40));
        cajaLogin.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 5, 0);
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(new Color(44, 62, 80));
        cajaLogin.add(lblPass, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 30, 0); 
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        cajaLogin.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        btnEntrar.setText("Ingresar al Sistema");
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnEntrar.setBackground(new Color(52, 152, 219)); 
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setPreferredSize(new Dimension(300, 45));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        cajaLogin.add(btnEntrar, gbc);

        this.add(cajaLogin);
        this.revalidate();
        this.repaint();
    }
    private void iniciarMotorBurbujas() {
        
        for (int i = 0; i < 300; i++) {
            burbujas.add(new Burbuja());
        }
        
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                mouseX = -1000;
                mouseY = -1000;
            }
        });

        timerAnimacion = new javax.swing.Timer(16, e -> {
            for (Burbuja b : burbujas) {
                b.mover(getWidth(), getHeight(), mouseX, mouseY);
            }
            repaint(); 
        });
        timerAnimacion.start();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new java.awt.Color(240, 244, 248));
        g2.fillRect(0, 0, getWidth(), getHeight());

        for (Burbuja b : burbujas) {
            g2.setColor(b.color);
            // fillOval dibuja círculos.
            g2.fillOval((int) (b.x - b.radio), (int) (b.y - b.radio), (int) (b.radio * 2), (int) (b.radio * 2));
        }
    }
    

    
    private void txtPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPasswordKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            btnEntrar.doClick();
        }
    }//GEN-LAST:event_txtPasswordKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEntrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
