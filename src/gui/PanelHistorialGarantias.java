package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelHistorialGarantias extends JPanel {

    private JTextField txtBuscar;
    private JTable tablaGarantias;
    private DefaultTableModel modeloGarantias;
    
    // Elementos del panel lateral
    private JTextArea txtVistaPrevia;
    private JButton btnReimprimir;

    public PanelHistorialGarantias() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE); // Fondo blanco como en tu captura
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // --- PANEL SUPERIOR (Título a la izquierda + Buscador a la derecha) ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setOpaque(false);

        JLabel lblTitulo = new JLabel("HISTORIAL DE GARANTÍAS Y EQUIPOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(44, 62, 80));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBuscador.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscar (IMEI, Cliente o Ticket):");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(250, 35));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarTabla(txtBuscar.getText().trim());
            }
        });
        panelBuscador.add(lblBuscar);
        panelBuscador.add(txtBuscar);
        panelSuperior.add(panelBuscador, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL CENTRAL (Tabla a la izquierda) ---
        add(construirPanelTabla(), BorderLayout.CENTER);
        
        // --- PANEL DERECHO (Vista Previa) ---
        add(construirPanelVistaPrevia(), BorderLayout.EAST);
        
        cargarTabla(""); 
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // --- AÑADIMOS COLUMNAS OCULTAS PARA PRECIO, CATEGORÍA, TELÉFONO Y DÍAS ---
        String[] columnas = {"N° Ticket", "Fecha Compra", "Vencimiento", "Cliente", "Equipo / Producto", "IMEI", "Estado",
                             "Precio_Hidden", "Categoria_Hidden", "Telefono_Hidden", "Dias_Hidden"}; // 4 nuevas columnas
        modeloGarantias = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaGarantias = new JTable(modeloGarantias);
        tablaGarantias.setRowHeight(35);
        tablaGarantias.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Estilo del encabezado
        tablaGarantias.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaGarantias.getTableHeader().setBackground(new Color(52, 152, 219)); 
        tablaGarantias.getTableHeader().setForeground(Color.WHITE);
        tablaGarantias.getTableHeader().setOpaque(false);

        // --- OCULTAR LAS COLUMNAS EXTRA (7, 8, 9, 10) ---
        for(int i = 7; i < columnas.length; i++) {
            tablaGarantias.getColumnModel().getColumn(i).setMinWidth(0);
            tablaGarantias.getColumnModel().getColumn(i).setMaxWidth(0);
            tablaGarantias.getColumnModel().getColumn(i).setWidth(0);
            tablaGarantias.getTableHeader().getColumnModel().getColumn(i).setMinWidth(0);
            tablaGarantias.getTableHeader().getColumnModel().getColumn(i).setMaxWidth(0);
            tablaGarantias.getTableHeader().getColumnModel().getColumn(i).setWidth(0);
        }

        // Configuración de anchos de columnas visibles
        if (tablaGarantias.getColumnModel().getColumnCount() > 0) {
            tablaGarantias.getColumnModel().getColumn(0).setPreferredWidth(80);
            tablaGarantias.getColumnModel().getColumn(1).setPreferredWidth(130);
            tablaGarantias.getColumnModel().getColumn(2).setPreferredWidth(110);
            tablaGarantias.getColumnModel().getColumn(3).setPreferredWidth(180);
            tablaGarantias.getColumnModel().getColumn(4).setPreferredWidth(250);
            tablaGarantias.getColumnModel().getColumn(5).setPreferredWidth(150);
            tablaGarantias.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        // Semáforo Visual para el Estado (Verde / Rojo)
        tablaGarantias.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                if (value != null) {
                    String estado = value.toString();
                    if (estado.equals("VIGENTE")) {
                        setForeground(new Color(39, 174, 96)); 
                    } else {
                        setForeground(new Color(231, 76, 60)); 
                    }
                }
                if (isSelected) setForeground(Color.WHITE); 
                return c;
            }
        });

        // Evento al hacer clic en la tabla para actualizar el panel de la derecha
        tablaGarantias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                actualizarVistaPrevia();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaGarantias);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirPanelVistaPrevia() {
        // Panel lateral derecho
        JPanel panelDerecho = new JPanel(new BorderLayout(10, 10));
        panelDerecho.setPreferredSize(new Dimension(320, 0));
        panelDerecho.setBackground(Color.WHITE);
        
        JLabel lblTitVista = new JLabel("Póliza de Garantía", JLabel.CENTER);
        lblTitVista.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitVista.setForeground(new Color(127, 140, 141));
        panelDerecho.add(lblTitVista, BorderLayout.NORTH);

        txtVistaPrevia = new JTextArea();
        txtVistaPrevia.setEditable(false);
        txtVistaPrevia.setFont(new Font("Consolas", Font.PLAIN, 13)); 
        txtVistaPrevia.setBackground(new Color(255, 255, 240)); // Fondo amarillento simula ticket amarillo
        txtVistaPrevia.setText("\n\n\n\n\n\n    Seleccione un equipo\n    de la tabla para ver\n    su garantía aquí.");
        txtVistaPrevia.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollVista = new JScrollPane(txtVistaPrevia);
        scrollVista.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panelDerecho.add(scrollVista, BorderLayout.CENTER);

        // Botón al fondo
        btnReimprimir = new JButton("REIMPRIMIR PÓLIZA");
        btnReimprimir.setPreferredSize(new Dimension(0, 45));
        btnReimprimir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReimprimir.setBackground(new Color(236, 240, 241));
        btnReimprimir.setForeground(new Color(127, 140, 141));
        btnReimprimir.setFocusPainted(false);
        btnReimprimir.setEnabled(false); 
        
        btnReimprimir.addActionListener(e -> {
            int fila = tablaGarantias.getSelectedRow();
            if (fila >= 0) {
                try {
                    String ticket = modeloGarantias.getValueAt(fila, 0).toString();
                    String fecha = modeloGarantias.getValueAt(fila, 1).toString();
                    String vence = modeloGarantias.getValueAt(fila, 2).toString();
                    String cliente = modeloGarantias.getValueAt(fila, 3).toString();
                    String equipo = modeloGarantias.getValueAt(fila, 4).toString();
                    String imei = modeloGarantias.getValueAt(fila, 5).toString();
                    String cat = modeloGarantias.getValueAt(fila, 8).toString();
                    String tel = modeloGarantias.getValueAt(fila, 9).toString();
                    int dias = Integer.parseInt(modeloGarantias.getValueAt(fila, 10).toString());

                    // Intentamos imprimir
                    utilidades.ImpresoraDirecta imp = new utilidades.ImpresoraDirecta();
                    imp.imprimirPolizaGarantia(ticket, fecha, vence, cliente, tel, equipo, imei, dias, cat);
                    
                    // Mensaje de cortesía para saber que el código sí corrió
                    JOptionPane.showMessageDialog(this, "Orden de impresión enviada a la SAT 38T.\nVerifica si la impresora tiene papel.", "Impresión", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al procesar los datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un equipo de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        panelDerecho.add(btnReimprimir, BorderLayout.SOUTH);

        return panelDerecho;
    }

    private void cargarTabla(String filtro) {
        modeloGarantias.setRowCount(0);
        dao.VentaDAO daoVenta = new dao.VentaDAO();
        List<Object[]> lista = daoVenta.listarGarantias(filtro);
        
        for (Object[] filaCompleta : lista) {
            // Creamos una fila recortada para la JTable (solo lo que se debe mostrar visualmente)
            // Mostramos: Recibo, Fecha, Vence, Cliente, Descripción, IMEI, Estado
            Object[] filaVisible = new Object[7];
            System.arraycopy(filaCompleta, 0, filaVisible, 0, 7);
            
            // Agregamos la fila visible al modelo
            modeloGarantias.addRow(filaVisible);
            
            // TIP: Si necesitas los datos ocultos (teléfono, categoría, etc.) para la vista previa,
            // asegúrate de guardarlos en una lista global o recuperarlos del modelo si tu tabla 
            // tiene columnas ocultas.
        }
        
        // Reset visual al cargar
        txtVistaPrevia.setText("\n\n\n\n\n\n    Seleccione un equipo\n    de la tabla para ver\n    su garantía aquí.");
        btnReimprimir.setEnabled(false);
        btnReimprimir.setBackground(new Color(236, 240, 241));
        btnReimprimir.setForeground(new Color(127, 140, 141));
    }
    
    // --- REESTRUCTURACIÓN COMPLETA DEL TICKET BASADO EN TU FORMULARIO FÍSICO ---
    private void actualizarVistaPrevia() {
        int fila = tablaGarantias.getSelectedRow();
        if (fila >= 0) {
            String numTicket = modeloGarantias.getValueAt(fila, 0).toString();
            String fechaCompra = modeloGarantias.getValueAt(fila, 1).toString();
            String fechaVence = modeloGarantias.getValueAt(fila, 2).toString();
            String cliente = modeloGarantias.getValueAt(fila, 3).toString();
            String producto = modeloGarantias.getValueAt(fila, 4).toString();
            String imei = modeloGarantias.getValueAt(fila, 5).toString();
            String estado = modeloGarantias.getValueAt(fila, 6).toString();
            
            String precioUnitario = modeloGarantias.getValueAt(fila, 7).toString();
            String categoria = modeloGarantias.getValueAt(fila, 8).toString();
            String telefono = modeloGarantias.getValueAt(fila, 9) != null ? modeloGarantias.getValueAt(fila, 9).toString() : "N/D";
            String diasGarantiaValue = modeloGarantias.getValueAt(fila, 10).toString();
            
            StringBuilder sb = new StringBuilder();
            
            // 1. ENCABEZADO (Optimizado para 32 caracteres)
            sb.append("     SAIRTECH - TECNOLOGIA\n");
            sb.append("         Tel: 9988-3561\n");
            sb.append(" Santa Barbara, S.B.\n");
            sb.append("================================\n");
            sb.append("       POLIZA DE GARANTIA\n");
            sb.append("================================\n\n");
            
            // 2. DATOS DEL TICKET Y CLIENTE
            sb.append("Recibo: #").append(numTicket).append("\n");
            sb.append("F. Compra: ").append(fechaCompra).append("\n");
            sb.append("F. Vence:  ").append(fechaVence).append("\n");
            sb.append("--------------------------------\n");
            
            // Acortamos el nombre del cliente si es muy largo para evitar saltos raros
            if(cliente.length() > 22) cliente = cliente.substring(0, 22) + ".";
            sb.append("Cliente: ").append(cliente).append("\n");
            sb.append("Tel: ").append(telefono).append("\n");
            sb.append("IMEI: ").append(imei).append("\n");
            sb.append("================================\n\n");
            
            // 3. DETALLE DEL EQUIPO Y PRECIO
            sb.append("EQUIPO / PRODUCTO:\n");
            if(producto.length() > 32) {
                sb.append(producto.substring(0, 29)).append("...\n");
            } else {
                sb.append(producto).append("\n");
            }
            sb.append("Precio Unid: L. ").append(precioUnitario).append("\n");
            sb.append("================================\n\n");
            
            // 4. CONDICIONES (Ajustadas a márgenes seguros)
            sb.append("CONDICIONES:\n");
            sb.append("--------------------------------\n");
            
            sb.append("1. Todo ").append(categoria).append(" de SAIRTECH\n");
            sb.append("tiene garantia de ").append(diasGarantiaValue).append(" dias desde\n");
            sb.append("la fecha de facturacion.\n");
            
            sb.append("2. Se cambiara el producto\n");
            sb.append("previo revision tecnica.\n");
            
            sb.append("3. Los accesorios no tienen\n");
            sb.append("garantia despues de 7 dias.\n");
            
            sb.append("4. La garantia es nula si el\n");
            sb.append("producto presenta golpes, esta\n");
            sb.append("mojado, o fue alterado por\n");
            sb.append("personas no autorizadas.\n");
            
            sb.append("5. Sin devolucion en efectivo.\n");
            
            if (categoria.equalsIgnoreCase("TELEFONO") || categoria.equalsIgnoreCase("CELULAR")) {
                sb.append("6. El cambio de ").append(categoria.toLowerCase()).append(" se\n");
                sb.append("hara por defecto de fabrica\n");
                sb.append("y por el mismo modelo.\n");
            }
            sb.append("--------------------------------\n\n");
            
            // 5. SECCIÓN DE FIRMA
            sb.append("      __________________\n");
            sb.append("        Firma del Cliente\n\n");
            
            txtVistaPrevia.setText(sb.toString());
            
            btnReimprimir.setEnabled(true);
            btnReimprimir.setBackground(new Color(52, 152, 219)); 
            btnReimprimir.setForeground(Color.WHITE);
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
