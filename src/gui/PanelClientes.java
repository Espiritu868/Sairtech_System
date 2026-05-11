package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.ClienteDAO;
import modelo.Cliente;

public class PanelClientes extends JPanel {

    // Nuestras variables manuales
    private int idClienteSeleccionado = -1;
    
    private JTable tablaClientes;
    private JTextField txtClienteBusqueda;
    private JTextField txtIdentidad;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public PanelClientes() {
        initComponents(); // Método inofensivo para que NetBeans no llore
        inicializarComponentesManual(); // Inicializamos nuestros elementos
        aplicarDisenoClientes(); // Armamos la UI visualmente
        cargarTablaClientes(""); // Llenamos datos
    }
    
    private void inicializarComponentesManual() {
        tablaClientes = new JTable();
        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaClientesMouseClicked();
            }
        });
        
        txtClienteBusqueda = new JTextField();
        txtClienteBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarTablaClientes(txtClienteBusqueda.getText().trim());
            }
        });
        
        txtIdentidad = new JTextField();
        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();
        
        btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> btnGuardarActionPerformed());
        
        btnModificar = new JButton("Modificar");
        btnModificar.setEnabled(false);
        btnModificar.addActionListener(e -> btnModificarActionPerformed());
        
        btnEliminar = new JButton("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> btnEliminarActionPerformed());
        
        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setEnabled(false);
        btnLimpiar.addActionListener(e -> btnLimpiarActionPerformed());
    }

    private void cargarTablaClientes(String textoBusqueda) {
        ClienteDAO dao = new ClienteDAO();
        List<Cliente> lista;   
       
        if (textoBusqueda.isEmpty()) {
            lista = dao.listar();
        } else {
            lista = dao.buscar(textoBusqueda);
        }
        
        Collections.reverse(lista);
        
        DefaultTableModel modeloTabla = new DefaultTableModel(new Object[]{"ID", "Identidad", "Nombre", "Apellido", "Teléfono", "Correo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Cliente c : lista) {
            Object[] fila = new Object[6];
            String iden = c.getNumeroIdentidad();
            String tel = c.getTelefono();
            String cor = c.getCorreo();

            fila[0] = c.getIdCliente();
            fila[1] = iden;
            fila[2] = c.getNombre();
            fila[3] = c.getApellido();
            
            // --- LÓGICA DE LIMPIEZA VISUAL (N/A) ---
            if (tel == null || tel.trim().isEmpty() || tel.contains(iden) || tel.equals("N/A")) {
                fila[4] = "N/A";
            } else {
                fila[4] = tel;
            }

            if (cor == null || cor.trim().isEmpty() || cor.contains(iden) || cor.equals("N/A")) {
                fila[5] = "N/A";
            } else {
                fila[5] = cor;
            }

            modeloTabla.addRow(fila);
        }
        
        tablaClientes.setModel(modeloTabla);

        if (tablaClientes.getColumnModel().getColumnCount() > 0) {
            tablaClientes.getColumnModel().getColumn(0).setPreferredWidth(50);   
            tablaClientes.getColumnModel().getColumn(1).setPreferredWidth(150); 
            tablaClientes.getColumnModel().getColumn(2).setPreferredWidth(160); 
            tablaClientes.getColumnModel().getColumn(3).setPreferredWidth(160); 
            tablaClientes.getColumnModel().getColumn(4).setPreferredWidth(120); 
            tablaClientes.getColumnModel().getColumn(5).setPreferredWidth(250); 
            tablaClientes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        }
    }

    private void btnGuardarActionPerformed() {                                           
        String identidad = txtIdentidad.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (identidad.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La Identidad, Nombre y Apellido son obligatorios.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (identidad.length() > 13) {
            JOptionPane.showMessageDialog(this, "La identidad no debe superar los 13 dígitos.", "Identidad Inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (telefono.isEmpty()) telefono = "N/A";
        if (correo.isEmpty()) correo = "N/A";

        ClienteDAO dao = new ClienteDAO();

        if (dao.existeIdentidad(identidad, -1)) {
            JOptionPane.showMessageDialog(this, "Esta identidad ya está registrada.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!telefono.equals("N/A") && dao.existeTelefono(telefono, -1)) {
            JOptionPane.showMessageDialog(this, "Este teléfono ya está registrado a otro cliente.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente nuevoCliente = new Cliente(identidad, nombre, apellido, telefono, correo);

        if (dao.insertar(nuevoCliente) != -1) {
            JOptionPane.showMessageDialog(this, "¡Cliente guardado exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            btnLimpiarActionPerformed(); 
        } else {
            JOptionPane.showMessageDialog(this, "Error crítico de base de datos.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }                                          

    private void tablaClientesMouseClicked() {                                           
        int fila = tablaClientes.getSelectedRow();
        
        if (fila >= 0) {
            try {
                idClienteSeleccionado = Integer.parseInt(tablaClientes.getValueAt(fila, 0).toString());
                
                Object identidad = tablaClientes.getValueAt(fila, 1);
                txtIdentidad.setText(identidad != null ? identidad.toString() : "");
                
                Object nombre = tablaClientes.getValueAt(fila, 2);
                txtNombre.setText(nombre != null ? nombre.toString() : "");
                
                Object apellido = tablaClientes.getValueAt(fila, 3);
                txtApellido.setText(apellido != null ? apellido.toString() : "");
                
                Object telefono = tablaClientes.getValueAt(fila, 4);
                txtTelefono.setText(telefono != null && !telefono.toString().equals("N/A") ? telefono.toString() : "");
                
                Object correo = tablaClientes.getValueAt(fila, 5);
                txtCorreo.setText(correo != null && !correo.toString().equals("N/A") ? correo.toString() : "");
                
                btnGuardar.setEnabled(false);
                btnModificar.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnLimpiar.setEnabled(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al leer la tabla: " + e.getMessage());
            }
        }
    }                                          

    private void btnModificarActionPerformed() {                                             
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String identidadActual = txtIdentidad.getText().trim();
        String nombreActual = txtNombre.getText().trim();
        String apellidoActual = txtApellido.getText().trim();
        String telefonoActual = txtTelefono.getText().trim();
        String correoActual = txtCorreo.getText().trim();

        int fila = tablaClientes.getSelectedRow();
        String identidadTabla = tablaClientes.getValueAt(fila, 1).toString();
        String nombreTabla = tablaClientes.getValueAt(fila, 2).toString();
        String apellidoTabla = tablaClientes.getValueAt(fila, 3).toString();
        
        Object telefonoObj = tablaClientes.getValueAt(fila, 4);
        String telefonoTabla = (telefonoObj != null && !telefonoObj.toString().equals("N/A")) ? telefonoObj.toString() : "";
        
        Object correoObj = tablaClientes.getValueAt(fila, 5);
        String correoTabla = (correoObj != null && !correoObj.toString().equals("N/A")) ? correoObj.toString() : "";

        if (identidadActual.equals(identidadTabla) && nombreActual.equals(nombreTabla) && 
            apellidoActual.equals(apellidoTabla) && telefonoActual.equals(telefonoTabla) && 
            correoActual.equals(correoTabla)) {
            JOptionPane.showMessageDialog(this, "No se han detectado cambios en los datos.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }

        if (identidadActual.isEmpty() || nombreActual.isEmpty() || apellidoActual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La Identidad, Nombre y Apellido no pueden quedar vacíos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (identidadActual.length() > 13) {
            JOptionPane.showMessageDialog(this, "La identidad no debe superar los 13 dígitos.", "Identidad Inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ClienteDAO dao = new ClienteDAO();

        if (dao.existeIdentidad(identidadActual, idClienteSeleccionado)) {
            JOptionPane.showMessageDialog(this, "La nueva identidad ya pertenece a otro cliente.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (telefonoActual.isEmpty()) telefonoActual = "N/A";
        if (correoActual.isEmpty()) correoActual = "N/A";

        if (!telefonoActual.equals("N/A") && dao.existeTelefono(telefonoActual, idClienteSeleccionado)) {
            JOptionPane.showMessageDialog(this, "El nuevo teléfono ya pertenece a otro cliente.", "Error de Duplicado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente clienteModificado = new Cliente(idClienteSeleccionado, identidadActual, nombreActual, apellidoActual, telefonoActual, correoActual);
        
        if (dao.actualizar(clienteModificado)) {
            JOptionPane.showMessageDialog(this, "¡Datos actualizados!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            btnLimpiarActionPerformed();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la base de datos.", "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }                                            
    
    private void btnEliminarActionPerformed() {                                            
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente de la tabla para eliminarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClienteDAO dao = new ClienteDAO();

        if (dao.tieneHistorial(idClienteSeleccionado)) {
            JOptionPane.showMessageDialog(this, 
                "No se puede eliminar este cliente porque tiene equipos o reparaciones en el historial.\nPor seguridad contable, sus datos están protegidos.", 
                "Acción Denegada", 
                JOptionPane.ERROR_MESSAGE);
            return; 
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de que desea eliminar este cliente?\n(Sus datos serán anonimizados por seguridad del sistema)", 
                "Confirmar Eliminación", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (dao.eliminar(idClienteSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                btnLimpiarActionPerformed();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }                                           

    private void btnLimpiarActionPerformed() {                                           
        txtIdentidad.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        txtClienteBusqueda.setText(""); 
        
        idClienteSeleccionado = -1;
        
        btnGuardar.setEnabled(true);     
        btnModificar.setEnabled(false);  
        btnEliminar.setEnabled(false);   
        btnLimpiar.setEnabled(false);   
        
        tablaClientes.clearSelection();
        cargarTablaClientes("");
    }                                          

    private void aplicarDisenoClientes() {
        this.removeAll();
        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        this.setBackground(new Color(240, 244, 248)); 

        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(44, 62, 80));
        this.add(lblTitulo, BorderLayout.NORTH);

        // ZONA IZQUIERDA: BUSCADOR Y TABLA
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 10));
        panelIzquierdo.setOpaque(false);
        
        JPanel panelBuscador = new JPanel(new BorderLayout(10, 0));
        panelBuscador.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscar Cliente (Nombre o Identidad):");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtClienteBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        txtClienteBusqueda.setPreferredSize(new Dimension(0, 35));
        panelBuscador.add(lblBuscar, BorderLayout.WEST);
        panelBuscador.add(txtClienteBusqueda, BorderLayout.CENTER);
        
        tablaClientes.setRowHeight(35);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        JScrollPane scrollBusqueda = new JScrollPane(tablaClientes);
        scrollBusqueda.getViewport().setBackground(Color.WHITE);
        scrollBusqueda.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panelIzquierdo.add(panelBuscador, BorderLayout.NORTH);
        panelIzquierdo.add(scrollBusqueda, BorderLayout.CENTER);
        this.add(panelIzquierdo, BorderLayout.CENTER);

        // ZONA DERECHA: FORMULARIO BLANCO
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(Color.WHITE);
        panelFormulario.setPreferredSize(new Dimension(300, 0));
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 2, 0);
        gbc.weightx = 1.0; gbc.gridx = 0;

        JLabel lblSub = new JLabel("Datos del Cliente");
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSub.setForeground(Color.GRAY);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 15, 0);
        panelFormulario.add(lblSub, gbc);

        // Estilizar los inputs
        JTextField[] inputs = {txtIdentidad, txtNombre, txtApellido, txtTelefono, txtCorreo};
        for (JTextField txt : inputs) {
            txt.setPreferredSize(new Dimension(0, 35));
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        gbc.insets = new Insets(5, 0, 2, 0);
        gbc.gridy++; panelFormulario.add(new JLabel("Identidad:"), gbc);
        gbc.gridy++; panelFormulario.add(txtIdentidad, gbc);

        gbc.gridy++; panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridy++; panelFormulario.add(txtNombre, gbc);

        gbc.gridy++; panelFormulario.add(new JLabel("Apellido:"), gbc);
        gbc.gridy++; panelFormulario.add(txtApellido, gbc);

        gbc.gridy++; panelFormulario.add(new JLabel("Teléfono: (Opcional)"), gbc);
        gbc.gridy++; panelFormulario.add(txtTelefono, gbc);

        gbc.gridy++; panelFormulario.add(new JLabel("Correo: (Opcional)"), gbc);
        gbc.gridy++; panelFormulario.add(txtCorreo, gbc);

        gbc.gridy++; gbc.insets = new Insets(20, 0, 0, 0);
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        panelBotones.setOpaque(false);

        estilizarBoton(btnGuardar, new Color(46, 204, 113));
        estilizarBoton(btnModificar, new Color(52, 152, 219));
        estilizarBoton(btnEliminar, new Color(231, 76, 60));
        estilizarBoton(btnLimpiar, Color.GRAY);

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridy++; panelFormulario.add(panelBotones, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        panelFormulario.add(Box.createVerticalGlue(), gbc);

        this.add(panelFormulario, BorderLayout.EAST);

        this.revalidate();
        this.repaint();
    }
    
    private void estilizarBoton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
