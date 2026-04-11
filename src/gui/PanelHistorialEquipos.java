package gui;

import dao.HistorialDAO;
import modelo.Expediente;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PanelHistorialEquipos extends JPanel {

    private JTextField txtBuscar;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    private JLabel lblOrdenFicha;
    private JLabel lblEstadoFicha;
    private JLabel lblFechas;
    private JLabel lblCliente;
    private JLabel lblModelo;
    private JTextArea txtFalla;
    private JTextArea txtDiagnostico;
    private JLabel lblTecnicos;
    private JLabel lblTotal;

    private HistorialDAO dao;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
    
    private List<Expediente> listaExpedientesActual; 

    public PanelHistorialEquipos() {
        dao = new HistorialDAO();
        
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel(" Historial Médico de Equipos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(construirPanelIzquierdo());
        splitPane.setRightComponent(construirFichaClinica());
        splitPane.setDividerLocation(550); 
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        add(splitPane, BorderLayout.CENTER);
        
        cargarTabla(""); 
    }

    private JPanel construirPanelIzquierdo() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel panelBuscador = new JPanel(new BorderLayout(10, 0));
        panelBuscador.setOpaque(false);
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtBuscar.setPreferredSize(new Dimension(0, 40));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarTabla(txtBuscar.getText().trim());
            }
        });
        
        panelBuscador.add(new JLabel(" Buscar Cliente, Equipo u Orden: "), BorderLayout.WEST);
        panelBuscador.add(txtBuscar, BorderLayout.CENTER);

        String[] columnas = {"Orden", "Fecha", "Cliente", "Equipo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setRowHeight(35);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(50); 
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(100); 
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(150); 
        
        tablaHistorial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mostrarDetallesOrden();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        panel.add(panelBuscador, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirFichaClinica() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; 
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(5, 0, 5, 0);

        lblOrdenFicha = new JLabel("ORDEN #---");
        lblOrdenFicha.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblOrdenFicha.setForeground(new Color(41, 128, 185));
        
        lblEstadoFicha = new JLabel("ESTADO");
        lblEstadoFicha.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblEstadoFicha.setForeground(Color.GRAY);
        lblEstadoFicha.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel pnlCabecera = new JPanel(new BorderLayout()); pnlCabecera.setOpaque(false);
        pnlCabecera.add(lblOrdenFicha, BorderLayout.WEST);
        pnlCabecera.add(lblEstadoFicha, BorderLayout.EAST);
        panel.add(pnlCabecera, gbc);
        
        panel.add(new JSeparator(), ++gbc.gridy);

        lblFechas = new JLabel("Ingreso: --/--/---- | Estatus: ---");
        lblFechas.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblFechas.setForeground(Color.DARK_GRAY);
        gbc.gridy++; panel.add(lblFechas, gbc);

        lblCliente = new JLabel("Cliente: Seleccione una orden...");
        lblCliente.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gbc.gridy++; gbc.insets = new Insets(15, 0, 2, 0); panel.add(lblCliente, gbc);

        lblModelo = new JLabel("Equipo: ---");
        lblModelo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        gbc.gridy++; gbc.insets = new Insets(0, 0, 15, 0); panel.add(lblModelo, gbc);

        txtFalla = crearTextAreaVisual("Problema Reportado:");
        gbc.gridy++; gbc.weighty = 0.3; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(txtFalla), gbc);

        txtDiagnostico = crearTextAreaVisual("Trabajo Realizado:");
        gbc.gridy++; gbc.insets = new Insets(15, 0, 15, 0);
        panel.add(new JScrollPane(txtDiagnostico), gbc);

        gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0);
        panel.add(new JSeparator(), ++gbc.gridy);
        
        lblTecnicos = new JLabel("Técnico que Entregó: ---");
        lblTecnicos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTecnicos.setForeground(Color.GRAY);
        gbc.gridy++; panel.add(lblTecnicos, gbc);

        lblTotal = new JLabel("TOTAL COBRADO: L. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(new Color(39, 174, 96));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridy++; gbc.insets = new Insets(15, 0, 0, 0); panel.add(lblTotal, gbc);

        return panel;
    }

    private JTextArea crearTextAreaVisual(String titulo) {
        JTextArea txt = new JTextArea();
        txt.setLineWrap(true); txt.setWrapStyleWord(true);
        txt.setEditable(false); txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBackground(new Color(250, 250, 250));
        txt.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)), 
                titulo, TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 12), Color.GRAY
        ));
        return txt;
    }

    private void cargarTabla(String filtro) {
        modeloTabla.setRowCount(0);
        listaExpedientesActual = dao.buscarExpedienteCompleto(filtro);
        
        for (Expediente exp : listaExpedientesActual) {
            String fechaCorta = exp.getFechaIngreso() != null ? new SimpleDateFormat("dd/MM/yy").format(exp.getFechaIngreso()) : "N/A";
            
            modeloTabla.addRow(new Object[]{
                "#" + exp.getIdOrden(), 
                fechaCorta,    
                exp.getNombreCliente(),       
                exp.getModeloEquipo(),       
                exp.getEstado()        
            });
        }
        limpiarFicha();
    }

    private void mostrarDetallesOrden() {
        int fila = tablaHistorial.getSelectedRow();
        if (fila >= 0) {
            int filaReal = tablaHistorial.convertRowIndexToModel(fila);
            Expediente exp = listaExpedientesActual.get(filaReal);
            
            lblOrdenFicha.setText("ORDEN #" + exp.getIdOrden());
            lblEstadoFicha.setText(exp.getEstado().toUpperCase());
            
            String fEntregaTexto = "Aún en taller";
            if(exp.getEstado().equalsIgnoreCase("Entregado")) {
                lblEstadoFicha.setForeground(new Color(39, 174, 96));
                fEntregaTexto = "Entregado al cliente";
            }
            else if(exp.getEstado().equalsIgnoreCase("Cancelado") || exp.getEstado().equalsIgnoreCase("Sin Reparacion")) {
                lblEstadoFicha.setForeground(new Color(231, 76, 60));
                fEntregaTexto = exp.getEstado();
            }
            else {
                lblEstadoFicha.setForeground(new Color(243, 156, 18)); 
            }

            String fIngreso = exp.getFechaIngreso() != null ? sdf.format(exp.getFechaIngreso()) : "N/A";
            
            lblFechas.setText("Ingresó: " + fIngreso + "   |   Estatus: " + fEntregaTexto);
            
            lblCliente.setText("Cliente: " + exp.getNombreCliente() + " (Tel: " + exp.getTelefonoCliente() + ")");
            lblModelo.setText("Equipo: " + exp.getModeloEquipo());
            
            txtFalla.setText(exp.getProblemaReportado());
            txtDiagnostico.setText(exp.getTrabajoRealizado());
            
            lblTecnicos.setText("Técnico que Entregó: " + exp.getTecnicoEntrega());
            lblTotal.setText(String.format("TOTAL COBRADO: L. %.2f", exp.getCosto()));
        }
    }

    private void limpiarFicha() {
        lblOrdenFicha.setText("ORDEN #---");
        lblEstadoFicha.setText("ESTADO");
        lblEstadoFicha.setForeground(Color.GRAY);
        lblFechas.setText("Ingreso: --/--/---- | Estatus: ---");
        lblCliente.setText("Cliente: Seleccione una orden...");
        lblModelo.setText("Equipo: ---");
        txtFalla.setText("");
        txtDiagnostico.setText("");
        lblTecnicos.setText("Técnico que Entregó: ---");
        lblTotal.setText("TOTAL COBRADO: L. 0.00");
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
