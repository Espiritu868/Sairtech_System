package utilidades;

import com.itextpdf.text.pdf.Barcode128;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.TrayIcon.MessageType; // <-- IMPORTACIÓN PARA NOTIFICACIONES DE WINDOWS

public class ImpresoraDirecta implements Printable {

    private String nombreProducto;
    private String codigoBarras;
    private int totalEtiquetas;

    // =========================================================
    // LÓGICA INTELIGENTE MEJORADA: RESPETA EL LOGO Y EL CÓDIGO
    // =========================================================
    private void dibujarNombreCentradoAjustable(Graphics2D g2d, String texto, double anchoReal) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 7)); // Fuente original
        FontMetrics fm = g2d.getFontMetrics();
        double maxWidth = anchoReal - 10; // Margen de 5px a los lados
        
        List<String> lineas = new ArrayList<>();
        String[] palabras = texto.split(" ");
        String lineaActual = "";
        
        for (String palabra : palabras) {
            String prueba = lineaActual.isEmpty() ? palabra : lineaActual + " " + palabra;
            if (fm.stringWidth(prueba) < maxWidth) {
                lineaActual = prueba;
            } else {
                if (!lineaActual.isEmpty()) lineas.add(lineaActual);
                lineaActual = palabra;
            }
        }
        if (!lineaActual.isEmpty()) lineas.add(lineaActual);
        
        if (lineas.isEmpty()) return;
        
        if (lineas.size() == 1) {
            // CABE EN 1 LÍNEA: La dejamos exactamente en medio, como siempre (y=34)
            int x = (int)((anchoReal - fm.stringWidth(lineas.get(0))) / 2);
            g2d.drawString(lineas.get(0), x, 34); 
            
        } else {
            // NO CABE: Usamos 2 líneas, pero sin tocar el logo (y=24) ni el código (y=37)
            
            // LÍNEA 1: Arriba, bajamos un poco la y para alejarnos del logo
            String l1 = lineas.get(0);
            int x1 = (int)((anchoReal - fm.stringWidth(l1)) / 2);
            g2d.drawString(l1, x1, 28); // Antes estaba en 26, lo bajamos a 28 para esquivar el logo

            // LÍNEA 2: Abajo, pero si el texto es muy largo, reducimos la letra
            String l2 = lineas.get(1);
            
            // Si hay una 3ra línea perdida por ahí, la ignoramos y le clavamos el "..." a la 2da
            if (lineas.size() > 2) {
                while(g2d.getFontMetrics().stringWidth(l2 + "...") > maxWidth && l2.length() > 0) {
                    l2 = l2.substring(0, l2.length() - 1);
                }
                l2 = l2 + "...";
            }
            
            // MAGIA DE TAMAÑO: Hacemos la segunda línea un poquito más pequeña (tamaño 6) 
            // para que no choque con el código de barras que empieza en y=37
            g2d.setFont(new Font("SansSerif", Font.BOLD, 6)); 
            FontMetrics fmPeque = g2d.getFontMetrics();
            int x2 = (int)((anchoReal - fmPeque.stringWidth(l2)) / 2);
            g2d.drawString(l2, x2, 35); // Justo un pelito arriba del código de barras
            
            // Restauramos la fuente original por si los métodos de abajo la necesitan
            g2d.setFont(new Font("SansSerif", Font.BOLD, 7)); 
        }
    }

    // =========================================================
    // 1. ETIQUETA INVENTARIO GENERAL (GEMELA DE KNIJICO - 5x3 cm)
    // =========================================================
    public boolean imprimirEtiquetasDirecto(String nombreProducto, String codigoBarras, String ubicacion) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        if (printServices.length == 0) {
            utilidades.NotificadorWindows.mostrarAlerta("Error de Impresora", "No se detectó ninguna impresora en Windows.", MessageType.ERROR);
            return false;
        }
        
        String[] nombresImpresoras = new String[printServices.length];
        for (int i = 0; i < printServices.length; i++) nombresImpresoras[i] = printServices[i].getName();

        JComboBox<String> cmbImpresoras = new JComboBox<>(nombresImpresoras);
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultService != null) cmbImpresoras.setSelectedItem(defaultService.getName());

        JSpinner spinCopias = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); 
        JPanel panelDialogo = new JPanel(new GridLayout(2, 2, 10, 10));
        panelDialogo.add(new JLabel("Seleccionar Impresora:")); panelDialogo.add(cmbImpresoras);
        panelDialogo.add(new JLabel("Cantidad de Copias:")); panelDialogo.add(spinCopias);

        if (JOptionPane.showConfirmDialog(null, panelDialogo, "Imprimir Etiqueta - Inventario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return false; 

        PrintService impresoraSeleccionada = null;
        for (PrintService ps : printServices) { if (ps.getName().equals(cmbImpresoras.getSelectedItem())) { impresoraSeleccionada = ps; break; } }

        PrinterJob pj = PrinterJob.getPrinterJob();
        try {
            if (impresoraSeleccionada != null) pj.setPrintService(impresoraSeleccionada); 
            pj.setCopies((Integer) spinCopias.getValue()); 
        } catch (PrinterException e) { return false; }
        
        PageFormat pf = pj.defaultPage(); Paper paper = new Paper();
        
        // --- TAMAÑO: 5x3 cm EXACTOS ---
        double width = 142; 
        double height = 85; 
        
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
                double anchoReal = pageFormat.getImageableWidth();

                // 1. LOGO 
                try {
                    java.net.URL logoUrl = getClass().getResource("/image/logo_bk.png");
                    if (logoUrl != null) {
                        java.awt.Image logo = javax.imageio.ImageIO.read(logoUrl);
                        g2d.drawImage(logo, 5, 2, 22, 22, null);
                    } else {
                        java.io.File fileLocal = new java.io.File("C:\\SairTech_System\\src\\image\\logo_bk.png");
                        java.io.File fileRed = new java.io.File("\\\\192.168.0.131\\SairTech_System\\src\\image\\logo_bk.png");
                        if (fileLocal.exists()) g2d.drawImage(javax.imageio.ImageIO.read(fileLocal), 5, 2, 22, 22, null);
                        else if (fileRed.exists()) g2d.drawImage(javax.imageio.ImageIO.read(fileRed), 5, 2, 22, 22, null);
                    }
                } catch (Exception e) {}

                // 2. CÓDIGO (Arriba a la derecha)
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 9)); 
                int codeWidthTop = g2d.getFontMetrics().stringWidth(codigoBarras);
                g2d.drawString(codigoBarras, (int)(anchoReal - codeWidthTop - 5), 18);

                // 3. MODELO / DESCRIPCIÓN (MAGIA DE AUTO-AJUSTE)
                dibujarNombreCentradoAjustable(g2d, nombreProducto, anchoReal);

                // 4. CÓDIGO DE BARRAS (Centro - Intocable)
                try {
                    com.itextpdf.text.pdf.Barcode128 barcode = new com.itextpdf.text.pdf.Barcode128(); 
                    barcode.setCode(codigoBarras); 
                    barcode.setBarHeight(18f); 
                    java.awt.Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
                    g2d.drawImage(img, (int) ((anchoReal - 90) / 2), 37, 90, 15, null);
                } catch (Exception e) {}
                
                // 5. CÓDIGO NUMÉRICO
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
                int codeWidthBot = g2d.getFontMetrics().stringWidth(codigoBarras);
                g2d.drawString(codigoBarras, (int)((anchoReal - codeWidthBot) / 2), 60);

                // 6. NOTA INVENTARIO
                g2d.setFont(new Font("SansSerif", Font.BOLD, 6));
                String nota = "** REPUESTO / ACCESORIO **";
                g2d.drawString(nota, (int)((anchoReal - g2d.getFontMetrics().stringWidth(nota)) / 2), 69);
                
                // 7. UBICACIÓN FÍSICA
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
                String ubi = "UBICACIÓN: " + (ubicacion.isEmpty() ? "NO ASIGNADA" : ubicacion.toUpperCase());
                g2d.drawString(ubi, (int)((anchoReal - g2d.getFontMetrics().stringWidth(ubi)) / 2), 78);

                return PAGE_EXISTS;
            }
        }, pf);
        try { pj.print(); return true; } catch (PrinterException e) { return false; }
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex >= totalEtiquetas) return NO_SUCH_PAGE;
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
        g2d.drawString(nombreProducto, 5, 20);
        return PAGE_EXISTS;
    }

    // =========================================================
    // 2. ETIQUETAS DE INVENTARIO MULTIPLES
    // =========================================================
    public boolean imprimirEtiquetasDirecto(String nombreProducto, String codigoBarras, int cantidad, String ubicacion) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat format = new PageFormat();
        Paper paper = new Paper();
        
        double width = 142;  
        double height = 85;  
        
        paper.setSize(width, height);
        paper.setImageableArea(2, 2, width - 4, height - 4); 
        format.setPaper(paper);

        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex >= cantidad) return NO_SUCH_PAGE; 

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setColor(Color.BLACK);
                double anchoReal = pageFormat.getImageableWidth();

                try {
                    Image logo = null;
                    File fileLocal = new File("C:\\SairTech_System\\src\\image\\logo_bk.png");
                    File fileRed = new File("\\\\192.168.0.131\\SairTech_System\\src\\image\\logo_bk.png");
                    if (fileLocal.exists()) logo = ImageIO.read(fileLocal);
                    else if (fileRed.exists()) logo = ImageIO.read(fileRed);
                    if (logo != null) g2d.drawImage(logo, 5, 2, 22, 22, null);
                } catch (Exception e) {}

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8)); 
                int codeWidthTop = g2d.getFontMetrics().stringWidth(codigoBarras);
                g2d.drawString(codigoBarras, (int)(anchoReal - codeWidthTop - 5), 18);

                // MAGIA DE AUTO-AJUSTE
                dibujarNombreCentradoAjustable(g2d, nombreProducto, anchoReal);

                try {
                    Barcode128 barcode = new Barcode128(); 
                    barcode.setCode(codigoBarras); 
                    barcode.setBarHeight(18f); 
                    Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
                    g2d.drawImage(img, (int) ((anchoReal - 90) / 2), 37, 90, 15, null);
                } catch (Exception e) {}
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
                int codeWidthBot = g2d.getFontMetrics().stringWidth(codigoBarras);
                g2d.drawString(codigoBarras, (int)((anchoReal - codeWidthBot) / 2), 60);

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 5));
                String nota = "Etiqueta " + (pageIndex + 1) + " de " + cantidad;
                g2d.drawString(nota, (int)((anchoReal - g2d.getFontMetrics().stringWidth(nota)) / 2), 69);
                
                g2d.setFont(new Font("SansSerif", Font.BOLD, 6));
                String ubi = "UBICACIÓN: " + (ubicacion.isEmpty() ? "NO ASIGNADA" : ubicacion.toUpperCase());
                g2d.drawString(ubi, (int)((anchoReal - g2d.getFontMetrics().stringWidth(ubi)) / 2), 78);

                return PAGE_EXISTS;
            }
        }, format);

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
                return true;
            } catch (PrinterException ex) { return false; }
        }
        return false; 
    }
    
    // =========================================================
    // 3. ETIQUETAS KNIJICO
    // =========================================================
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
        
        double width = 142; 
        double height = 85; 
        
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
                double anchoReal = pageFormat.getImageableWidth();

                try {
                    Image logo = null;
                    File fileLocal = new File("C:\\SairTech_System\\src\\image\\logo_bk.png");
                    File fileRed = new File("\\\\192.168.0.131\\SairTech_System\\src\\image\\logo_bk.png");
                    if (fileLocal.exists()) logo = ImageIO.read(fileLocal);
                    else if (fileRed.exists()) logo = ImageIO.read(fileRed);
                    if (logo != null) g2d.drawImage(logo, 5, 2, 22, 22, null);
                } catch (Exception e) {}

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 9)); 
                int codeWidthTop = g2d.getFontMetrics().stringWidth(codigo);
                g2d.drawString(codigo, (int)(anchoReal - codeWidthTop - 5), 18);

                // MAGIA DE AUTO-AJUSTE PARA KNIJICO
                dibujarNombreCentradoAjustable(g2d, modelo, anchoReal);

                try {
                    Barcode128 barcode = new Barcode128(); 
                    barcode.setCode(codigo); 
                    barcode.setBarHeight(18f); 
                    Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
                    g2d.drawImage(img, (int) ((anchoReal - 90) / 2), 37, 90, 15, null);
                } catch (Exception e) {}
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
                int codeWidthBot = g2d.getFontMetrics().stringWidth(codigo);
                g2d.drawString(codigo, (int)((anchoReal - codeWidthBot) / 2), 60);

                g2d.setFont(new Font("SansSerif", Font.BOLD, 6));
                String nota = "**Prueba por favor antes de instalar**";
                g2d.drawString(nota, (int)((anchoReal - g2d.getFontMetrics().stringWidth(nota)) / 2), 69);
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
                String ubi = "LOTE: " + lote + " | CAJA: " + caja;
                g2d.drawString(ubi, (int)((anchoReal - g2d.getFontMetrics().stringWidth(ubi)) / 2), 78);

                return PAGE_EXISTS;
            }
        }, pf);
        try { pj.print(); return true; } catch (PrinterException e) { return false; }
    }
    
    // =========================================================
    // 4. GENERADOR DE STICKER DEL TÉCNICO
    // =========================================================
    public boolean imprimirTicketTecnicoDirecto(String idOrden, String cliente, String equipo, String problema, boolean esCelular, String tecnico, String clave) {
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (!pj.printDialog()) return false;

        PageFormat pf = pj.defaultPage();
        Paper paper = new Paper();
        
        double width = 142;  
        double height = 85;  
        
        paper.setSize(width, height);
        paper.setImageableArea(1, 1, width - 2, height - 2);
        pf.setPaper(paper);

        pj.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE;

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setColor(Color.BLACK);

                int y = 10; 
                
                g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2d.drawString("ORDEN: " + idOrden, 5, y); 
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                g2d.drawString("TEC: " + (tecnico.length() > 10 ? tecnico.substring(0,10) : tecnico), 90, y); 
                y += 10;
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                String cliLimpio = cliente.length() > 28 ? cliente.substring(0, 28) : cliente;
                g2d.drawString("CLI: " + cliLimpio, 5, y); 
                y += 10;
                
                g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                String eqLimpio = equipo.length() > 14 ? equipo.substring(0,14) : equipo; 
                g2d.drawString("EQ: " + eqLimpio, 5, y); 
                
                g2d.setFont(new Font("SansSerif", Font.BOLD, 8));
                g2d.drawString("CLAVE:", 70, y); 
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                if (clave.toLowerCase().contains("patr") || clave.equalsIgnoreCase("p")) {
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
                    g2d.drawString("O O O", 100, y-5); 
                    g2d.drawString("O O O", 100, y); 
                    g2d.drawString("O O O", 100, y+5); 
                } else {
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                    g2d.drawString(clave.length() > 10 ? clave.substring(0,10) : clave, 100, y); 
                }
                y += 10;
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                String fallaLimpia = problema.replace("\n", " ");
                if (fallaLimpia.length() > 34) {
                    g2d.drawString("F: " + fallaLimpia.substring(0, 34), 5, y); y += 9;
                    g2d.drawString("   " + (fallaLimpia.length() > 68 ? fallaLimpia.substring(34, 68) + "..." : fallaLimpia.substring(34)), 5, y); y += 6;
                } else {
                    g2d.drawString("F: " + fallaLimpia, 5, y); y += 15;
                }
                
                if (esCelular) {
                    try {
                        Barcode128 barcode = new Barcode128();
                        barcode.setCode(idOrden);
                        barcode.setBarHeight(18f); 
                        Image img = barcode.createAwtImage(Color.BLACK, Color.WHITE);
                        g2d.drawImage(img, 21, y, 100, 22, null); 
                    } catch (Exception e) {}
                } else {
                    g2d.drawString("REPUESTOS: _________________", 5, y + 5);
                }

                return PAGE_EXISTS;
            }
        }, pf);

        try { pj.print(); return true; } catch (PrinterException e) { return false; }
    }

    // =========================================================
    // 5. GENERADOR DE RECIBOS TÉRMICOS DE VENTA
    // =========================================================
    public boolean imprimirReciboVenta(int idVenta) {
        String fechaVenta = ""; String cajero = ""; double total = 0.0; String metodoPago = "";
        int idOrdenVinculada = 0;
        List<String[]> detalles = new ArrayList<>();

        String sqlVenta = "SELECT v.fecha_venta, v.total, v.metodo_pago, u.usuario, v.id_orden FROM ventas v JOIN usuarios u ON v.id_usuario = u.id_usuario WHERE v.id_venta = ?";
        String sqlDetalles = "SELECT cantidad, descripcion, precio_unitario, subtotal FROM detalles_venta WHERE id_venta = ?";

        try (java.sql.Connection con = new factory.ConexionFactory().getConexion()) {
            try (java.sql.PreparedStatement ps = con.prepareStatement(sqlVenta)) {
                ps.setInt(1, idVenta);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fechaVenta = rs.getString("fecha_venta");
                        total = rs.getDouble("total");
                        metodoPago = rs.getString("metodo_pago");
                        cajero = rs.getString("usuario");
                        idOrdenVinculada = rs.getInt("id_orden"); 
                    }
                }
            }
            try (java.sql.PreparedStatement ps = con.prepareStatement(sqlDetalles)) {
                ps.setInt(1, idVenta);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detalles.add(new String[]{ rs.getString("cantidad"), rs.getString("descripcion"), rs.getString("precio_unitario"), rs.getString("subtotal") });
                    }
                }
            }
        } catch (Exception e) { return false; }

        String repEquipo = ""; String repFalla = ""; String repTrabajo = ""; String repCliente = "";
        if (idOrdenVinculada > 0) {
            String sqlRep = "SELECT e.modelo, o.falla_reportada, o.trabajo_realizado, CONCAT(c.nombre, ' ', c.apellido) as cliente FROM ordenes_reparacion o JOIN equipos_registrados e ON o.id_equipo = e.id_equipo JOIN clientes c ON e.id_cliente = c.id_cliente WHERE o.id_orden = ?";
            try (java.sql.Connection con = new factory.ConexionFactory().getConexion(); java.sql.PreparedStatement ps = con.prepareStatement(sqlRep)) {
                ps.setInt(1, idOrdenVinculada);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
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

                int y = 8;  
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12)); 
                centrarTexto(g2d, "SAIRTECH", width, y); y += 12;
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                centrarTexto(g2d, "Reparación y Venta de Accesorios", width, y); y += 9;
                centrarTexto(g2d, "Santa Bárbara, Honduras", width, y); y += 15;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 9));
                centrarTexto(g2d, fIdOrden > 0 ? "COMPROBANTE DE ENTREGA" : "NOTA DE VENTA / RECIBO", width, y); y += 12;
                
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 7));
                g2d.drawString("Ticket No: " + idVenta + "  |  Fecha: " + fFecha, 5, y); y += 10;
                
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
                    g2d.drawString(sub, (int)width - subWidth - 5, y); y += 9; 
                }

                y += 3; g2d.drawString("-----------------------------------------", 5, y); y += 12;
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10)); 
                g2d.drawString("TOTAL A PAGAR:", 5, y);
                String totalStr = String.format("L. %.2f", fTotal);
                int totWidth = g2d.getFontMetrics().stringWidth(totalStr);
                g2d.drawString(totalStr, (int)width - totWidth - 5, y); y += 15;

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 7));
                g2d.drawString("Método de Pago: " + fMetodo, 5, y); y += 12;
                g2d.drawString("-----------------------------------------", 5, y); y += 12;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 7));
                centrarTexto(g2d, "PÓLIZA DE GARANTÍA", width, y); y += 10;
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 6)); 
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
    
    private int dibujarTextoMultilinea(Graphics2D g2d, String texto, int x, int y, int maxW) {
        if (texto == null) return y;
        String[] words = texto.split(" ");
        String line = "";
        for (String word : words) {
            if (g2d.getFontMetrics().stringWidth(line + word) < maxW) {
                line += word + " ";
            } else {
                g2d.drawString(line, x, y);
                y += 8; 
                line = word + " ";
            }
        }
        g2d.drawString(line, x, y);
        return y + 8;
    }

    // --- MÉTODO PARA IMPRIMIR PÓLIZA DE GARANTÍA ---
    public void imprimirPolizaGarantia(String ticket, String fecha, String vence, String cliente, String tel, String equipo, String imei, int dias, String cat) {
        try {
            javax.print.PrintService[] printServices = javax.print.PrintServiceLookup.lookupPrintServices(null, null);
            javax.print.PrintService defaultService = javax.print.PrintServiceLookup.lookupDefaultPrintService();
            
            if (printServices.length == 0) {
                utilidades.NotificadorWindows.mostrarAlerta("Error", "No se encontraron impresoras instaladas en el sistema.", MessageType.ERROR);
                return;
            }

            javax.print.attribute.PrintRequestAttributeSet attributes = new javax.print.attribute.HashPrintRequestAttributeSet();

            javax.print.PrintService selectedService = javax.print.ServiceUI.printDialog(
                    null, 200, 200, printServices, defaultService, null, attributes 
            );

            if (selectedService == null) {
                return;
            }

            javax.print.DocPrintJob job = selectedService.createPrintJob();
            
            StringBuilder sb = new StringBuilder();
            sb.append((char)27 + "a" + (char)1); 
            sb.append((char)27 + "!" + (char)32); 
            sb.append("SAIRTECH\n");
            sb.append((char)27 + "!" + (char)1); 
            sb.append("TECNOLOGIA Y REPARACION\n");
            sb.append("Tel: 9988-3561\n");
            sb.append("Trinidad, Santa Barbara\n");
            sb.append("================================\n");
            sb.append((char)27 + "!" + (char)8); 
            sb.append("POLIZA DE GARANTIA\n");
            sb.append((char)27 + "!" + (char)1); 
            sb.append("================================\n\n");
            
            sb.append((char)27 + "a" + (char)0); 
            sb.append("Recibo: #").append(ticket).append("\n");
            sb.append("F. Compra: ").append(fecha).append("\n");
            sb.append("F. Vence:  ").append(vence).append("\n");
            sb.append("--------------------------------\n");
            sb.append("Cliente: ").append(cliente.length() > 22 ? cliente.substring(0, 22) : cliente).append("\n");
            sb.append("Tel: ").append(tel).append("\n");
            sb.append("IMEI: ").append(imei).append("\n");
            sb.append("================================\n\n");
            
            sb.append("EQUIPO / PRODUCTO:\n");
            sb.append(equipo.length() > 32 ? equipo.substring(0, 29) + "..." : equipo).append("\n");
            sb.append("--------------------------------\n\n");
            
            sb.append("CONDICIONES:\n");
            sb.append("1. Todo ").append(cat).append(" de SAIRTECH\n");
            sb.append("tiene garantia de ").append(dias).append(" dias.\n");
            sb.append("2. Cambio solo por defecto de\n");
            sb.append("fabrica y mismo modelo.\n");
            sb.append("3. Accesorios: 7 dias max.\n");
            sb.append("4. Garantia NULA por golpes o\n");
            sb.append("humedad (mojado).\n\n");
            
            sb.append((char)27 + "a" + (char)1); 
            sb.append("\n      __________________\n");
            sb.append("        Firma Cliente\n\n\n\n\n");
            
            sb.append((char)29 + "V" + (char)66 + (char)0); 

            byte[] bytes = sb.toString().getBytes("CP437");
            javax.print.Doc doc = new javax.print.SimpleDoc(bytes, javax.print.DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
            job.print(doc, null);

        } catch (Exception e) {
            utilidades.NotificadorWindows.mostrarAlerta("Error de Impresora", "Fallo de comunicación: " + e.getMessage(), MessageType.ERROR);
        }
    }
    
    // =========================================================
    // VISTA PREVIA EN PANTALLA PROFESIONAL (ESTILIZADA)
    // =========================================================
    public void previsualizarEtiqueta(String nombreProducto, String codigoBarras, String ubicacion) {
        // --- 1. GENERACIÓN DE LA IMAGEN (Igual que antes) ---
        int width = 142; // 5cm
        int height = 85; // 3cm
        int scale = 4;   // Zoom x4

        java.awt.image.BufferedImage bImage = new java.awt.image.BufferedImage(width * scale, height * scale, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bImage.createGraphics();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.scale(scale, scale);

        // Fondo blanco (papel)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        
        double anchoReal = width;

        // DIBUJO DE LA ETIQUETA (Llama a tu método optimizado que no pisa el logo)
        // 1. Logo
        try {
            java.net.URL logoUrl = getClass().getResource("/image/logo_bk.png");
            if (logoUrl != null) g2d.drawImage(javax.imageio.ImageIO.read(logoUrl), 5, 2, 22, 22, null);
        } catch (Exception e) {}
        // 2. Código arriba
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 9)); 
        g2d.drawString(codigoBarras, (int)(anchoReal - g2d.getFontMetrics().stringWidth(codigoBarras) - 5), 18);
        // 3. Nombre (AUTO-AJUSTABLE)
        dibujarNombreCentradoAjustable(g2d, nombreProducto, anchoReal);
        // 4. Barras
        try {
            com.itextpdf.text.pdf.Barcode128 barcode = new com.itextpdf.text.pdf.Barcode128(); 
            barcode.setCode(codigoBarras); barcode.setBarHeight(18f); 
            g2d.drawImage(barcode.createAwtImage(Color.BLACK, Color.WHITE), (int) ((anchoReal - 90) / 2), 37, 90, 15, null);
        } catch (Exception e) {}
        // 5. Números abajo
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
        g2d.drawString(codigoBarras, (int)((anchoReal - g2d.getFontMetrics().stringWidth(codigoBarras)) / 2), 60);
        // 6. Nota
        g2d.setFont(new Font("SansSerif", Font.BOLD, 6));
        String nota = "** REPUESTO / ACCESORIO **";
        g2d.drawString(nota, (int)((anchoReal - g2d.getFontMetrics().stringWidth(nota)) / 2), 69);
        // 7. Ubicación
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 6));
        String ubi = "UBICACIÓN: " + (ubicacion.isEmpty() ? "NO ASIGNADA" : ubicacion.toUpperCase());
        g2d.drawString(ubi, (int)((anchoReal - g2d.getFontMetrics().stringWidth(ubi)) / 2), 78);

        g2d.dispose();


        // --- 2. CREACIÓN DE LA VENTANA DE PREVIA ESTILIZADA (JDialog) ---
        
        // Buscamos la ventana principal para que sea modal sobre ella
        java.awt.Window parentWindow = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
        final javax.swing.JDialog dialogo = new javax.swing.JDialog(parentWindow, "Previsualización Exacta de Etiqueta (5x3 cm)", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setLayout(new java.awt.BorderLayout());
        dialogo.setResizable(false);
        
        // Ícono de la ventana (usamos el logo del sistema)
        try {
            java.net.URL logoUrl = getClass().getResource("/image/logo_bk.png");
            if (logoUrl != null) dialogo.setIconImage(javax.imageio.ImageIO.read(logoUrl));
        } catch (Exception e) {}

        // --- PANEL CENTRAL (Fondo de contraste y Sticker) ---
        // Usamos un gris claro de fondo para que el sticker blanco resalte mucho más
        JPanel panelContenedor = new JPanel(new java.awt.GridBagLayout()); 
        panelContenedor.setBackground(new Color(236, 240, 241)); // Gris "Clouds" plano
        panelContenedor.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Margen interno

        // El JLabel que sostiene la imagen del sticker
        javax.swing.JLabel lblSticker = new javax.swing.JLabel(new javax.swing.ImageIcon(bImage));
        
        // Le ponemos un borde compuesto: una línea gris fina y una sombra suave
        lblSticker.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(189, 195, 199), 1), // Línea gris
            javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1) // Pequeño aire interno blanco
        ));
        
        // Usamos GridBagLayout para centrar el sticker perfecto en el panel gris
        panelContenedor.add(lblSticker, new java.awt.GridBagConstraints());

        // --- PANEL INFERIOR (Botón Cerrar) ---
        JPanel panelBotones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        panelBotones.setOpaque(false); // Transparente para ver el fondo gris
        panelBotones.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 20, 0)); // Aire arriba del botón

        javax.swing.JButton btnCerrar = new javax.swing.JButton("Cerrar Vista Previa");
        // Estilo del botón (Gris oscuro, letra blanca, Segoe UI BOLD)
        btnCerrar.setBackground(new Color(44, 62, 80)); 
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setPreferredSize(new java.awt.Dimension(250, 40));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        btnCerrar.addActionListener(e -> dialogo.dispose()); // Cierra la ventana
        panelBotones.add(btnCerrar);

        // Armamos la ventana
        dialogo.add(panelContenedor, java.awt.BorderLayout.CENTER);
        dialogo.add(panelBotones, java.awt.BorderLayout.SOUTH);

        // Ajustar tamaño automáticamente al contenido y centrar en pantalla
        dialogo.pack();
        dialogo.setLocationRelativeTo(parentWindow);
        dialogo.setVisible(true); // Se queda esperando aquí hasta que cierren la previa
    }
}