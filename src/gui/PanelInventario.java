package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanelInventario extends javax.swing.JPanel {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtUbicacion; 
    private JComboBox<String> cmbCategoria;
    private JComboBox<modelo.Proveedor> cmbProveedor; 
    
    private JCheckBox chkAplicaTecnico;
    private JTextField txtPrecioTecnico;

    private JTextField txtPrecioCompra;
    private JTextField txtPrecioVenta;
    private JTextField txtStock;
    private JTextField txtStockMinimo;
    private JTextField txtBuscar;
    private JTable tablaProductos;
    
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnLimpiar;
    private JButton btnImprimirEtiqueta;
    private JButton btnNuevaCategoria; // <--- NUEVO BOTÓN
    private JButton btnEliminar; 
    private JCheckBox chkVerEliminados; 

    private int idProductoSeleccionado = -1;
    private List<Integer> listaIdCategorias = new ArrayList<>();

    public PanelInventario() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel lblTitulo = new JLabel("Gestión de Inventario y Repuestos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelFormulario = construirFormulario();
        add(panelFormulario, BorderLayout.WEST);

        JPanel panelTabla = construirPanelTabla();
        add(panelTabla, BorderLayout.CENTER);

        cargarCategorias();
        cargarProveedores(); 
        cargarTabla("");
    }

    private JPanel construirFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;

        JLabel lblSub = new JLabel("Datos del Producto"); lblSub.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSub.setForeground(Color.GRAY); gbc.insets = new Insets(0, 0, 10, 0); panel.add(lblSub, gbc);

        txtCodigo = crearTextField(); 
        txtNombre = crearTextField();
        txtUbicacion = crearTextField(); 
        
        cmbCategoria = new JComboBox<>(); cmbCategoria.setPreferredSize(new Dimension(0, 35));
        cmbProveedor = new JComboBox<>(); cmbProveedor.setPreferredSize(new Dimension(0, 35));
        cmbProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        chkAplicaTecnico = new JCheckBox("Habilitar Precio de Técnico (Mayorista)");
        chkAplicaTecnico.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        chkAplicaTecnico.setForeground(new Color(41, 128, 185)); chkAplicaTecnico.setOpaque(false);
        txtPrecioTecnico = crearTextField(); txtPrecioTecnico.setText("0.00"); txtPrecioTecnico.setEnabled(false); 
        chkAplicaTecnico.addActionListener(e -> txtPrecioTecnico.setEnabled(chkAplicaTecnico.isSelected()));
        
        txtPrecioCompra = crearTextField(); txtPrecioVenta = crearTextField();
        txtStock = crearTextField(); txtStockMinimo = crearTextField(); txtStockMinimo.setText("5"); 

        gbc.insets = new Insets(2, 0, 2, 0);
        gbc.gridy++; panel.add(new JLabel("Código de Barras (Opcional):"), gbc); gbc.gridy++; panel.add(txtCodigo, gbc);
        gbc.gridy++; panel.add(new JLabel("Nombre del Producto/Repuesto: *"), gbc); gbc.gridy++; panel.add(txtNombre, gbc);
        
        gbc.gridy++; panel.add(new JLabel("Ubicación Física (Ej: Vitrina 1):"), gbc); 
        gbc.gridy++; panel.add(txtUbicacion, gbc);

        JPanel pCombos = new JPanel(new java.awt.GridLayout(1, 2, 10, 0)); pCombos.setOpaque(false);
        JPanel pCat = new JPanel(new BorderLayout()); pCat.setOpaque(false); pCat.add(new JLabel("Categoría: *"), BorderLayout.NORTH); pCat.add(cmbCategoria, BorderLayout.CENTER);
        JPanel pProv = new JPanel(new BorderLayout()); pProv.setOpaque(false); pProv.add(new JLabel("Proveedor:"), BorderLayout.NORTH); pProv.add(cmbProveedor, BorderLayout.CENTER);
        pCombos.add(pCat); pCombos.add(pProv);
        gbc.gridy++; panel.add(pCombos, gbc);
        
        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0); panel.add(new JLabel("P. Compra: *"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0); panel.add(txtPrecioCompra, gbc);
        
        JPanel pVentas = new JPanel(new java.awt.GridLayout(1, 2, 10, 0)); pVentas.setOpaque(false);
        JPanel pV = new JPanel(new BorderLayout()); pV.setOpaque(false); pV.add(new JLabel("P. Venta Público: *"), BorderLayout.NORTH); pV.add(txtPrecioVenta, BorderLayout.CENTER);
        JPanel pT = new JPanel(new BorderLayout()); pT.setOpaque(false); pT.add(chkAplicaTecnico, BorderLayout.NORTH); pT.add(txtPrecioTecnico, BorderLayout.CENTER);
        pVentas.add(pV); pVentas.add(pT);
        gbc.gridy++; panel.add(pVentas, gbc);

        JPanel panelStocks = new JPanel(new java.awt.GridLayout(1, 2, 10, 0)); panelStocks.setOpaque(false);
        JPanel pStock = new JPanel(new BorderLayout()); pStock.setOpaque(false); pStock.add(new JLabel("Stock Actual: *"), BorderLayout.NORTH); pStock.add(txtStock, BorderLayout.CENTER);
        JPanel pMin = new JPanel(new BorderLayout()); pMin.setOpaque(false); pMin.add(new JLabel("Stock Mínimo:"), BorderLayout.NORTH); pMin.add(txtStockMinimo, BorderLayout.CENTER);
        panelStocks.add(pStock); panelStocks.add(pMin);
        gbc.gridy++; gbc.insets = new Insets(10, 0, 5, 0); panel.add(panelStocks, gbc);

        btnGuardar = new JButton("Guardar Producto"); estilizarBoton(btnGuardar, new Color(46, 204, 113)); btnGuardar.addActionListener(e -> guardarProducto());
        btnActualizar = new JButton("Actualizar"); estilizarBoton(btnActualizar, new Color(52, 152, 219)); btnActualizar.setEnabled(false); btnActualizar.addActionListener(e -> actualizarProducto());
        btnLimpiar = new JButton("Limpiar"); estilizarBoton(btnLimpiar, new Color(149, 165, 166)); btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        btnEliminar = new JButton("Eliminar"); estilizarBoton(btnEliminar, new Color(231, 76, 60)); 
        btnEliminar.setEnabled(false); btnEliminar.addActionListener(e -> alternarEstadoProducto());

        btnImprimirEtiqueta = new JButton("Imprimir Etiquetas"); estilizarBoton(btnImprimirEtiqueta, new Color(155, 89, 182)); 
        btnImprimirEtiqueta.setEnabled(false); btnImprimirEtiqueta.addActionListener(e -> imprimirEtiquetas());

        // --- NUEVO BOTÓN DE CATEGORÍA ---
        btnNuevaCategoria = new JButton("+ Nueva Categoría"); estilizarBoton(btnNuevaCategoria, new Color(243, 156, 18));
        btnNuevaCategoria.addActionListener(e -> abrirModalCrearCategoria());

        gbc.gridy++; gbc.insets = new Insets(15, 0, 5, 0); panel.add(btnGuardar, gbc);
        
        JPanel panelAcciones = new JPanel(new java.awt.GridLayout(1, 3, 5, 0)); panelAcciones.setOpaque(false); 
        panelAcciones.add(btnActualizar); panelAcciones.add(btnLimpiar); panelAcciones.add(btnEliminar);
        
        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0); panel.add(panelAcciones, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0); panel.add(btnImprimirEtiqueta, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 0, 0); panel.add(btnNuevaCategoria, gbc); // Se agrega al panel
        gbc.gridy++; gbc.weighty = 1.0; panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);
        JPanel panelBuscador = new JPanel(new BorderLayout(10, 0)); panelBuscador.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscar Producto:"); lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtBuscar = new JTextField(); txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 16)); txtBuscar.setPreferredSize(new Dimension(0, 40));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() { public void keyReleased(java.awt.event.KeyEvent evt) { cargarTabla(txtBuscar.getText().trim()); } });
        
        chkVerEliminados = new JCheckBox("Ver Papelera");
        chkVerEliminados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkVerEliminados.setOpaque(false);
        chkVerEliminados.addActionListener(e -> cargarTabla(txtBuscar.getText().trim()));
        
        panelBuscador.add(lblBuscar, BorderLayout.WEST); 
        panelBuscador.add(txtBuscar, BorderLayout.CENTER);
        panelBuscador.add(chkVerEliminados, BorderLayout.EAST);

        tablaProductos = new JTable(); tablaProductos.setRowHeight(30); tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 14)); tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() { public void mouseClicked(java.awt.event.MouseEvent evt) { seleccionarProducto(); } });

        JScrollPane scroll = new JScrollPane(tablaProductos); scroll.getViewport().setBackground(Color.WHITE); scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(panelBuscador, BorderLayout.NORTH); panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField(); txt.setPreferredSize(new Dimension(0, 35)); txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return txt;
    }

    private void estilizarBoton(JButton btn, Color color) {
        btn.setBackground(color); btn.setForeground(Color.WHITE); btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(0, 40)); btn.setFocusPainted(false); btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    // --- NUEVO: MODAL DE CREACIÓN DE CATEGORÍAS ---
    private void abrirModalCrearCategoria() {
        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Nueva Categoría", true);
        dialogo.setSize(400, 360);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panelFondo = new JPanel(new java.awt.GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Registrar Categoría");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0; gbc.insets = new java.awt.Insets(0, 0, 15, 0);
        panelFondo.add(lblTitulo, gbc);

        JTextField txtNombreCat = new JTextField();
        txtNombreCat.setPreferredSize(new Dimension(0, 35));
        txtNombreCat.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JCheckBox chkGarantia = new JCheckBox("Esta categoría aplica garantía");
        chkGarantia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkGarantia.setForeground(new Color(41, 128, 185));
        chkGarantia.setBackground(Color.WHITE);
        chkGarantia.setFocusPainted(false);

        JComboBox<String> cmbDias = new JComboBox<>(new String[]{
            "7", "15", "30", "60", "90", "180", "365"
        });
        cmbDias.setPreferredSize(new Dimension(0, 35));
        cmbDias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbDias.setEnabled(false); // Apagado por defecto

        // Lógica de encendido/apagado
        chkGarantia.addActionListener(e -> cmbDias.setEnabled(chkGarantia.isSelected()));

        gbc.insets = new java.awt.Insets(5, 0, 2, 0);
        gbc.gridy++; panelFondo.add(new JLabel("Nombre de la Categoría: *"), gbc);
        gbc.gridy++; panelFondo.add(txtNombreCat, gbc);
        
        gbc.gridy++; gbc.insets = new java.awt.Insets(15, 0, 5, 0);
        panelFondo.add(chkGarantia, gbc);
        
        gbc.gridy++; gbc.insets = new java.awt.Insets(5, 0, 2, 0);
        panelFondo.add(new JLabel("Seleccione los días de cobertura:"), gbc);
        gbc.gridy++; panelFondo.add(cmbDias, gbc);

        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166)); btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardarCat = new JButton("Guardar");
        btnGuardarCat.setBackground(new Color(46, 204, 113)); btnGuardarCat.setForeground(Color.WHITE);
        btnGuardarCat.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnGuardarCat.setFocusPainted(false);

        btnGuardarCat.addActionListener(e -> {
            String nombre = txtNombreCat.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "El nombre de la categoría es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dias = 0;
            if (chkGarantia.isSelected()) {
                dias = Integer.parseInt(cmbDias.getSelectedItem().toString());
            }

            // --- MAGIA CONECTADA AL DAO ---
            modelo.CategoriaProducto nuevaCat = new modelo.CategoriaProducto();
            nuevaCat.setNombreCategoria(nombre);
            nuevaCat.setDescripcion(""); // No ocupamos descripción por ahora, se envía vacío
            nuevaCat.setDiasGarantia(dias);

            dao.CategoriaProductoDAO daoCat = new dao.CategoriaProductoDAO();
            
            // Aquí intentamos insertar en la BD
            if (daoCat.insertar(nuevaCat)) {
                JOptionPane.showMessageDialog(dialogo, "Categoría guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarCategorias(); // Recarga el ComboBox principal al instante
                cmbCategoria.setSelectedItem(nombre); // Selecciona la que acabas de crear
                dialogo.dispose(); // Cierra el modal
            } else {
                JOptionPane.showMessageDialog(dialogo, "Error al guardar la categoría en la Base de Datos. Revisa la consola de NetBeans.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardarCat);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.insets = new java.awt.Insets(25, 0, 0, 0);
        panelFondo.add(panelBotones, gbc);

        dialogo.add(panelFondo);
        dialogo.setVisible(true);
    }

    private void cargarCategorias() {
        cmbCategoria.removeAllItems(); listaIdCategorias.clear(); cmbCategoria.addItem("--- Seleccione ---"); listaIdCategorias.add(-1);
        for (modelo.CategoriaProducto c : new dao.CategoriaProductoDAO().listar()) { cmbCategoria.addItem(c.getNombreCategoria()); listaIdCategorias.add(c.getIdCategoria()); }
    }

    private void cargarProveedores() {
        cmbProveedor.removeAllItems();
        modelo.Proveedor provVacio = new modelo.Proveedor(); provVacio.setIdProveedor(0); provVacio.setEmpresa("--- Sin Proveedor ---"); cmbProveedor.addItem(provVacio);
        for(modelo.Proveedor p : new dao.ProveedorDAO().listarActivos()) { cmbProveedor.addItem(p); }
    }

    private void cargarTabla(String filtro) {
        boolean verPapelera = chkVerEliminados.isSelected();
        List<Object[]> lista = new dao.ProductoDAO().buscarProductoCompleto(filtro, verPapelera);
        
        btnEliminar.setText(verPapelera ? "Restaurar" : "Eliminar");
        btnEliminar.setBackground(verPapelera ? new Color(46, 204, 113) : new Color(231, 76, 60));
        
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Código", "Nombre", "Categoría", "Ubicación", "P. Compra", "P. Venta", "Stock", "Mínimo", "ID_Prov", "AplicaTec", "P_Tecnico"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Object[] fila : lista) { modelo.addRow(fila); }
        tablaProductos.setModel(modelo);
        
        if (tablaProductos.getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(200); 
            for (int i = 9; i <= 11; i++) {
                tablaProductos.getColumnModel().getColumn(i).setMinWidth(0);
                tablaProductos.getColumnModel().getColumn(i).setMaxWidth(0);
                tablaProductos.getColumnModel().getColumn(i).setWidth(0);
            }
        }
    }

    private void seleccionarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            idProductoSeleccionado = Integer.parseInt(tablaProductos.getValueAt(fila, 0).toString());
            
            Object codBarras = tablaProductos.getValueAt(fila, 1);
            txtCodigo.setText(codBarras != null ? codBarras.toString() : "");
            
            txtNombre.setText(tablaProductos.getValueAt(fila, 2).toString());
            cmbCategoria.setSelectedItem(tablaProductos.getValueAt(fila, 3).toString());
            
            Object ubic = tablaProductos.getValueAt(fila, 4);
            txtUbicacion.setText(ubic != null ? ubic.toString() : "");
            
            txtPrecioCompra.setText(tablaProductos.getValueAt(fila, 5).toString());
            txtPrecioVenta.setText(tablaProductos.getValueAt(fila, 6).toString());
            txtStock.setText(tablaProductos.getValueAt(fila, 7).toString());
            txtStockMinimo.setText(tablaProductos.getValueAt(fila, 8).toString());

            if (tablaProductos.getColumnCount() > 11) {
                Object objProv = tablaProductos.getValueAt(fila, 9);
                int idProv = (objProv != null) ? Integer.parseInt(objProv.toString()) : 0;
                for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
                    if (cmbProveedor.getItemAt(i).getIdProveedor() == idProv) { cmbProveedor.setSelectedIndex(i); break; }
                }
                boolean aplicaTecnico = (boolean) tablaProductos.getValueAt(fila, 10);
                chkAplicaTecnico.setSelected(aplicaTecnico);
                txtPrecioTecnico.setEnabled(aplicaTecnico);
                txtPrecioTecnico.setText(tablaProductos.getValueAt(fila, 11).toString());
            }

            btnGuardar.setEnabled(false); btnActualizar.setEnabled(true); btnImprimirEtiqueta.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
    }

    private void guardarProducto() {
        if (!validarFormulario()) return;
        modelo.Producto p = capturarDatosFormulario();
        dao.ProductoDAO daoProd = new dao.ProductoDAO();
        int idGenerado = daoProd.insertarConId(p);
        
        if (idGenerado != -1) {
            if (p.getCodigoBarras() == null || p.getCodigoBarras().trim().isEmpty()) {
                String codigoAutomatico = String.format("%011d", idGenerado);
                daoProd.actualizarCodigoBarras(idGenerado, codigoAutomatico);
            }
            JOptionPane.showMessageDialog(this, "Producto guardado exitosamente.");
            limpiarFormulario(); cargarTabla("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarProducto() {
        if (!validarFormulario()) return;
        modelo.Producto p = capturarDatosFormulario();
        p.setIdProducto(idProductoSeleccionado);
        
        if (new dao.ProductoDAO().actualizar(p)) {
            JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.");
            limpiarFormulario(); cargarTabla("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void alternarEstadoProducto() {
        if (idProductoSeleccionado == -1) return;
        boolean esPapelera = chkVerEliminados.isSelected();
        dao.ProductoDAO daoProd = new dao.ProductoDAO();
        
        if (esPapelera) {
            if (daoProd.restaurar(idProductoSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Producto restaurado con éxito.");
            }
        } else {
            int resp = JOptionPane.showConfirmDialog(this, "¿Está seguro de ocultar este producto?\nNo se mostrará en el Punto de Venta.", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                if (daoProd.eliminar(idProductoSeleccionado)) {
                    JOptionPane.showMessageDialog(this, "Producto enviado a la papelera.");
                }
            }
        }
        limpiarFormulario();
        cargarTabla(txtBuscar.getText().trim());
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty() || cmbCategoria.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "El nombre y la categoría son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Double.parseDouble(txtPrecioCompra.getText().trim());
            Double.parseDouble(txtPrecioVenta.getText().trim());
            if(chkAplicaTecnico.isSelected()) Double.parseDouble(txtPrecioTecnico.getText().trim());
            Integer.parseInt(txtStock.getText().trim());
            Integer.parseInt(txtStockMinimo.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Verifique que todos los precios y el stock tengan un formato numérico válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private modelo.Producto capturarDatosFormulario() {
        modelo.Producto p = new modelo.Producto();
        p.setCodigoBarras(txtCodigo.getText().trim().isEmpty() ? null : txtCodigo.getText().trim());
        p.setNombreProducto(txtNombre.getText().trim());
        p.setIdCategoria(listaIdCategorias.get(cmbCategoria.getSelectedIndex()));
        p.setPrecioCompra(Double.parseDouble(txtPrecioCompra.getText().trim()));
        p.setPrecioVenta(Double.parseDouble(txtPrecioVenta.getText().trim()));
        p.setStock(Integer.parseInt(txtStock.getText().trim()));
        p.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
        
        modelo.Proveedor prov = (modelo.Proveedor) cmbProveedor.getSelectedItem();
        p.setIdProveedor(prov != null ? prov.getIdProveedor() : 0);
        
        p.setAplicaPrecioTecnico(chkAplicaTecnico.isSelected());
        double pTecnico = 0.0;
        if(chkAplicaTecnico.isSelected() && !txtPrecioTecnico.getText().trim().isEmpty()){
            pTecnico = Double.parseDouble(txtPrecioTecnico.getText().trim());
        }
        p.setPrecioTecnico(pTecnico);
        p.setUbicacion(txtUbicacion.getText().trim());
        
        return p;
    }

    private void limpiarFormulario() {
        idProductoSeleccionado = -1;
        txtCodigo.setText(""); txtNombre.setText(""); txtUbicacion.setText("");
        cmbCategoria.setSelectedIndex(0); cmbProveedor.setSelectedIndex(0); 
        txtPrecioCompra.setText(""); txtPrecioVenta.setText("");
        txtStock.setText(""); txtStockMinimo.setText("5");
        
        chkAplicaTecnico.setSelected(false);
        txtPrecioTecnico.setText("0.00");
        txtPrecioTecnico.setEnabled(false);
        
        btnGuardar.setEnabled(true); btnActualizar.setEnabled(false); 
        btnImprimirEtiqueta.setEnabled(false); btnEliminar.setEnabled(false);
        tablaProductos.clearSelection();
    }

    private void imprimirEtiquetas() {
        if (idProductoSeleccionado == -1) return;
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            codigo = String.format("%011d", idProductoSeleccionado);
            dao.ProductoDAO daoProd = new dao.ProductoDAO();
            if (daoProd.actualizarCodigoBarras(idProductoSeleccionado, codigo)) {
                txtCodigo.setText(codigo); cargarTabla(""); 
            } else { return; }
        }
        String cantidadStr = JOptionPane.showInputDialog(this, "¿Cuántas etiquetas desea imprimir para este producto?", "Imprimir Etiquetas", JOptionPane.QUESTION_MESSAGE);
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) return;
        try {
            int cantidad = Integer.parseInt(cantidadStr.trim());
            if (cantidad <= 0) throw new NumberFormatException();
            String nombreProd = txtNombre.getText().trim();
            utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta();
            boolean exito = impresora.imprimirEtiquetasDirecto(nombreProd, codigo, cantidad);
            if (exito) JOptionPane.showMessageDialog(this, "Se han enviado " + cantidad + " etiquetas a la impresora.", "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número entero válido mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
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