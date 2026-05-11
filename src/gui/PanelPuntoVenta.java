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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

public class PanelPuntoVenta extends JPanel {

    // --- MAGIA DE LA MEMORIA (PATRÓN SINGLETON) ---
    private static PanelPuntoVenta instancia;
    
    public static PanelPuntoVenta getInstancia() {
        if (instancia == null) {
            instancia = new PanelPuntoVenta();
        }
        // Cada vez que se llama, refrescamos la tabla de equipos por si algo cambió en el taller
        instancia.cargarOrdenesPendientesVisuales();
        return instancia;
    }
    // ----------------------------------------------

    private JLabel lblTituloCliente;
    private JPanel panelCliente;
    private JTextField txtClienteAsignado;
    private JButton btnBuscarCliente;
    private JButton btnNuevoCliente; // <--- NUEVO BOTÓN
    
    private JLabel lblEscaner; 
    private JTextField txtCodigoBarras;
    private JButton btnBuscarManual;
    
    private JLabel lblTitKnijico;
    private JButton btnBuscarKnijico;
    private JCheckBox chkPrecioTecnico;
    private JButton btnServicioManual;
    
    private JLabel lblOrden;   
    private JPanel panelOrden; 
    private JTextField txtBuscarOrden;
    private JButton btnVincularOrden;
    
    private JTable tablaPendientes;
    private DefaultTableModel modeloPendientes;
    private JPanel panelPendientes;
    
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotalGlobal;
    private JComboBox<String> cmbMetodoPago;
    private JButton btnCobrar;
    private JButton btnQuitarItem;
    private JButton btnModificarCant; 
    private JButton btnEditarItem; 

    private double totalVenta = 0.0;
    private int idOrdenVinculada = -1; 
    private int idClienteSeleccionado = 0; 
    
    private String[] firmaTemporal = null; 

    // Constructor privado para que nadie pueda crear uno nuevo por accidente y borrar la memoria
    private PanelPuntoVenta() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Punto de Venta y Entregas de Taller");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        add(construirPanelControles(), BorderLayout.WEST);
        add(construirPanelCarrito(), BorderLayout.CENTER);
        
        cargarOrdenesPendientesVisuales(); 

        SwingUtilities.invokeLater(() -> txtCodigoBarras.requestFocus());
    }

    private JPanel construirPanelControles() {
        JPanel panelContenedorIzq = new JPanel(new BorderLayout(0, 15));
        panelContenedorIzq.setPreferredSize(new Dimension(380, 0)); // Lo hice un poquito más ancho
        panelContenedorIzq.setOpaque(false);

        JPanel panel = new JPanel(new GridBagLayout()); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;

        lblTituloCliente = new JLabel("Cliente Asignado:"); lblTituloCliente.setFont(new Font("Segoe UI", Font.BOLD, 14)); panel.add(lblTituloCliente, gbc);

        panelCliente = new JPanel(new BorderLayout(5, 0)); panelCliente.setOpaque(false);
        txtClienteAsignado = new JTextField("Consumidor Final"); txtClienteAsignado.setEditable(false); txtClienteAsignado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtClienteAsignado.setForeground(new Color(41, 128, 185));
        
        // --- BOTONES DE CLIENTE ---
        JPanel pBotonesCliente = new JPanel(new java.awt.GridLayout(1, 2, 5, 0));
        pBotonesCliente.setOpaque(false);
        
        btnBuscarCliente = new JButton("Buscar"); 
        btnBuscarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuscarCliente.addActionListener(e -> seleccionarClientePOS());
        
        btnNuevoCliente = new JButton("+ Nuevo"); 
        btnNuevoCliente.setBackground(new Color(46, 204, 113));
        btnNuevoCliente.setForeground(Color.WHITE);
        btnNuevoCliente.setFocusPainted(false);
        btnNuevoCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        // ¡Cambiado! Llama al nuevo modal
        btnNuevoCliente.addActionListener(e -> abrirModalNuevoCliente());
        
        pBotonesCliente.add(btnBuscarCliente);
        pBotonesCliente.add(btnNuevoCliente);
        
        panelCliente.add(txtClienteAsignado, BorderLayout.CENTER); 
        panelCliente.add(pBotonesCliente, BorderLayout.EAST);
        gbc.gridy++; panel.add(panelCliente, gbc);

        gbc.gridy++; gbc.insets = new Insets(15, 0, 2, 0);
        lblEscaner = new JLabel("Lector / Búsqueda General:"); lblEscaner.setFont(new Font("Segoe UI", Font.BOLD, 14)); panel.add(lblEscaner, gbc);

        txtCodigoBarras = new JTextField(); txtCodigoBarras.setPreferredSize(new Dimension(0, 35)); 
        txtCodigoBarras.setFont(new Font("Consolas", Font.BOLD, 16)); txtCodigoBarras.setHorizontalAlignment(SwingConstants.CENTER);
        txtCodigoBarras.setBackground(new Color(255, 255, 204)); txtCodigoBarras.addActionListener(e -> procesarCodigoBarras());
        gbc.gridy++; gbc.insets = new Insets(2, 0, 5, 0); panel.add(txtCodigoBarras, gbc);
        
        btnBuscarManual = new JButton("Buscar Producto Manual"); btnBuscarManual.setBackground(new Color(189, 195, 199));
        btnBuscarManual.setFocusPainted(false); btnBuscarManual.addActionListener(e -> seleccionarProductoManual());
        gbc.gridy++; panel.add(btnBuscarManual, gbc);

        gbc.gridy++; gbc.insets = new Insets(15, 0, 2, 0);
        lblTitKnijico = new JLabel("Pantallas Knijico:"); lblTitKnijico.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitKnijico.setForeground(new Color(243, 156, 18)); panel.add(lblTitKnijico, gbc);
        
        btnBuscarKnijico = new JButton("Buscar Pantalla Knijico"); btnBuscarKnijico.setBackground(new Color(243, 156, 18));
        btnBuscarKnijico.setForeground(Color.WHITE); btnBuscarKnijico.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBuscarKnijico.setFocusPainted(false); btnBuscarKnijico.addActionListener(e -> buscarPantallaKnijico());
        gbc.gridy++; gbc.insets = new Insets(2, 0, 5, 0); panel.add(btnBuscarKnijico, gbc);
        
        chkPrecioTecnico = new JCheckBox("Aplicar Precio de Técnico (Gremio)"); chkPrecioTecnico.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        chkPrecioTecnico.setForeground(new Color(121, 85, 72)); chkPrecioTecnico.setOpaque(false);
        gbc.gridy++; panel.add(chkPrecioTecnico, gbc);

        gbc.gridy++; gbc.insets = new Insets(15, 0, 2, 0);
        lblOrden = new JLabel("Vincular Orden por Código:"); lblOrden.setFont(new Font("Segoe UI", Font.BOLD, 14)); panel.add(lblOrden, gbc);

        panelOrden = new JPanel(new BorderLayout(5, 0)); panelOrden.setOpaque(false);
        txtBuscarOrden = new JTextField(); txtBuscarOrden.setPreferredSize(new Dimension(0, 35)); 
        txtBuscarOrden.addActionListener(e -> vincularOrdenReparacion()); 
        
        btnVincularOrden = new JButton("Vincular"); btnVincularOrden.setBackground(new Color(52, 152, 219));
        btnVincularOrden.setForeground(Color.WHITE); btnVincularOrden.setFocusPainted(false);
        btnVincularOrden.addActionListener(e -> vincularOrdenReparacion());

        panelOrden.add(txtBuscarOrden, BorderLayout.CENTER); panelOrden.add(btnVincularOrden, BorderLayout.EAST);
        gbc.gridy++; gbc.insets = new Insets(2, 0, 5, 0); panel.add(panelOrden, gbc);

        btnServicioManual = new JButton("+ Agregar Servicio Rápido"); btnServicioManual.setBackground(new Color(149, 165, 166));
        btnServicioManual.setForeground(Color.WHITE); btnServicioManual.setFocusPainted(false);
        btnServicioManual.addActionListener(e -> agregarServicioManual());
        gbc.gridy++; gbc.insets = new Insets(5, 0, 10, 0); panel.add(btnServicioManual, gbc);

        // --- TABLA DE VER EQUIPOS ---
        panelPendientes = new JPanel(new BorderLayout()); panelPendientes.setBackground(Color.WHITE);
        panelPendientes.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        JLabel lblPend = new JLabel("Ver Equipos:"); lblPend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPend.setForeground(new Color(127, 140, 141)); panelPendientes.add(lblPend, BorderLayout.NORTH);

        modeloPendientes = new DefaultTableModel(new String[]{"Orden", "Cliente", "Equipo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaPendientes = new JTable(modeloPendientes);
        tablaPendientes.setRowHeight(30); 
        tablaPendientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        tablaPendientes.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaPendientes.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaPendientes.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        tablaPendientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaPendientes.getSelectedRow() != -1) {
                    String idOrden = modeloPendientes.getValueAt(tablaPendientes.getSelectedRow(), 0).toString().replace("#", "");
                    txtBuscarOrden.setText(idOrden); 
                    vincularOrdenReparacion();
                }
            }
        });

        panelPendientes.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);

        panelContenedorIzq.add(panel, BorderLayout.NORTH); 
        panelContenedorIzq.add(panelPendientes, BorderLayout.CENTER); 

        return panelContenedorIzq;
    }

    private JPanel construirPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(0, 15)); panel.setOpaque(false);

        String[] columnas = {"ID", "Descripción", "Cant.", "Precio U.", "Subtotal", "StockMax", "IMEI", "DiasGarantia"};
        modeloCarrito = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tablaCarrito.setRowHeight(30); tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        for(int i = 5; i <= 7; i++) {
            tablaCarrito.getColumnModel().getColumn(i).setMinWidth(0);
            tablaCarrito.getColumnModel().getColumn(i).setMaxWidth(0);
            tablaCarrito.getColumnModel().getColumn(i).setWidth(0);
        }

        JScrollPane scroll = new JScrollPane(tablaCarrito); scroll.getViewport().setBackground(Color.WHITE);
        
        btnModificarCant = new JButton("Modificar Cant."); btnModificarCant.setBackground(new Color(52, 152, 219)); 
        btnModificarCant.setForeground(Color.WHITE); btnModificarCant.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModificarCant.setPreferredSize(new Dimension(160, 35)); btnModificarCant.setFocusPainted(false);
        btnModificarCant.setEnabled(false);
        btnModificarCant.addActionListener(e -> modificarCantidadCarrito());

        tablaCarrito.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tablaCarrito.getSelectedRow();
                if (fila >= 0) {
                    String descripcion = modeloCarrito.getValueAt(fila, 1).toString();
                    if (descripcion.startsWith("Orden #")) {
                        btnModificarCant.setEnabled(false);
                    } else {
                        btnModificarCant.setEnabled(true); 
                    }
                } else {
                    btnModificarCant.setEnabled(false);
                }
            }
        });

        btnQuitarItem = new JButton("Quitar Producto"); btnQuitarItem.setBackground(new Color(231, 76, 60)); 
        btnQuitarItem.setForeground(Color.WHITE); btnQuitarItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnQuitarItem.setPreferredSize(new Dimension(160, 35)); btnQuitarItem.setFocusPainted(false);
        btnQuitarItem.addActionListener(e -> quitarItemCarrito());

        btnEditarItem = new JButton("Modificar Precio / Detalles"); 
        btnEditarItem.setBackground(new Color(243, 156, 18)); 
        btnEditarItem.setForeground(Color.WHITE); 
        btnEditarItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEditarItem.setPreferredSize(new Dimension(210, 35)); 
        btnEditarItem.setFocusPainted(false);
        btnEditarItem.addActionListener(e -> editarItemSeleccionado());

        JPanel panelBotonesTabla = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        panelBotonesTabla.setOpaque(false); panelBotonesTabla.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        panelBotonesTabla.add(btnEditarItem); 
        panelBotonesTabla.add(btnModificarCant); 
        panelBotonesTabla.add(btnQuitarItem);

        JPanel panelTablaSup = new JPanel(new BorderLayout()); panelTablaSup.setOpaque(false);
        panelTablaSup.add(scroll, BorderLayout.CENTER); panelTablaSup.add(panelBotonesTabla, BorderLayout.SOUTH);
        panel.add(panelTablaSup, BorderLayout.CENTER);

        JPanel panelCobro = new JPanel(new BorderLayout(20, 0)); panelCobro.setBackground(Color.WHITE);
        panelCobro.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        lblTotalGlobal = new JLabel("TOTAL: L. 0.00"); lblTotalGlobal.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
        lblTotalGlobal.setForeground(new Color(46, 204, 113)); panelCobro.add(lblTotalGlobal, BorderLayout.WEST);

        JPanel panelAccionesCobro = new JPanel(new GridBagLayout()); panelAccionesCobro.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints(); gc.insets = new Insets(0, 10, 0, 10);
        
        cmbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        cmbMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 14)); cmbMetodoPago.setPreferredSize(new Dimension(150, 45));
        
        btnCobrar = new JButton("COBRAR E IMPRIMIR"); btnCobrar.setBackground(new Color(39, 174, 96)); 
        btnCobrar.setForeground(Color.WHITE); btnCobrar.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        btnCobrar.setPreferredSize(new Dimension(220, 45)); btnCobrar.setFocusPainted(false);
        btnCobrar.addActionListener(e -> procesarCobroFinal());

        panelAccionesCobro.add(new JLabel("Pago con:"), gc); panelAccionesCobro.add(cmbMetodoPago, gc); panelAccionesCobro.add(btnCobrar, gc);
        panelCobro.add(panelAccionesCobro, BorderLayout.EAST); panel.add(panelCobro, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarOrdenesPendientesVisuales() {
        modeloPendientes.setRowCount(0);
        String sql = "SELECT o.id_orden, CONCAT(c.nombre, ' ', c.apellido) as cliente, e.modelo " +
                     "FROM ordenes_reparacion o " +
                     "INNER JOIN equipos_registrados e ON o.id_equipo = e.id_equipo " +
                     "INNER JOIN clientes c ON e.id_cliente = c.id_cliente " +
                     "WHERE o.estado NOT IN ('Entregado', 'Cancelado') " +
                     "ORDER BY o.id_orden DESC";
        
        try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modeloPendientes.addRow(new Object[]{"#" + rs.getInt("id_orden"), rs.getString("cliente"), rs.getString("modelo")});
            }
        } catch (Exception e) {}
    }

    private void seleccionarClientePOS() {
        dao.ClienteDAO daoC = new dao.ClienteDAO();
        List<modelo.Cliente> clientes = daoC.listar();
        
        // --- MAGIA UX: Invertir lista para que los más nuevos salgan arriba ---
        java.util.Collections.reverse(clientes);
        
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Directorio de Clientes", true);
        dialog.setSize(600, 450); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10)); dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panelNorte = new JPanel(new BorderLayout(5, 0)); panelNorte.setBackground(Color.WHITE); panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtBuscarC = new JTextField(); txtBuscarC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelNorte.add(new JLabel("Buscar Cliente: "), BorderLayout.WEST); panelNorte.add(txtBuscarC, BorderLayout.CENTER);
        
        String[] col = {"ID", "Identidad", "Nombre Completo", "Teléfono"};
        DefaultTableModel modC = new DefaultTableModel(col, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        
        // Consumidor final siempre fijo en la primera línea
        modC.addRow(new Object[]{0, "N/A", "Consumidor Final", "N/A"}); 
        for (modelo.Cliente c : clientes) modC.addRow(new Object[]{c.getIdCliente(), c.getNumeroIdentidad(), c.getNombre() + " " + c.getApellido(), c.getTelefono()});
        
        JTable tablaC = new JTable(modC); tablaC.setRowHeight(30); tablaC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaC.getColumnModel().getColumn(0).setPreferredWidth(50); tablaC.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modC); tablaC.setRowSorter(sorter);
        txtBuscarC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = txtBuscarC.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null); 
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        
        tablaC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaC.getSelectedRow() != -1) {
                    idClienteSeleccionado = (int) tablaC.getValueAt(tablaC.getSelectedRow(), 0);
                    txtClienteAsignado.setText(tablaC.getValueAt(tablaC.getSelectedRow(), 2).toString());
                    dialog.dispose();
                }
            }
        });
        
        dialog.add(panelNorte, BorderLayout.NORTH); dialog.add(new JScrollPane(tablaC), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void seleccionarProductoManual() {
        dao.ProductoDAO daoP = new dao.ProductoDAO();
        List<Object[]> resultados = daoP.buscarProductoParaVenta("");                                                                                                                                                                                                                                                                                                                                                                    
        
        // --- INVERTIR PARA VER LO MÁS NUEVO ARRIBA ---
        java.util.Collections.reverse(resultados);
        
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Inventario General", true);
        dialog.setSize(950, 480); // Ventana más ancha para los dos filtros
        dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10)); dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panelNorte = new JPanel(new java.awt.GridBagLayout()); 
        panelNorte.setBackground(Color.WHITE); panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // --- EXTRACTOR AUTOMÁTICO DE CATEGORÍAS ---
        java.util.Set<String> categorias = new java.util.TreeSet<>();
        for(Object[] r : resultados) if(r[3] != null) categorias.add(r[3].toString());
        
        JComboBox<String> cmbCategoria = new JComboBox<>();
        cmbCategoria.addItem("Todas las Categorías");
        for(String c : categorias) cmbCategoria.addItem(c);
        cmbCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField txtBuscarP = new JTextField(); txtBuscarP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0; panelNorte.add(new JLabel("Categoría: "), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3; panelNorte.add(cmbCategoria, gbc);
        gbc.gridx = 2; gbc.weightx = 0; panelNorte.add(new JLabel("  Buscar Producto: "), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7; panelNorte.add(txtBuscarP, gbc);
        
        String[] col = {"ID", "Categoría", "Nombre Producto", "Ubicación", "Precio V.", "Stock"};
        DefaultTableModel modP = new DefaultTableModel(col, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        for (Object[] p : resultados) modP.addRow(new Object[]{p[0], p[3], p[2], p[4], p[6], p[7]});
        
        JTable tablaP = new JTable(modP); tablaP.setRowHeight(30); tablaP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaP.getColumnModel().getColumn(0).setPreferredWidth(50); tablaP.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modP); tablaP.setRowSorter(sorter);
        
        // --- LÓGICA DE FILTRO COMBINADO ---
        Runnable aplicarFiltros = () -> {
            java.util.List<RowFilter<Object,Object>> filtros = new java.util.ArrayList<>();
            String texto = txtBuscarP.getText().trim();
            if (texto.length() > 0) filtros.add(RowFilter.regexFilter("(?i)" + texto));
            if (cmbCategoria.getSelectedIndex() > 0) {
                // Filtramos exactamente la columna 1 (Categoría) para evitar errores entre "Cable" y "Cable USB"
                String catElegida = java.util.regex.Pattern.quote(cmbCategoria.getSelectedItem().toString());
                filtros.add(RowFilter.regexFilter("^" + catElegida + "$", 1)); 
            }
            if (filtros.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.andFilter(filtros));
        };
        
        txtBuscarP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { aplicarFiltros.run(); }
        });
        cmbCategoria.addActionListener(e -> aplicarFiltros.run());
        
        tablaP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaP.getSelectedRow() != -1) {
                    int filaReal = tablaP.convertRowIndexToModel(tablaP.getSelectedRow());
                    Object[] productoCompleto = resultados.get(filaReal); 
                    
                    int stock = Integer.parseInt(productoCompleto[7].toString()); 
                    if (stock <= 0) {
                        JOptionPane.showMessageDialog(dialog, "¡Sin Stock!", "Agotado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    boolean aplicaTecnico = (boolean) productoCompleto[10];
                    double precioCobrar = Double.parseDouble(productoCompleto[6].toString());
                    String nombreDesc = productoCompleto[2].toString();
                    String ubic = productoCompleto[4] != null ? productoCompleto[4].toString() : "";
                    if(!ubic.isEmpty()) nombreDesc += " [" + ubic + "]";
                    
                    if (chkPrecioTecnico.isSelected() && aplicaTecnico) {
                        precioCobrar = Double.parseDouble(productoCompleto[11].toString()); 
                        nombreDesc += " (Precio Gremio)";
                    }
                    
                    int idProducto = (int)productoCompleto[0];
                    int diasGarantia = daoP.obtenerDiasGarantia(idProducto);
                    String imei = "";
                    
                    if (diasGarantia > 0) {
                        imei = JOptionPane.showInputDialog(dialog, 
                            "El equipo tiene " + diasGarantia + " días de garantía.\nIngrese el IMEI / Serie:", 
                            "Garantía", JOptionPane.WARNING_MESSAGE);
                            
                        if (imei == null || imei.trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Venta cancelada. El IMEI es obligatorio.");
                            return; 
                        }
                        nombreDesc += " | IMEI: " + imei; 
                    }
                    
                    agregarAlCarrito(idProducto, nombreDesc, 1, precioCobrar, stock, imei, diasGarantia);
                    dialog.dispose();
                }
            }
        });
        
        dialog.add(panelNorte, BorderLayout.NORTH); dialog.add(new JScrollPane(tablaP), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void buscarPantallaKnijico() {
        dao.KnijicoDAO daoK = new dao.KnijicoDAO();
        List<Object[]> resultados = daoK.listarPantallas("", false, 0); 
        
        // --- INVERTIR PARA VER LO MÁS NUEVO ARRIBA ---
        java.util.Collections.reverse(resultados);
        
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Inventario Knijico", true);
        dialog.setSize(950, 480); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10)); dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panelNorte = new JPanel(new java.awt.GridBagLayout()); 
        panelNorte.setBackground(Color.WHITE); panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // --- EXTRACTOR AUTOMÁTICO DE LOTES ---
        java.util.Set<String> lotes = new java.util.TreeSet<>();
        for(Object[] p : resultados) if(p[1] != null) lotes.add(p[1].toString());
        
        JComboBox<String> cmbLote = new JComboBox<>();
        cmbLote.addItem("Todos los Lotes");
        for(String l : lotes) cmbLote.addItem(l);
        cmbLote.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField txtBuscarK = new JTextField(); txtBuscarK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL; gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0; panelNorte.add(new JLabel("Lote: "), gbc);
        gbc.gridx = 1; gbc.weightx = 0.3; panelNorte.add(cmbLote, gbc);
        gbc.gridx = 2; gbc.weightx = 0; panelNorte.add(new JLabel("  Buscar Modelo: "), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7; panelNorte.add(txtBuscarK, gbc);
        
        String[] col = {"ID", "Lote", "Modelo", "Stock", "P. Cliente", "P. Técnico"};
        DefaultTableModel modK = new DefaultTableModel(col, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        for (Object[] p : resultados) modK.addRow(new Object[]{p[0], p[1], p[2], p[6], p[4], p[5]});
        
        JTable tablaK = new JTable(modK); tablaK.setRowHeight(30); tablaK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaK.getColumnModel().getColumn(0).setPreferredWidth(40); tablaK.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modK); tablaK.setRowSorter(sorter);
        
        // --- LÓGICA DE FILTRO COMBINADO ---
        Runnable aplicarFiltros = () -> {
            java.util.List<RowFilter<Object,Object>> filtros = new java.util.ArrayList<>();
            String texto = txtBuscarK.getText().trim();
            if (texto.length() > 0) filtros.add(RowFilter.regexFilter("(?i)" + texto));
            if (cmbLote.getSelectedIndex() > 0) {
                // Filtramos exactamente la columna 1 (Lote)
                String loteElegido = java.util.regex.Pattern.quote(cmbLote.getSelectedItem().toString());
                filtros.add(RowFilter.regexFilter("^" + loteElegido + "$", 1)); 
            }
            if (filtros.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.andFilter(filtros));
        };
        
        txtBuscarK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { aplicarFiltros.run(); }
        });
        cmbLote.addActionListener(e -> aplicarFiltros.run());
        
        tablaK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaK.getSelectedRow() != -1) {
                    int filaReal = tablaK.convertRowIndexToModel(tablaK.getSelectedRow());
                    int idOriginal = (int)modK.getValueAt(filaReal, 0);
                    int stock = Integer.parseInt(modK.getValueAt(filaReal, 3).toString());
                    
                    if (stock <= 0) {
                        JOptionPane.showMessageDialog(dialog, "¡Sin Stock!", "Agotado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    int idVirtual = 70000 + idOriginal;
                    
                    double precioACobrar = chkPrecioTecnico.isSelected() ? Double.parseDouble(modK.getValueAt(filaReal, 5).toString()) : Double.parseDouble(modK.getValueAt(filaReal, 4).toString());
                    String desc = "PANTALLA KNIJICO: " + modK.getValueAt(filaReal, 2) + (chkPrecioTecnico.isSelected() ? " (Precio Gremio)" : "");
                    
                    agregarAlCarrito(idVirtual, desc, 1, precioACobrar, stock);
                    dialog.dispose();
                }
            }
        });
        
        dialog.add(panelNorte, BorderLayout.NORTH); dialog.add(new JScrollPane(tablaK), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void procesarCodigoBarras() {
        String codigo = txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) return;

        dao.ProductoDAO daoProd = new dao.ProductoDAO();
        modelo.Producto p = daoProd.buscarPorCodigo(codigo);

        if (p != null) {
            if (p.getStock() <= 0) {
                JOptionPane.showMessageDialog(this, "¡Sin Stock!", "Agotado", JOptionPane.ERROR_MESSAGE);
            } else {
                double precioCobrar = p.getPrecioVenta();
                String desc = p.getNombreProducto();
                if(p.getUbicacion() != null && !p.getUbicacion().isEmpty()) desc += " [" + p.getUbicacion() + "]";
                if (chkPrecioTecnico.isSelected() && p.isAplicaPrecioTecnico()) {
                    precioCobrar = p.getPrecioTecnico();
                    desc += " (Precio Gremio)";
                }
                
                int diasGarantia = daoProd.obtenerDiasGarantia(p.getIdProducto());
                String imei = "";
                
                if (diasGarantia > 0) {
                    imei = JOptionPane.showInputDialog(this, 
                        "El equipo '" + p.getNombreProducto() + "' tiene " + diasGarantia + " días de garantía.\n\nPor favor, escanee o ingrese el IMEI / Número de Serie:", 
                        "Captura de Garantía Obligatoria", 
                        JOptionPane.WARNING_MESSAGE);
                        
                    if (imei == null || imei.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Venta cancelada. El IMEI es obligatorio para generar la garantía.", "Operación Cancelada", JOptionPane.ERROR_MESSAGE);
                        txtCodigoBarras.setText(""); txtCodigoBarras.requestFocus();
                        return; 
                    }
                    desc += " | IMEI: " + imei; 
                }
                
                agregarAlCarrito(p.getIdProducto(), desc, 1, precioCobrar, p.getStock(), imei, diasGarantia);
            }
        } 
        else {
            dao.KnijicoDAO daoK = new dao.KnijicoDAO();
            Object[] pantalla = daoK.buscarPorCodigoBarra(codigo); 

            if (pantalla != null) {
                int idReal = (int) pantalla[0];
                int stock = (int) pantalla[6];
                
                if (stock <= 0) {
                    JOptionPane.showMessageDialog(this, "¡Pantalla agotada!", "Sin Stock", JOptionPane.ERROR_MESSAGE);
                } else {
                    double precio = chkPrecioTecnico.isSelected() ? (double) pantalla[5] : (double) pantalla[4];
                    String desc = "PANTALLA KNIJICO: " + pantalla[2];
                    
                    agregarAlCarrito(70000 + idReal, desc, 1, precio, stock, "", 0); 
                }
            } else {
                JOptionPane.showMessageDialog(this, "Código no registrado en sistema.", "No encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
        txtCodigoBarras.setText("");
        txtCodigoBarras.requestFocus();
    }

    private void agregarAlCarrito(int idProd, String desc, int cant, double precioU, int maxStock, String imei, int diasGarantia) {
        if (maxStock != -1 && idProd != 0) { 
            int cantidadYaEnCarrito = 0;
            for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                int idExistente = Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString());
                if (idExistente == idProd) cantidadYaEnCarrito += Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString());
            }
            if ((cantidadYaEnCarrito + cant) > maxStock) {
                JOptionPane.showMessageDialog(this, "Stock insuficiente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            int idExistente = Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString());
            String descExistente = modeloCarrito.getValueAt(i, 1).toString();
            double precioExistente = Double.parseDouble(modeloCarrito.getValueAt(i, 3).toString());
            
            if (idExistente == idProd && idProd != 0 && descExistente.equals(desc) && precioExistente == precioU && !desc.startsWith("Orden #") && imei.isEmpty()) { 
                int cantActual = Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString());
                modeloCarrito.setValueAt(cantActual + cant, i, 2);
                modeloCarrito.setValueAt((cantActual + cant) * precioU, i, 4);
                recalcularTotal();
                return;
            }
        }
        
        modeloCarrito.addRow(new Object[]{idProd, desc, cant, precioU, cant * precioU, maxStock, imei, diasGarantia});
        recalcularTotal();
    }

    private void agregarAlCarrito(int idProd, String desc, int cant, double precioU, int maxStock) {
        agregarAlCarrito(idProd, desc, cant, precioU, maxStock, "", 0);
    }

    private void modificarCantidadCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            if (modeloCarrito.getValueAt(fila, 1).toString().startsWith("Orden #")) {
                JOptionPane.showMessageDialog(this, "Órdenes tienen cantidad fija de 1.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String cantStr = JOptionPane.showInputDialog(this, "Nueva cantidad:", modeloCarrito.getValueAt(fila, 2).toString());
            if (cantStr == null || cantStr.trim().isEmpty()) return;
            try {
                int nuevaCant = Integer.parseInt(cantStr);
                if (nuevaCant <= 0) { quitarItemCarrito(); return; }
                int maxStock = Integer.parseInt(modeloCarrito.getValueAt(fila, 5).toString());
                int idProdActual = Integer.parseInt(modeloCarrito.getValueAt(fila, 0).toString());
                
                if (maxStock != -1 && idProdActual != 0) {
                    int cantidadEnOtrasFilas = 0;
                    for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                        if (i != fila && Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString()) == idProdActual) {
                            cantidadEnOtrasFilas += Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString());
                        }
                    }
                    if ((cantidadEnOtrasFilas + nuevaCant) > maxStock) {
                        JOptionPane.showMessageDialog(this, "Stock insuficiente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                double precioU = Double.parseDouble(modeloCarrito.getValueAt(fila, 3).toString());
                modeloCarrito.setValueAt(nuevaCant, fila, 2);
                modeloCarrito.setValueAt(nuevaCant * precioU, fila, 4);
                recalcularTotal();
            } catch (Exception ex) {}
        }
    }

    private void quitarItemCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            if (modeloCarrito.getValueAt(fila, 1).toString().startsWith("Orden #")) {
                idOrdenVinculada = -1; 
                firmaTemporal = null; 
                
                txtBuscarOrden.setEnabled(true); 
                btnVincularOrden.setEnabled(true);
                
                tablaPendientes.setEnabled(true);
                tablaPendientes.setBackground(Color.WHITE);
                tablaPendientes.setForeground(Color.BLACK);
                
                idClienteSeleccionado = 0;
                txtClienteAsignado.setText("Consumidor Final");
            }
            
            modeloCarrito.removeRow(fila); 
            recalcularTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recalcularTotal() {
        totalVenta = 0.0;
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            totalVenta += Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString());
        }
        lblTotalGlobal.setText(String.format("TOTAL: L. %.2f", totalVenta));
    }

    private void agregarServicioManual() {
        String desc = JOptionPane.showInputDialog(this, "Descripción:");
        if (desc == null || desc.trim().isEmpty()) return;
        String precioStr = JOptionPane.showInputDialog(this, "Precio:");
        if (precioStr == null || precioStr.trim().isEmpty()) return;
        try { agregarAlCarrito(0, "Servicio: " + desc, 1, Double.parseDouble(precioStr), -1); } 
        catch (Exception ex) {}
    }

    private void vincularOrdenReparacion() {
        String idStr = txtBuscarOrden.getText().trim();
        if (idStr.isEmpty()) return;
        dao.OrdenReparacionDAO daoOrden = new dao.OrdenReparacionDAO();
        List<Object[]> resultados = daoOrden.buscarOrden(idStr);
        if (resultados.isEmpty()) return;
        Object[] ord = resultados.get(0); 
        if (ord[4].toString().equalsIgnoreCase("Entregado")) return;

        idOrdenVinculada = Integer.parseInt(ord[0].toString());
        
        txtClienteAsignado.setText(ord[1].toString()); 
        if (ord.length > 7 && ord[7] != null) {
            idClienteSeleccionado = Integer.parseInt(ord[7].toString()); 
        }

        agregarAlCarrito(0, "Orden #" + idOrdenVinculada + " - Rep: " + ord[2], 1, Double.parseDouble(ord[5].toString()), 1); 
        
        txtBuscarOrden.setText(""); txtBuscarOrden.setEnabled(false); btnVincularOrden.setEnabled(false);
        tablaPendientes.setEnabled(false);
        tablaPendientes.clearSelection();
        tablaPendientes.setBackground(new Color(240, 240, 240));
        tablaPendientes.setForeground(Color.GRAY);
    }

    private void procesarCobroFinal() {
        if (modeloCarrito.getRowCount() == 0) return;

        if (totalVenta > 0) {
            if (cmbMetodoPago.getSelectedItem().toString().equals("Efectivo")) {
                System.out.println("Procesando pago en efectivo por: L. " + totalVenta);
            }
        } else {
            System.out.println("Venta de cortesía (L. 0.00). Saltando validación de efectivo.");
        }

        String[] datosFirma;
        if (firmaTemporal != null) {
            datosFirma = firmaTemporal; 
        } else {
            datosFirma = solicitarFirmaUsuario(); 
            if (datosFirma == null) return; 
        }
        
        int idCajeroFirma = Integer.parseInt(datosFirma[0]);
        String nombreCajeroFirma = datosFirma[1];

        btnCobrar.setEnabled(false); setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        
        modelo.Venta venta = new modelo.Venta();
        venta.setIdCliente(idClienteSeleccionado); 
        venta.setIdUsuario(idCajeroFirma); 
        venta.setIdOrden(idOrdenVinculada); 
        venta.setTotal(totalVenta); 
        venta.setMetodoPago(cmbMetodoPago.getSelectedItem().toString());

        List<modelo.DetalleVenta> listaDetalles = new ArrayList<>();
        double costoSoloReparacion = 0.0; // <-- NUEVA VARIABLE para aislar el costo del taller

        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            modelo.DetalleVenta dv = new modelo.DetalleVenta();
            int idCarrito = Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString());
            String descripcionItem = modeloCarrito.getValueAt(i, 1).toString();
            
            if (idCarrito >= 70000) {
                // Dejamos el ID virtual intacto para que VentaDAO sepa que es Knijico
                dv.setIdProducto(idCarrito); 
            } else {
                dv.setIdProducto(idCarrito); 
            }
            dv.setDescripcion(descripcionItem);
            dv.setCantidad(Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString()));
            dv.setPrecioUnitario(Double.parseDouble(modeloCarrito.getValueAt(i, 3).toString()));
            dv.setSubtotal(Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString()));
            dv.setImei(modeloCarrito.getValueAt(i, 6) != null ? modeloCarrito.getValueAt(i, 6).toString() : "");
            dv.setDiasGarantia(Integer.parseInt(modeloCarrito.getValueAt(i, 7).toString()));
            
            // Si el ítem actual es la orden, guardamos su precio aparte para el PDF
            if (descripcionItem.startsWith("Orden #")) {
                costoSoloReparacion = dv.getSubtotal();
            }
            
            listaDetalles.add(dv);
        }

        dao.VentaDAO daoVenta = new dao.VentaDAO();
        int idRecibo = daoVenta.registrarVentaCompleta(venta, listaDetalles);

        if (idRecibo != -1) {
            JOptionPane.showMessageDialog(this, "¡Éxito!\nTransacción #" + idRecibo + "\nCajero/Técnico: " + nombreCajeroFirma.toUpperCase());
            
            // =========================================================================
            // 1. IMPRESIÓN DEL RECIBO DE CAJA (SIEMPRE SE IMPRIME)
            // =========================================================================
            utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta(); 
            impresora.imprimirReciboVenta(idRecibo);
            
            // Imprimir pólizas de garantía si hay productos que lo requieran
            for (modelo.DetalleVenta dv : listaDetalles) {
                if (dv.getDiasGarantia() > 0) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    String fCompra = sdf.format(cal.getTime());
                    cal.add(java.util.Calendar.DAY_OF_YEAR, dv.getDiasGarantia());
                    String fVence = sdf.format(cal.getTime());
                    
                    String nomCliente = txtClienteAsignado.getText();
                    
                    impresora.imprimirPolizaGarantia(
                        String.valueOf(idRecibo), fCompra, fVence, nomCliente, "VER REGISTRO", 
                        dv.getDescripcion(), dv.getImei(), dv.getDiasGarantia(), "ARTICULO"
                    );
                }
            }

            // =========================================================================
            // 2. IMPRESIÓN DEL PDF DE TALLER (SOLO SI HAY UNA ORDEN VINCULADA)
            // =========================================================================
            if (idOrdenVinculada != -1) {
                try {
                    dao.OrdenReparacionDAO daoOrden = new dao.OrdenReparacionDAO();
                    String[] textos = daoOrden.obtenerTextosOrden(idOrdenVinculada);
                    String fallaOr = (textos[0] != null && !textos[0].isEmpty()) ? textos[0] : "Revisión general";
                    String trabOr = (textos[1] != null && !textos[1].isEmpty()) ? textos[1] : "Revisión técnica general.";
                    String claveOr = (textos.length > 2 && textos[2] != null) ? textos[2] : "Sin Clave";
                    String fechaOr = daoOrden.obtenerFechaOrden(idOrdenVinculada);
                    
                    String cliOr = txtClienteAsignado.getText();
                    String modOr = "";
                    String tipoOr = "";
                    
                    String q = "SELECT e.modelo FROM ordenes_reparacion o JOIN equipos_registrados e ON o.id_equipo = e.id_equipo WHERE o.id_orden = ?";
                    try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
                         java.sql.PreparedStatement ps = con.prepareStatement(q)) {
                        ps.setInt(1, idOrdenVinculada);
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) modOr = rs.getString("modelo");
                        }
                    }
                    
                    String equipoConClave = modOr + "  |  Clave: " + claveOr;
                    String tecnico = nombreCajeroFirma; 
                    
                    utilidades.GeneradorPDF generador = new utilidades.GeneradorPDF();
                    // Usamos costoSoloReparacion en lugar de totalVenta
                    generador.crearTicket(
                        String.valueOf(idOrdenVinculada), fechaOr, cliOr, equipoConClave, fallaOr, String.valueOf(costoSoloReparacion),
                        "SAIRTECH - TECNOLOGIA", "Santa Barbara, Barrio La Soledad, Frente a Sastreria La Elegancia", "8951-8040",
                        "OJO no aplica garantia en equipos mojados, pantallas no cuentan con garantía.",
                        tecnico, trabOr, false, tipoOr, true
                    );
                    
                    // Actualizamos la orden con su costo real individual
                    daoOrden.actualizarEstadoYCosto(idOrdenVinculada, "Entregado", costoSoloReparacion);
                    daoOrden.marcarComoEntregado(idOrdenVinculada, idCajeroFirma);

                } catch (Exception ex) {
                    System.err.println("Error al crear PDF de entrega: " + ex.getMessage());
                }
            } 
            
            // =========================================================================
            // 3. LIMPIEZA DE PANTALLA Y MEMORIA
            // =========================================================================
            modeloCarrito.setRowCount(0); recalcularTotal();
            idOrdenVinculada = -1; 
            firmaTemporal = null; 
            txtBuscarOrden.setEnabled(true); btnVincularOrden.setEnabled(true);
            tablaPendientes.setEnabled(true); tablaPendientes.setBackground(Color.WHITE); tablaPendientes.setForeground(Color.BLACK);
            idClienteSeleccionado = 0; txtClienteAsignado.setText("Consumidor Final");
            cargarOrdenesPendientesVisuales(); 
            txtCodigoBarras.requestFocus();
        }
        btnCobrar.setEnabled(true); setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }
    
    private String[] solicitarFirmaUsuario() {
        javax.swing.JPasswordField txtPass = new javax.swing.JPasswordField();
        Object[] mensaje = {"Ingrese su PIN / Contraseña para autorizar:", txtPass};

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Firma de Cajero / Técnico", 
                                     JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                                     
        if (opcion == JOptionPane.OK_OPTION) {
            String clave = new String(txtPass.getPassword());
            dao.UsuarioDAO daoUsuario = new dao.UsuarioDAO();
            
            String nombreTecnico = daoUsuario.obtenerUsuarioPorClave(clave);
            
            if (nombreTecnico != null) {
                int idTecnico = daoUsuario.obtenerIdPorNombre(nombreTecnico);
                return new String[]{String.valueOf(idTecnico), nombreTecnico}; 
            } else {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta o no registrada.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
    }
    
    private void editarItemSeleccionado() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem del carrito para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String descripcion = modeloCarrito.getValueAt(fila, 1).toString();

        if (descripcion.startsWith("Orden #")) {
            editarDetallesOrden(fila);
        } else {
            modificarPrecioProductoLibre(fila, descripcion);
        }
    }

    private void modificarPrecioProductoLibre(int filaCarrito, String descripcion) {
        String precioActual = modeloCarrito.getValueAt(filaCarrito, 3).toString();
        
        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Modificar Precio Manual", true);
        dialogo.setSize(400, 220);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblTitulo = new JLabel("Ajuste de Precio");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(44, 62, 80));
        
        JLabel lblDesc = new JLabel(descripcion.length() > 35 ? descripcion.substring(0,35) + "..." : descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDesc.setForeground(Color.GRAY);

        JTextField txtNuevoPrecio = new JTextField(precioActual);
        txtNuevoPrecio.setPreferredSize(new Dimension(0, 35));
        txtNuevoPrecio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtNuevoPrecio.setForeground(new Color(39, 174, 96));

        gbc.gridy = 0; panelFondo.add(lblTitulo, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 15, 0); panelFondo.add(lblDesc, gbc);
        gbc.gridy++; gbc.insets = new Insets(5, 0, 5, 0); panelFondo.add(new JLabel("Nuevo Precio a Cobrar (L.):"), gbc);
        gbc.gridy++; panelFondo.add(txtNuevoPrecio, gbc);

        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166)); btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(52, 152, 219)); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnGuardar.setFocusPainted(false);
        
        btnGuardar.addActionListener(e -> {
            try {
                double nuevoPrecio = Double.parseDouble(txtNuevoPrecio.getText().trim());
                if(nuevoPrecio < 0) throw new NumberFormatException();
                
                int cantidad = Integer.parseInt(modeloCarrito.getValueAt(filaCarrito, 2).toString());
                modeloCarrito.setValueAt(nuevoPrecio, filaCarrito, 3);
                modeloCarrito.setValueAt(nuevoPrecio * cantidad, filaCarrito, 4);
                recalcularTotal();
                dialogo.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnCancelar); panelBotones.add(btnGuardar);
        panelBotones.setPreferredSize(new Dimension(0, 40));

        gbc.gridy++; gbc.insets = new Insets(15, 0, 0, 0); panelFondo.add(panelBotones, gbc);

        dialogo.add(panelFondo);
        dialogo.setVisible(true);
    }

    private void editarDetallesOrden(int filaCarrito) {
        if (idOrdenVinculada == -1) return;

        String[] datosFirma = solicitarFirmaUsuario();
        if (datosFirma == null) return; 

        dao.OrdenReparacionDAO daoOrden = new dao.OrdenReparacionDAO();
        String[] textosActuales = daoOrden.obtenerTextosOrden(idOrdenVinculada);
        double precioActual = Double.parseDouble(modeloCarrito.getValueAt(filaCarrito, 3).toString());

        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Modificar Orden de Taller", true);
        dialogo.setSize(500, 550);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panelFondo = new JPanel(new GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblTituloModal = new JLabel("Editar Orden #" + idOrdenVinculada);
        lblTituloModal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloModal.setForeground(new Color(44, 62, 80));
        
        JLabel lblAutoriza = new JLabel("Autorizado por: " + datosFirma[1].toUpperCase());
        lblAutoriza.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblAutoriza.setForeground(new Color(149, 165, 166)); 

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 2, 0); panelFondo.add(lblTituloModal, gbc);
        gbc.gridy++; gbc.insets = new Insets(0, 0, 15, 0); panelFondo.add(lblAutoriza, gbc);

        JTextField txtNuevoPrecio = new JTextField(String.valueOf(precioActual)); 
        txtNuevoPrecio.setPreferredSize(new Dimension(0, 35)); 
        txtNuevoPrecio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtNuevoPrecio.setForeground(new Color(39, 174, 96)); 

        javax.swing.JTextArea txtProblema = new javax.swing.JTextArea(textosActuales[0] != null ? textosActuales[0] : "");
        txtProblema.setLineWrap(true); txtProblema.setWrapStyleWord(true); txtProblema.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        javax.swing.JTextArea txtTrabajo = new javax.swing.JTextArea(textosActuales[1] != null ? textosActuales[1] : "");
        txtTrabajo.setLineWrap(true); txtTrabajo.setWrapStyleWord(true); txtTrabajo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField txtClave = new JTextField(textosActuales[2] != null ? textosActuales[2] : "");
        txtClave.setPreferredSize(new Dimension(0, 35)); txtClave.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.gridy++; gbc.weighty = 0.0; panelFondo.add(new JLabel("Precio Final a Cobrar (L.):"), gbc); 
        gbc.gridy++; panelFondo.add(txtNuevoPrecio, gbc);
        
        gbc.gridy++; panelFondo.add(new JLabel("Clave / Patrón del Equipo:"), gbc); 
        gbc.gridy++; panelFondo.add(txtClave, gbc);

        gbc.gridy++; panelFondo.add(new JLabel("Problema Reportado:"), gbc); 
        gbc.gridy++; gbc.weighty = 0.3; panelFondo.add(new JScrollPane(txtProblema), gbc);

        gbc.gridy++; gbc.weighty = 0.0; panelFondo.add(new JLabel("Trabajo Realizado (Saldrá en PDF):"), gbc); 
        gbc.gridy++; gbc.weighty = 0.3; panelFondo.add(new JScrollPane(txtTrabajo), gbc);

        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166)); btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setBackground(new Color(243, 156, 18)); 
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnGuardar.setFocusPainted(false);
        
        btnGuardar.addActionListener(e -> {
            try {
                double nuevoPrecio = Double.parseDouble(txtNuevoPrecio.getText().trim());
                if (nuevoPrecio < 0) throw new NumberFormatException();

                String nuevoProblema = txtProblema.getText().trim();
                String nuevoTrabajo = txtTrabajo.getText().trim();
                String nuevaClave = txtClave.getText().trim();

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                String firmaAuditoria = "\n[Editado en caja por: " + datosFirma[1].toUpperCase() + " el " + sdf.format(new java.util.Date()) + "]";
                nuevoTrabajo = nuevoTrabajo + firmaAuditoria;

                daoOrden.actualizarTextosOrden(idOrdenVinculada, nuevoProblema, nuevoTrabajo, nuevaClave);
                daoOrden.actualizarEstadoYCosto(idOrdenVinculada, "Reparado", nuevoPrecio);

                modeloCarrito.setValueAt(nuevoPrecio, filaCarrito, 3);
                modeloCarrito.setValueAt(nuevoPrecio, filaCarrito, 4);
                recalcularTotal();
                
                firmaTemporal = datosFirma; 
                
                JOptionPane.showMessageDialog(dialogo, "Orden actualizada por " + datosFirma[1] + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose(); 

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "El precio ingresado no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.weighty = 0.0; gbc.insets = new Insets(20, 0, 0, 0);
        panelFondo.add(panelBotones, gbc);

        dialogo.add(panelFondo);
        dialogo.setVisible(true);
    }
    
    private void abrirModalNuevoCliente() {
        // 1. Crear el JDialog (Ventana Flotante Personalizada)
        javax.swing.JDialog dialogo = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Registrar Nuevo Cliente", true);
        dialogo.setSize(450, 550);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        // 2. Panel principal con diseño limpio
        JPanel panelFondo = new JPanel(new java.awt.GridBagLayout());
        panelFondo.setBackground(Color.WHITE);
        panelFondo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // Título interno
        JLabel lblTituloModal = new JLabel("Formulario Rápido");
        lblTituloModal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTituloModal.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0; gbc.insets = new java.awt.Insets(0, 0, 20, 0);
        panelFondo.add(lblTituloModal, gbc);

        // Definir componentes
        JTextField txtId = new JTextField(); txtId.setPreferredSize(new Dimension(0, 35)); txtId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField txtNom = new JTextField(); txtNom.setPreferredSize(new Dimension(0, 35)); txtNom.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField txtApe = new JTextField(); txtApe.setPreferredSize(new Dimension(0, 35)); txtApe.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField txtTel = new JTextField(); txtTel.setPreferredSize(new Dimension(0, 35)); txtTel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JTextField txtCor = new JTextField(); txtCor.setPreferredSize(new Dimension(0, 35)); txtCor.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Agregar etiquetas y campos al formulario
        gbc.insets = new java.awt.Insets(5, 0, 2, 0);
        gbc.gridy++; panelFondo.add(new JLabel("Número de Identidad: *"), gbc); gbc.gridy++; panelFondo.add(txtId, gbc);
        gbc.gridy++; panelFondo.add(new JLabel("Nombres: *"), gbc); gbc.gridy++; panelFondo.add(txtNom, gbc);
        gbc.gridy++; panelFondo.add(new JLabel("Apellidos: *"), gbc); gbc.gridy++; panelFondo.add(txtApe, gbc);
        gbc.gridy++; panelFondo.add(new JLabel("Teléfono (Opcional):"), gbc); gbc.gridy++; panelFondo.add(txtTel, gbc);
        gbc.gridy++; panelFondo.add(new JLabel("Correo (Opcional):"), gbc); gbc.gridy++; panelFondo.add(txtCor, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new java.awt.GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(149, 165, 166)); btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dialogo.dispose());

        JButton btnGuardar = new JButton("Guardar Cliente");
        btnGuardar.setBackground(new Color(46, 204, 113)); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14)); btnGuardar.setFocusPainted(false);
        
        // Lógica al guardar
        btnGuardar.addActionListener(e -> {
            String identidad = txtId.getText().trim();
            String nombre = txtNom.getText().trim();
            String apellido = txtApe.getText().trim();

            if (identidad.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Identidad, Nombres y Apellidos son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dao.ClienteDAO daoC = new dao.ClienteDAO();
            if (daoC.existeIdentidad(identidad, 0)) {
                JOptionPane.showMessageDialog(dialogo, "La Identidad ya existe en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            modelo.Cliente c = new modelo.Cliente();
            c.setNumeroIdentidad(identidad);
            c.setNombre(nombre);
            c.setApellido(apellido);
            c.setTelefono(txtTel.getText().trim());
            c.setCorreo(txtCor.getText().trim());

            int nuevoId = daoC.insertar(c);
            
            if (nuevoId != -1) {
                // Auto-asignar al Punto de Venta
                idClienteSeleccionado = nuevoId;
                txtClienteAsignado.setText(nombre + " " + apellido);
                
                JOptionPane.showMessageDialog(dialogo, "Cliente guardado y seleccionado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose(); // Cierra el modal automáticamente
            } else {
                JOptionPane.showMessageDialog(dialogo, "Error al guardar el cliente.", "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        panelBotones.setPreferredSize(new Dimension(0, 45));

        gbc.gridy++; gbc.insets = new java.awt.Insets(25, 0, 0, 0);
        panelFondo.add(panelBotones, gbc);

        // Agregamos el panel al diálogo y lo mostramos
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
