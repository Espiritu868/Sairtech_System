package gui;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.imageio.ImageIO;
import modelo.Despiece;
import dao.DespieceDAO;
import utilidades.ServidorCamara;

public class PanelDespiece extends JPanel {

    private JTextField txtModelo;
    private JComboBox<String> cmbPlaca, cmbLcd, cmbBateria, cmbMarco, cmbCarga, cmbCamaras;
    private JTextArea txtComentarios;
    private JTextField txtBuscar;
    private JTable tablaDespiece;
    
    // Contenedores estáticos
    private static JLabel staticLblImg1, staticLblImg2, staticLblImg3, staticLblQR;
    private static ServidorCamara servidor;
    private static int idActivo = -1; 
    
    private JButton btnGuardar, btnActualizar, btnLimpiar, btnEliminar;

    public PanelDespiece() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250)); 
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setOpaque(false);
        JLabel lblTitulo = new JLabel("Banco de Despiece y Donantes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(44, 62, 80));
        JLabel lblSub = new JLabel("  Gestión integral de partes y evidencia fotográfica");
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSub.setForeground(new Color(127, 140, 141));
        panelTitulo.add(lblTitulo, BorderLayout.WEST);
        panelTitulo.add(lblSub, BorderLayout.CENTER);
        
        add(panelTitulo, BorderLayout.NORTH);

        add(construirFormulario(), BorderLayout.EAST);
        add(construirPanelTabla(), BorderLayout.CENTER);

        cargarTabla("");
        actualizarQRVisual(); 
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(530, 0)); 
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 225, 231), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;

        txtModelo = new JTextField(); txtModelo.setPreferredSize(new Dimension(0, 32));
        txtModelo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        String[] opcPlaca = {"Intacta / Funcional", "En Corto / Dañada", "Bloqueada", "Extraída / No tiene"};
        String[] opcLcd = {"Intacta", "Estrellada (Táctil funciona)", "Manchada / Líneas", "Extraída"};
        String[] opcBateria = {"Buena", "Degradada", "Inflada", "Extraída"};
        String[] opcMarco = {"Intacto", "Desgastado / Rayado", "Doblado", "Extraído"};
        String[] opcCarga = {"Funcional", "Dañado / Falso", "Extraído"};
        String[] opcCamaras = {"Intactas", "Cristal Roto", "Dañadas", "Extraídas"};

        cmbPlaca = crearComboBox(opcPlaca); cmbLcd = crearComboBox(opcLcd);
        cmbBateria = crearComboBox(opcBateria); cmbMarco = crearComboBox(opcMarco);
        cmbCarga = crearComboBox(opcCarga); cmbCamaras = crearComboBox(opcCamaras);

        txtComentarios = new JTextArea(3, 20);
        txtComentarios.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtComentarios.setLineWrap(true); txtComentarios.setWrapStyleWord(true);
        txtComentarios.setBorder(BorderFactory.createLineBorder(new Color(218, 225, 231)));
        JScrollPane scrollComentarios = new JScrollPane(txtComentarios);
        scrollComentarios.setBorder(BorderFactory.createEmptyBorder());

        gbc.gridwidth = 2;
        panel.add(crearEtiqueta("Modelo del Dispositivo:"), gbc);
        gbc.gridy++; panel.add(txtModelo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; panel.add(crearEtiqueta("Placa Base:"), gbc);
        gbc.gridx = 1; panel.add(crearEtiqueta("Pantalla LCD:"), gbc);
        
        gbc.gridx = 0; gbc.gridy++; panel.add(cmbPlaca, gbc);
        gbc.gridx = 1; panel.add(cmbLcd, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(crearEtiqueta("Batería:"), gbc);
        gbc.gridx = 1; panel.add(crearEtiqueta("Marco / Chasis:"), gbc);
        
        gbc.gridx = 0; gbc.gridy++; panel.add(cmbBateria, gbc);
        gbc.gridx = 1; panel.add(cmbMarco, gbc);

        gbc.gridx = 0; gbc.gridy++; panel.add(crearEtiqueta("Módulo de Carga:"), gbc);
        gbc.gridx = 1; panel.add(crearEtiqueta("Cámaras:"), gbc);
        
        gbc.gridx = 0; gbc.gridy++; panel.add(cmbCarga, gbc);
        gbc.gridx = 1; panel.add(cmbCamaras, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        panel.add(crearEtiqueta("Comentarios / Detalles extra:"), gbc);
        gbc.gridy++; panel.add(scrollComentarios, gbc);

        // --- SECCIÓN: EVIDENCIA FOTOGRÁFICA ---
        gbc.gridy++; gbc.insets = new Insets(15, 6, 5, 6);
        
        JPanel pTituloEvidencia = new JPanel(new BorderLayout());
        pTituloEvidencia.setOpaque(false);
        JLabel lblEvi = new JLabel("Evidencia Fotográfica:");
        lblEvi.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEvi.setForeground(new Color(41, 128, 185));
        
        // BOTÓN CARRUSEL
        // Busca esta línea y cámbiala por esta ruta exacta:
btnVerGaleria = new BotonPremium("/image/icono_galeria.png", "Ver Galería");
        btnVerGaleria.setBackground(new Color(155, 89, 182));
        btnVerGaleria.setForeground(Color.WHITE);
        btnVerGaleria.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerGaleria.setFocusPainted(false);
        btnVerGaleria.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerGaleria.addActionListener(e -> abrirGaleriaCarrusel());
        
        pTituloEvidencia.add(lblEvi, BorderLayout.WEST);
        pTituloEvidencia.add(btnVerGaleria, BorderLayout.EAST);
        
        panel.add(pTituloEvidencia, gbc);

        JPanel panelEvidencia = new JPanel(new BorderLayout(15, 0));
        panelEvidencia.setOpaque(false);
        panelEvidencia.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        JPanel pContenedorQR = new JPanel(new BorderLayout());
        pContenedorQR.setOpaque(false);
        staticLblQR = new JLabel("...", SwingConstants.CENTER);
        // Agrandamos el contenedor del QR
        staticLblQR.setPreferredSize(new Dimension(145, 145));
        staticLblQR.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        
        JLabel lblTextoQR = new JLabel("Escanear QR:", SwingConstants.CENTER);
        lblTextoQR.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTextoQR.setForeground(Color.GRAY);
        pContenedorQR.add(lblTextoQR, BorderLayout.NORTH);
        pContenedorQR.add(staticLblQR, BorderLayout.CENTER);

        JPanel panelFotos = new JPanel(new GridLayout(1, 3, 10, 0));
        panelFotos.setOpaque(false);
        
        staticLblImg1 = new JLabel("Vacío", SwingConstants.CENTER);
        staticLblImg2 = new JLabel("Vacío", SwingConstants.CENTER);
        staticLblImg3 = new JLabel("Vacío", SwingConstants.CENTER);
        
        panelFotos.add(crearCajaFotoConX(staticLblImg1));
        panelFotos.add(crearCajaFotoConX(staticLblImg2));
        panelFotos.add(crearCajaFotoConX(staticLblImg3));

        panelEvidencia.add(pContenedorQR, BorderLayout.WEST);
        panelEvidencia.add(panelFotos, BorderLayout.CENTER);
        
        gbc.gridy++; gbc.insets = new Insets(0, 6, 6, 6);
        panel.add(panelEvidencia, gbc);

        // --- BOTONES INFERIORES ---
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 8, 8));
        panelBotones.setOpaque(false);

        btnGuardar = crearBoton("Guardar Equipo", new Color(46, 204, 113));
        btnGuardar.addActionListener(e -> guardar());
        
        btnActualizar = crearBoton("Actualizar Datos", new Color(52, 152, 219)); 
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> actualizar());
        
        btnEliminar = crearBoton("Eliminar Registro", new Color(231, 76, 60)); 
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminar());
        
        btnLimpiar = crearBoton("Limpiar Panel", new Color(149, 165, 166));
        btnLimpiar.addActionListener(e -> limpiar());

        panelBotones.add(btnGuardar); panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar); panelBotones.add(btnLimpiar);

        gbc.gridy++; gbc.insets = new Insets(15, 6, 5, 6); panel.add(panelBotones, gbc);
        gbc.gridy++; gbc.weighty = 1.0; panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.DARK_GRAY);
        return lbl;
    }

    private JComboBox<String> crearComboBox(String[] opciones) {
        JComboBox<String> cmb = new JComboBox<>(opciones);
        cmb.setPreferredSize(new Dimension(0, 32));
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmb.setBackground(Color.WHITE);
        return cmb;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }

    private JPanel crearCajaFotoConX(JLabel lblImagen) {
        JPanel pContenedor = new JPanel(new BorderLayout());
        pContenedor.setOpaque(false);
        pContenedor.setBorder(BorderFactory.createLineBorder(new Color(218, 225, 231), 2));

        lblImagen.setPreferredSize(new Dimension(100, 100)); // Ligeramente más grandes
        lblImagen.setForeground(new Color(189, 195, 199));
        lblImagen.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JButton btnX = new JButton("X");
        btnX.setBackground(new Color(231, 76, 60));
        btnX.setForeground(Color.WHITE);
        btnX.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btnX.setMargin(new Insets(0, 4, 0, 4));
        btnX.setFocusPainted(false);
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnX.addActionListener(e -> {
            if (idActivo == -1) return;
            
            int numImg = 0;
            if (lblImagen == staticLblImg1) numImg = 1;
            else if (lblImagen == staticLblImg2) numImg = 2;
            else if (lblImagen == staticLblImg3) numImg = 3;

            try {
                String ruta = "C:\\SairTech_System\\Evidencias\\Despiece_" + idActivo + "_Img" + numImg + ".jpg";
                File archivo = new File(ruta);
                if (archivo.exists()) { archivo.delete(); }
            } catch (Exception ex) { }

            lblImagen.setIcon(null);
            lblImagen.setText("Vacío");
            actualizarQRVisual(); 
        });

        JPanel pTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pTop.setOpaque(false);
        pTop.add(btnX);

        pContenedor.add(pTop, BorderLayout.NORTH);
        pContenedor.add(lblImagen, BorderLayout.CENTER);
        return pContenedor;
    }

    // --- NUEVO: CARRUSEL DE IMÁGENES ---
    private void abrirGaleriaCarrusel() {
        if (idActivo == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un equipo primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Buscamos qué fotos existen realmente en el disco
        List<String> rutasValidas = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String ruta = "C:\\SairTech_System\\Evidencias\\Despiece_" + idActivo + "_Img" + i + ".jpg";
            if (new File(ruta).exists()) {
                rutasValidas.add(ruta);
            }
        }

        if (rutasValidas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este equipo no tiene fotos guardadas.", "Sin evidencia", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Configuramos la ventana del carrusel
        JDialog dialogo = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Visor de Galería", true);
        dialogo.setSize(800, 650);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());
        dialogo.getContentPane().setBackground(new Color(30, 39, 46)); // Modo oscuro tipo cine

        JLabel lblImagenCentral = new JLabel("Cargando...", SwingConstants.CENTER);
        lblImagenCentral.setForeground(Color.WHITE);
        
        final int[] indice = {0}; // Para saber qué foto estamos viendo

        // Método interno para repintar la foto según el índice
        Runnable actualizarCarrusel = () -> {
            try {
                Image imgOriginal = ImageIO.read(new File(rutasValidas.get(indice[0])));
                int w = imgOriginal.getWidth(null);
                int h = imgOriginal.getHeight(null);
                
                // Calculamos la escala para que no se salga de la pantalla (máximo 700x550)
                double scale = Math.min(700.0 / w, 550.0 / h);
                Image imgEscalada = imgOriginal.getScaledInstance((int)(w * scale), (int)(h * scale), Image.SCALE_SMOOTH);
                
                lblImagenCentral.setIcon(new ImageIcon(imgEscalada));
                lblImagenCentral.setText("");
                dialogo.setTitle("Foto " + (indice[0] + 1) + " de " + rutasValidas.size() + " - Equipo ID: " + idActivo);
            } catch (Exception e) {
                lblImagenCentral.setIcon(null);
                lblImagenCentral.setText("Error al cargar la foto");
            }
        };

        // Botón Izquierdo
        JButton btnAnt = new JButton(" < ");
        btnAnt.setFont(new Font("Segoe UI", Font.BOLD, 30));
        btnAnt.setBackground(new Color(30, 39, 46));
        btnAnt.setForeground(Color.WHITE);
        btnAnt.setFocusPainted(false);
        btnAnt.setBorderPainted(false);
        btnAnt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnt.addActionListener(e -> {
            if (indice[0] > 0) {
                indice[0]--;
                actualizarCarrusel.run();
            }
        });

        // Botón Derecho
        JButton btnSig = new JButton(" > ");
        btnSig.setFont(new Font("Segoe UI", Font.BOLD, 30));
        btnSig.setBackground(new Color(30, 39, 46));
        btnSig.setForeground(Color.WHITE);
        btnSig.setFocusPainted(false);
        btnSig.setBorderPainted(false);
        btnSig.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSig.addActionListener(e -> {
            if (indice[0] < rutasValidas.size() - 1) {
                indice[0]++;
                actualizarCarrusel.run();
            }
        });

        // Cargamos la primera foto
        actualizarCarrusel.run();

        dialogo.add(btnAnt, BorderLayout.WEST);
        dialogo.add(lblImagenCentral, BorderLayout.CENTER);
        dialogo.add(btnSig, BorderLayout.EAST);
        
        dialogo.setVisible(true);
    }

    private static void cargarFotosDeDisco(int idEquipo) {
        staticLblImg1.setIcon(null); staticLblImg1.setText("Vacío");
        staticLblImg2.setIcon(null); staticLblImg2.setText("Vacío");
        staticLblImg3.setIcon(null); staticLblImg3.setText("Vacío");

        for (int i = 1; i <= 3; i++) {
            String ruta = "C:\\SairTech_System\\Evidencias\\Despiece_" + idEquipo + "_Img" + i + ".jpg";
            File archivo = new File(ruta);
            
            if (archivo.exists()) {
                try {
                    Image imgOriginal = ImageIO.read(archivo);
                    if (imgOriginal != null) {
                        ImageIcon icono = new ImageIcon(imgOriginal.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                        if (i == 1) { staticLblImg1.setIcon(icono); staticLblImg1.setText(""); }
                        else if (i == 2) { staticLblImg2.setIcon(icono); staticLblImg2.setText(""); }
                        else if (i == 3) { staticLblImg3.setIcon(icono); staticLblImg3.setText(""); }
                    }
                } catch (Exception e) { }
            }
        }
        actualizarQRVisual(); 
    }

    public static void actualizarQRVisual() {
        if (idActivo == -1) {
            staticLblQR.setIcon(null);
            staticLblQR.setText("<html><center>Seleccione un<br>equipo primero</center></html>");
            return;
        }
        
        if (getEspacioDisponible() == -1) {
            staticLblQR.setIcon(null);
            staticLblQR.setText("<html><center><font color='#27ae60' size='4'><b>✓ Fotos<br>Completas</b></font></center></html>");
            return;
        }

        try {
            if (servidor == null) servidor = new ServidorCamara();
            servidor.prepararRecepcion(String.valueOf(idActivo));

            String ipLocal = obtenerIpLocalReal();
            String url = "http://" + ipLocal + ":8080";
            
            // Agrandamos el QR a 140x140 para un escaneo más cómodo
            java.awt.image.BufferedImage qrImg = ServidorCamara.generarQR(url, 140, 140);
            
            staticLblQR.setText("");
            staticLblQR.setIcon(new ImageIcon(qrImg));
        } catch (Exception e) {
            staticLblQR.setIcon(null);
            staticLblQR.setText("Error QR");
        }
    }

    public static int getEspacioDisponible() {
        if (staticLblImg1.getIcon() == null) return 1;
        if (staticLblImg2.getIcon() == null) return 2;
        if (staticLblImg3.getIcon() == null) return 3;
        return -1; 
    }

    public static void imagenRecibida(String ruta, int numFotoAsignado) {
        cargarFotosDeDisco(idActivo);
    }

    private static String obtenerIpLocalReal() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual() || 
                    iface.getDisplayName().toLowerCase().contains("virtual") ||
                    iface.getName().toLowerCase().contains("vbox") ||
                    iface.getName().toLowerCase().contains("vmnet")) { continue; }
                java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    java.net.InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address) {
                        String ip = addr.getHostAddress();
                        if (ip.startsWith("192.168.") || ip.startsWith("10.") || ip.startsWith("172.")) return ip;
                    }
                }
            }
        } catch (Exception e) {}
        try { return java.net.InetAddress.getLocalHost().getHostAddress(); } catch (Exception ex) { return "127.0.0.1"; }
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);
        JPanel panelBuscador = new JPanel(new BorderLayout(10, 0)); panelBuscador.setOpaque(false);
        
        JLabel lblBuscar = new JLabel("🔍 Buscar Modelo:"); 
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBuscar.setForeground(new Color(44, 62, 80));
        
        txtBuscar = new JTextField(); txtBuscar.setPreferredSize(new Dimension(0, 38));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { cargarTabla(txtBuscar.getText().trim()); }
        });
        
        panelBuscador.add(lblBuscar, BorderLayout.WEST);
        panelBuscador.add(txtBuscar, BorderLayout.CENTER);

        tablaDespiece = new JTable(); 
        tablaDespiece.setRowHeight(32);
        tablaDespiece.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaDespiece.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaDespiece.getTableHeader().setBackground(new Color(236, 240, 241));
        tablaDespiece.setSelectionBackground(new Color(52, 152, 219));
        tablaDespiece.setSelectionForeground(Color.WHITE);
        
        tablaDespiece.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { seleccionarFila(); }
        });

        JScrollPane scroll = new JScrollPane(tablaDespiece);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(218, 225, 231), 2));
        scroll.getViewport().setBackground(Color.WHITE);
        
        panel.add(panelBuscador, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void cargarTabla(String filtro) {
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Modelo", "Placa", "Pantalla", "Batería", "Cámaras"}, 0){
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        List<Despiece> lista = new DespieceDAO().buscarPorModelo(filtro);
        for (Despiece d : lista) {
            modelo.addRow(new Object[]{ d.getIdDespiece(), d.getModeloDispositivo(), d.getPlacaBase(), d.getPantallaLcd(), d.getBateria(), d.getCamaras() });
        }
        tablaDespiece.setModel(modelo);
        if (tablaDespiece.getColumnCount() > 0) {
            tablaDespiece.getColumnModel().getColumn(0).setMaxWidth(40);
            tablaDespiece.getColumnModel().getColumn(1).setPreferredWidth(200);
        }
    }

    private void guardar() {
        if (txtModelo.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "El modelo es obligatorio"); return; }
        Despiece d = capturarDatos();
        if (new DespieceDAO().insertar(d)) {
            JOptionPane.showMessageDialog(this, "Equipo donante registrado.");
            limpiar(); cargarTabla("");
        }
    }

    private void actualizar() {
        if (idActivo == -1) return;
        Despiece d = capturarDatos();
        d.setIdDespiece(idActivo);
        if (new DespieceDAO().actualizar(d)) {
            JOptionPane.showMessageDialog(this, "Datos actualizados exitosamente.");
            limpiar(); cargarTabla("");
        }
    }
    
    private void eliminar() {
        if (idActivo == -1) return;
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este equipo donante?\nSe borrarán sus datos (las fotos físicas deben borrarse con la 'X').", "Aviso", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if(new DespieceDAO().eliminar(idActivo)) { limpiar(); cargarTabla(""); }
        }
    }

    private Despiece capturarDatos() {
        Despiece d = new Despiece();
        d.setModeloDispositivo(txtModelo.getText().trim());
        d.setPlacaBase(cmbPlaca.getSelectedItem().toString());
        d.setPantallaLcd(cmbLcd.getSelectedItem().toString());
        d.setBateria(cmbBateria.getSelectedItem().toString());
        d.setMarcoChasis(cmbMarco.getSelectedItem().toString());
        d.setModuloCarga(cmbCarga.getSelectedItem().toString());
        d.setCamaras(cmbCamaras.getSelectedItem().toString());
        d.setComentarios(txtComentarios.getText().trim());
        return d;
    }

    private void seleccionarFila() {
        int fila = tablaDespiece.getSelectedRow();
        if (fila >= 0) {
            idActivo = Integer.parseInt(tablaDespiece.getValueAt(fila, 0).toString());
            
            List<Despiece> lista = new DespieceDAO().buscarPorModelo("");
            for (Despiece d : lista) {
                if (d.getIdDespiece() == idActivo) {
                    txtModelo.setText(d.getModeloDispositivo());
                    cmbPlaca.setSelectedItem(d.getPlacaBase());
                    cmbLcd.setSelectedItem(d.getPantallaLcd());
                    cmbBateria.setSelectedItem(d.getBateria());
                    cmbMarco.setSelectedItem(d.getMarcoChasis());
                    cmbCarga.setSelectedItem(d.getModuloCarga());
                    cmbCamaras.setSelectedItem(d.getCamaras());
                    txtComentarios.setText(d.getComentarios());
                    
                    cargarFotosDeDisco(idActivo);
                    break;
                }
            }
            btnGuardar.setEnabled(false); btnActualizar.setEnabled(true); btnEliminar.setEnabled(true);
        }
    }

    private void limpiar() {
        idActivo = -1;
        txtModelo.setText(""); txtComentarios.setText("");
        cmbPlaca.setSelectedIndex(0); cmbLcd.setSelectedIndex(0); cmbBateria.setSelectedIndex(0);
        cmbMarco.setSelectedIndex(0); cmbCarga.setSelectedIndex(0); cmbCamaras.setSelectedIndex(0);
        
        if (staticLblImg1 != null) { staticLblImg1.setIcon(null); staticLblImg1.setText("Vacío"); }
        if (staticLblImg2 != null) { staticLblImg2.setIcon(null); staticLblImg2.setText("Vacío"); }
        if (staticLblImg3 != null) { staticLblImg3.setIcon(null); staticLblImg3.setText("Vacío"); }
        
        actualizarQRVisual(); 
        
        btnGuardar.setEnabled(true); btnActualizar.setEnabled(false); btnEliminar.setEnabled(false);
        tablaDespiece.clearSelection();
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
