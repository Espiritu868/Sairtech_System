package utilidades;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;
import javax.swing.JOptionPane; // Importación añadida para las alertas

public class GeneradorPDF {

    private final Font fuenteEmpresa = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK);
    private final Font fuenteTelefono = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.BLACK);
    private final Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
    private final Font fuenteEtiqueta = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.DARK_GRAY);
    private final Font fuenteDato = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
    private final Font fuenteMini = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.DARK_GRAY);
    private final Font fuenteLegal = new Font(Font.FontFamily.HELVETICA, 6, Font.ITALIC, BaseColor.GRAY);

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

            // CORRECCIÓN 1: Usamos user.home en lugar de user.dir para evitar problemas de permisos en red
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
            // CORRECCIÓN 2: Mostrar el error en pantalla para saber exactamente qué falla
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
        
        Document documento = new Document(new Rectangle(210, 900), 18, 18, 10, 10);
        PdfWriter.getInstance(documento, new FileOutputStream(ruta));
        documento.open();

        Paragraph header = new Paragraph();
        header.setAlignment(Element.ALIGN_CENTER);
        header.add(new Chunk(nombreEmpresa.toUpperCase() + "\n", fuenteEmpresa));
        header.add(new Chunk(direccionEmpresa + "\n", fuenteEtiqueta));
        header.add(new Chunk("CEL: " + telefonoEmpresa + "\n", fuenteTelefono));
        header.setSpacingAfter(5f);
        documento.add(header);

        LineSeparator lineaSolida = new LineSeparator(1f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -2f);
        documento.add(new Chunk(lineaSolida));

        String tituloStr = esRecepcion ? "ORDEN DE SERVICIO" : "COMPROBANTE DE ENTREGA";
        Paragraph titulo = new Paragraph(tituloStr, fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingBefore(8f);
        titulo.setSpacingAfter(8f);
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
            tablaInfo.addCell(crearCeldaInvalida("DIBUJAR PATRÓN:\n\n  O    O    O\n\n  O    O    O\n\n  O    O    O", fuenteDato));
        } else {
            tablaInfo.addCell(crearCeldaInvalida(claveExtraida, fuenteDato));
        }

        tablaInfo.addCell(crearCeldaInvalida(esRecepcion ? "ATENDIDO POR:" : "ENTREGADO POR:", fuenteEtiqueta));
        tablaInfo.addCell(crearCeldaInvalida(nombreTecnico.toUpperCase(), fuenteDato));
        
        documento.add(tablaInfo);
        documento.add(new Chunk(lineaSolida));

        PdfPTable tablaProblema = new PdfPTable(1);
        tablaProblema.setWidthPercentage(100);
        tablaProblema.setSpacingBefore(10f);
        
        PdfPCell celdaTituloFalla = new PdfPCell(new Phrase(esRecepcion ? "SÍNTOMAS / FALLA REPORTADA:" : "TRABAJO REALIZADO:", fuenteEtiqueta));
        celdaTituloFalla.setBorder(Rectangle.NO_BORDER);
        tablaProblema.addCell(celdaTituloFalla);
        
        String textoMostrar = esRecepcion ? problema : trabajo;
        if (textoMostrar == null || textoMostrar.trim().isEmpty()) {
            textoMostrar = "Revisión técnica general."; 
        }

        PdfPCell celdaFalla = new PdfPCell(new Phrase(textoMostrar, fuenteDato));
        celdaFalla.setPadding(8f);
        celdaFalla.setBorderWidth(1f);
        celdaFalla.setBorderColor(BaseColor.BLACK);
        celdaFalla.setMinimumHeight(35f);
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
        tablaBloqueLegal.setSpacingBefore(10f);
        
        PdfPCell celdaLegal = new PdfPCell();
        celdaLegal.setBorderColor(BaseColor.BLACK);
        celdaLegal.setPadding(8f);

        if (!esRecepcion) {
            Paragraph tituloGar = new Paragraph("PÓLIZA DE GARANTÍA\n\n", fuenteEtiqueta);
            tituloGar.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(tituloGar);

            String terminosGarantia = "NOTA: " + politicaGarantia + "\n\n" +
                                      "1. COBERTURA: Válida exclusivamente por defectos de fábrica del repuesto instalado o en la mano de obra realizada.\n\n" +
                                      "2. EXCLUSIONES: Se anula automáticamente la garantía por rastros de humedad, golpes, presión excesiva o uso de cargadores genéricos.\n\n" +
                                      "3. SELLOS: La remoción, ruptura o alteración de los sellos de seguridad del taller invalidan cualquier reclamo.\n\n" +
                                      "4. SOFTWARE: Los trabajos de sistema, cuentas o liberación no tienen garantía contra bloqueos futuros por actualizaciones del usuario.\n\n" +
                                      "5. REQUISITO: Es estrictamente necesario presentar este ticket para procesar cualquier validación de garantía.";

            Paragraph cuerpoGar = new Paragraph(terminosGarantia, fuenteMini);
            cuerpoGar.setAlignment(Element.ALIGN_JUSTIFIED);
            celdaLegal.addElement(cuerpoGar);

            Paragraph aceptacionGar = new Paragraph("\n* Revise su equipo. Su firma confirma que recibe el equipo reparado a entera satisfacción.", fuenteLegal);
            aceptacionGar.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(aceptacionGar);

        } else {
            Paragraph tituloCond = new Paragraph("TÉRMINOS Y CONDICIONES DEL SERVICIO\n\n", fuenteEtiqueta);
            tituloCond.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(tituloCond);

            String terminos = "1. ABANDONO: Todo equipo no reclamado pasados 30 días calendario desde su ingreso, pasará a ser propiedad del taller para cubrir costos de revisión y almacenaje.\n\n" +
                              "2. REVISIÓN: El diagnóstico de equipos tiene un cargo no reembolsable de L. 100.00 en caso de no autorizar la reparación.\n\n" +
                              "3. RETIRO: Es estrictamente obligatorio presentar este comprobante (físico o foto legible) para la entrega de su equipo.\n\n" +
                              "4. DATOS: No nos responsabilizamos por la pérdida de información o archivos durante el servicio.\n\n" +
                              "5. RIESGOS: Equipos mojados, muy golpeados o que no encienden, se reciben bajo el propio riesgo del cliente.";

            Paragraph cuerpoCond = new Paragraph(terminos, fuenteMini);
            cuerpoCond.setAlignment(Element.ALIGN_JUSTIFIED);
            celdaLegal.addElement(cuerpoCond);

            Paragraph aceptacion = new Paragraph("\n* Al entregar su equipo y recibir este documento, usted acepta todas las condiciones.", fuenteLegal);
            aceptacion.setAlignment(Element.ALIGN_CENTER);
            celdaLegal.addElement(aceptacion);
        }

        tablaBloqueLegal.addCell(celdaLegal);
        documento.add(tablaBloqueLegal);

        DottedLineSeparator lineaPunteada = new DottedLineSeparator();
        lineaPunteada.setGap(3f);
        
        documento.add(new Paragraph("\n\n"));
        documento.add(new Chunk(lineaPunteada));
        Paragraph firma = new Paragraph("Firma de Conformidad del Cliente\n\n", fuenteEtiqueta);
        firma.setAlignment(Element.ALIGN_CENTER);
        documento.add(firma);
        
        documento.close();
    }

    private PdfPCell crearCeldaInvalida(String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPaddingBottom(4f);
        return celda;
    }

    public boolean reimprimirTicketExistente(String nroOrden, String cliente, int tipo) {
        String subCarpeta = (tipo == 0 || tipo == 1) ? "Recepciones" : "Entregas";
        String clienteLimpio = cliente.replace(" ", "_");
        String nombreArchivo = "Ticket_" + nroOrden + "_" + clienteLimpio + "_CLIENTE.pdf";
        
        // CORRECCIÓN 3: También usamos user.home aquí para poder encontrar y reimprimir los tickets
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