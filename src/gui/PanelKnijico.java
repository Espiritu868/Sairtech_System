package gui;

import dao.KnijicoDAO;
import modelo.PantallaKnijico;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelKnijico extends JPanel {

    // --- PALETA DE COLORES KNIJICO ---
    private static final Color COLOR_FONDO = new Color(255, 245, 235); // Crema muy pálido
    private static final Color COLOR_NARANJA = new Color(243, 156, 18); // Naranja marca
    private static final Color COLOR_MOCA = new Color(121, 85, 72); // Texto oscuro elegante
    private static final Color COLOR_AZUL = new Color(52, 152, 219); // Detalles técnicos

    private KnijicoDAO dao;

    // Componentes de Lotes (Izquierda)
    private JTextField txtNuevoLote;
    private JComboBox<ComboItem> cmbFiltroLote;

    // Componentes de Tabla (Centro)
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JCheckBox chkVerOcultas;

    // Componentes de Registro (Derecha)
    private JComboBox<ComboItem> cmbLoteRegistro;
    private JTextField txtModelo;
    private JTextField txtCosto;
    private JTextField txtPrecioCliente;
    private JTextField txtPrecioTecnico;
    private JTextField txtStock;

    public PanelKnijico() {
        dao = new KnijicoDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. BANNER SUPERIOR
        JLabel lblTitulo = new JLabel(" ADMINISTRACIÓN DE INVENTARIO KNIJICO", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBackground(COLOR_NARANJA);
        lblTitulo.setOpaque(true);
        lblTitulo.setPreferredSize(new Dimension(0, 50));
        add(lblTitulo, BorderLayout.NORTH);

        // Construir las 3 zonas
        add(construirPanelLotes(), BorderLayout.WEST);
        add(construirPanelCentro(), BorderLayout.CENTER);
        add(construirPanelRegistro(), BorderLayout.EAST);

        // Cargar datos iniciales
        refrescarLotes();
        refrescarTabla();
    }

    // =========================================================
    // ZONA 1: LOTES (IZQUIERDA)
    // =========================================================
    private JPanel construirPanelLotes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 0, 5, 0);
        gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;

        JLabel lblTit = new JLabel("Gestión de Lotes");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(COLOR_NARANJA);
        panel.add(lblTit, gbc);

        gbc.gridy++; panel.add(new JLabel("Nombre del Lote (Ej: Lote 1):"), gbc);
        txtNuevoLote = new JTextField();
        txtNuevoLote.setPreferredSize(new Dimension(0, 35));
        txtNuevoLote.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy++; panel.add(txtNuevoLote, gbc);

        JButton btnCrear = new JButton("Crear Lote");
        btnCrear.setBackground(COLOR_NARANJA); btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCrear.setFocusPainted(false);
        btnCrear.addActionListener(e -> crearLote());
        gbc.gridy++; gbc.insets = new Insets(10, 0, 30, 0);
        panel.add(btnCrear, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(new JLabel("Filtrar Tabla por Lote:"), gbc);
        cmbFiltroLote = new JComboBox<>();
        cmbFiltroLote.setPreferredSize(new Dimension(0, 35));
        cmbFiltroLote.addActionListener(e -> refrescarTabla());
        gbc.gridy++; panel.add(cmbFiltroLote, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    // =========================================================
    // ZONA 2: TABLA MAESTRA (CENTRO)
    // =========================================================
    private JPanel construirPanelCentro() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Barra de búsqueda superior
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setOpaque(false);
        
        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(0, 35));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { refrescarTabla(); }
        });
        
        chkVerOcultas = new JCheckBox("Ver Pantallas Ocultas");
        chkVerOcultas.setOpaque(false);
        chkVerOcultas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkVerOcultas.setForeground(COLOR_MOCA);
        chkVerOcultas.addActionListener(e -> refrescarTabla());

        panelBusqueda.add(new JLabel("🔍 Buscar Modelo: "), BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(chkVerOcultas, BorderLayout.EAST);

        // Tabla
        String[] columnas = {"ID", "Lote", "Modelo", "Stock", "Costo", "P. Cliente", "P. Técnico", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setRowHeight(30);
        tablaInventario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaInventario.getTableHeader().setBackground(new Color(255, 228, 196)); // Naranja muy suave
        
        tablaInventario.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaInventario.getColumnModel().getColumn(2).setPreferredWidth(200);

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.getViewport().setBackground(Color.WHITE);

        // Botón Ocultar abajo
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setOpaque(false);
        JButton btnOcultar = new JButton("Ocultar / Restaurar Seleccionada");
        btnOcultar.setBackground(COLOR_MOCA); btnOcultar.setForeground(Color.WHITE);
        btnOcultar.setFocusPainted(false);
        btnOcultar.addActionListener(e -> alternarEstadoPantalla());
        panelSur.add(btnOcultar);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================
    // ZONA 3: REGISTRO DE PANTALLAS (DERECHA)
    // =========================================================
    private JPanel construirPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 2, 0);
        gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;

        JLabel lblTit = new JLabel("Agregar Pantalla");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setForeground(COLOR_AZUL); 
        gbc.insets = new Insets(0, 0, 35, 0);
        panel.add(lblTit, gbc);

        gbc.insets = new Insets(5, 0, 2, 0);
        panel.add(new JLabel("Asignar al Lote:"), gbc);
        cmbLoteRegistro = new JComboBox<>();
        cmbLoteRegistro.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(cmbLoteRegistro, gbc);

        gbc.gridy++; panel.add(new JLabel("Modelo:"), gbc); 
        txtModelo = new JTextField(); txtModelo.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtModelo, gbc);

        gbc.gridy++; panel.add(new JLabel("Precio Costo (L.):"), gbc);
        txtCosto = new JTextField(); txtCosto.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtCosto, gbc);

        gbc.gridy++; panel.add(new JLabel("Precio Cliente Final (L.):"), gbc);
        txtPrecioCliente = new JTextField(); txtPrecioCliente.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtPrecioCliente, gbc);

        gbc.gridy++; panel.add(new JLabel("Precio Técnico (L.):"), gbc);
        txtPrecioTecnico = new JTextField(); txtPrecioTecnico.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtPrecioTecnico, gbc);

        gbc.gridy++; panel.add(new JLabel("Stock Inicial:"), gbc);
        txtStock = new JTextField(); txtStock.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtStock, gbc);

        JButton btnGuardar = new JButton("Guardar en Inventario");
        btnGuardar.setBackground(COLOR_AZUL); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarPantalla());
        gbc.gridy++; gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(btnGuardar, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    // =========================================================
    // MÉTODOS DE LÓGICA Y CONEXIÓN A BD
    // =========================================================

    private void refrescarLotes() {
        cmbFiltroLote.removeAllItems();
        cmbLoteRegistro.removeAllItems();
        
        cmbFiltroLote.addItem(new ComboItem(0, "Todos los Lotes"));
        
        List<Object[]> lotes = dao.obtenerLotesActivos();
        for (Object[] lote : lotes) {
            ComboItem item = new ComboItem((int)lote[0], lote[1].toString());
            cmbFiltroLote.addItem(item);
            cmbLoteRegistro.addItem(item);
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        
        String busqueda = txtBuscar != null ? txtBuscar.getText().trim() : "";
        boolean verOcultas = chkVerOcultas != null && chkVerOcultas.isSelected();
        int idLote = 0;
        
        if (cmbFiltroLote != null && cmbFiltroLote.getSelectedItem() != null) {
            idLote = ((ComboItem) cmbFiltroLote.getSelectedItem()).getId();
        }

        List<Object[]> pantallas = dao.listarPantallas(busqueda, verOcultas, idLote);
        for (Object[] p : pantallas) {
            modeloTabla.addRow(new Object[]{
                p[0], p[1], p[2], p[6], "L. " + p[3], "L. " + p[4], "L. " + p[5], p[7]
            });
        }
    }
    

    private void crearLote() {
        String nombre = txtNuevoLote.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para el lote.");
            return;
        }
        if (dao.crearLote(nombre)) {
            JOptionPane.showMessageDialog(this, "Lote creado exitosamente.");
            txtNuevoLote.setText("");
            refrescarLotes();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear lote.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarPantalla() {
        try {
            if (cmbLoteRegistro.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe crear y seleccionar un lote primero.");
                return;
            }
            
            int idLote = ((ComboItem) cmbLoteRegistro.getSelectedItem()).getId();
            String modelo = txtModelo.getText().trim().toUpperCase();
            double costo = Double.parseDouble(txtCosto.getText().trim());
            double pCliente = Double.parseDouble(txtPrecioCliente.getText().trim());
            double pTecnico = Double.parseDouble(txtPrecioTecnico.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (modelo.isEmpty()) throw new Exception("El modelo no puede estar vacío");

            if (dao.registrarPantalla(idLote, modelo, costo, pCliente, pTecnico, stock)) {
                JOptionPane.showMessageDialog(this, "Pantalla guardada en el lote.");
                txtModelo.setText(""); txtCosto.setText(""); 
                txtPrecioCliente.setText(""); txtPrecioTecnico.setText(""); txtStock.setText("");
                refrescarTabla();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique que los precios y el stock sean números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void alternarEstadoPantalla() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una pantalla de la tabla.");
            return;
        }
        
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        String estadoActual = modeloTabla.getValueAt(fila, 7).toString();
        
        if (estadoActual.equals("Activo")) {
            dao.ocultarPantalla(id);
        } else {
            dao.restaurarPantalla(id);
        }
        refrescarTabla();
    }

    // Clase auxiliar para guardar el ID invisible en los ComboBox
    class ComboItem {
        private int id;
        private String name;
        public ComboItem(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        @Override public String toString() { return name; }
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
