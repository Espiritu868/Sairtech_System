package utilidades;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;
import javax.swing.JOptionPane; 

public class GeneradorPDF {

    // --- FUENTES OPTIMIZADAS Y MÁS COMPACTAS ---
    private final Font fuenteEmpresa = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
    private final Font fuenteTelefono = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
    private final Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
    private final Font fuenteEtiqueta = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.DARK_GRAY);
    private final Font fuenteDato = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
    private final Font fuenteMini = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL, BaseColor.DARK_GRAY);
    private final Font fuenteLegal = new Font(Font.FontFamily.HELVETICA, 5, Font.ITALIC, BaseColor.GRAY);

    public boolean crearTicket(String idOrden, String fecha, String cliente, String equipo, String problema, 
                               String total, String nombreEmpresa, String direccionEmpresa, 
                               String telefonoEmpresa, String politicaGarantia, String nombreTecnico, String trabajo, 
                               boolean esRecepcion, String tipoEquipo, boolean abrirAlFinal) {
        try {
            // --- EXTRACCIÓN INTELIGENTE DE CLAVE Y PATRÓN ---
            String claveExtraida = "Sin Clave";
            String equipoLimpio = equipo;
            
            if (equipo.contains("|")) {
                int idx = equipo.indexOf("|");
                equipoLimpio = equipo.substring(0, idx).trim();
                String resto = equipo.substring(idx + 1).toLowerCase();
                if (resto.contains("clave:")) {
                    claveExtraida = equipo.substring(equipo.toLowerCase().indexOf("clave:") + 6).trim();
                }
            }

            String rutaBase = System.getProperty("user.home") + File.separator + "Tickets_Sairtech";
            String subCarpetaCliente = esRecepcion ? "Recepciones" : "Entregas";
            File dirCliente = new File(rutaBase + File.separator + subCarpetaCliente);
            if (!dirCliente.exists()) dirCliente.mkdirs();

            String clienteLimpio = cliente.replace(" ", "_");
            String rutaCliente = dirCliente.getAbsolutePath() + File.separator + "Ticket_" + idOrden + "_" + clienteLimpio + "_CLIENTE.pdf";

            // 1. Generar el PDF del Cliente
            generarArchivoCliente(rutaCliente, idOrden, fecha, cliente, equipoLimpio, problema, total, nombreEmpresa, 
                                  direccionEmpresa, telefonoEmpresa, politicaGarantia, nombreTecnico, esRecepcion, trabajo, claveExtraida, tipoEquipo);

            // 2. Si es recepción, mandar el Sticker del Técnico DIRECTO a la impresora térmica
            if (esRecepcion) {
                boolean esCelular = tipoEquipo != null && (
                    tipoEquipo.toLowerCase().contains("celular") || tipoEquipo.toLowerCase().contains("telefono") || 
                    tipoEquipo.toLowerCase().contains("smartphone") || tipoEquipo.toLowerCase().contains("movil")
                );
                
                utilidades.ImpresoraDirecta impresora = new utilidades.ImpresoraDirecta();
                impresora.imprimirTicketTecnicoDirecto(idOrden, cliente, equipoLimpio, problema, esCelular, nombreTecnico, claveExtraida);
            }

            // 3. Abrir el PDF del cliente para que el usuario elija su impresora
            File archivoCliente = new File(rutaCliente);
            if (archivoCliente.exists()) {
                Desktop.getDesktop().open(archivoCliente);
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error crítico en GeneradorPDF: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "No se pudo generar el PDF. Detalle del error:\n" + e.getMessage(), 
                "Error de Archivo PDF", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void generarArchivoCliente(String ruta, String idOrden, String fecha, String cliente, String equipoLimpio, String problema, 
                                       String total, String nombreEmpresa, String direccionEmpresa, String telefonoEmpresa, 
                                       String politicaGarantia, String nombreTecnico, boolean esRecepcion, String trabajo, 
                                       String claveExtraida, String tipoEquipo) throws Exception {
        
        // --- SOLUCIÓN: CÁLCULO DE ALTURA DINÁMICA ---
        int largoProblema = (problema != null) ? problema.length() : 0;
        int largoTrabajo = (trabajo != null) ? trabajo.length() : 0;
        int largoTotal = largoProblema + largoTrabajo;
        
        // Sumamos unos 12 puntos de altura por cada 30 letras que escribas
        float alturaExtra = (largoTotal / 30f) * 12f;
        
        // 600 para recepción (más términos legales), 500 para entrega, más lo que ocupe el texto
        float altoPDF = (esRecepcion ? 600f : 500f) + alturaExtra;
        
        // Aplicamos el alto dinámico y los márgenes ajustados (5 arriba, 2 abajo)
        Document documento = new Document(new Rectangle(210, altoPDF), 12, 12, 5, 2); 
        // ---------------------------------------------
        
        PdfWriter.getInstance(documento, new FileOutputStream(ruta));
        documento.open();

        Paragraph header = new Paragraph();
        header.setAlignment(Element.ALIGN_CENTER);
        header.add(new Chunk(nombreEmpresa.toUpperCase() + "\n", fuenteEmpresa));
        header.add(new Chunk(direccionEmpresa + "\n", fuenteEtiqueta));
        header.add(new Chunk("CEL: " + telefonoEmpresa + "\n", fuenteTelefono));
        header.setSpacingAfter(3f); // Menos espacio debajo del encabezado
        documento.add(header);

        LineSeparator lineaSolida = new LineSeparator(1f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -2f);
        documento.add(new Chunk(lineaSolida));

        String tituloStr = esRecepcion ? "ORDEN DE SERVICIO" : "COMPROBANTE DE ENTREGA";
        Paragraph titulo = new Paragraph(tituloStr, fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(4f);
        titulo.setSpacingAfter(4f);
        documento.add(titulo);

        PdfPTable tablaInfo = new PdfPTable(2);
        tablaInfo.setWidthPercentage(100);
        tablaInfo.setWidths(new float[]{35f, 65f}); 

        tablaInfo.addCell(crearCeldaInvalida("TICKET #:", fuenteEtiqueta));
        tablaInfo.addCell(crearCeldaInvalida(idOrden, fuenteTitulo));

        tablaInfo.addCell(crearCeldaInvalida("FECHA:", fuenteEtiqueta));
        String fechaMostrar = (fecha == null || fecha.isEmpty()) ? "No registrada" : fecha;
        tablaInfo.addCell(crearCeldaInvalida(fechaMostrar, fuenteDato));

        tablaInfo.addCell(crearCeldaInvalida("CLIENTE:", fuenteEtiqueta));
        tablaInfo.addCell(crearCeldaInvalida(cliente.toUpperCase(), fuenteDato));

        tablaInfo.addCell(crearCeldaInvalida("EQUIPO:", fuenteEtiqueta));
        tablaInfo.addCell(crearCeldaInvalida(equipoLimpio + (tipoEquipo != null ? " (" + tipoEquipo + ")" : ""), fuenteDato));

        tablaInfo.addCell(crearCeldaInvalida("SEGURIDAD:", fuenteEtiqueta));
        
        if (claveExtraida.toLowerCase().contains("patr") || claveExtraida.equalsIgnoreCase("p") || claveExtraida.equalsIgnoreCase("patron")) {
            tablaInfo.addCell(crearCeldaInvalida("PATRÓN VISUAL:\n O   O   O\n O   O   O\n O   O   O", fuenteDato));
        } else {
            tablaInfo.addCell(crearCeldaInvalida(claveExtraida, fuenteDato));
        }

        tablaInfo.addCell(crearCeldaInvalida(esRecepcion ? "ATENDIDO POR:" : "ENTREGADO POR:", fuenteEtiqueta));
        tablaInfo.addCell(crearCeldaInvalida(nombreTecnico.toUpperCase(), fuenteDato));
        
        documento.add(tablaInfo);
        documento.add(new Chunk(lineaSolida));

        PdfPTable tablaProblema = new PdfPTable(1);
        tablaProblema.setWidthPercentage(100);
        tablaProblema.setSpacingBefore(5f); // Reducido
        
        PdfPCell celdaTituloFalla = new PdfPCell(new Phrase(esRecepcion ? "SÍNTOMAS / FALLA REPORTADA:" : "TRABAJO REALIZADO:", fuenteEtiqueta));
        celdaTituloFalla.setBorder(Rectangle.NO_BORDER);
        tablaProblema.addCell(celdaTituloFalla);
        
        String textoMostrar = esRecepcion ? problema : trabajo;
        if (textoMostrar == null || textoMostrar.trim().isEmpty()) {
            textoMostrar = "Revisión técnica general."; 
        }

        PdfPCell celdaFalla = new PdfPCell(new Phrase(textoMostrar, fuenteDato));
        celdaFalla.setPadding(5f); // Reducido
        celdaFalla.setBorderWidth(1f);
        celdaFalla.setBorderColor(BaseColor.BLACK);
        celdaFalla.setMinimumHeight(25f); // Reducido
        tablaProblema.addCell(celdaFalla);
        documento.add(tablaProblema);

        if (!esRecepcion) {
            PdfPTable tablaCobro = new PdfPTable(2);
            tablaCobro.setWidthPercentage(100);
            tablaCobro.setWidths(new float[]{60f, 40f});
            tablaCobro.setSpacingBefore(5f);
            
            PdfPCell celdaVacia = new PdfPCell(new Phrase(""));
            celdaVacia.setBorder(Rectangle.NO_BORDER);
            tablaCobro.addCell(celdaVacia);
            
            PdfPCell celdaTotal = new PdfPCell(new Phrase("TOTAL: L. " + total, fuenteTitulo));
            celdaTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            celdaTotal.setBorder(Rectangle.NO_BORDER);
            tablaCobro.addCell(celdaTotal);
            
            documento.add(tablaCobro);
        }

        PdfPTable tablaBloqueLegal = new PdfPTable(1);
        tablaBloqueLegal.setWidthPercentage(100);
        tablaBloqueLegal.setSpacingBefore(5f); // Reducido
        
        PdfPCell celdaLegal = new PdfPCell();
        celdaLegal.setBorderColor(BaseColor.BLACK);
        celdaLegal.setPadding(5f); // Reducido

        if (!esRecepcion) {
            Paragraph tituloGar = new Paragraph("PÓLIZA DE GARANTÍA\n", fuenteEtiqueta);
            tituloGar.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(tituloGar);

            String terminosGarantia = "NOTA: " + politicaGarantia + "\n" +
                                      "1. COBERTURA: Defectos de fábrica o mano de obra.\n" +
                                      "2. EXCLUSIONES: Humedad, golpes o cargadores genéricos.\n" +
                                      "3. SELLOS: Remoción o ruptura invalidan el reclamo.\n" +
                                      "4. REQUISITO: Obligatorio presentar este ticket.";

            Paragraph cuerpoGar = new Paragraph(terminosGarantia, fuenteMini);
            cuerpoGar.setAlignment(Element.ALIGN_JUSTIFIED);
            celdaLegal.addElement(cuerpoGar);

            Paragraph aceptacionGar = new Paragraph("\n* Su firma confirma que recibe a entera satisfacción.", fuenteLegal);
            aceptacionGar.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(aceptacionGar);

        } else {
            Paragraph tituloCond = new Paragraph("TÉRMINOS DEL SERVICIO\n", fuenteEtiqueta);
            tituloCond.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(tituloCond);

            String terminos = "1. ABANDONO: Equipos no reclamados en 30 días pasarán al taller.\n" +
                              "2. REVISIÓN: L. 100.00 por diagnóstico si no autoriza reparación.\n" +
                              "3. RETIRO: Obligatorio presentar este comprobante.\n" +
                              "4. RIESGOS: Equipos mojados/muertos se reciben bajo su riesgo.";

            Paragraph cuerpoCond = new Paragraph(terminos, fuenteMini);
            cuerpoCond.setAlignment(Element.ALIGN_JUSTIFIED);
            celdaLegal.addElement(cuerpoCond);

            Paragraph aceptacion = new Paragraph("\n* Al entregar su equipo, usted acepta todas las condiciones.", fuenteLegal);
            aceptacion.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(aceptacion);
        }

        tablaBloqueLegal.addCell(celdaLegal);
        documento.add(tablaBloqueLegal);

        DottedLineSeparator lineaPunteada = new DottedLineSeparator();
        lineaPunteada.setGap(3f);
        
        documento.add(new Paragraph("\n"));
        documento.add(new Chunk(lineaPunteada));
        Paragraph firma = new Paragraph("Firma de Conformidad del Cliente\n", fuenteEtiqueta);
        firma.setAlignment(Element.ALIGN_CENTER);
        documento.add(firma);
        
        documento.close();
    }

    private PdfPCell crearCeldaInvalida(String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPaddingBottom(3f); // Reducido
        return celda;
    }

    public boolean reimprimirTicketExistente(String nroOrden, String cliente, int tipo) {
        String subCarpeta = (tipo == 0 || tipo == 1) ? "Recepciones" : "Entregas";
        String clienteLimpio = cliente.replace(" ", "_");
        String nombreArchivo = "Ticket_" + nroOrden + "_" + clienteLimpio + "_CLIENTE.pdf";
        
        String rutaBase = System.getProperty("user.home") + File.separator + "Tickets_Sairtech";
        File archivo = new File(rutaBase + File.separator + subCarpeta + File.separator + nombreArchivo);

        if (archivo.exists()) {
            try {
                Desktop.getDesktop().open(archivo);
                return true;
            } catch (Exception e) { 
                JOptionPane.showMessageDialog(null, "Error al intentar abrir el ticket:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false; 
            }
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró el archivo PDF para este ticket.\nRuta buscada: " + archivo.getAbsolutePath(), "Archivo no encontrado", JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }
}