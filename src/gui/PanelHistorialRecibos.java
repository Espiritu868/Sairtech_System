package gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PanelHistorialRecibos extends JPanel {

    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    
    // Componentes del Visualizador
    private JTextArea txtVistaPrevia;
    private JButton btnReimprimir;
    private int idVentaSeleccionada = -1;

    public PanelHistorialRecibos() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- CABECERA ---
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);
        
        JLabel lblTitulo = new JLabel(" HISTORIAL DE RECIBOS Y VENTAS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));
        panelNorte.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBuscador.setOpaque(false);
        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(250, 35));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarTabla();
            }
        });
        panelBuscador.add(new JLabel("🔍 Buscar (Ticket o Cliente): "));
        panelBuscador.add(txtBuscar);
        panelNorte.add(panelBuscador, BorderLayout.EAST);

        add(panelNorte, BorderLayout.NORTH);

        // --- ZONA CENTRAL (TABLA) ---
        String[] columnas = {"N° Ticket", "Fecha", "Cliente", "Total (L.)", "Método", "Cajero"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setRowHeight(30);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaVentas.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaVentas.getTableHeader().setForeground(Color.WHITE);

        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaVentas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaVentas.getColumnModel().getColumn(2).setPreferredWidth(200);

        tablaVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarVenta();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaVentas);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        add(scrollTabla, BorderLayout.CENTER);

        // --- ZONA DERECHA (VISUALIZADOR DE TICKET) ---
        add(construirPanelVisualizador(), BorderLayout.EAST);

        // Cargar datos al iniciar
        cargarTabla();
    }

    private JPanel construirPanelVisualizador() {
        JPanel panelDer = new JPanel(new BorderLayout(0, 15));
        panelDer.setPreferredSize(new Dimension(320, 0));
        panelDer.setBackground(Color.WHITE);
        panelDer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitVisualizador = new JLabel("Vista Previa del Ticket", SwingConstants.CENTER);
        lblTitVisualizador.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitVisualizador.setForeground(new Color(127, 140, 141));
        panelDer.add(lblTitVisualizador, BorderLayout.NORTH);

        // El truco para simular papel térmico: Letra Monospaced, fondo amarillo claro
        txtVistaPrevia = new JTextArea();
        txtVistaPrevia.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtVistaPrevia.setBackground(new Color(255, 255, 240)); 
        txtVistaPrevia.setEditable(false);
        txtVistaPrevia.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollTicket = new JScrollPane(txtVistaPrevia);
        scrollTicket.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelDer.add(scrollTicket, BorderLayout.CENTER);

        btnReimprimir = new JButton("REIMPRIMIR TICKET");
        btnReimprimir.setBackground(new Color(39, 174, 96));
        btnReimprimir.setForeground(Color.WHITE);
        btnReimprimir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReimprimir.setPreferredSize(new Dimension(0, 45));
        btnReimprimir.setFocusPainted(false);
        btnReimprimir.setEnabled(false);
        btnReimprimir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReimprimir.addActionListener(e -> reimprimirTicket());
        
        panelDer.add(btnReimprimir, BorderLayout.SOUTH);

        return panelDer;
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        dao.VentaDAO daoVentas = new dao.VentaDAO();
        String filtro = txtBuscar.getText().trim();
        
        List<Object[]> ventas = daoVentas.listarHistorialVentas(filtro);
        for (Object[] v : ventas) {
            modeloTabla.addRow(v);
        }
        limpiarVisualizador();
    }

    private void seleccionarVenta() {
        int fila = tablaVentas.getSelectedRow();
        if (fila >= 0) {
            int modelRow = tablaVentas.convertRowIndexToModel(fila);
            idVentaSeleccionada = Integer.parseInt(modeloTabla.getValueAt(modelRow, 0).toString());
            
            btnReimprimir.setEnabled(true);
            generarTextoVistaPrevia(idVentaSeleccionada);
        }
    }

    private void limpiarVisualizador() {
        idVentaSeleccionada = -1;
        txtVistaPrevia.setText("\n\n\n\n\n       Seleccione un ticket\n       de la tabla para ver\n       su contenido aquí.");
        btnReimprimir.setEnabled(false);
    }

    // =========================================================
    // LÓGICA: DIBUJAR EL TICKET EN FORMATO TEXTO (VISUALIZADOR)
    // =========================================================
    private void generarTextoVistaPrevia(int idVenta) {
        StringBuilder sb = new StringBuilder();
        
        String fecha = ""; String cajero = ""; double total = 0.0; String metodo = "";
        int idOrden = 0;
        java.util.List<String[]> detalles = new java.util.ArrayList<>();

        // 1. Consultar base de datos para armar la vista previa
        try (java.sql.Connection con = new factory.ConexionFactory().getConexion()) {
            String sqlVenta = "SELECT v.fecha_venta, v.total, v.metodo_pago, u.usuario, v.id_orden FROM ventas v JOIN usuarios u ON v.id_usuario = u.id_usuario WHERE v.id_venta = ?";
            try (java.sql.PreparedStatement ps = con.prepareStatement(sqlVenta)) {
                ps.setInt(1, idVenta);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fecha = rs.getString("fecha_venta");
                        total = rs.getDouble("total");
                        metodo = rs.getString("metodo_pago");
                        cajero = rs.getString("usuario");
                        idOrden = rs.getInt("id_orden");
                    }
                }
            }
            
            String sqlDet = "SELECT cantidad, descripcion, precio_unitario, subtotal FROM detalles_venta WHERE id_venta = ?";
            try (java.sql.PreparedStatement ps = con.prepareStatement(sqlDet)) {
                ps.setInt(1, idVenta);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detalles.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(4)});
                    }
                }
            }
        } catch (Exception e) {
            txtVistaPrevia.setText("Error al cargar datos.");
            return;
        }

        // 2. Armar el String imitando el ticket térmico
        String linea = "--------------------------------\n";
        
        sb.append("            SAIRTECH\n");
        sb.append("Reparación y Venta de Accesorios\n");
        sb.append("    Santa Barbara, Honduras\n");
        sb.append("         CEL: 8951-8040\n\n");
        
        if(idOrden > 0) sb.append("     COMPROBANTE DE ENTREGA\n\n");
        else sb.append("     NOTA DE VENTA / RECIBO\n\n");
        
        sb.append("Ticket No: ").append(idVenta).append("\n");
        sb.append("Fecha: ").append(fecha).append("\n");
        sb.append("Cajero: ").append(cajero).append("\n");
        
        if (idOrden > 0) {
            sb.append("Orden Ref: #").append(idOrden).append("\n");
        }
        
        sb.append(linea);
        sb.append("CANT DESCRIPCION        SUBTOTAL\n");
        sb.append(linea);
        
        for (String[] d : detalles) {
            String cant = d[0] + "x";
            String desc = d[1].length() > 18 ? d[1].substring(0, 18) : d[1];
            String sub = d[2];
            
            // Formateo para alinear a la derecha el subtotal en modo texto
            sb.append(String.format("%-4s %-18s %7s\n", cant, desc, sub));
        }
        
        sb.append(linea);
        sb.append(String.format("TOTAL A PAGAR:        L. %-7.2f\n", total));
        sb.append("Método: ").append(metodo).append("\n");
        sb.append(linea);
        
        if (idOrden > 0) {
            sb.append("\n      PÓLIZA DE GARANTÍA\n");
            sb.append("1. Válida por defecto fábrica.\n");
            sb.append("2. Se anula por humedad/golpes.\n");
        }
        
        sb.append("\n  ¡Gracias por su preferencia!\n");

        txtVistaPrevia.setText(sb.toString());
        txtVistaPrevia.setCaretPosition(0); // Para que el scroll se quede arriba
    }

    private void reimprimirTicket() {
        if (idVentaSeleccionada != -1) {
            utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta();
            boolean ok = impresora.imprimirReciboVenta(idVentaSeleccionada);
            
            if (ok) {
                // CAMBIO: Notificación nativa de Windows
                utilidades.NotificadorWindows.mostrarAlerta(
                    "Reimpresión Exitosa", 
                    "El recibo #" + idVentaSeleccionada + " ha sido enviado a la ticketera.", 
                    java.awt.TrayIcon.MessageType.INFO
                );
            } else {
                JOptionPane.showMessageDialog(this, "Error al comunicar con la impresora.", "Error de Hardware", JOptionPane.ERROR_MESSAGE);
            }
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
