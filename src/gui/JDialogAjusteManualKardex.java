package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JDialogAjusteManualKardex extends JDialog {

    private int idProducto;
    private JTextField txtCantidad, txtReferencia;
    private JComboBox<String> cmbTipoAjuste;

    public JDialogAjusteManualKardex(int idProducto) {
        this.idProducto = idProducto;
        setModal(true); 
        setSize(480, 360); 
        setTitle("Datos Kardex");
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        panelContenedor.setBackground(Color.WHITE);
        panelContenedor.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("Registrar Ajuste Manual");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(44, 62, 80));
        panelContenedor.add(lblTitulo, gbc);
        
        gbc.gridy++; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDesc.setForeground(Color.DARK_GRAY);
        panelContenedor.add(lblDesc, gbc);
        
        txtReferencia = new JTextField("AJUSTE MANUAL DEL KARDEX");
        txtReferencia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtReferencia.setPreferredSize(new Dimension(0, 38));
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelContenedor.add(txtReferencia, gbc);
        
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0.3;
        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTipo.setForeground(Color.DARK_GRAY);
        panelContenedor.add(lblTipo, gbc);
        
        cmbTipoAjuste = new JComboBox<>(new String[]{"Incremento", "Decremento"});
        cmbTipoAjuste.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTipoAjuste.setPreferredSize(new Dimension(0, 38));
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelContenedor.add(cmbTipoAjuste, gbc);
        
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0.3;
        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCant.setForeground(Color.DARK_GRAY);
        panelContenedor.add(lblCant, gbc);
        
        txtCantidad = new JTextField("1");
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCantidad.setPreferredSize(new Dimension(0, 38));
        gbc.gridx = 1; gbc.weightx = 0.7;
        panelContenedor.add(txtCantidad, gbc);
        
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(20, 5, 5, 5);
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setBackground(new Color(245, 245, 245));
        btnCerrar.setForeground(new Color(100, 100, 100));
        btnCerrar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btnCerrar.setPreferredSize(new Dimension(90, 38));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> this.dispose());
        
        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(41, 128, 185)); 
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setPreferredSize(new Dimension(150, 38));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // --- MAGIA CON CONTRASEÑA MODIFICADA PARA AMBOS ROLES ---
        btnGuardar.addActionListener(e -> {
            String referencia = txtReferencia.getText().trim();
            String tipo = cmbTipoAjuste.getSelectedItem().toString();
            
            if (referencia.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Debe ingresar una descripción/motivo.");
                return;
            }
            
            int cantidad = 0;
            try {
                cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(this, "Ingrese una cantidad entera mayor a 0.");
                return;
            }

            // PEDIR CONTRASEÑA ANTES DE GUARDAR
            javax.swing.JPasswordField pwd = new javax.swing.JPasswordField(15);
            pwd.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 16));
            Object[] mensaje = {
                "Autorización Requerida.\nIngrese su PIN / Contraseña para firmar el movimiento del Kardex:\n\n", pwd
            };
            
            int opcion = javax.swing.JOptionPane.showConfirmDialog(this, mensaje, "Firma Electrónica", javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
            
            if (opcion == javax.swing.JOptionPane.OK_OPTION) {
                String passwordIngresada = new String(pwd.getPassword());
                int idUsuarioEncontrado = verificarContrasenaUsuario(passwordIngresada);
                
                if (idUsuarioEncontrado != -1) {
                    // Contraseña Correcta (Admin o Técnico): Hacemos el movimiento
                    dao.KardexDAO kardexDao = new dao.KardexDAO();
                    boolean exito = kardexDao.registrarAjusteManual(this.idProducto, idUsuarioEncontrado, referencia, tipo, cantidad);
                    
                    if (exito) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Ajuste manual registrado exitosamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        this.dispose(); 
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(this, "Error al registrar el Kardex.\nVerifique que la cantidad no deje el stock en negativo.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Acceso denegado.\nLa contraseña es incorrecta o no existe en el sistema.", "Error de Seguridad", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        panelBotones.add(btnCerrar);
        panelBotones.add(javax.swing.Box.createHorizontalStrut(10));
        panelBotones.add(btnGuardar);
        panelContenedor.add(panelBotones, gbc);
        
        this.add(panelContenedor);
    }
    
    // --- FUNCIÓN PARA VERIFICAR CONTRASEÑA (Ahora acepta cualquier usuario activo) ---
    private int verificarContrasenaUsuario(String password) {
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
            return -1;
        }
        
        // CORRECCIÓN: Quitamos el "AND rol = 'Administrador'"
        String sql = "SELECT id_usuario FROM usuarios WHERE password_hash = ?";
        try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashGenerado);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_usuario"); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error al consultar BD por contraseña: " + e.getMessage());
        }
        return -1;
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
