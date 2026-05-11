package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TrayIcon.MessageType;
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
    private JComboBox<String> cmbUbicacion; 
    private JComboBox<String> cmbCategoria;
    private JComboBox<modelo.Proveedor> cmbProveedor; 
    
    private JCheckBox chkAplicaTecnico;
    private JTextField txtPrecioTecnico;

    private JTextField txtPrecioCompra;
    private JTextField txtPrecioVenta;
    private JTextField txtStock;
    private JTextField txtStockMinimo;
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltroCategoria; 
    private JTable tablaProductos;
    
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnLimpiar;
    private JButton btnImprimirEtiqueta;
    private JButton btnPrevisualizar;
    private JButton btnNuevaCategoria;
    private JButton btnKardex; 
    private JButton btnEliminar; 
    private JCheckBox chkVerEliminados; 

    private int idProductoSeleccionado = -1;
    private List<Integer> listaIdCategorias = new ArrayList<>();
    private List<Integer> listaIdFiltroCategorias = new ArrayList<>(); 

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
        cargarUbicaciones(); 
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
        
        // --- CONFIGURACIÓN UBICACIÓN ---
        JPanel pUbicacion = new JPanel(new BorderLayout(5, 0)); pUbicacion.setOpaque(false);
        cmbUbicacion = new JComboBox<>(); cmbUbicacion.setPreferredSize(new Dimension(0, 35));
        cmbUbicacion.setFont(new Font("Segoe UI", Font.PLAIN, 14)); cmbUbicacion.setEditable(false); 

        JPanel pBotonesUbic = new JPanel(new java.awt.GridLayout(1, 2, 2, 0)); pBotonesUbic.setOpaque(false);
        JButton btnNuevaUbicacion = new JButton("+"); btnNuevaUbicacion.setPreferredSize(new Dimension(45, 35));
        btnNuevaUbicacion.setBackground(new Color(52, 152, 219)); btnNuevaUbicacion.setForeground(Color.WHITE);
        btnNuevaUbicacion.setFont(new Font("Segoe UI", Font.BOLD, 16)); btnNuevaUbicacion.setFocusPainted(false);
        btnNuevaUbicacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNuevaUbicacion.addActionListener(e -> abrirModalNuevaUbicacion());

        JButton btnEditarUbicacion = new JButton("Edit"); btnEditarUbicacion.setPreferredSize(new Dimension(75, 35)); 
        btnEditarUbicacion.setBackground(new Color(243, 156, 18)); btnEditarUbicacion.setForeground(Color.WHITE);
        btnEditarUbicacion.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnEditarUbicacion.setFocusPainted(false);
        btnEditarUbicacion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditarUbicacion.addActionListener(e -> abrirModalEditarUbicacion());

        pBotonesUbic.add(btnNuevaUbicacion); pBotonesUbic.add(btnEditarUbicacion);
        pUbicacion.add(cmbUbicacion, BorderLayout.CENTER); pUbicacion.add(pBotonesUbic, BorderLayout.EAST);
        
        // --- CONFIGURACIÓN CATEGORÍA Y PROVEEDOR ---
        cmbCategoria = new JComboBox<>(); cmbCategoria.setPreferredSize(new Dimension(0, 35));
        
        JPanel pCategoriaContenedor = new JPanel(new BorderLayout(5, 0)); pCategoriaContenedor.setOpaque(false);
        JButton btnEditarCategoria = new JButton("Edit"); 
        btnEditarCategoria.setPreferredSize(new Dimension(75, 35)); 
        btnEditarCategoria.setBackground(new Color(243, 156, 18)); 
        btnEditarCategoria.setForeground(Color.WHITE);
        btnEditarCategoria.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btnEditarCategoria.setFocusPainted(false);
        btnEditarCategoria.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditarCategoria.setToolTipText("Editar la categoría seleccionada");
        btnEditarCategoria.addActionListener(e -> abrirModalEditarCategoria());
        
        pCategoriaContenedor.add(cmbCategoria, BorderLayout.CENTER);
        pCategoriaContenedor.add(btnEditarCategoria, BorderLayout.EAST);
        
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
        gbc.gridy++; panel.add(pUbicacion, gbc); 

        gbc.gridy++; gbc.insets = new Insets(10, 0, 2, 0); panel.add(new JLabel("Categoría: *"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0); panel.add(pCategoriaContenedor, gbc);
        
        gbc.gridy++; gbc.insets = new Insets(5, 0, 2, 0); panel.add(new JLabel("Proveedor:"), gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0); panel.add(cmbProveedor, gbc);
        
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

        // --- INICIO DE BOTONES DE IMPRESIÓN MEJORADOS ---
        
        // 1. Intentamos cargar el ícono del ojito de forma segura
        javax.swing.ImageIcon iconoOjo = null;
        try {
            java.net.URL eyeUrl = getClass().getResource("/image/icon_eye.png");
            if (eyeUrl != null) {
                // Lo cargamos y lo escalamos a 18x18 para que se vea nítido al lado del texto
                java.awt.Image imgOjo = javax.imageio.ImageIO.read(eyeUrl);
                iconoOjo = new javax.swing.ImageIcon(imgOjo.getScaledInstance(18, 18, java.awt.Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            // Si falla, el botón saldrá solo con texto, no pasa nada grave
            System.err.println("No se pudo cargar el ícono del ojo: " + e.getMessage());
        }

        // 2. Creamos el botón pasándole el texto Y el ícono
        btnPrevisualizar = new JButton("Ver Previa", iconoOjo); 
        estilizarBoton(btnPrevisualizar, new Color(254, 254, 254));
        btnPrevisualizar.setForeground(Color.BLACK);
        btnPrevisualizar.setEnabled(false); 
        // Alinear ícono y texto al centro
        btnPrevisualizar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnPrevisualizar.setIconTextGap(8); // Separación de 8px entre ojo y texto
        btnPrevisualizar.addActionListener(e -> mostrarVistaPrevia());

        // Botón de imprimir normal (Morado)
        btnImprimirEtiqueta = new JButton("Imprimir"); 
        estilizarBoton(btnImprimirEtiqueta, new Color(155, 89, 182)); 
        btnImprimirEtiqueta.setEnabled(false); 
        btnImprimirEtiqueta.addActionListener(e -> imprimirEtiquetas());

        // Agrupamos en un panel de 2 columnas
        JPanel panelImpresion = new JPanel(new java.awt.GridLayout(1, 2, 5, 0));
        panelImpresion.setOpaque(false);
        panelImpresion.add(btnPrevisualizar);
        panelImpresion.add(btnImprimirEtiqueta);
        // --- FIN DE BOTONES DE IMPRESIÓN ---

        btnNuevaCategoria = new JButton("+ Nueva Categoría"); estilizarBoton(btnNuevaCategoria, new Color(243, 156, 18));
        btnNuevaCategoria.addActionListener(e -> abrirModalCrearCategoria());
        btnNuevaCategoria.addActionListener(e -> abrirModalCrearCategoria());

        btnKardex = new JButton("VER KARDEX / AUDITORÍA"); 
        estilizarBoton(btnKardex, new Color(44, 62, 80)); 
        btnKardex.setEnabled(false); 
        btnKardex.addActionListener(e -> {
            if (idProductoSeleccionado != -1) {
                int filaVisual = tablaProductos.getSelectedRow(); 
                String nombreSeleccionado = tablaProductos.getValueAt(filaVisual, 0).toString();
                
                gui.JDialogVisualizarKardex modalKardex = new gui.JDialogVisualizarKardex(idProductoSeleccionado, nombreSeleccionado);
                
                // 1. Obligamos al código a "pausarse" hasta que el usuario cierre la ventana del Kardex
                modalKardex.setModal(true);
                modalKardex.setVisible(true);
                
                // 2. --- MAGIA DE ACTUALIZACIÓN EN CALIENTE ---
                // Cuando la ventana se cierra, el código continúa aquí.
                // Buscamos el nuevo stock directamente en la BD para este producto
                try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
                     java.sql.PreparedStatement ps = con.prepareStatement("SELECT stock FROM productos WHERE id_producto = ?")) {
                     
                    ps.setInt(1, idProductoSeleccionado);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int nuevoStock = rs.getInt("stock");
                            
                            // A) Actualizamos el campo de texto en el formulario
                            txtStock.setText(String.valueOf(nuevoStock));
                            
                            // B) Actualizamos la celda exacta en la tabla (El Stock está en la columna 7 del Modelo)
                            int filaModelo = tablaProductos.convertRowIndexToModel(filaVisual);
                            tablaProductos.getModel().setValueAt(nuevoStock, filaModelo, 7);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error al refrescar el stock tras cerrar kardex: " + ex.getMessage());
                }
            }
        });

        gbc.gridy++; gbc.insets = new Insets(15, 0, 5, 0); panel.add(btnGuardar, gbc);
        
        JPanel panelAcciones = new JPanel(new java.awt.GridLayout(1, 3, 5, 0)); panelAcciones.setOpaque(false); 
        panelAcciones.add(btnActualizar); panelAcciones.add(btnLimpiar); panelAcciones.add(btnEliminar);
        
        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0); panel.add(panelAcciones, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 5, 0); panel.add(panelImpresion, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 0, 0); panel.add(btnNuevaCategoria, gbc); 
        
        gbc.gridy++; 
        gbc.insets = new Insets(25, 0, 0, 0); 
        panel.add(btnKardex, gbc);
        
        gbc.gridy++; gbc.weighty = 1.0; panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);
        JPanel panelBuscador = new JPanel(new BorderLayout(10, 0)); panelBuscador.setOpaque(false);
        
        JLabel lblBuscar = new JLabel("Buscar Producto:"); lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JPanel pBuscadorCentro = new JPanel(new BorderLayout(10, 0));
        pBuscadorCentro.setOpaque(false);
        
        txtBuscar = new JTextField(); txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 16)); txtBuscar.setPreferredSize(new Dimension(0, 40));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() { public void keyReleased(java.awt.event.KeyEvent evt) { cargarTabla(txtBuscar.getText().trim()); } });
        
        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbFiltroCategoria.setPreferredSize(new Dimension(200, 40));
        cmbFiltroCategoria.addActionListener(e -> {
            if(cmbFiltroCategoria.getItemCount() > 0) cargarTabla(txtBuscar.getText().trim());
        });
        
        pBuscadorCentro.add(txtBuscar, BorderLayout.CENTER);
        pBuscadorCentro.add(cmbFiltroCategoria, BorderLayout.EAST);
        
        chkVerEliminados = new JCheckBox("Ver Papelera");
        chkVerEliminados.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkVerEliminados.setOpaque(false);
        chkVerEliminados.addActionListener(e -> cargarTabla(txtBuscar.getText().trim()));
        
        panelBuscador.add(lblBuscar, BorderLayout.WEST); 
        panelBuscador.add(pBuscadorCentro, BorderLayout.CENTER);
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

    private void abrirModalCrearCategoria() {
        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Nueva Categoría", true);
        dialogo.setSize(400, 360);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panelFondo = new JPanel(new java.awt.GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Registrar Categoría");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        panelFondo.add(lblTitulo, gbc);

        JTextField txtNombreCat = new JTextField();
        txtNombreCat.setPreferredSize(new Dimension(0, 35));
        txtNombreCat.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JCheckBox chkGarantia = new JCheckBox("Esta categoría aplica garantía");
        chkGarantia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkGarantia.setForeground(new Color(41, 128, 185));
        chkGarantia.setBackground(Color.WHITE);
        chkGarantia.setFocusPainted(false);

        JComboBox<String> cmbDias = new JComboBox<>(new String[]{"7", "15", "30", "60", "90", "180", "365"});
        cmbDias.setPreferredSize(new Dimension(0, 35));
        cmbDias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbDias.setEnabled(false);

        chkGarantia.addActionListener(e -> cmbDias.setEnabled(chkGarantia.isSelected()));

        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.gridy++; panelFondo.add(new JLabel("Nombre de la Categoría: *"), gbc);
        gbc.gridy++; panelFondo.add(txtNombreCat, gbc);
        
        gbc.gridy++; gbc.insets = new Insets(15, 0, 5, 0);
        panelFondo.add(chkGarantia, gbc);
        
        gbc.gridy++; gbc.insets = new Insets(5, 0, 2, 0);
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
                utilidades.NotificadorWindows.mostrarAlerta("Aviso", "El nombre de la categoría es obligatorio.", MessageType.WARNING);
                return;
            }

            int dias = 0;
            if (chkGarantia.isSelected()) {
                dias = Integer.parseInt(cmbDias.getSelectedItem().toString());
            }

            modelo.CategoriaProducto nuevaCat = new modelo.CategoriaProducto();
            nuevaCat.setNombreCategoria(nombre);
            nuevaCat.setDescripcion(""); 
            nuevaCat.setDiasGarantia(dias);

            dao.CategoriaProductoDAO daoCat = new dao.CategoriaProductoDAO();
            
            if (daoCat.insertar(nuevaCat)) {
                utilidades.NotificadorWindows.mostrarAlerta("Categoría Creada", "La categoría fue guardada exitosamente.", MessageType.INFO);
                cargarCategorias(); 
                cmbCategoria.setSelectedItem(nombre); 
                dialogo.dispose(); 
            } else {
                utilidades.NotificadorWindows.mostrarAlerta("Error", "No se pudo guardar. Es posible que el nombre ya exista.", MessageType.ERROR);
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardarCat);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.insets = new Insets(25, 0, 0, 0);
        panelFondo.add(panelBotones, gbc);

        dialogo.add(panelFondo);
        dialogo.setVisible(true);
    }
    
    private void abrirModalEditarCategoria() {
        int indiceSeleccionado = cmbCategoria.getSelectedIndex();
        if (indiceSeleccionado <= 0) {
            utilidades.NotificadorWindows.mostrarAlerta("Aviso", "Seleccione una categoría válida de la lista para editarla.", MessageType.WARNING);
            return;
        }

        int idCategoria = listaIdCategorias.get(indiceSeleccionado);
        String nombreAntiguo = cmbCategoria.getSelectedItem().toString().trim();
        
        dao.CategoriaProductoDAO daoCat = new dao.CategoriaProductoDAO();
        modelo.CategoriaProducto catExistente = daoCat.obtenerPorId(idCategoria);
        
        if(catExistente == null) {
            utilidades.NotificadorWindows.mostrarAlerta("Error", "No se encontraron los datos de la categoría.", MessageType.ERROR);
            return;
        }

        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Editar Categoría", true);
        dialogo.setSize(400, 360);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panelFondo = new JPanel(new java.awt.GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Editar Categoría");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        panelFondo.add(lblTitulo, gbc);

        JTextField txtNombreCat = new JTextField(nombreAntiguo);
        txtNombreCat.setPreferredSize(new Dimension(0, 35));
        txtNombreCat.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JCheckBox chkGarantia = new JCheckBox("Esta categoría aplica garantía");
        chkGarantia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkGarantia.setForeground(new Color(41, 128, 185));
        chkGarantia.setBackground(Color.WHITE);
        chkGarantia.setFocusPainted(false);
        chkGarantia.setSelected(catExistente.getDiasGarantia() > 0);

        JComboBox<String> cmbDias = new JComboBox<>(new String[]{"7", "15", "30", "60", "90", "180", "365"});
        cmbDias.setPreferredSize(new Dimension(0, 35));
        cmbDias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbDias.setEnabled(chkGarantia.isSelected());
        
        if (catExistente.getDiasGarantia() > 0) {
            cmbDias.setSelectedItem(String.valueOf(catExistente.getDiasGarantia()));
        }

        chkGarantia.addActionListener(e -> cmbDias.setEnabled(chkGarantia.isSelected()));

        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.gridy++; panelFondo.add(new JLabel("Nuevo nombre de la Categoría: *"), gbc);
        gbc.gridy++; panelFondo.add(txtNombreCat, gbc);
        
        gbc.gridy++; gbc.insets = new Insets(15, 0, 5, 0);
        panelFondo.add(chkGarantia, gbc);
        
        gbc.gridy++; gbc.insets = new Insets(5, 0, 2, 0);
        panelFondo.add(new JLabel("Seleccione los días de cobertura:"), gbc);
        gbc.gridy++; panelFondo.add(cmbDias, gbc);

        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166)); btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardarCat = new JButton("Actualizar");
        btnGuardarCat.setBackground(new Color(243, 156, 18)); btnGuardarCat.setForeground(Color.WHITE);
        btnGuardarCat.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnGuardarCat.setFocusPainted(false);

        btnGuardarCat.addActionListener(e -> {
            String nombre = txtNombreCat.getText().trim();
            if (nombre.isEmpty()) {
                utilidades.NotificadorWindows.mostrarAlerta("Aviso", "El nombre de la categoría es obligatorio.", MessageType.WARNING);
                return;
            }

            int dias = 0;
            if (chkGarantia.isSelected()) {
                dias = Integer.parseInt(cmbDias.getSelectedItem().toString());
            }
            
            catExistente.setNombreCategoria(nombre);
            catExistente.setDiasGarantia(dias);
            
            if (daoCat.actualizar(catExistente)) {
                utilidades.NotificadorWindows.mostrarAlerta("Categoría Actualizada", "La categoría fue modificada exitosamente.", MessageType.INFO);
                cargarCategorias(); 
                cmbCategoria.setSelectedItem(nombre); 
                cargarTabla(txtBuscar.getText().trim()); 
                dialogo.dispose(); 
            } else {
                utilidades.NotificadorWindows.mostrarAlerta("Error", "Posiblemente el nombre ya exista en otra categoría.", MessageType.ERROR);
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardarCat);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.insets = new Insets(25, 0, 0, 0);
        panelFondo.add(panelBotones, gbc);

        dialogo.add(panelFondo);
        dialogo.setVisible(true);
    }

    private void cargarCategorias() {
        cmbCategoria.removeAllItems(); 
        cmbFiltroCategoria.removeAllItems(); 
        
        listaIdCategorias.clear(); 
        listaIdFiltroCategorias.clear(); 
        
        cmbCategoria.addItem("--- Seleccione ---"); 
        cmbFiltroCategoria.addItem("Todas las Categorías"); 
        
        listaIdCategorias.add(-1);
        listaIdFiltroCategorias.add(-1); 
        
        for (modelo.CategoriaProducto c : new dao.CategoriaProductoDAO().listar()) { 
            cmbCategoria.addItem(c.getNombreCategoria()); 
            listaIdCategorias.add(c.getIdCategoria()); 
            
            cmbFiltroCategoria.addItem(c.getNombreCategoria()); 
            listaIdFiltroCategorias.add(c.getIdCategoria()); 
        }
    }

    private void cargarUbicaciones() {
        cmbUbicacion.removeAllItems();
        cmbUbicacion.addItem("--- Seleccione ---");
        List<String> lista = new dao.UbicacionDAO().listar();
        for (String u : lista) {
            cmbUbicacion.addItem(u);
        }
    }

    private void cargarProveedores() {
        cmbProveedor.removeAllItems();
        modelo.Proveedor provVacio = new modelo.Proveedor(); provVacio.setIdProveedor(0); provVacio.setEmpresa("--- Sin Proveedor ---"); cmbProveedor.addItem(provVacio);
        for(modelo.Proveedor p : new dao.ProveedorDAO().listarActivos()) { cmbProveedor.addItem(p); }
    }

    private void cargarTabla(String filtro) {
        boolean verPapelera = chkVerEliminados.isSelected();
        
        int indexCat = cmbFiltroCategoria.getSelectedIndex();
        int idCatFiltro = (indexCat > 0) ? listaIdFiltroCategorias.get(indexCat) : -1;
        
        List<Object[]> lista = new dao.ProductoDAO().buscarProductoCompleto(filtro, idCatFiltro, verPapelera);
        java.util.Collections.reverse(lista); // Último agregado de primero

        btnEliminar.setText(verPapelera ? "Restaurar" : "Eliminar");
        btnEliminar.setBackground(verPapelera ? new Color(46, 204, 113) : new Color(231, 76, 60));
        
        // Títulos súper cortos para ahorrar espacio
        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Código", "Nombre", "Categoría", "Ubicación", "P. Cos", "P. Ven", "Stock", "Mínimo", "ID_Prov", "AplicaTec", "P. Tec"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Object[] fila : lista) { modelo.addRow(fila); }
        tablaProductos.setModel(modelo);
        
        if (tablaProductos.getColumnCount() > 0) {
            // Ocultamos las columnas internas
            int[] columnasOcultas = {0, 1, 4, 8, 9, 10};
            for (int col : columnasOcultas) {
                tablaProductos.getColumnModel().getColumn(col).setMinWidth(0);
                tablaProductos.getColumnModel().getColumn(col).setMaxWidth(0);
                tablaProductos.getColumnModel().getColumn(col).setWidth(0);
                tablaProductos.getColumnModel().getColumn(col).setPreferredWidth(0);
            }
            
            // Reorganizamos visualmente: Nombre, Categoría, Costo, Venta, Técnico, Stock
            tablaProductos.getColumnModel().moveColumn(2, 0); // Nombre a pos 0
            tablaProductos.getColumnModel().moveColumn(3, 1); // Categoria a pos 1
            tablaProductos.getColumnModel().moveColumn(5, 2); // P. Cos a pos 2
            tablaProductos.getColumnModel().moveColumn(6, 3); // P. Ven a pos 3
            tablaProductos.getColumnModel().moveColumn(11, 4); // P. Tec a pos 4
            tablaProductos.getColumnModel().moveColumn(7, 5); // Stock a pos 5

            // AJUSTE DE ANCHOS: El Nombre recibe todo el espacio sobrante
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(450); // Nombre (Grande)
            tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(110); // Categoría
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(55);  // P. Cos (Mini)
            tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(55);  // P. Ven (Mini)
            tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(55);  // P. Tec (Mini)
            tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(45);  // Stock (Mini)
        }
    }

    private void seleccionarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila >= 0) {
            // CUIDADO AQUÍ: Tenemos que usar los índices del "modelo interno" de la tabla original, NO los visuales
            int filaReal = tablaProductos.convertRowIndexToModel(fila);
            idProductoSeleccionado = Integer.parseInt(tablaProductos.getModel().getValueAt(filaReal, 0).toString());
            
            Object codBarras = tablaProductos.getModel().getValueAt(filaReal, 1);
            txtCodigo.setText(codBarras != null ? codBarras.toString() : "");
            
            txtNombre.setText(tablaProductos.getModel().getValueAt(filaReal, 2).toString());
            cmbCategoria.setSelectedItem(tablaProductos.getModel().getValueAt(filaReal, 3).toString());
            
            Object ubic = tablaProductos.getModel().getValueAt(filaReal, 4);
            cmbUbicacion.setSelectedItem(ubic != null ? ubic.toString() : ""); 
            
            txtPrecioCompra.setText(tablaProductos.getModel().getValueAt(filaReal, 5).toString());
            txtPrecioVenta.setText(tablaProductos.getModel().getValueAt(filaReal, 6).toString());
            
            txtStock.setText(tablaProductos.getModel().getValueAt(filaReal, 7).toString());
            txtStock.setEnabled(false); 
            txtStock.setBackground(new Color(236, 240, 241)); 
            txtStock.setToolTipText("Para ajustar el stock use el botón KARDEX");

            txtStockMinimo.setText(tablaProductos.getModel().getValueAt(filaReal, 8).toString());

            if (tablaProductos.getModel().getColumnCount() > 11) {
                Object objProv = tablaProductos.getModel().getValueAt(filaReal, 9);
                int idProv = (objProv != null) ? Integer.parseInt(objProv.toString()) : 0;
                for (int i = 0; i < cmbProveedor.getItemCount(); i++) {
                    if (cmbProveedor.getItemAt(i).getIdProveedor() == idProv) { cmbProveedor.setSelectedIndex(i); break; }
                }
                boolean aplicaTecnico = (boolean) tablaProductos.getModel().getValueAt(filaReal, 10);
                chkAplicaTecnico.setSelected(aplicaTecnico);
                txtPrecioTecnico.setEnabled(aplicaTecnico);
                txtPrecioTecnico.setText(tablaProductos.getModel().getValueAt(filaReal, 11).toString());
            }

            btnGuardar.setEnabled(false); 
            btnActualizar.setEnabled(true); 
            btnImprimirEtiqueta.setEnabled(true);
            btnPrevisualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
            btnKardex.setEnabled(true); 
        }
    }
    
    private void guardarProducto() {
        if (!validarFormulario()) return;
        
        btnGuardar.setEnabled(false); 
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR)); 

        try {
            modelo.Producto p = capturarDatosFormulario();
            dao.ProductoDAO daoProd = new dao.ProductoDAO();
            int idGenerado = daoProd.insertarConId(p);
            
            if (idGenerado != -1) {
                if (p.getCodigoBarras() == null || p.getCodigoBarras().trim().isEmpty()) {
                    String codigoAutomatico = String.format("%011d", idGenerado);
                    daoProd.actualizarCodigoBarras(idGenerado, codigoAutomatico);
                }
                
                utilidades.NotificadorWindows.mostrarAlerta("Inventario Actualizado", "Producto guardado exitosamente en el sistema.", MessageType.INFO);
                
                limpiarFormulario(); 
                cargarTabla("");
                cargarUbicaciones(); 
            } else {
                utilidades.NotificadorWindows.mostrarAlerta("Error", "Error al guardar el producto. Es probable que haya datos inválidos o el código se repita.", MessageType.ERROR);
                btnGuardar.setEnabled(true);
            }
        } finally {
            setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        }
    }

    private void actualizarProducto() {
        if (!validarFormulario()) return;
        modelo.Producto p = capturarDatosFormulario();
        p.setIdProducto(idProductoSeleccionado);
        
        if (new dao.ProductoDAO().actualizar(p)) {
            utilidades.NotificadorWindows.mostrarAlerta("Inventario Actualizado", "Producto actualizado exitosamente.", MessageType.INFO);
            limpiarFormulario(); 
            cargarTabla("");
            cargarUbicaciones(); 
        } else {
            utilidades.NotificadorWindows.mostrarAlerta("Error", "Error al intentar actualizar el producto.", MessageType.ERROR);
        }
    }
    
    private void alternarEstadoProducto() {
        if (idProductoSeleccionado == -1) return;
        boolean esPapelera = chkVerEliminados.isSelected();
        dao.ProductoDAO daoProd = new dao.ProductoDAO();
        
        if (esPapelera) {
            if (daoProd.restaurar(idProductoSeleccionado)) {
                utilidades.NotificadorWindows.mostrarAlerta("Producto Restaurado", "El producto vuelve a estar disponible en el Punto de Venta.", MessageType.INFO);
            }
        } else {
            // Este SÍ se queda con JOptionPane porque es una acción destructiva que requiere confirmación
            int resp = JOptionPane.showConfirmDialog(this, "¿Está seguro de ocultar este producto?\nNo se mostrará en el Punto de Venta.", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                if (daoProd.eliminar(idProductoSeleccionado)) {
                    utilidades.NotificadorWindows.mostrarAlerta("Producto Oculto", "El producto ha sido enviado a la papelera.", MessageType.WARNING);
                }
            }
        }
        limpiarFormulario();
        cargarTabla(txtBuscar.getText().trim());
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty() || cmbCategoria.getSelectedIndex() <= 0) {
            utilidades.NotificadorWindows.mostrarAlerta("Formulario Incompleto", "El nombre y la categoría son obligatorios.", MessageType.WARNING);
            return false;
        }
        try {
            Double.parseDouble(txtPrecioCompra.getText().trim());
            Double.parseDouble(txtPrecioVenta.getText().trim());
            if(chkAplicaTecnico.isSelected()) Double.parseDouble(txtPrecioTecnico.getText().trim());
            Integer.parseInt(txtStock.getText().trim());
            Integer.parseInt(txtStockMinimo.getText().trim());
        } catch (NumberFormatException e) {
            utilidades.NotificadorWindows.mostrarAlerta("Error de Formato", "Verifique que todos los precios y el stock sean números válidos.", MessageType.ERROR);
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
        
        String ubic = "";
        if (cmbUbicacion.getSelectedIndex() > 0) { 
            ubic = cmbUbicacion.getSelectedItem().toString().trim();
        }
        p.setUbicacion(ubic);
        
        return p;
    }

    private void limpiarFormulario() {
        idProductoSeleccionado = -1;
        txtCodigo.setText(""); txtNombre.setText(""); 
        cmbUbicacion.setSelectedIndex(0); 
        cmbCategoria.setSelectedIndex(0); cmbProveedor.setSelectedIndex(0); 
        txtPrecioCompra.setText(""); txtPrecioVenta.setText("");
        txtStock.setText("");
        txtStock.setEnabled(true); 
        txtStock.setBackground(Color.WHITE); 
        txtStock.setToolTipText(null);
        txtStockMinimo.setText("5");
        
        chkAplicaTecnico.setSelected(false);
        txtPrecioTecnico.setText("0.00");
        txtPrecioTecnico.setEnabled(false);
        
        btnGuardar.setEnabled(true); btnActualizar.setEnabled(false); 
        btnImprimirEtiqueta.setEnabled(false); 
        btnPrevisualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnKardex.setEnabled(false); 
        tablaProductos.clearSelection();
    }

    private void mostrarVistaPrevia() {
        if (idProductoSeleccionado == -1) return;
        String codigo = txtCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            utilidades.NotificadorWindows.mostrarAlerta("Aviso", "Primero asigne un código de barras o guarde el producto.", MessageType.WARNING);
            return;
        }
        
        String nombreProd = txtNombre.getText().trim();
        String ubicacion = cmbUbicacion.getSelectedIndex() > 0 ? cmbUbicacion.getSelectedItem().toString() : "BODEGA";
        
        new utilidades.ImpresoraDirecta().previsualizarEtiqueta(nombreProd, codigo, ubicacion);
    }

    private void imprimirEtiquetas() {
        if (idProductoSeleccionado == -1) return;
        String codigo = txtCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            codigo = String.format("%011d", idProductoSeleccionado);
            dao.ProductoDAO daoProd = new dao.ProductoDAO();
            if (daoProd.actualizarCodigoBarras(idProductoSeleccionado, codigo)) {
                txtCodigo.setText(codigo); cargarTabla(txtBuscar.getText().trim()); 
            } else { return; }
        }
        
        String nombreProd = txtNombre.getText().trim();
        String ubicacion = cmbUbicacion.getSelectedIndex() > 0 ? cmbUbicacion.getSelectedItem().toString() : "BODEGA";
        
        boolean exito = new utilidades.ImpresoraDirecta().imprimirEtiquetasDirecto(nombreProd, codigo, ubicacion);
        
        if (exito) {
            utilidades.NotificadorWindows.mostrarAlerta("Impresión Exitosa", "La etiqueta se envió a la impresora.", MessageType.INFO);
        } else {
            utilidades.NotificadorWindows.mostrarAlerta("Error de Impresora", "No se pudo comunicar con la impresora térmica.", MessageType.ERROR);
        }
    }
    
    private void abrirModalNuevaUbicacion() {
        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Nueva Ubicación", true);
        dialogo.setSize(380, 240);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panelFondo = new JPanel(new java.awt.GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Nueva Ubicación");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        panelFondo.add(lblTitulo, gbc);

        JTextField txtNuevoNombre = new JTextField();
        txtNuevoNombre.setPreferredSize(new Dimension(0, 35));
        txtNuevoNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.gridy++; panelFondo.add(new JLabel("Nombre de la nueva vitrina (Ej: Vitrina 5):"), gbc);
        gbc.gridy++; panelFondo.add(txtNuevoNombre, gbc);

        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166)); 
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(46, 204, 113)); 
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        btnGuardar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> {
            String nueva = txtNuevoNombre.getText().trim();
            if (nueva.isEmpty()) {
                utilidades.NotificadorWindows.mostrarAlerta("Aviso", "El nombre de la ubicación no puede estar vacío.", MessageType.WARNING);
                return;
            }

            if (new dao.UbicacionDAO().insertar(nueva)) {
                cargarUbicaciones(); 
                cmbUbicacion.setSelectedItem(nueva);
                utilidades.NotificadorWindows.mostrarAlerta("Ubicación Guardada", "La nueva vitrina se ha agregado al sistema.", MessageType.INFO);
                dialogo.dispose();
            } else {
                utilidades.NotificadorWindows.mostrarAlerta("Error", "La ubicación ya existe o hubo un error al guardarla.", MessageType.ERROR);
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.insets = new Insets(25, 0, 0, 0);
        panelFondo.add(panelBotones, gbc);

        dialogo.add(panelFondo);
        dialogo.setVisible(true);
    }
    
    private void abrirModalEditarUbicacion() {
        if (cmbUbicacion.getSelectedIndex() <= 0) {
            utilidades.NotificadorWindows.mostrarAlerta("Aviso", "Seleccione una ubicación de la lista para poder editarla.", MessageType.WARNING);
            return;
        }

        String nombreAntiguo = cmbUbicacion.getSelectedItem().toString().trim();

        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Modificar Ubicación", true);
        dialogo.setSize(380, 240);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panelFondo = new JPanel(new java.awt.GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Editar Ubicación");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        panelFondo.add(lblTitulo, gbc);

        JTextField txtNuevoNombre = new JTextField(nombreAntiguo);
        txtNuevoNombre.setPreferredSize(new Dimension(0, 35));
        txtNuevoNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.gridy++; panelFondo.add(new JLabel("Nuevo nombre de la vitrina:"), gbc);
        gbc.gridy++; panelFondo.add(txtNuevoNombre, gbc);

        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166)); btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardar = new JButton("Actualizar");
        btnGuardar.setBackground(new Color(243, 156, 18)); 
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnGuardar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> {
            String nombreNuevo = txtNuevoNombre.getText().trim();
            if (nombreNuevo.isEmpty()) {
                utilidades.NotificadorWindows.mostrarAlerta("Aviso", "El nombre de la ubicación no puede estar vacío.", MessageType.WARNING);
                return;
            }
            if (nombreNuevo.equals(nombreAntiguo)) { 
                dialogo.dispose(); 
                return;
            }

            if (new dao.UbicacionDAO().actualizar(nombreAntiguo, nombreNuevo)) {
                cargarUbicaciones(); 
                cmbUbicacion.setSelectedItem(nombreNuevo);
                cargarTabla(txtBuscar.getText().trim()); 
                utilidades.NotificadorWindows.mostrarAlerta("Ubicación Actualizada", "El nombre de la ubicación fue modificado.", MessageType.INFO);
                dialogo.dispose();
            } else {
                utilidades.NotificadorWindows.mostrarAlerta("Error", "Posiblemente el nombre de la ubicación ya exista.", MessageType.ERROR);
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.insets = new Insets(25, 0, 0, 0);
        panelFondo.add(panelBotones, gbc);

        dialogo.add(panelFondo);
        dialogo.setVisible(true);
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