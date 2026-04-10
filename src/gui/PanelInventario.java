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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PanelInventario extends javax.swing.JPanel {

   private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cmbCategoria;
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

    private int idProductoSeleccionado = -1;
    private List<Integer> listaIdCategorias = new ArrayList<>();

    public PanelInventario() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // TÍTULO
        JLabel lblTitulo = new JLabel("Gestión de Inventario y Repuestos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        // FORMULARIO (IZQUIERDA)
        JPanel panelFormulario = construirFormulario();
        add(panelFormulario, BorderLayout.WEST);

        // TABLA Y BUSCADOR (DERECHA)
        JPanel panelTabla = construirPanelTabla();
        add(panelTabla, BorderLayout.CENTER);

        // INICIALIZAR DATOS
        cargarCategorias();
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblSub = new JLabel("Datos del Producto");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSub.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(lblSub, gbc);

        // Inicializar campos
        txtCodigo = crearTextField();
        txtNombre = crearTextField();
        cmbCategoria = new JComboBox<>();
        cmbCategoria.setPreferredSize(new Dimension(0, 35));
        txtPrecioCompra = crearTextField();
        txtPrecioVenta = crearTextField();
        txtStock = crearTextField();
        txtStockMinimo = crearTextField();
        txtStockMinimo.setText("5"); // Por defecto

        // Agregar al panel
        gbc.insets = new Insets(5, 0, 2, 0);
        
        gbc.gridy++; panel.add(new JLabel("Código de Barras (Opcional):"), gbc);
        gbc.gridy++; panel.add(txtCodigo, gbc);
        
        gbc.gridy++; panel.add(new JLabel("Nombre del Producto/Repuesto: *"), gbc);
        gbc.gridy++; panel.add(txtNombre, gbc);
        
        gbc.gridy++; panel.add(new JLabel("Categoría: *"), gbc);
        gbc.gridy++; panel.add(cmbCategoria, gbc);
        
        JPanel panelPrecios = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelPrecios.setOpaque(false);
        
        JPanel pCompra = new JPanel(new BorderLayout()); pCompra.setOpaque(false);
        pCompra.add(new JLabel("P. Compra: *"), BorderLayout.NORTH); pCompra.add(txtPrecioCompra, BorderLayout.CENTER);
        
        JPanel pVenta = new JPanel(new BorderLayout()); pVenta.setOpaque(false);
        pVenta.add(new JLabel("P. Venta: *"), BorderLayout.NORTH); pVenta.add(txtPrecioVenta, BorderLayout.CENTER);
        
        panelPrecios.add(pCompra); panelPrecios.add(pVenta);
        gbc.gridy++; gbc.insets = new Insets(10, 0, 5, 0); panel.add(panelPrecios, gbc);

        JPanel panelStocks = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelStocks.setOpaque(false);
        
        JPanel pStock = new JPanel(new BorderLayout()); pStock.setOpaque(false);
        pStock.add(new JLabel("Stock Actual: *"), BorderLayout.NORTH); pStock.add(txtStock, BorderLayout.CENTER);
        
        JPanel pMin = new JPanel(new BorderLayout()); pMin.setOpaque(false);
        pMin.add(new JLabel("Stock Mínimo:"), BorderLayout.NORTH); pMin.add(txtStockMinimo, BorderLayout.CENTER);
        
        panelStocks.add(pStock); panelStocks.add(pMin);
        gbc.gridy++; panel.add(panelStocks, gbc);

        // Botones
        btnGuardar = new JButton("Guardar Producto");
        estilizarBoton(btnGuardar, new Color(46, 204, 113));
        btnGuardar.addActionListener(e -> guardarProducto());

        btnActualizar = new JButton("Actualizar");
        estilizarBoton(btnActualizar, new Color(52, 152, 219));
        btnActualizar.setEnabled(false);
        btnActualizar.addActionListener(e -> actualizarProducto());

        btnLimpiar = new JButton("Limpiar");
        estilizarBoton(btnLimpiar, new Color(149, 165, 166));
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        // --- NUEVO BOTÓN DE ETIQUETAS ---
        btnImprimirEtiqueta = new JButton("Imprimir Etiquetas");
        estilizarBoton(btnImprimirEtiqueta, new Color(155, 89, 182)); // Color Morado
        btnImprimirEtiqueta.setEnabled(false); // Apagado por defecto
        btnImprimirEtiqueta.addActionListener(e -> imprimirEtiquetas());

        gbc.gridy++; gbc.insets = new Insets(25, 0, 5, 0); panel.add(btnGuardar, gbc);
        
        JPanel panelAcciones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelAcciones.setOpaque(false);
        panelAcciones.add(btnActualizar); panelAcciones.add(btnLimpiar);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 10, 0); panel.add(panelAcciones, gbc); // Cambié Insets abajo a 10
        
        // Agregamos el botón nuevo debajo de los otros dos
        gbc.gridy++; gbc.insets = new Insets(0, 0, 0, 0); panel.add(btnImprimirEtiqueta, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Buscador
        JPanel panelBuscador = new JPanel(new BorderLayout(10, 0));
        panelBuscador.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscar Producto:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtBuscar.setPreferredSize(new Dimension(0, 40));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarTabla(txtBuscar.getText().trim());
            }
        });
        
        panelBuscador.add(lblBuscar, BorderLayout.WEST);
        panelBuscador.add(txtBuscar, BorderLayout.CENTER);

        // Tabla
        tablaProductos = new JTable();
        tablaProductos.setRowHeight(30);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                seleccionarProducto();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(panelBuscador, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JTextField crearTextField() {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(0, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return txt;
    }

    private void estilizarBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    // =========================================================
    // LÓGICA DE BASE DE DATOS
    // =========================================================

    private void cargarCategorias() {
        cmbCategoria.removeAllItems();
        listaIdCategorias.clear();
        cmbCategoria.addItem("--- Seleccione ---");
        listaIdCategorias.add(-1);

        for (modelo.CategoriaProducto c : new dao.CategoriaProductoDAO().listar()) {
            cmbCategoria.addItem(c.getNombreCategoria());
            listaIdCategorias.add(c.getIdCategoria());
        }
    }

    private void cargarTabla(String filtro) {
        List<Object[]> lista = new dao.ProductoDAO().buscarProductoCompleto(filtro);
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Código", "Nombre", "Categoría", "P. Compra", "P. Venta", "Stock", "Mínimo"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Object[] fila : lista) {
            modelo.addRow(fila);
        }
        tablaProductos.setModel(modelo);
        
        // Ajustar anchos
        if (tablaProductos.getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(200); // Nombre grande
        }
    }

    private void seleccionarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            idProductoSeleccionado = Integer.parseInt(tablaProductos.getValueAt(fila, 0).toString());
            
            // Si el código de barras es null en BD, evitamos error
            Object codBarras = tablaProductos.getValueAt(fila, 1);
            txtCodigo.setText(codBarras != null ? codBarras.toString() : "");
            
            txtNombre.setText(tablaProductos.getValueAt(fila, 2).toString());
            cmbCategoria.setSelectedItem(tablaProductos.getValueAt(fila, 3).toString());
            txtPrecioCompra.setText(tablaProductos.getValueAt(fila, 4).toString());
            txtPrecioVenta.setText(tablaProductos.getValueAt(fila, 5).toString());
            txtStock.setText(tablaProductos.getValueAt(fila, 6).toString());
            txtStockMinimo.setText(tablaProductos.getValueAt(fila, 7).toString());

            btnGuardar.setEnabled(false);
            btnActualizar.setEnabled(true);
            btnImprimirEtiqueta.setEnabled(true);
        }
    }

    private void guardarProducto() {
        if (!validarFormulario()) return;

        modelo.Producto p = capturarDatosFormulario();
        dao.ProductoDAO daoProd = new dao.ProductoDAO();
        
        // 1. Guardamos el producto y obtenemos su ID
        int idGenerado = daoProd.insertarConId(p);
        
        if (idGenerado != -1) {
            // 2. MAGIA: Si el código de barras venía vacío, lo auto-generamos
            if (p.getCodigoBarras() == null || p.getCodigoBarras().trim().isEmpty()) {
                // String.format("%011d") llena de ceros a la izquierda hasta llegar a 11 dígitos
                String codigoAutomatico = String.format("%011d", idGenerado);
                daoProd.actualizarCodigoBarras(idGenerado, codigoAutomatico);
            }
            
            JOptionPane.showMessageDialog(this, "Producto guardado exitosamente.");
            limpiarFormulario();
            cargarTabla("");
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
            limpiarFormulario();
            cargarTabla("");
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty() || cmbCategoria.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "El nombre y la categoría son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Double.parseDouble(txtPrecioCompra.getText().trim());
            Double.parseDouble(txtPrecioVenta.getText().trim());
            Integer.parseInt(txtStock.getText().trim());
            Integer.parseInt(txtStockMinimo.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los precios deben ser números (ej. 150.50) y los stocks números enteros.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
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
        return p;
    }

    private void limpiarFormulario() {
        idProductoSeleccionado = -1;
        txtCodigo.setText("");
        txtNombre.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtPrecioCompra.setText("");
        txtPrecioVenta.setText("");
        txtStock.setText("");
        txtStockMinimo.setText("5");
        
        btnGuardar.setEnabled(true);
        btnActualizar.setEnabled(false);
        btnImprimirEtiqueta.setEnabled(false);
        tablaProductos.clearSelection();
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

    // =========================================================
    // LÓGICA DE IMPRESIÓN DE ETIQUETAS DIRECTA
    // =========================================================
    private void imprimirEtiquetas() {
        if (idProductoSeleccionado == -1) return;

        String codigo = txtCodigo.getText().trim();
        
        // Si no tiene código, lo creamos rellenando con ceros
        if (codigo.isEmpty()) {
            codigo = String.format("%011d", idProductoSeleccionado);
            dao.ProductoDAO daoProd = new dao.ProductoDAO();
            
            if (daoProd.actualizarCodigoBarras(idProductoSeleccionado, codigo)) {
                txtCodigo.setText(codigo); 
                cargarTabla(""); 
            } else {
                JOptionPane.showMessageDialog(this, "Error al generar un código automáticamente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String cantidadStr = JOptionPane.showInputDialog(this, "¿Cuántas etiquetas desea imprimir para este producto?", "Imprimir Etiquetas", JOptionPane.QUESTION_MESSAGE);
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) return;

        try {
            int cantidad = Integer.parseInt(cantidadStr.trim());
            if (cantidad <= 0) throw new NumberFormatException();

            String nombreProd = txtNombre.getText().trim();

            // --- LLAMAMOS A LA NUEVA IMPRESORA EN MEMORIA ---
            utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta();
            boolean exito = impresora.imprimirEtiquetasDirecto(nombreProd, codigo, cantidad);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Se han enviado " + cantidad + " etiquetas a la impresora.", "Impresión Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un número entero válido mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
