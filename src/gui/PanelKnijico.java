package gui;

import dao.KnijicoDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelKnijico extends JPanel {

    private static final Color COLOR_FONDO = new Color(255, 245, 235); 
    private static final Color COLOR_NARANJA = new Color(243, 156, 18); 
    private static final Color COLOR_MOCA = new Color(121, 85, 72); 
    private static final Color COLOR_AZUL = new Color(52, 152, 219); 

    private KnijicoDAO dao;
    private int idPantallaSeleccionada = -1;

    private JTextField txtNuevoLote;
    private JComboBox<ComboItem> cmbFiltroLote;
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JCheckBox chkVerOcultas;

    private JComboBox<ComboItem> cmbLoteRegistro;
    private JTextField txtModelo, txtCaja, txtCosto, txtPrecioCliente, txtPrecioTecnico, txtStock;
    private JTextField txtCodigoBarras; 
    
    private JButton btnGuardar, btnActualizar, btnLimpiar;
    private JButton btnImprimirEtiqueta; // <--- NUEVO BOTÓN

    public PanelKnijico() {
        dao = new KnijicoDAO();
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel(" ADMINISTRACIÓN DE INVENTARIO KNIJICO", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBackground(COLOR_NARANJA);
        lblTitulo.setOpaque(true);
        lblTitulo.setPreferredSize(new Dimension(0, 50));
        add(lblTitulo, BorderLayout.NORTH);

        add(construirPanelLotes(), BorderLayout.WEST);
        add(construirPanelCentro(), BorderLayout.CENTER);
        add(construirPanelRegistro(), BorderLayout.EAST);

        refrescarLotes();
        refrescarTabla();
    }

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
        txtNuevoLote = new JTextField(); txtNuevoLote.setPreferredSize(new Dimension(0, 35));
        gbc.gridy++; panel.add(txtNuevoLote, gbc);

        JButton btnCrear = new JButton("Crear Lote");
        btnCrear.setBackground(COLOR_NARANJA); btnCrear.setForeground(Color.WHITE);
        btnCrear.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCrear.setFocusPainted(false);
        btnCrear.addActionListener(e -> crearLote());
        gbc.gridy++; gbc.insets = new Insets(10, 0, 30, 0); panel.add(btnCrear, gbc);

        gbc.gridy++; gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(new JLabel("Filtrar Tabla por Lote:"), gbc);
        cmbFiltroLote = new JComboBox<>(); cmbFiltroLote.setPreferredSize(new Dimension(0, 35));
        cmbFiltroLote.addActionListener(e -> refrescarTabla());
        gbc.gridy++; panel.add(cmbFiltroLote, gbc);

        gbc.gridy++; gbc.weighty = 1.0; panel.add(Box.createVerticalGlue(), gbc);
        return panel;
    }

    private JPanel construirPanelCentro() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); panel.setOpaque(false);
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0)); panelBusqueda.setOpaque(false);
        txtBuscar = new JTextField(); txtBuscar.setPreferredSize(new Dimension(0, 35));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { refrescarTabla(); }
        });
        
        chkVerOcultas = new JCheckBox("Ver Ocultas"); chkVerOcultas.setOpaque(false);
        chkVerOcultas.setForeground(COLOR_MOCA); chkVerOcultas.addActionListener(e -> refrescarTabla());

        panelBusqueda.add(new JLabel(" Buscar Modelo o Caja: "), BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);
        panelBusqueda.add(chkVerOcultas, BorderLayout.EAST);

        String[] col = {"ID", "Lote", "Modelo (Ubicación)", "Costo", "P. Cliente", "P. Técnico", "Stock", "Estado", "RawM", "Caja", "IdL", "Codigo"};
        modeloTabla = new DefaultTableModel(col, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setRowHeight(30);
        
        // Ocultar columnas técnicas (8 al 11)
        for (int i = 8; i <= 11; i++) {
            tablaInventario.getColumnModel().getColumn(i).setMinWidth(0);
            tablaInventario.getColumnModel().getColumn(i).setMaxWidth(0);
            tablaInventario.getColumnModel().getColumn(i).setWidth(0);
        }
        
        tablaInventario.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaInventario.getColumnModel().getColumn(2).setPreferredWidth(230);

        tablaInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { seleccionarPantalla(); }
        });

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT)); panelSur.setOpaque(false);
        JButton btnOcultar = new JButton("Ocultar / Restaurar Seleccionada");
        btnOcultar.setBackground(COLOR_MOCA); btnOcultar.setForeground(Color.WHITE);
        btnOcultar.setFocusPainted(false); btnOcultar.addActionListener(e -> alternarEstadoPantalla());
        panelSur.add(btnOcultar);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE); panel.setPreferredSize(new Dimension(320, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 2, 0);
        gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;

        JLabel lblTit = new JLabel("Datos de Pantalla"); lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18)); lblTit.setForeground(COLOR_AZUL); 
        gbc.insets = new Insets(0, 0, 20, 0); panel.add(lblTit, gbc);

        gbc.insets = new Insets(5, 0, 2, 0);
        panel.add(new JLabel("Asignar al Lote:"), gbc);
        cmbLoteRegistro = new JComboBox<>(); cmbLoteRegistro.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(cmbLoteRegistro, gbc);

        gbc.gridy++; panel.add(new JLabel("Modelo:"), gbc);
        txtModelo = new JTextField(); txtModelo.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtModelo, gbc);

        gbc.gridy++; panel.add(new JLabel("Número de Caja (Física):"), gbc);
        txtCaja = new JTextField("1"); txtCaja.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtCaja, gbc);

        gbc.gridy++; panel.add(new JLabel("Código de Barras (Opcional):"), gbc);
        txtCodigoBarras = new JTextField(); 
        txtCodigoBarras.setPreferredSize(new Dimension(0, 30));
        txtCodigoBarras.setFont(new Font("Consolas", Font.BOLD, 14));
        txtCodigoBarras.setBackground(new Color(255, 255, 220));
        gbc.gridy++; panel.add(txtCodigoBarras, gbc);

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

        btnGuardar = new JButton("Guardar Nueva");
        btnGuardar.setBackground(COLOR_AZUL); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarPantalla());
        
        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(46, 204, 113)); btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setEnabled(false); btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> actualizarDatos());

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(189, 195, 199)); btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // --- BOTÓN DE IMPRIMIR ETIQUETA ---
        btnImprimirEtiqueta = new JButton("Imprimir Etiqueta");
        btnImprimirEtiqueta.setBackground(new Color(52, 73, 94)); // Gris oscuro elegante
        btnImprimirEtiqueta.setForeground(Color.WHITE);
        btnImprimirEtiqueta.setEnabled(false);
        btnImprimirEtiqueta.setFocusPainted(false);
        btnImprimirEtiqueta.addActionListener(e -> imprimirEtiquetaCodigo());

        gbc.gridy++; gbc.insets = new Insets(15, 0, 0, 0); panel.add(btnGuardar, gbc);
        
        JPanel pAcciones = new JPanel(new GridLayout(1, 2, 5, 0)); pAcciones.setOpaque(false);
        pAcciones.add(btnActualizar); pAcciones.add(btnLimpiar);
        gbc.gridy++; gbc.insets = new Insets(5, 0, 0, 0); panel.add(pAcciones, gbc);
        
        gbc.gridy++; gbc.insets = new Insets(5, 0, 0, 0); panel.add(btnImprimirEtiqueta, gbc);

        gbc.gridy++; gbc.weighty = 1.0; panel.add(Box.createVerticalGlue(), gbc);
        return panel;
    }

    private void refrescarLotes() {
        cmbFiltroLote.removeAllItems(); cmbLoteRegistro.removeAllItems();
        cmbFiltroLote.addItem(new ComboItem(0, "Todos los Lotes"));
        List<Object[]> lotes = dao.obtenerLotesActivos();
        for (Object[] lote : lotes) {
            ComboItem item = new ComboItem((int)lote[0], lote[1].toString());
            cmbFiltroLote.addItem(item); cmbLoteRegistro.addItem(item);
        }
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0); 
        String busqueda = txtBuscar.getText().trim();
        boolean verOcultas = chkVerOcultas.isSelected();
        int idLote = 0;
        if (cmbFiltroLote.getSelectedItem() != null) {
            idLote = ((ComboItem) cmbFiltroLote.getSelectedItem()).getId();
        }

        List<Object[]> pantallas = dao.listarPantallas(busqueda, verOcultas, idLote);
        for (Object[] p : pantallas) {
            modeloTabla.addRow(new Object[]{ p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7], p[8], p[9], p[10], (p.length > 11 ? p[11] : "") });
        }
    }

    private void seleccionarPantalla() {
        int fila = tablaInventario.getSelectedRow();
        if (fila >= 0) {
            idPantallaSeleccionada = Integer.parseInt(tablaInventario.getValueAt(fila, 0).toString());
            txtModelo.setText(tablaInventario.getValueAt(fila, 8).toString());
            txtCaja.setText(tablaInventario.getValueAt(fila, 9).toString());
            int idLoteOrig = Integer.parseInt(tablaInventario.getValueAt(fila, 10).toString());
            
            // Verificamos que el código de barras no sea null antes de pintar
            Object objCodigo = tablaInventario.getValueAt(fila, 11);
            txtCodigoBarras.setText(objCodigo != null ? objCodigo.toString() : ""); 
            
            txtCosto.setText(tablaInventario.getValueAt(fila, 3).toString());
            txtPrecioCliente.setText(tablaInventario.getValueAt(fila, 4).toString());
            txtPrecioTecnico.setText(tablaInventario.getValueAt(fila, 5).toString());
            txtStock.setText(tablaInventario.getValueAt(fila, 6).toString());

            for (int i = 0; i < cmbLoteRegistro.getItemCount(); i++) {
                if (cmbLoteRegistro.getItemAt(i).getId() == idLoteOrig) {
                    cmbLoteRegistro.setSelectedIndex(i); break;
                }
            }
            btnGuardar.setEnabled(false); 
            btnActualizar.setEnabled(true);
            btnImprimirEtiqueta.setEnabled(true); // <--- ACTIVAMOS BOTÓN IMPRIMIR
        }
    }

    private void guardarPantalla() {
        try {
            int idLote = ((ComboItem) cmbLoteRegistro.getSelectedItem()).getId();
            String mod = txtModelo.getText().trim().toUpperCase();
            int numCaja = Integer.parseInt(txtCaja.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());
            double pCli = Double.parseDouble(txtPrecioCliente.getText().trim());
            double pTec = Double.parseDouble(txtPrecioTecnico.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String codigo = txtCodigoBarras.getText().trim();

            if (dao.registrarPantalla(idLote, mod, costo, pCli, pTec, stock, numCaja, codigo)) {
                JOptionPane.showMessageDialog(this, "Guardada con éxito.");
                limpiarFormulario(); refrescarTabla();
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Datos inválidos."); }
    }

    private void actualizarDatos() {
        if (idPantallaSeleccionada == -1) return;
        try {
            int idLote = ((ComboItem) cmbLoteRegistro.getSelectedItem()).getId();
            String mod = txtModelo.getText().trim().toUpperCase();
            int numCaja = Integer.parseInt(txtCaja.getText().trim());
            double costo = Double.parseDouble(txtCosto.getText().trim());
            double pCli = Double.parseDouble(txtPrecioCliente.getText().trim());
            double pTec = Double.parseDouble(txtPrecioTecnico.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String codigo = txtCodigoBarras.getText().trim();

            if (dao.actualizarPantalla(idPantallaSeleccionada, idLote, mod, costo, pCli, pTec, stock, numCaja, codigo)) {
                JOptionPane.showMessageDialog(this, "Actualizada correctamente.");
                limpiarFormulario(); refrescarTabla();
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Datos inválidos."); }
    }

    private void limpiarFormulario() {
        idPantallaSeleccionada = -1;
        txtModelo.setText(""); txtCosto.setText(""); txtPrecioCliente.setText("");
        txtPrecioTecnico.setText(""); txtStock.setText(""); txtCaja.setText("1");
        txtCodigoBarras.setText(""); 
        tablaInventario.clearSelection();
        btnGuardar.setEnabled(true); 
        btnActualizar.setEnabled(false);
        btnImprimirEtiqueta.setEnabled(false); // <--- DESACTIVAMOS BOTÓN IMPRIMIR
    }

    private void alternarEstadoPantalla() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) return;
        int id = Integer.parseInt(modeloTabla.getValueAt(fila, 0).toString());
        String estadoActual = modeloTabla.getValueAt(fila, 7).toString();
        if (estadoActual.equals("Activo")) dao.ocultarPantalla(id);
        else dao.restaurarPantalla(id);
        refrescarTabla();
    }

    private void crearLote() {
        String nombre = txtNuevoLote.getText().trim();
        if (nombre.isEmpty()) return;
        if (dao.crearLote(nombre)) {
            JOptionPane.showMessageDialog(this, "Lote creado."); txtNuevoLote.setText(""); refrescarLotes();
        }
    }

    // --- MAGIA: MÉTODO DE IMPRESIÓN ---
    private void imprimirEtiquetaCodigo() {
        if (idPantallaSeleccionada == -1) return;
        
        String modelo = txtModelo.getText().trim();
        String codigo = txtCodigoBarras.getText().trim();
        String caja = txtCaja.getText().trim();
        
        ComboItem itemLote = (ComboItem) cmbLoteRegistro.getSelectedItem();
        String lote = itemLote != null ? itemLote.toString() : "Lote Desconocido";

        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Esta pantalla no tiene un código asignado. Guarde o actualice primero.");
            return;
        }

        // --- LLAMADO A LA IMPRESIÓN DIRECTA ---
        utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta();
        boolean ok = impresora.imprimirEtiquetaKnijicoDirecta(modelo, codigo, lote, caja);
        
        if (ok) {
            // Como la impresión es instantánea, puedes omitir este mensaje o dejarlo.
            JOptionPane.showMessageDialog(this, "Etiqueta enviada a la impresora.", "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo comunicar con la impresora.", "Error de Impresión", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ComboItem {
        private int id; private String name;
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
