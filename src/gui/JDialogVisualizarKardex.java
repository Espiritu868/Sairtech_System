package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class JDialogVisualizarKardex extends JDialog {

    private int idProducto;
    private JTable tablaKardex;
    private DefaultTableModel modeloKardex;

    public JDialogVisualizarKardex(int idProducto) {
        this.idProducto = idProducto;
        setModal(true); 
        setSize(900, 600); 
        setTitle("Movimientos del Kardex");
        setLocationRelativeTo(null); 
        
        JPanel panelContenedor = new JPanel(new BorderLayout(15, 15));
        panelContenedor.setBackground(Color.WHITE);
        panelContenedor.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel panelCabecera = new JPanel(new BorderLayout());
        panelCabecera.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("Lista De Movimientos Kardex");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(44, 62, 80));
        panelCabecera.add(lblTitulo, BorderLayout.WEST);
        
        JButton btnAgregarAjuste = new JButton("+ Agregar");
        btnAgregarAjuste.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgregarAjuste.setBackground(new Color(41, 128, 185)); 
        btnAgregarAjuste.setForeground(Color.WHITE);
        btnAgregarAjuste.setBorderPainted(false);
        btnAgregarAjuste.setPreferredSize(new Dimension(110, 35));
        btnAgregarAjuste.setFocusPainted(false);
        btnAgregarAjuste.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        btnAgregarAjuste.addActionListener(e -> {
            new gui.JDialogAjusteManualKardex(this.idProducto).setVisible(true);
            cargarDatosKardex(""); 
        });
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.add(btnAgregarAjuste);
        panelCabecera.add(panelBoton, BorderLayout.EAST);
        
        panelContenedor.add(panelCabecera, BorderLayout.NORTH);
        
        // --- TABLA SIN LA COLUMNA DE COSTO ---
        String[] columnas = {"Código", "Fecha Movimiento", "Descripción / Acción", "Cant.", "Saldo", "Usuario"};
        modeloKardex = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaKardex = new JTable(modeloKardex);
        tablaKardex.setRowHeight(38);
        tablaKardex.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tablaKardex.setShowVerticalLines(false);
        tablaKardex.setGridColor(new Color(235, 235, 235));
        tablaKardex.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaKardex.getTableHeader().setBackground(Color.WHITE);
        tablaKardex.getTableHeader().setForeground(Color.GRAY);
        tablaKardex.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        
        // --- LE DAMOS TODO EL ESPACIO A LA DESCRIPCIÓN ---
        if (tablaKardex.getColumnModel().getColumnCount() > 0) {
            tablaKardex.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
            tablaKardex.getColumnModel().getColumn(1).setPreferredWidth(130); // Fecha
            tablaKardex.getColumnModel().getColumn(2).setPreferredWidth(350); // Descripción GIGANTE
            tablaKardex.getColumnModel().getColumn(3).setPreferredWidth(80);  // Cant
            tablaKardex.getColumnModel().getColumn(4).setPreferredWidth(80);  // Saldo
            tablaKardex.getColumnModel().getColumn(5).setPreferredWidth(100); // Usuario
        }
        
        JScrollPane scroll = new JScrollPane(tablaKardex);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        panelContenedor.add(scroll, BorderLayout.CENTER);
        
        this.add(panelContenedor);
        cargarDatosKardex(""); 
    }
    
    public void cargarDatosKardex(String busqueda) {
        modeloKardex.setRowCount(0);
        dao.KardexDAO kardexDao = new dao.KardexDAO();
        java.util.List<Object[]> lista = kardexDao.listarKardexPorProducto(this.idProducto);
        
        for (Object[] fila : lista) {
            // Saltamos la columna del precio (fila[5]) y armamos el arreglo solo con lo que necesitamos
            modeloKardex.addRow(new Object[]{fila[0], fila[1], fila[2], fila[3], fila[4], fila[6]});
        }
        
        // Pinta de rojo si es negativo y verde si es positivo (Columna Cantidad)
        tablaKardex.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    if (value.toString().startsWith("-")) {
                        setForeground(new Color(231, 76, 60)); // Rojo
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        setForeground(new Color(39, 174, 96)); // Verde
                        setFont(new Font("Segoe UI", Font.BOLD, 14));
                    }
                }
                if (isSelected) setForeground(Color.WHITE);
                return c;
            }
        });
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
