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
    // 1. GENERADOR DE STICKER DEL TÉCNICO (REEMPLAZA AL PDF)
    // =========================================================
    public boolean imprimirTicketTecnicoDirecto(String idOrden, String cliente, String equipo, String problema, boolean esCelular, String tecnico, String clave) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (!pj.printDialog()) return false; // AQUÍ TE DEJA ELEGIR LA IMPRESORA

        PageFormat pf = pj.defaultPage();
        Paper paper = new Paper();
        double width = 160; // Ancho para sticker de 58mm aprox
        
        // --- CORRECCIÓN: AUMENTAMOS EL ALTO DEL PAPEL ---
        // Antes era 160, lo subimos a 190 para que el código de barras y las notas quepan perfectos sin cortarse
        double height = esCelular ? 190 : 140; 
        
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

                int y = 15;
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2d.drawString("ORDEN: " + idOrden, 5, y); 
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g2d.drawString("CLI: " + (cliente.length() > 15 ? cliente.substring(0, 15) : cliente), 80, y); y += 15;
                
                g2d.drawString("EQ: " + equipo, 5, y); y += 15;
                g2d.drawString("TEC: " + tecnico, 5, y); y += 15;
                
                g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2d.drawString("SEGURIDAD:", 5, y); y += 12;
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 9));
                
                if (clave.toLowerCase().contains("patr") || clave.equalsIgnoreCase("p")) {
                    g2d.drawString("O    O    O", 20, y); y += 12;
                    g2d.drawString("O    O    O", 20, y); y += 12;
                    g2d.drawString("O    O    O", 20, y); y += 15;
                } else {
                    g2d.drawString(clave, 20, y); y += 15;
                }
                
                // Cortar texto de falla si es absurdamente largo para que no estropee el diseño
                String problemaCorto = problema.length() > 30 ? problema.substring(0, 30) + "..." : problema;
                g2d.drawString("F: " + problemaCorto, 5, y); y += 15;

                if (esCelular) {
                    try {
                        Barcode128 barcode = new Barcode128();
                        barcode.setCode(idOrden);
                        barcode.setBarHeight(25f); 
                        java.awt.Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
                        g2d.drawImage(img, 15, y, 120, 25, null); // IMPRIME CÓDIGO DE BARRAS
                        y += 35;
                    } catch (Exception e) {}
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                    g2d.drawString("NOTAS:___________________", 5, y);
                } else {
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                    g2d.drawString("TRABAJO/REPUESTOS:", 5, y); y += 15;
                    g2d.drawString("________________________", 5, y);
                }

                return PAGE_EXISTS;
            }
        }, pf);

        try { pj.print(); return true; } catch (PrinterException e) { return false; }
    }
    
    // =========================================================
    // 2. GENERADOR DE RECIBOS TÉRMICOS DE VENTA (AHORA DETECTA ÓRDENES)
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
                        idOrdenVinculada = rs.getInt("id_orden"); // DETECTAMOS SI ES REPARACIÓN
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

        // --- BUSCAR DATOS DE LA REPARACIÓN SI EXISTE ---
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
        // Si es reparación, el ticket debe ser mucho más largo para que quepa toda la información
        double height = idOrdenVinculada > 0 ? 550 + (detalles.size() * 25) : 340 + (detalles.size() * 25); 

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

                int y = 15; 
                g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                centrarTexto(g2d, "SAIRTECH", width, y); y += 15;
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                centrarTexto(g2d, "Reparación y Venta de Accesorios", width, y); y += 12;
                centrarTexto(g2d, "Santa Bárbara, Honduras", width, y); y += 20;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                // CAMBIAR TÍTULO SI ES REPARACIÓN
                centrarTexto(g2d, fIdOrden > 0 ? "COMPROBANTE DE ENTREGA" : "NOTA DE VENTA / RECIBO", width, y); y += 15;
                
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 8));
                g2d.drawString("Ticket No: " + idVenta, 10, y); y += 12;
                g2d.drawString("Fecha: " + fFecha, 10, y); y += 12;
                
                // --- SECCIÓN EXCLUSIVA PARA REPARACIONES ---
                if (fIdOrden > 0) {
                    g2d.drawString("------------------------------------------", 10, y); y += 12;
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
                    g2d.drawString("ORDEN DE SERVICIO #" + fIdOrden, 10, y); y += 15;
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                    g2d.drawString("CLIENTE: " + fRepCli, 10, y); y += 12;
                    g2d.drawString("EQUIPO: " + fRepEq, 10, y); y += 15;
                    
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                    g2d.drawString("SÍNTOMAS / FALLA:", 10, y); y += 12;
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                    y = dibujarTextoMultilinea(g2d, fRepFalla, 10, y, (int)width - 20); y += 5;

                    g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                    g2d.drawString("TRABAJO REALIZADO:", 10, y); y += 12;
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                    y = dibujarTextoMultilinea(g2d, fRepTrab, 10, y, (int)width - 20); y += 5;
                    
                    g2d.drawString("ENTREGADO POR: " + fCajero, 10, y); y += 12;
                } else {
                    g2d.drawString("Cajero: " + fCajero, 10, y); y += 12;
                }

                g2d.setFont(new Font("Monospaced", Font.PLAIN, 8));
                g2d.drawString("------------------------------------------", 10, y); y += 12;
                g2d.drawString("CANT DESCRIPCION           SUBTOTAL", 10, y); y += 12;
                g2d.drawString("------------------------------------------", 10, y); y += 15;

                for (String[] det : detalles) {
                    String cant = det[0]; String desc = det[1]; String sub = det[3];
                    if (desc.length() > 20) desc = desc.substring(0, 20) + "..";
                    g2d.drawString(cant + "x", 10, y); g2d.drawString(desc, 35, y); 
                    int subWidth = g2d.getFontMetrics().stringWidth(sub);
                    g2d.drawString(sub, (int)width - subWidth - 10, y); y += 12;
                }

                y += 5; g2d.drawString("------------------------------------------", 10, y); y += 20;
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2d.drawString("TOTAL A PAGAR:", 10, y);
                String totalStr = String.format("L. %.2f", fTotal);
                int totWidth = g2d.getFontMetrics().stringWidth(totalStr);
                g2d.drawString(totalStr, (int)width - totWidth - 10, y); y += 20;

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g2d.drawString("Método de Pago: " + fMetodo, 10, y); y += 20;
                g2d.drawString("------------------------------------------", 10, y); y += 15;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                centrarTexto(g2d, "PÓLIZA DE GARANTÍA", width, y); y += 15;
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                g2d.drawString("1. Válida solo por defectos de fábrica.", 10, y); y += 10;
                g2d.drawString("2. Se anula por humedad, golpes o", 10, y); y += 10;
                g2d.drawString("   uso de cargadores genéricos.", 10, y); y += 10;
                g2d.drawString("3. Sellos intactos obligatorios.", 10, y); y += 10;
                g2d.drawString("4. Indispensable presentar este ticket.", 10, y); y += 20;

                centrarTexto(g2d, "¡Gracias por su preferencia!", width, y); y += 12;
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
    
    // Función auxiliar para que los textos largos en el ticket térmico no se salgan del papel
    private int dibujarTextoMultilinea(Graphics2D g2d, String texto, int x, int y, int maxW) {
        if (texto == null) return y;
        String[] words = texto.split(" ");
        String line = "";
        for (String word : words) {
            if (g2d.getFontMetrics().stringWidth(line + word) < maxW) {
                line += word + " ";
            } else {
                g2d.drawString(line, x, y);
                y += 10;
                line = word + " ";
            }
        }
        g2d.drawString(line, x, y);
        return y + 10;
    }

    public boolean imprimirEtiquetaKnijicoDirecta(String modelo, String codigo, String lote, String caja) {
        // ... (Tu código intacto de Knijico)
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