package utilidades;

import com.itextpdf.text.pdf.Barcode128;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class ImpresoraDirecta implements Printable {

    private String nombreProducto;
    private String codigoBarras;
    private int totalEtiquetas;

    public boolean imprimirEtiquetasDirecto(String nombreProducto, String codigoBarras, int cantidad) {
        this.nombreProducto = nombreProducto;
        this.codigoBarras = codigoBarras;
        this.totalEtiquetas = cantidad;

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(this, obtenerFormatoEtiqueta());

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
                return true;
            } catch (PrinterException ex) { return false; }
        }
        return false; 
    }

    private PageFormat obtenerFormatoEtiqueta() {
        PageFormat format = new PageFormat();
        Paper paper = new Paper();
        double width = 150; 
        double height = 90; 
        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height); 
        format.setPaper(paper);
        return format;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex >= totalEtiquetas) return NO_SUCH_PAGE; 

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.setColor(Color.BLACK);
        String nombreCorto = nombreProducto.length() > 20 ? nombreProducto.substring(0, 20) + "..." : nombreProducto;
        
        int stringWidth = g2d.getFontMetrics().stringWidth(nombreCorto);
        int xCentered = (int) ((pageFormat.getImageableWidth() - stringWidth) / 2);
        g2d.drawString(nombreCorto, xCentered, 15);

        try {
            Barcode128 barcode = new Barcode128();
            barcode.setCode(codigoBarras);
            barcode.setBarHeight(30f);
            barcode.setSize(8f);
            
            java.awt.Image awtImage = barcode.createAwtImage(Color.BLACK, Color.WHITE);
            int xBar = (int) ((pageFormat.getImageableWidth() - awtImage.getWidth(null)) / 2);
            g2d.drawImage(awtImage, xBar, 25, null);
            
        } catch (Exception e) { g2d.drawString("Error en código", 10, 40); }

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
        String contador = "Etiqueta " + (pageIndex + 1) + " de " + totalEtiquetas;
        int countWidth = g2d.getFontMetrics().stringWidth(contador);
        int xCount = (int) ((pageFormat.getImageableWidth() - countWidth) / 2);
        g2d.drawString(contador, xCount, 80);

        return PAGE_EXISTS;
    }

    // =========================================================
    // 1. GENERADOR DE STICKER DEL TÉCNICO (OPTIMIZADO)
    // =========================================================
    public boolean imprimirTicketTecnicoDirecto(String idOrden, String cliente, String equipo, String problema, boolean esCelular, String tecnico, String clave) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (!pj.printDialog()) return false;

        PageFormat pf = pj.defaultPage();
        Paper paper = new Paper();
        double width = 160; 
        // Redujimos el alto para ahorrar papel
        double height = esCelular ? 170 : 120; 
        
        paper.setSize(width, height);
        paper.setImageableArea(2, 2, width - 4, height - 4);
        pf.setPaper(paper);

        pj.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setColor(Color.BLACK);

                int y = 12; // Empezamos más arriba
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2d.drawString("ORDEN: " + idOrden, 5, y); 
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g2d.drawString("CLI: " + (cliente.length() > 15 ? cliente.substring(0, 15) : cliente), 75, y); y += 12;
                
                g2d.drawString("EQ: " + equipo, 5, y); y += 12;
                g2d.drawString("TEC: " + tecnico, 5, y); y += 12;
                
                g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2d.drawString("SEGURIDAD:", 5, y); y += 10;
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 9));
                
                if (clave.toLowerCase().contains("patr") || clave.equalsIgnoreCase("p")) {
                    g2d.drawString("O   O   O", 20, y); y += 10;
                    g2d.drawString("O   O   O", 20, y); y += 10;
                    g2d.drawString("O   O   O", 20, y); y += 12;
                } else {
                    g2d.drawString(clave, 20, y); y += 12;
                }
                
                String problemaCorto = problema.length() > 35 ? problema.substring(0, 35) + "..." : problema;
                g2d.drawString("F: " + problemaCorto, 5, y); y += 15;

                if (esCelular) {
                    try {
                        Barcode128 barcode = new Barcode128();
                        barcode.setCode(idOrden);
                        barcode.setBarHeight(20f); // Código de barras un poco más bajo 
                        java.awt.Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
                        g2d.drawImage(img, 15, y, 120, 20, null); 
                        y += 28;
                    } catch (Exception e) {}
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                    g2d.drawString("NOTAS:___________________", 5, y);
                } else {
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                    g2d.drawString("TRABAJO/REPUESTOS:", 5, y); y += 12;
                    g2d.drawString("________________________", 5, y);
                }

                return PAGE_EXISTS;
            }
        }, pf);

        try { pj.print(); return true; } catch (PrinterException e) { return false; }
    }
    
    // =========================================================
    // 2. GENERADOR DE RECIBOS TÉRMICOS DE VENTA (OPTIMIZADO)
    // =========================================================
    public boolean imprimirReciboVenta(int idVenta) {
        String fechaVenta = ""; String cajero = ""; double total = 0.0; String metodoPago = "";
        int idOrdenVinculada = 0;
        List<String[]> detalles = new ArrayList<>();

        String sqlVenta = "SELECT v.fecha_venta, v.total, v.metodo_pago, u.usuario, v.id_orden FROM ventas v JOIN usuarios u ON v.id_usuario = u.id_usuario WHERE v.id_venta = ?";
        String sqlDetalles = "SELECT cantidad, descripcion, precio_unitario, subtotal FROM detalles_venta WHERE id_venta = ?";

        try (Connection con = new factory.ConexionFactory().getConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlVenta)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fechaVenta = rs.getString("fecha_venta");
                        total = rs.getDouble("total");
                        metodoPago = rs.getString("metodo_pago");
                        cajero = rs.getString("usuario");
                        idOrdenVinculada = rs.getInt("id_orden"); 
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlDetalles)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detalles.add(new String[]{ rs.getString("cantidad"), rs.getString("descripcion"), rs.getString("precio_unitario"), rs.getString("subtotal") });
                    }
                }
            }
        } catch (Exception e) { return false; }

        String repEquipo = ""; String repFalla = ""; String repTrabajo = ""; String repCliente = "";
        if (idOrdenVinculada > 0) {
            String sqlRep = "SELECT e.modelo, o.falla_reportada, o.trabajo_realizado, CONCAT(c.nombre, ' ', c.apellido) as cliente FROM ordenes_reparacion o JOIN equipos_registrados e ON o.id_equipo = e.id_equipo JOIN clientes c ON e.id_cliente = c.id_cliente WHERE o.id_orden = ?";
            try (Connection con = new factory.ConexionFactory().getConexion(); PreparedStatement ps = con.prepareStatement(sqlRep)) {
                ps.setInt(1, idOrdenVinculada);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        repEquipo = rs.getString("modelo");
                        repFalla = rs.getString("falla_reportada");
                        repTrabajo = rs.getString("trabajo_realizado");
                        repCliente = rs.getString("cliente");
                        if(repTrabajo == null || repTrabajo.trim().isEmpty()) repTrabajo = "Revisión técnica general.";
                    }
                }
            } catch (Exception e) {}
        }

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat format = new PageFormat();
        Paper paper = new Paper();

        double width = 200; 
        // --- REDUCCIÓN DRÁSTICA DEL LARGO DEL PAPEL ---
        double height = idOrdenVinculada > 0 ? 380 + (detalles.size() * 12) : 230 + (detalles.size() * 12); 

        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        format.setPaper(paper);

        final String fFecha = fechaVenta; final String fCajero = cajero; final double fTotal = total; final String fMetodo = metodoPago;
        final int fIdOrden = idOrdenVinculada; final String fRepEq = repEquipo; final String fRepFalla = repFalla; final String fRepTrab = repTrabajo; final String fRepCli = repCliente;

        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setColor(Color.BLACK);

                int y = 12; 
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12)); // Encabezado un poco más pequeño
                centrarTexto(g2d, "SAIRTECH", width, y); y += 12;
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                centrarTexto(g2d, "Reparación y Venta de Accesorios", width, y); y += 9;
                centrarTexto(g2d, "Santa Bárbara, Honduras", width, y); y += 15;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
                centrarTexto(g2d, fIdOrden > 0 ? "COMPROBANTE DE ENTREGA" : "NOTA DE VENTA / RECIBO", width, y); y += 12;
                
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 7));
                g2d.drawString("Ticket No: " + idVenta + "  |  Fecha: " + fFecha, 5, y); y += 10;
                
                // --- SECCIÓN EXCLUSIVA PARA REPARACIONES ---
                if (fIdOrden > 0) {
                    g2d.drawString("-----------------------------------------", 5, y); y += 10;
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                    g2d.drawString("ORDEN DE SERVICIO #" + fIdOrden, 5, y); y += 10;
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                    g2d.drawString("CLIENTE: " + fRepCli, 5, y); y += 9;
                    g2d.drawString("EQUIPO: " + fRepEq, 5, y); y += 12;
                    
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 7));
                    g2d.drawString("SÍNTOMAS / FALLA:", 5, y); y += 9;
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                    y = dibujarTextoMultilinea(g2d, fRepFalla, 5, y, (int)width - 10); y += 3;

                    g2d.setFont(new Font("SansSerif", Font.BOLD, 7));
                    g2d.drawString("TRABAJO REALIZADO:", 5, y); y += 9;
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                    y = dibujarTextoMultilinea(g2d, fRepTrab, 5, y, (int)width - 10); y += 3;
                    
                    g2d.drawString("ENTREGADO POR: " + fCajero, 5, y); y += 10;
                } else {
                    g2d.drawString("Cajero: " + fCajero, 5, y); y += 10;
                }

                g2d.setFont(new Font("Monospaced", Font.PLAIN, 7));
                g2d.drawString("-----------------------------------------", 5, y); y += 9;
                g2d.drawString("CANT DESCRIPCION             SUBTOTAL", 5, y); y += 9;
                g2d.drawString("-----------------------------------------", 5, y); y += 12;

                for (String[] det : detalles) {
                    String cant = det[0]; String desc = det[1]; String sub = det[3];
                    if (desc.length() > 22) desc = desc.substring(0, 22) + "..";
                    g2d.drawString(cant + "x", 5, y); g2d.drawString(desc, 25, y); 
                    int subWidth = g2d.getFontMetrics().stringWidth(sub);
                    g2d.drawString(sub, (int)width - subWidth - 5, y); y += 9; // Interlineado de productos más corto
                }

                y += 3; g2d.drawString("-----------------------------------------", 5, y); y += 12;
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10)); // Total destacado pero no gigante
                g2d.drawString("TOTAL A PAGAR:", 5, y);
                String totalStr = String.format("L. %.2f", fTotal);
                int totWidth = g2d.getFontMetrics().stringWidth(totalStr);
                g2d.drawString(totalStr, (int)width - totWidth - 5, y); y += 15;

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                g2d.drawString("Método de Pago: " + fMetodo, 5, y); y += 12;
                g2d.drawString("-----------------------------------------", 5, y); y += 12;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 7));
                centrarTexto(g2d, "PÓLIZA DE GARANTÍA", width, y); y += 10;
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 6)); // Letra chica para términos legales
                g2d.drawString("1. Válida solo por defectos de fábrica.", 5, y); y += 8;
                g2d.drawString("2. Se anula por humedad, golpes o", 5, y); y += 8;
                g2d.drawString("   uso de cargadores genéricos.", 5, y); y += 8;
                g2d.drawString("3. Indispensable presentar este ticket.", 5, y); y += 15;

                centrarTexto(g2d, "¡Gracias por su preferencia!", width, y); y += 9;
                centrarTexto(g2d, "Revise sus productos. No hay cambios.", width, y);

                return PAGE_EXISTS;
            }
        }, format);

        if (printerJob.printDialog()) {
            try { printerJob.print(); return true; } catch (PrinterException ex) { return false; }
        }
        return false;
    }

    private void centrarTexto(Graphics2D g2d, String texto, double width, int y) {
        int stringWidth = g2d.getFontMetrics().stringWidth(texto);
        g2d.drawString(texto, (int) ((width - stringWidth) / 2), y);
    }
    
    // Función auxiliar para texto multilínea
    private int dibujarTextoMultilinea(Graphics2D g2d, String texto, int x, int y, int maxW) {
        if (texto == null) return y;
        String[] words = texto.split(" ");
        String line = "";
        for (String word : words) {
            if (g2d.getFontMetrics().stringWidth(line + word) < maxW) {
                line += word + " ";
            } else {
                g2d.drawString(line, x, y);
                y += 8; // Salto de renglón más corto
                line = word + " ";
            }
        }
        g2d.drawString(line, x, y);
        return y + 8;
    }

    public boolean imprimirEtiquetaKnijicoDirecta(String modelo, String codigo, String lote, String caja) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        if (printServices.length == 0) return false;
        String[] nombresImpresoras = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) nombresImpresoras[i] = printServices[i].getName();

        JComboBox<String> cmbImpresoras = new JComboBox<>(nombresImpresoras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultService != null) cmbImpresoras.setSelectedItem(defaultService.getName());

        JSpinner spinCopias = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); 
        JPanel panelDialogo = new JPanel(new GridLayout(2, 2, 10, 10));
        panelDialogo.add(new JLabel("Seleccionar Impresora:")); panelDialogo.add(cmbImpresoras);
        panelDialogo.add(new JLabel("Cantidad de Copias:")); panelDialogo.add(spinCopias);

        if (JOptionPane.showConfirmDialog(null, panelDialogo, "Impresión Knijico", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return false; 

        PrintService impresoraSeleccionada = null;
        for (PrintService ps : printServices) { if (ps.getName().equals(cmbImpresoras.getSelectedItem())) { impresoraSeleccionada = ps; break; } }

        PrinterJob pj = PrinterJob.getPrinterJob();
        try {
            if (impresoraSeleccionada != null) pj.setPrintService(impresoraSeleccionada); 
            pj.setCopies((Integer) spinCopias.getValue()); 
        } catch (PrinterException e) { return false; }
        
        PageFormat pf = pj.defaultPage(); Paper paper = new Paper();
        double width = 160; double height = 90; 
        paper.setSize(width, height); paper.setImageableArea(2, 2, width - 4, height - 4); pf.setPaper(paper);

        pj.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY()); g2d.setColor(Color.BLACK);
                double anchoReal = pageFormat.getImageableWidth();

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7)); FontMetrics fm = g2d.getFontMetrics();
                String titulo = "SAIRTECH - KNIJICO"; g2d.drawString(titulo, (int) ((anchoReal - fm.stringWidth(titulo)) / 2), 10);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 8)); fm = g2d.getFontMetrics();
                int xMod = (int) ((anchoReal - fm.stringWidth(modelo)) / 2); g2d.drawString(modelo, xMod < 0 ? 0 : xMod, 22);
                try {
                    Barcode128 barcode = new Barcode128(); barcode.setCode(codigo); barcode.setBarHeight(25f); 
                    java.awt.Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
                    g2d.drawImage(img, (int) ((anchoReal - 130) / 2), 28, 130, 35, null);
                } catch (Exception e) {}
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7)); fm = g2d.getFontMetrics();
                String ubi = lote + " - CAJA: " + caja; g2d.drawString(ubi, (int) ((anchoReal - fm.stringWidth(ubi)) / 2), 75);
                return PAGE_EXISTS;
            }
        }, pf);
        try { pj.print(); return true; } catch (PrinterException e) { return false; }
    }
}