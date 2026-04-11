package gui;

import dao.ProveedorDAO;
import modelo.Proveedor;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PanelProveedores extends JPanel {

    // Componentes del Formulario
    private JTextField txtId;
    private JTextField txtEmpresa;
    private JTextField txtContacto;
    private JTextField txtTelefono;
    private JTextArea txtDireccion;
    private JTextField txtTipoRepuestos;
    
    // Componentes de la Tabla
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    private ProveedorDAO dao;

    public PanelProveedores() {
        dao = new ProveedorDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 244, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título Principal
        JLabel lblTitulo = new JLabel(" Directorio de Proveedores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));
        add(lblTitulo, BorderLayout.NORTH);

        add(construirPanelFormulario(), BorderLayout.WEST);
        add(construirPanelTabla(), BorderLayout.CENTER);

        cargarDatos();
    }

    private JPanel construirPanelFormulario() {
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

        JLabel lblSub = new JLabel("Datos del Proveedor");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSub.setForeground(new Color(41, 128, 185));
        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(lblSub, gbc);

        // ID Oculto para lógica interna
        txtId = new JTextField("0");
        txtId.setVisible(false);
        panel.add(txtId, gbc);

        gbc.insets = new Insets(5, 0, 2, 0);
        
        gbc.gridy++; panel.add(new JLabel("Nombre de la Empresa (*):"), gbc);
        txtEmpresa = new JTextField(); txtEmpresa.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtEmpresa, gbc);

        gbc.gridy++; panel.add(new JLabel("Nombre del Contacto:"), gbc);
        txtContacto = new JTextField(); txtContacto.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtContacto, gbc);

        gbc.gridy++; panel.add(new JLabel("Teléfono / WhatsApp:"), gbc);
        txtTelefono = new JTextField(); txtTelefono.setPreferredSize(new Dimension(0, 30));
        gbc.gridy++; panel.add(txtTelefono, gbc);

        gbc.gridy++; panel.add(new JLabel("Tipo de Repuestos que vende:"), gbc);
        txtTipoRepuestos = new JTextField(); txtTipoRepuestos.setPreferredSize(new Dimension(0, 30));
        txtTipoRepuestos.setToolTipText("Ej: Pantallas, Baterías, Herramientas...");
        gbc.gridy++; panel.add(txtTipoRepuestos, gbc);

        gbc.gridy++; panel.add(new JLabel("Dirección:"), gbc);
        txtDireccion = new JTextArea(3, 20);
        txtDireccion.setLineWrap(true); txtDireccion.setWrapStyleWord(true);
        txtDireccion.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridy++; panel.add(new JScrollPane(txtDireccion), gbc);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(39, 174, 96)); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false); btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.addActionListener(e -> guardarProveedor());

        JButton btnLimpiar = new JButton("Nuevo / Limpiar");
        btnLimpiar.setBackground(new Color(189, 195, 199)); btnLimpiar.setForeground(Color.BLACK);
        btnLimpiar.setFocusPainted(false); btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        
        gbc.gridy++; gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(panelBotones, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Buscador
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setOpaque(false);
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(0, 35));
        
        panelBusqueda.add(new JLabel(" Buscar Empresa / Contacto: "), BorderLayout.WEST);
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);

        // Tabla
        String[] columnas = {"ID", "Empresa", "Contacto", "Teléfono", "Repuestos", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setRowHeight(30);
        tablaProveedores.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaProveedores.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // Ajustar anchos
        tablaProveedores.getColumnModel().getColumn(0).setPreferredWidth(40); // ID
        tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(150); // Empresa
        tablaProveedores.getColumnModel().getColumn(4).setPreferredWidth(120); // Tipo repuestos

        // Filtro en tiempo real
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaProveedores.setRowSorter(sorter);
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = txtBuscar.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null); 
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        // Evento al hacer clic en una fila para editar
        tablaProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tablaProveedores.getSelectedRow();
                if (fila != -1) {
                    int filaReal = tablaProveedores.convertRowIndexToModel(fila);
                    txtId.setText(modeloTabla.getValueAt(filaReal, 0).toString());
                    txtEmpresa.setText(modeloTabla.getValueAt(filaReal, 1).toString());
                    txtContacto.setText(modeloTabla.getValueAt(filaReal, 2) != null ? modeloTabla.getValueAt(filaReal, 2).toString() : "");
                    txtTelefono.setText(modeloTabla.getValueAt(filaReal, 3) != null ? modeloTabla.getValueAt(filaReal, 3).toString() : "");
                    txtTipoRepuestos.setText(modeloTabla.getValueAt(filaReal, 4) != null ? modeloTabla.getValueAt(filaReal, 4).toString() : "");
                    txtDireccion.setText(modeloTabla.getValueAt(filaReal, 5) != null ? modeloTabla.getValueAt(filaReal, 5).toString() : "");
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProveedores);
        scroll.getViewport().setBackground(Color.WHITE);

        // Botón Eliminar
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.setOpaque(false);
        JButton btnOcultar = new JButton("Eliminar Proveedor Seleccionado");
        btnOcultar.setBackground(new Color(231, 76, 60)); btnOcultar.setForeground(Color.WHITE);
        btnOcultar.setFocusPainted(false);
        btnOcultar.addActionListener(e -> ocultarProveedor());
        panelSur.add(btnOcultar);

        panel.add(panelBusqueda, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Proveedor> lista = dao.listarActivos();
        for (Proveedor p : lista) {
            modeloTabla.addRow(new Object[]{
                p.getIdProveedor(), p.getEmpresa(), p.getNombreContacto(), 
                p.getTelefono(), p.getTipoRepuestos(), p.getDireccion()
            });
        }
    }

    private void limpiarFormulario() {
        txtId.setText("0"); txtEmpresa.setText(""); txtContacto.setText("");
        txtTelefono.setText(""); txtTipoRepuestos.setText(""); txtDireccion.setText("");
        tablaProveedores.clearSelection(); txtEmpresa.requestFocus();
    }

    private void guardarProveedor() {
        if (txtEmpresa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la empresa es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Proveedor p = new Proveedor();
        p.setEmpresa(txtEmpresa.getText().trim());
        p.setNombreContacto(txtContacto.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setTipoRepuestos(txtTipoRepuestos.getText().trim());
        p.setDireccion(txtDireccion.getText().trim());

        int id = Integer.parseInt(txtId.getText());
        boolean exito;

        if (id == 0) {
            // Guardar Nuevo
            exito = dao.guardar(p);
        } else {
            // Actualizar Existente
            p.setIdProveedor(id);
            exito = dao.actualizar(p);
        }

        if (exito) {
            JOptionPane.showMessageDialog(this, "Proveedor guardado exitosamente.");
            limpiarFormulario();
            cargarDatos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ocultarProveedor() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tablaProveedores.getValueAt(tablaProveedores.convertRowIndexToModel(fila), 0).toString());
        String empresa = tablaProveedores.getValueAt(tablaProveedores.convertRowIndexToModel(fila), 1).toString();

        int res = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar al proveedor " + empresa + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            if (dao.ocultar(id)) {
                JOptionPane.showMessageDialog(this, "Proveedor eliminado correctamente.");
                limpiarFormulario();
                cargarDatos();
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
