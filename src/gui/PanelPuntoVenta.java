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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

public class PanelPuntoVenta extends JPanel {

    private JLabel lblTituloCliente;
    private JPanel panelCliente;
    private JTextField txtClienteAsignado;
    private JButton btnBuscarCliente;
    
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

    private double totalVenta = 0.0;
    private int idOrdenVinculada = -1; 
    private int idClienteSeleccionado = 0; 
    private String modoActual = ""; 

    public PanelPuntoVenta(String modo) {
        this.modoActual = modo;
        
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String tituloPanel = modo.equals("TALLER") ? " Entrega de Equipos Reparados" : " Punto de Venta (Mostrador)";
        JLabel lblTitulo = new JLabel(tituloPanel);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        add(construirPanelControles(), BorderLayout.WEST);
        add(construirPanelCarrito(), BorderLayout.CENTER);
        
        aplicarModoEstricto();
        cargarOrdenesPendientesVisuales(); 

        SwingUtilities.invokeLater(() -> {
            if(modo.equals("MOSTRADOR")) txtCodigoBarras.requestFocus();
            else txtBuscarOrden.requestFocus();
        });
    }
    
    public PanelPuntoVenta() { this("MOSTRADOR"); }

    private void aplicarModoEstricto() {
        if (modoActual.equals("TALLER")) {
            lblTituloCliente.setVisible(false); panelCliente.setVisible(false);
            lblEscaner.setVisible(false); txtCodigoBarras.setVisible(false); btnBuscarManual.setVisible(false);
            lblTitKnijico.setVisible(false); btnBuscarKnijico.setVisible(false); chkPrecioTecnico.setVisible(false);
            btnServicioManual.setVisible(false);
        } else if (modoActual.equals("MOSTRADOR")) {
            lblOrden.setVisible(false); panelOrden.setVisible(false); panelPendientes.setVisible(false); 
        }
    }

    private JPanel construirPanelControles() {
        JPanel panelContenedorIzq = new JPanel(new BorderLayout(0, 15));
        panelContenedorIzq.setPreferredSize(new Dimension(360, 0)); panelContenedorIzq.setOpaque(false);

        JPanel panel = new JPanel(new GridBagLayout()); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.weightx = 1.0; gbc.gridx = 0; gbc.gridy = 0;

        lblTituloCliente = new JLabel("Cliente Asignado:"); lblTituloCliente.setFont(new Font("Segoe UI", Font.BOLD, 14)); panel.add(lblTituloCliente, gbc);

        panelCliente = new JPanel(new BorderLayout(5, 0)); panelCliente.setOpaque(false);
        txtClienteAsignado = new JTextField("Consumidor Final"); txtClienteAsignado.setEditable(false); txtClienteAsignado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtClienteAsignado.setForeground(new Color(41, 128, 185));
        
        btnBuscarCliente = new JButton("Buscar"); btnBuscarCliente.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBuscarCliente.addActionListener(e -> seleccionarClientePOS());
        
        panelCliente.add(txtClienteAsignado, BorderLayout.CENTER); panelCliente.add(btnBuscarCliente, BorderLayout.EAST);
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
        
        btnVincularOrden = new JButton("Vincular"); btnVincularOrden.setBackground(new Color(52, 152, 219));
        btnVincularOrden.setForeground(Color.WHITE); btnVincularOrden.setFocusPainted(false);
        btnVincularOrden.addActionListener(e -> vincularOrdenReparacion());

        panelOrden.add(txtBuscarOrden, BorderLayout.CENTER); panelOrden.add(btnVincularOrden, BorderLayout.EAST);
        gbc.gridy++; gbc.insets = new Insets(2, 0, 5, 0); panel.add(panelOrden, gbc);

        btnServicioManual = new JButton("+ Agregar Servicio Rápido"); btnServicioManual.setBackground(new Color(149, 165, 166));
        btnServicioManual.setForeground(Color.WHITE); btnServicioManual.setFocusPainted(false);
        btnServicioManual.addActionListener(e -> agregarServicioManual());
        gbc.gridy++; gbc.insets = new Insets(5, 0, 10, 0); panel.add(btnServicioManual, gbc);

        panelPendientes = new JPanel(new BorderLayout()); panelPendientes.setBackground(Color.WHITE);
        panelPendientes.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        JLabel lblPend = new JLabel("Órdenes Listas para Entrega:"); lblPend.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        String[] columnas = {"ID", "Descripción", "Cant.", "Precio U.", "Subtotal", "StockMax"};
        modeloCarrito = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(30); tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(300);
        tablaCarrito.getColumnModel().getColumn(5).setMinWidth(0);
        tablaCarrito.getColumnModel().getColumn(5).setMaxWidth(0);
        tablaCarrito.getColumnModel().getColumn(5).setWidth(0);

        JScrollPane scroll = new JScrollPane(tablaCarrito); scroll.getViewport().setBackground(Color.WHITE);
        
        btnModificarCant = new JButton("Modificar Cant."); btnModificarCant.setBackground(new Color(52, 152, 219)); 
        btnModificarCant.setForeground(Color.WHITE); btnModificarCant.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnModificarCant.setPreferredSize(new Dimension(160, 35)); btnModificarCant.setFocusPainted(false);
        btnModificarCant.addActionListener(e -> modificarCantidadCarrito());

        btnQuitarItem = new JButton("Quitar Producto"); btnQuitarItem.setBackground(new Color(231, 76, 60)); 
        btnQuitarItem.setForeground(Color.WHITE); btnQuitarItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnQuitarItem.setPreferredSize(new Dimension(160, 35)); btnQuitarItem.setFocusPainted(false);
        btnQuitarItem.addActionListener(e -> quitarItemCarrito());

        JPanel panelBotonesTabla = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        panelBotonesTabla.setOpaque(false); panelBotonesTabla.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); 
        panelBotonesTabla.add(btnModificarCant); panelBotonesTabla.add(btnQuitarItem);

        JPanel panelTablaSup = new JPanel(new BorderLayout()); panelTablaSup.setOpaque(false);
        panelTablaSup.add(scroll, BorderLayout.CENTER); panelTablaSup.add(panelBotonesTabla, BorderLayout.SOUTH);
        panel.add(panelTablaSup, BorderLayout.CENTER);

        JPanel panelCobro = new JPanel(new BorderLayout(20, 0)); panelCobro.setBackground(Color.WHITE);
        panelCobro.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)), BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        lblTotalGlobal = new JLabel("TOTAL: L. 0.00"); lblTotalGlobal.setFont(new Font("Segoe UI", Font.BOLD, 32)); 
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
        
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Directorio de Clientes", true);
        dialog.setSize(600, 450); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10)); dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panelNorte = new JPanel(new BorderLayout(5, 0)); panelNorte.setBackground(Color.WHITE); panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtBuscarC = new JTextField(); txtBuscarC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelNorte.add(new JLabel("Buscar Cliente: "), BorderLayout.WEST); panelNorte.add(txtBuscarC, BorderLayout.CENTER);
        
        String[] col = {"ID", "Identidad", "Nombre Completo", "Teléfono"};
        DefaultTableModel modC = new DefaultTableModel(col, 0) { public boolean isCellEditable(int r, int c) { return false; } };
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
        List<Object[]> resultados = daoP.buscarProductoCompleto(""); 
        
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Inventario General", true);
        dialog.setSize(800, 450); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10)); dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panelNorte = new JPanel(new BorderLayout(5, 0)); panelNorte.setBackground(Color.WHITE); panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtBuscarP = new JTextField(); txtBuscarP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelNorte.add(new JLabel("Buscar Producto: "), BorderLayout.WEST); panelNorte.add(txtBuscarP, BorderLayout.CENTER);
        
        String[] col = {"ID", "Categoría", "Nombre Producto", "Ubicación", "Precio V.", "Stock"};
        DefaultTableModel modP = new DefaultTableModel(col, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        for (Object[] p : resultados) modP.addRow(new Object[]{p[0], p[3], p[2], p[4], p[6], p[7]});
        
        JTable tablaP = new JTable(modP); tablaP.setRowHeight(30); tablaP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaP.getColumnModel().getColumn(0).setPreferredWidth(50); tablaP.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modP); tablaP.setRowSorter(sorter);
        txtBuscarP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = txtBuscarP.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null); 
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        
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
                    
                    // ID NORMAL
                    agregarAlCarrito((int)productoCompleto[0], nombreDesc, 1, precioCobrar, stock);
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
        
        javax.swing.JDialog dialog = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Inventario Knijico", true);
        dialog.setSize(800, 450); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout(10, 10)); dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panelNorte = new JPanel(new BorderLayout(5, 0)); panelNorte.setBackground(Color.WHITE); panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtBuscarK = new JTextField(); txtBuscarK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelNorte.add(new JLabel("Filtrar Modelo Knijico: "), BorderLayout.WEST); panelNorte.add(txtBuscarK, BorderLayout.CENTER);
        
        String[] col = {"ID", "Lote", "Modelo", "Stock", "P. Cliente", "P. Técnico"};
        DefaultTableModel modK = new DefaultTableModel(col, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        for (Object[] p : resultados) modK.addRow(new Object[]{p[0], p[1], p[2], p[6], p[4], p[5]});
        
        JTable tablaK = new JTable(modK); tablaK.setRowHeight(30); tablaK.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaK.getColumnModel().getColumn(0).setPreferredWidth(40); tablaK.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modK); tablaK.setRowSorter(sorter);
        txtBuscarK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = txtBuscarK.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null); 
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });
        
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
                    
                    // --- ID VIRTUAL PARA KNIJICO (70000 + ID) ---
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

        // 1. Intentamos buscar en Productos Normales
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
                agregarAlCarrito(p.getIdProducto(), desc, 1, precioCobrar, p.getStock());
            }
        } 
        else {
            // 2. Si no es producto, probamos buscar en Pantallas Knijico
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
                    
                    agregarAlCarrito(70000 + idReal, desc, 1, precio, stock);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Código no registrado en sistema.", "No encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
        txtCodigoBarras.setText("");
        txtCodigoBarras.requestFocus();
    }

    private void agregarAlCarrito(int idProd, String desc, int cant, double precioU, int maxStock) {
        if (maxStock != -1 && idProd != 0) { 
            int cantidadYaEnCarrito = 0;
            for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
                int idExistente = Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString());
                if (idExistente == idProd) {
                    cantidadYaEnCarrito += Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString());
                }
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
            
            if (idExistente == idProd && idProd != 0 && descExistente.equals(desc) && precioExistente == precioU && !desc.startsWith("Orden #")) { 
                int cantActual = Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString());
                int nuevaCant = cantActual + cant;
                modeloCarrito.setValueAt(nuevaCant, i, 2);
                modeloCarrito.setValueAt(nuevaCant * precioU, i, 4);
                recalcularTotal();
                return;
            }
        }
        modeloCarrito.addRow(new Object[]{idProd, desc, cant, precioU, cant * precioU, maxStock});
        recalcularTotal();
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
            // Verificamos si estamos quitando la orden vinculada
            if (modeloCarrito.getValueAt(fila, 1).toString().startsWith("Orden #")) {
                idOrdenVinculada = -1; 
                txtBuscarOrden.setEnabled(true); 
                btnVincularOrden.setEnabled(true);
                
                // Restaurar tabla de pendientes
                tablaPendientes.setEnabled(true);
                tablaPendientes.setBackground(Color.WHITE);
                tablaPendientes.setForeground(Color.BLACK);
                
                // --- RESTAURAR CLIENTE ---
                idClienteSeleccionado = 0;
                txtClienteAsignado.setText("Consumidor Final");
                // -------------------------
            }
            
            // ELIMINACIÓN FÍSICA
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
        
        // --- MAGIA: ASIGNAR CLIENTE AUTOMÁTICAMENTE ---
        txtClienteAsignado.setText(ord[1].toString()); // ord[1] es el nombre completo
        if (ord.length > 7 && ord[7] != null) {
            idClienteSeleccionado = Integer.parseInt(ord[7].toString()); // ord[7] es el id_cliente
        }
        // ----------------------------------------------

        agregarAlCarrito(0, "Orden #" + idOrdenVinculada + " - Rep: " + ord[2], 1, Double.parseDouble(ord[5].toString()), 1); 
        
        txtBuscarOrden.setText(""); txtBuscarOrden.setEnabled(false); btnVincularOrden.setEnabled(false);
        tablaPendientes.setEnabled(false);
        tablaPendientes.clearSelection();
        tablaPendientes.setBackground(new Color(240, 240, 240));
        tablaPendientes.setForeground(Color.GRAY);
    }

    private void procesarCobroFinal() {
        if (modeloCarrito.getRowCount() == 0) return;
        
        if (cmbMetodoPago.getSelectedItem().toString().equals("Efectivo")) {
            String pagoStr = JOptionPane.showInputDialog(this, "Total: L. " + totalVenta + "\n¿Efectivo Recibido?");
            if (pagoStr == null) return; 
            try {
                double pago = Double.parseDouble(pagoStr);
                if (pago < totalVenta) {
                    JOptionPane.showMessageDialog(this, "Pago insuficiente.");
                    return;
                }
                JOptionPane.showMessageDialog(this, "Cambio: L. " + String.format("%.2f", (pago - totalVenta)));
            } catch (Exception e) { return; }
        }

        // --- SOLICITAR FIRMA ANTES DE COBRAR ---
        String[] datosFirma = solicitarFirmaUsuario();
        if (datosFirma == null) {
            return; // Si cancela o pone mal la clave, se aborta el cobro
        }
        int idCajeroFirma = Integer.parseInt(datosFirma[0]);
        String nombreCajeroFirma = datosFirma[1];
        // ---------------------------------------

        btnCobrar.setEnabled(false); setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        
        modelo.Venta venta = new modelo.Venta();
        venta.setIdCliente(idClienteSeleccionado); 
        
        // AQUÍ REEMPLAZAMOS EL USUARIO ACTIVO POR EL QUE ACABA DE FIRMAR
        venta.setIdUsuario(idCajeroFirma); 
        
        venta.setIdOrden(idOrdenVinculada); venta.setTotal(totalVenta); 
        venta.setMetodoPago(cmbMetodoPago.getSelectedItem().toString());

        List<modelo.DetalleVenta> listaDetalles = new ArrayList<>();
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            modelo.DetalleVenta dv = new modelo.DetalleVenta();
            int idCarrito = Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString());
            
            if (idCarrito >= 70000) {
                dv.setIdProducto(idCarrito - 70000); // ID Real de Knijico
            } else {
                dv.setIdProducto(idCarrito); // ID Normal
            }
            
            dv.setDescripcion(modeloCarrito.getValueAt(i, 1).toString());
            dv.setCantidad(Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString()));
            dv.setPrecioUnitario(Double.parseDouble(modeloCarrito.getValueAt(i, 3).toString()));
            dv.setSubtotal(Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString()));
            listaDetalles.add(dv);
        }

        dao.VentaDAO daoVenta = new dao.VentaDAO();
        int idRecibo = daoVenta.registrarVentaCompleta(venta, listaDetalles);

        if (idRecibo != -1) {
            JOptionPane.showMessageDialog(this, "¡Éxito!\nTransacción #" + idRecibo + "\nCajero/Técnico: " + nombreCajeroFirma.toUpperCase());
            
            if (idOrdenVinculada != -1) {
                // MODO ENTREGA DE TALLER: GENERAR EL PDF TAMAÑO CARTA Y MARCAR ENTREGADO
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
                    
                    // EL PDF AHORA IMPRIME EL NOMBRE DEL QUE PUSO LA CLAVE
                    String tecnico = nombreCajeroFirma; 
                    
                    utilidades.GeneradorPDF generador = new utilidades.GeneradorPDF();
                    generador.crearTicket(
                        String.valueOf(idOrdenVinculada), fechaOr, cliOr, equipoConClave, fallaOr, String.valueOf(totalVenta),
                        "SAIRTECH - TECNOLOGIA", "Santa Barbara, Barrio La Soledad, Frente a Sastreria La Elegancia", "8951-8040",
                        "OJO no aplica garantia en equipos mojados, pantallas no cuentan con garantía.",
                        tecnico, trabOr, false, tipoOr, true
                    );
                    
                    // Actualizamos la orden y REGISTRAMOS QUIÉN ENTREGÓ
                    daoOrden.actualizarEstadoYCosto(idOrdenVinculada, "Entregado", totalVenta);
                    daoOrden.marcarComoEntregado(idOrdenVinculada, idCajeroFirma);

                } catch (Exception ex) {
                    System.err.println("Error al crear PDF de entrega: " + ex.getMessage());
                }
            } else {
                // MODO VENTA MOSTRADOR: RECIBO TÉRMICO PEQUEÑO
                // Como VentaDAO guardó el ID de quien firmó, ImpresoraDirecta sacará su nombre automáticamente en el ticket
                utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta(); 
                impresora.imprimirReciboVenta(idRecibo);
            }
            
            // --- RESETEO DE CARRITO ---
            modeloCarrito.setRowCount(0); recalcularTotal();
            idOrdenVinculada = -1; txtBuscarOrden.setEnabled(true); btnVincularOrden.setEnabled(true);
            tablaPendientes.setEnabled(true); tablaPendientes.setBackground(Color.WHITE); tablaPendientes.setForeground(Color.BLACK);
            idClienteSeleccionado = 0; txtClienteAsignado.setText("Consumidor Final");
            cargarOrdenesPendientesVisuales(); 
            if(modoActual.equals("MOSTRADOR")) txtCodigoBarras.requestFocus(); else txtBuscarOrden.requestFocus();
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
            
            // Buscamos quién es el dueño de esa contraseña
            String nombreTecnico = daoUsuario.obtenerUsuarioPorClave(clave);
            
            if (nombreTecnico != null) {
                // Obtenemos su ID para guardarlo en la base de datos
                int idTecnico = daoUsuario.obtenerIdPorNombre(nombreTecnico);
                return new String[]{String.valueOf(idTecnico), nombreTecnico}; 
            } else {
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta o no registrada.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null;
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
