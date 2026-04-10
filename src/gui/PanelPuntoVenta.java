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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class PanelPuntoVenta extends JPanel {

    // Componentes Izquierda (Búsqueda)
    private JTextField txtCodigoBarras;
    private JTextField txtBuscarOrden;
    private JButton btnVincularOrden;
    private JButton btnServicioManual;
    
    // Componentes Derecha (Carrito)
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotalGlobal;
    private JComboBox<String> cmbMetodoPago;
    private JButton btnCobrar;
    private JButton btnQuitarItem;

    // Variables de Estado
    private double totalVenta = 0.0;
    private int idOrdenVinculada = -1; // -1 significa que es solo venta de mostrador

    public PanelPuntoVenta() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TÍTULO (Más sutil)
        JLabel lblTitulo = new JLabel(" Caja Registradora y Entregas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        // PANEL IZQUIERDO (Controles)
        add(construirPanelControles(), BorderLayout.WEST);

        // PANEL DERECHO (Carrito y Cobro)
        add(construirPanelCarrito(), BorderLayout.CENTER);
        
        // Enfocar el cursor en el escáner al abrir
        SwingUtilities.invokeLater(() -> txtCodigoBarras.requestFocus());
    }

    private JPanel construirPanelControles() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(340, 0)); // Un poco más angosto
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0; gbc.gridy = 0;

        // SECCIÓN 1: Lector de Barras
        JLabel lblEscaner = new JLabel("Lector de Código de Barras:");
        lblEscaner.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Letra 14
        panel.add(lblEscaner, gbc);

        txtCodigoBarras = new JTextField();
        txtCodigoBarras.setPreferredSize(new Dimension(0, 40)); // Menos altura
        txtCodigoBarras.setFont(new Font("Consolas", Font.BOLD, 16));
        txtCodigoBarras.setHorizontalAlignment(SwingConstants.CENTER);
        txtCodigoBarras.setBackground(new Color(255, 255, 204)); 
        txtCodigoBarras.addActionListener(e -> procesarCodigoBarras());
        
        gbc.gridy++; gbc.insets = new Insets(5, 0, 25, 0);
        panel.add(txtCodigoBarras, gbc);

        // SECCIÓN 2: Vincular Reparación
        JLabel lblOrden = new JLabel("Entregar Reparación (No. Orden):");
        lblOrden.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Letra 14
        gbc.gridy++; gbc.insets = new Insets(10, 0, 5, 0);
        panel.add(lblOrden, gbc);

        JPanel panelOrden = new JPanel(new BorderLayout(10, 0));
        panelOrden.setOpaque(false);
        txtBuscarOrden = new JTextField();
        txtBuscarOrden.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Letra 14
        txtBuscarOrden.setPreferredSize(new Dimension(0, 35)); // Menos altura
        
        btnVincularOrden = new JButton("Vincular");
        btnVincularOrden.setBackground(new Color(52, 152, 219));
        btnVincularOrden.setForeground(Color.WHITE);
        btnVincularOrden.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVincularOrden.setFocusPainted(false);
        btnVincularOrden.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVincularOrden.addActionListener(e -> vincularOrdenReparacion());

        panelOrden.add(txtBuscarOrden, BorderLayout.CENTER);
        panelOrden.add(btnVincularOrden, BorderLayout.EAST);
        
        gbc.gridy++; panel.add(panelOrden, gbc);

        // SECCIÓN 3: Servicios Manuales
        gbc.gridy++; gbc.insets = new Insets(30, 0, 0, 0);
        btnServicioManual = new JButton("+ Agregar Servicio Libre");
        btnServicioManual.setBackground(new Color(149, 165, 166));
        btnServicioManual.setForeground(Color.WHITE);
        btnServicioManual.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnServicioManual.setPreferredSize(new Dimension(0, 40)); // Menos altura
        btnServicioManual.setFocusPainted(false);
        btnServicioManual.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnServicioManual.addActionListener(e -> agregarServicioManual());
        panel.add(btnServicioManual, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel construirPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // TABLA DEL CARRITO
        String[] columnas = {"ID", "Descripción", "Cant.", "Precio U.", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(30); 
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Ajustar columnas
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(300);

        JScrollPane scroll = new JScrollPane(tablaCarrito);
        scroll.getViewport().setBackground(Color.WHITE);
        
        // --- AQUÍ ESTÁ EL BOTÓN REVIVIDO Y MEJORADO ---
        btnQuitarItem = new JButton("Quitar Producto");
        btnQuitarItem.setBackground(new Color(231, 76, 60)); // Rojo elegante
        btnQuitarItem.setForeground(Color.WHITE);
        btnQuitarItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnQuitarItem.setPreferredSize(new Dimension(180, 35));
        btnQuitarItem.setFocusPainted(false);
        btnQuitarItem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuitarItem.addActionListener(e -> quitarItemCarrito());

        // Contenedor para alinear el botón a la derecha con un margen superior
        JPanel panelBotonesTabla = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        panelBotonesTabla.setOpaque(false);
        panelBotonesTabla.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Respiro arriba
        panelBotonesTabla.add(btnQuitarItem);

        // Juntamos la tabla y el botón en un solo bloque central
        JPanel panelTablaSup = new JPanel(new BorderLayout());
        panelTablaSup.setOpaque(false);
        panelTablaSup.add(scroll, BorderLayout.CENTER);
        panelTablaSup.add(panelBotonesTabla, BorderLayout.SOUTH);

        panel.add(panelTablaSup, BorderLayout.CENTER);

        // ZONA DE COBRO (ABAJO)
        JPanel panelCobro = new JPanel(new BorderLayout(20, 0));
        panelCobro.setBackground(Color.WHITE);
        panelCobro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Total
        lblTotalGlobal = new JLabel("TOTAL: L. 0.00");
        lblTotalGlobal.setFont(new Font("Segoe UI", Font.BOLD, 32)); 
        lblTotalGlobal.setForeground(new Color(46, 204, 113)); 
        panelCobro.add(lblTotalGlobal, BorderLayout.WEST);

        // Metodo y Boton
        JPanel panelAccionesCobro = new JPanel(new GridBagLayout());
        panelAccionesCobro.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(0, 10, 0, 10);
        
        cmbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        cmbMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        cmbMetodoPago.setPreferredSize(new Dimension(150, 45));
        
        btnCobrar = new JButton("COBRAR E IMPRIMIR");
        btnCobrar.setBackground(new Color(39, 174, 96)); 
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        btnCobrar.setPreferredSize(new Dimension(220, 45)); 
        btnCobrar.setFocusPainted(false);
        btnCobrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCobrar.addActionListener(e -> procesarCobroFinal());

        JLabel lblPago = new JLabel("Pago con:");
        lblPago.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelAccionesCobro.add(lblPago, gc);
        panelAccionesCobro.add(cmbMetodoPago, gc);
        panelAccionesCobro.add(btnCobrar, gc);

        panelCobro.add(panelAccionesCobro, BorderLayout.EAST);
        panel.add(panelCobro, BorderLayout.SOUTH);

        return panel;
    }

    // =========================================================
    // LÓGICA DEL CARRITO
    // =========================================================

    private void procesarCodigoBarras() {
        String codigo = txtCodigoBarras.getText().trim();
        if (codigo.isEmpty()) return;

        dao.ProductoDAO daoProd = new dao.ProductoDAO();
        modelo.Producto p = daoProd.buscarPorCodigo(codigo);

        if (p == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if (p.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "¡Sin Stock! No hay existencias de: " + p.getNombreProducto(), "Agotado", JOptionPane.ERROR_MESSAGE);
        } else {
            agregarAlCarrito(p.getIdProducto(), p.getNombreProducto(), 1, p.getPrecioVenta());
        }
        
        txtCodigoBarras.setText(""); 
        txtCodigoBarras.requestFocus();
    }

    private void agregarAlCarrito(int idProd, String desc, int cant, double precioU) {
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            int idExistente = Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString());
            if (idExistente == idProd && idProd != 0) { 
                int cantExistente = Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString());
                int nuevaCant = cantExistente + cant;
                double nuevoSub = nuevaCant * precioU;
                
                modeloCarrito.setValueAt(nuevaCant, i, 2);
                modeloCarrito.setValueAt(nuevoSub, i, 4);
                recalcularTotal();
                return;
            }
        }
        
        double subtotal = cant * precioU;
        modeloCarrito.addRow(new Object[]{idProd, desc, cant, precioU, subtotal});
        recalcularTotal();
    }

    private void quitarItemCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila >= 0) {
            if (Integer.parseInt(modeloCarrito.getValueAt(fila, 0).toString()) == 0 && 
                modeloCarrito.getValueAt(fila, 1).toString().startsWith("Orden #")) {
                idOrdenVinculada = -1;
                txtBuscarOrden.setEnabled(true);
                btnVincularOrden.setEnabled(true);
            }
            modeloCarrito.removeRow(fila);
            recalcularTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para quitarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recalcularTotal() {
        totalVenta = 0.0;
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            totalVenta += Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString());
        }
        lblTotalGlobal.setText(String.format("TOTAL: L. %.2f", totalVenta));
    }

    // =========================================================
    // SERVICIOS Y ORDENES
    // =========================================================

    private void vincularOrdenReparacion() {
        String idStr = txtBuscarOrden.getText().trim();
        if (idStr.isEmpty()) return;

        dao.OrdenReparacionDAO daoOrden = new dao.OrdenReparacionDAO();
        List<Object[]> resultados = daoOrden.buscarOrden(idStr);
        
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontró ninguna orden con ese criterio.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Object[] ordenSeleccionada = null;

        if (resultados.size() == 1) {
            // Solo hay uno, no molestamos al usuario con ventanas extra
            ordenSeleccionada = resultados.get(0);
        } else {
            // ¡HAY VARIOS! Llamamos a nuestra nueva tablita mágica flotante
            ordenSeleccionada = mostrarDialogoSeleccionOrden(resultados);
            
            // Si el usuario cerró la tablita sin elegir nada, abortamos
            if (ordenSeleccionada == null) return; 
        }
        
        // --- Continuamos con la orden elegida ---
        int idOrd = Integer.parseInt(ordenSeleccionada[0].toString());
        String estado = ordenSeleccionada[4].toString();
        double costo = Double.parseDouble(ordenSeleccionada[5].toString());
        String cliente = ordenSeleccionada[1].toString();
        String modeloEquipo = ordenSeleccionada[2].toString();
        
        if (estado.equalsIgnoreCase("Entregado")) {
            JOptionPane.showMessageDialog(this, "La orden #" + idOrd + " ya fue marcada como ENTREGADA anteriormente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        idOrdenVinculada = idOrd;
        String descripcion = "Orden #" + idOrd + " - Rep: " + modeloEquipo + " (" + cliente + ")"; 
        
        agregarAlCarrito(0, descripcion, 1, costo); 
        
        txtBuscarOrden.setText("");
        txtBuscarOrden.setEnabled(false); 
        btnVincularOrden.setEnabled(false);
    }
    
    // --- NUEVO: MODAL FLOTANTE PARA SELECCIONAR ORDENES ---
    private Object[] mostrarDialogoSeleccionOrden(List<Object[]> resultados) {
        // Creamos un JDialog (Ventana flotante) que bloquea el panel de atrás
        javax.swing.JDialog dialogo = new javax.swing.JDialog(
                (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Seleccionar Orden de la Lista", true);

        dialogo.setSize(750, 400); // Tamaño perfecto para ver los detalles
        dialogo.setLocationRelativeTo(this); // Que aparezca en el centro
        dialogo.setLayout(new BorderLayout(10, 10));
        dialogo.getContentPane().setBackground(Color.WHITE);

        // Diseñamos la tabla temporal
        String[] columnas = {"No. Orden", "Cliente", "Equipo", "Falla/Trabajo", "Costo"};
        DefaultTableModel modeloTemp = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Object[] fila : resultados) {
            // Llenamos la tabla con: ID, Cliente, Modelo, Problema, Costo
            modeloTemp.addRow(new Object[]{
                "#" + fila[0], fila[1], fila[2], fila[3], "L. " + fila[5]
            });
        }

        JTable tablaBusqueda = new JTable(modeloTemp);
        tablaBusqueda.setRowHeight(30);
        tablaBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaBusqueda.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Ajustamos anchos para que se lea bien
        tablaBusqueda.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaBusqueda.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaBusqueda.getColumnModel().getColumn(3).setPreferredWidth(250);

        JScrollPane scroll = new JScrollPane(tablaBusqueda);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialogo.add(scroll, BorderLayout.CENTER);

        // Panel de abajo con botón y texto de ayuda
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(Color.WHITE);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JLabel lblAyuda = new JLabel("Doble clic en una fila para seleccionarla.");
        lblAyuda.setForeground(Color.GRAY);
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panelSur.add(lblAyuda, BorderLayout.WEST);

        JButton btnSeleccionar = new JButton("Seleccionar y Añadir");
        btnSeleccionar.setBackground(new Color(46, 204, 113));
        btnSeleccionar.setForeground(Color.WHITE);
        btnSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSeleccionar.setFocusPainted(false);
        panelSur.add(btnSeleccionar, BorderLayout.EAST);

        // Variable para guardar lo que elija el usuario (usamos un array de 1 posición como truco de Java)
        final Object[][] seleccion = {null};

        // Evento del botón
        btnSeleccionar.addActionListener(e -> {
            int filaSeleccionada = tablaBusqueda.getSelectedRow();
            if (filaSeleccionada >= 0) {
                seleccion[0] = resultados.get(filaSeleccionada); // Agarramos el dato original de la BD
                dialogo.dispose(); // Cerramos y destruimos la ventana flotante
            } else {
                JOptionPane.showMessageDialog(dialogo, "Por favor, seleccione una orden de la lista.");
            }
        });

        // Evento mágico: Doble clic en la tabla
        tablaBusqueda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tablaBusqueda.getSelectedRow() != -1) {
                    seleccion[0] = resultados.get(tablaBusqueda.getSelectedRow());
                    dialogo.dispose(); // Cierra igual que el botón
                }
            }
        });

        dialogo.add(panelSur, BorderLayout.SOUTH);
        dialogo.setVisible(true); // Esto "congela" el programa aquí hasta que el diálogo se cierre

        return seleccion[0]; // Retorna la orden elegida (o null si cerró con la 'X')
    }

    private void agregarServicioManual() {
        String desc = JOptionPane.showInputDialog(this, "Descripción del servicio (Ej. Revisión, Soldadura, etc):");
        if (desc == null || desc.trim().isEmpty()) return;
        
        String precioStr = JOptionPane.showInputDialog(this, "Precio a cobrar por este servicio:");
        if (precioStr == null || precioStr.trim().isEmpty()) return;
        
        try {
            double precio = Double.parseDouble(precioStr);
            agregarAlCarrito(0, "Servicio: " + desc, 1, precio); 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Precio inválido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================
    // GUARDADO FINAL 
    // =========================================================

    private void procesarCobroFinal() {
        if (modeloCarrito.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cmbMetodoPago.getSelectedItem().toString().equals("Efectivo")) {
            String pagoStr = JOptionPane.showInputDialog(this, "Total: L. " + totalVenta + "\n¿Efectivo Recibido?", "Cobro", JOptionPane.QUESTION_MESSAGE);
            if (pagoStr == null) return; 
            try {
                double pago = Double.parseDouble(pagoStr);
                if (pago < totalVenta) {
                    JOptionPane.showMessageDialog(this, "El pago es menor al total.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double cambio = pago - totalVenta;
                JOptionPane.showMessageDialog(this, "Cambio a entregar: L. " + String.format("%.2f", cambio), "Cambio", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                return;
            }
        }

        btnCobrar.setEnabled(false);
        setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

        VentanaPrincipal v = (VentanaPrincipal) SwingUtilities.getWindowAncestor(this);
        int idUsuarioActivo = v.getIdUsuarioActivo();

        modelo.Venta venta = new modelo.Venta();
        venta.setIdCliente(0); 
        venta.setIdUsuario(idUsuarioActivo);
        venta.setIdOrden(idOrdenVinculada); 
        venta.setTotal(totalVenta);
        venta.setMetodoPago(cmbMetodoPago.getSelectedItem().toString());

        List<modelo.DetalleVenta> listaDetalles = new ArrayList<>();
        for (int i = 0; i < modeloCarrito.getRowCount(); i++) {
            modelo.DetalleVenta dv = new modelo.DetalleVenta();
            dv.setIdProducto(Integer.parseInt(modeloCarrito.getValueAt(i, 0).toString()));
            dv.setDescripcion(modeloCarrito.getValueAt(i, 1).toString());
            dv.setCantidad(Integer.parseInt(modeloCarrito.getValueAt(i, 2).toString()));
            dv.setPrecioUnitario(Double.parseDouble(modeloCarrito.getValueAt(i, 3).toString()));
            dv.setSubtotal(Double.parseDouble(modeloCarrito.getValueAt(i, 4).toString()));
            listaDetalles.add(dv);
        }

        dao.VentaDAO daoVenta = new dao.VentaDAO();
        int idRecibo = daoVenta.registrarVentaCompleta(venta, listaDetalles);

        if (idRecibo != -1) {
            JOptionPane.showMessageDialog(this, "¡Venta Registrada Exitosamente!\nRecibo #" + idRecibo, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // --- LLAMADA A LA IMPRESORA TÉRMICA ---
            utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta();
            impresora.imprimirReciboVenta(idRecibo);
            // --------------------------------------
            
            modeloCarrito.setRowCount(0);
            recalcularTotal();
            idOrdenVinculada = -1;
            txtBuscarOrden.setEnabled(true);
            btnVincularOrden.setEnabled(true);
            cmbMetodoPago.setSelectedIndex(0);
            txtCodigoBarras.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Hubo un error al registrar la venta. La base de datos no se modificó.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }

        btnCobrar.setEnabled(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
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
