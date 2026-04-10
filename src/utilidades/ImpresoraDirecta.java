package utilidades;

import com.itextpdf.text.pdf.Barcode128;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

        // Muestra el cuadro de diálogo de Windows para elegir la impresora (Xprinter, Zebra, etc.)
        if (printerJob.printDialog()) {
            try {
                printerJob.print();
                return true;
            } catch (PrinterException ex) {
                System.err.println("Error al imprimir directamente: " + ex.getMessage());
                return false;
            }
        }
        return false; // Retorna falso si el usuario presiona "Cancelar" en la ventana de impresión
    }

    // Definimos el tamaño del papel térmico (aprox 50mm x 30mm)
    private PageFormat obtenerFormatoEtiqueta() {
        PageFormat format = new PageFormat();
        Paper paper = new Paper();
        
        // Medidas en puntos (1 pulgada = 72 puntos). 
        double width = 150; 
        double height = 90; 
        
        paper.setSize(width, height);
        // Quitamos los márgenes para que la impresora térmica use absolutamente todo el sticker
        paper.setImageableArea(0, 0, width, height); 
        format.setPaper(paper);
        
        return format;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // pageIndex empieza en 0. Si piden 5, imprimimos del 0 al 4.
        if (pageIndex >= totalEtiquetas) {
            return NO_SUCH_PAGE; 
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // 1. Dibujar el Nombre del Producto (Cortamos a 20 letras para que no desborde)
        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
        g2d.setColor(Color.BLACK);
        String nombreCorto = nombreProducto.length() > 20 ? nombreProducto.substring(0, 20) + "..." : nombreProducto;
        
        // Matemática para centrar el texto
        int stringWidth = g2d.getFontMetrics().stringWidth(nombreCorto);
        int xCentered = (int) ((pageFormat.getImageableWidth() - stringWidth) / 2);
        g2d.drawString(nombreCorto, xCentered, 15);

        // 2. Generar el Código de Barras convirtiéndolo a imagen nativa
        try {
            Barcode128 barcode = new Barcode128();
            barcode.setCode(codigoBarras);
            barcode.setBarHeight(30f);
            barcode.setSize(8f);
            
            // Creamos la imagen en memoria (Sin PDF)
            java.awt.Image awtImage = barcode.createAwtImage(Color.BLACK, Color.WHITE);
            
            int xBar = (int) ((pageFormat.getImageableWidth() - awtImage.getWidth(null)) / 2);
            g2d.drawImage(awtImage, xBar, 25, null);
            
        } catch (Exception e) {
            g2d.drawString("Error en código", 10, 40);
        }

        // 3. Dibujar el contador (Ej. 1 de 5)
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
        String contador = "Etiqueta " + (pageIndex + 1) + " de " + totalEtiquetas;
        int countWidth = g2d.getFontMetrics().stringWidth(contador);
        int xCount = (int) ((pageFormat.getImageableWidth() - countWidth) / 2);
        g2d.drawString(contador, xCount, 80);

        return PAGE_EXISTS; // Confirmamos que esta página debe imprimirse
    }
    
    // =========================================================
    // GENERADOR DE RECIBOS TÉRMICOS DE VENTA
    // =========================================================
    public boolean imprimirReciboVenta(int idVenta) {
        // 1. Rescatar los datos de la base de datos
        String fechaVenta = "";
        String cajero = "";
        double total = 0.0;
        String metodoPago = "";
        List<String[]> detalles = new ArrayList<>();

        String sqlVenta = "SELECT v.fecha_venta, v.total, v.metodo_pago, u.usuario FROM ventas v JOIN usuarios u ON v.id_usuario = u.id_usuario WHERE v.id_venta = ?";
        String sqlDetalles = "SELECT cantidad, descripcion, precio_unitario, subtotal FROM detalles_venta WHERE id_venta = ?";

        try (Connection con = new factory.ConexionFactory().getConexion()) {
            // Datos de cabecera
            try (PreparedStatement ps = con.prepareStatement(sqlVenta)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        fechaVenta = rs.getString("fecha_venta");
                        total = rs.getDouble("total");
                        metodoPago = rs.getString("metodo_pago");
                        cajero = rs.getString("usuario");
                    }
                }
            }
            // Datos de los renglones
            try (PreparedStatement ps = con.prepareStatement(sqlDetalles)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        detalles.add(new String[]{
                            rs.getString("cantidad"),
                            rs.getString("descripcion"),
                            rs.getString("precio_unitario"),
                            rs.getString("subtotal")
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener datos del recibo: " + e.getMessage());
            return false;
        }

        // 2. Crear el formato dinámico del papel (Ancho fijo de 80mm, Alto dinámico)
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat format = new PageFormat();
        Paper paper = new Paper();

        // 200 puntos es aprox 72mm-80mm (Estándar de impresoras térmicas grandes)
        double width = 200; 
        // El alto base (cabecera + pie) es 220, más 25 por cada producto
        double height = 220 + (detalles.size() * 25); 

        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        format.setPaper(paper);

        // Copiamos las variables a "finales" para que el Printable pueda leerlas
        final String fFecha = fechaVenta;
        final String fCajero = cajero;
        final double fTotal = total;
        final String fMetodo = metodoPago;

        // 3. Dibujar el Recibo
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) return NO_SUCH_PAGE; // El recibo térmico es 1 sola página infinitamente larga

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2d.setColor(Color.BLACK);

                int y = 15; // Coordenada vertical inicial

                // --- CABECERA ---
                g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                centrarTexto(g2d, "SAIRTECH", width, y); y += 15;
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                centrarTexto(g2d, "Reparación y Venta de Accesorios", width, y); y += 12;
                centrarTexto(g2d, "Santa Bárbara, Honduras", width, y); y += 20;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                centrarTexto(g2d, "NOTA DE VENTA / RECIBO", width, y); y += 15;
                
                g2d.setFont(new Font("Monospaced", Font.PLAIN, 8));
                g2d.drawString("Ticket No: " + idVenta, 10, y); y += 12;
                g2d.drawString("Fecha: " + fFecha, 10, y); y += 12;
                g2d.drawString("Cajero: " + fCajero, 10, y); y += 15;

                g2d.drawString("------------------------------------------", 10, y); y += 12;
                g2d.drawString("CANT DESCRIPCION           SUBTOTAL", 10, y); y += 12;
                g2d.drawString("------------------------------------------", 10, y); y += 15;

                // --- RENGLONES DE PRODUCTOS ---
                for (String[] det : detalles) {
                    String cant = det[0];
                    String desc = det[1];
                    String sub = det[3];

                    // Cortamos la descripción si es muy larga
                    if (desc.length() > 20) desc = desc.substring(0, 20) + "..";

                    // Formateamos la línea
                    g2d.drawString(cant + "x", 10, y); 
                    g2d.drawString(desc, 35, y); 
                    
                    // Alineamos el subtotal a la derecha
                    int subWidth = g2d.getFontMetrics().stringWidth(sub);
                    g2d.drawString(sub, (int)width - subWidth - 10, y); 
                    y += 12;
                }

                // --- PIE Y TOTALES ---
                y += 5;
                g2d.drawString("------------------------------------------", 10, y); y += 20;

                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2d.drawString("TOTAL A PAGAR:", 10, y);
                String totalStr = String.format("L. %.2f", fTotal);
                int totWidth = g2d.getFontMetrics().stringWidth(totalStr);
                g2d.drawString(totalStr, (int)width - totWidth - 10, y); y += 20;

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 8));
                g2d.drawString("Método de Pago: " + fMetodo, 10, y); y += 25;

                centrarTexto(g2d, "¡Gracias por su preferencia!", width, y); y += 12;
                centrarTexto(g2d, "Revise sus productos. No hay cambios.", width, y);

                return PAGE_EXISTS;
            }
        }, format);

        // 4. Mostrar cuadro de diálogo y enviar a imprimir
        if (printerJob.printDialog()) {
            try {
                printerJob.print();
                return true;
            } catch (PrinterException ex) {
                System.err.println("Error al imprimir recibo: " + ex.getMessage());
                return false;
            }
        }
        return false;
    }

    // Función auxiliar para centrar texto
    private void centrarTexto(Graphics2D g2d, String texto, double width, int y) {
        int stringWidth = g2d.getFontMetrics().stringWidth(texto);
        int x = (int) ((width - stringWidth) / 2);
        g2d.drawString(texto, x, y);
    }
}