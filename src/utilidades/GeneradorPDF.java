package utilidades;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;

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
            String claveExtraida = "Sin Clave";
            String equipoLimpio = equipo;
            
            if (equipo.contains("|  Clave:")) {
                String[] partes = equipo.split("\\|  Clave:");
                equipoLimpio = partes[0].trim();
                if (partes.length > 1) {
                    claveExtraida = partes[1].trim();
                }
            }

            String rutaBase = System.getProperty("user.dir") + File.separator + "Tickets_Sairtech";
            String subCarpetaCliente = esRecepcion ? "Recepciones" : "Entregas";
            
            File dirCliente = new File(rutaBase + File.separator + subCarpetaCliente);
            File dirTecnico = new File(rutaBase + File.separator + "Tickets_Tecnico");

            if (!dirCliente.exists()) dirCliente.mkdirs();
            if (!dirTecnico.exists()) dirTecnico.mkdirs();

            String clienteLimpio = cliente.replace(" ", "_");
            String rutaCliente = dirCliente.getAbsolutePath() + File.separator + "Ticket_" + idOrden + "_" + clienteLimpio + "_CLIENTE.pdf";
            String rutaTecnico = dirTecnico.getAbsolutePath() + File.separator + "Ticket_" + idOrden + "_" + clienteLimpio + "_TECNICO.pdf";

            generarArchivoCliente(rutaCliente, idOrden, fecha, cliente, equipoLimpio, problema, total, nombreEmpresa, 
                                  direccionEmpresa, telefonoEmpresa, politicaGarantia, nombreTecnico, esRecepcion, trabajo, claveExtraida, tipoEquipo);

            if (esRecepcion) {
                boolean esCelular = tipoEquipo != null && (
                    tipoEquipo.toLowerCase().contains("celular") || 
                    tipoEquipo.toLowerCase().contains("telefono") || 
                    tipoEquipo.toLowerCase().contains("teléfono") || 
                    tipoEquipo.toLowerCase().contains("smartphone") || 
                    tipoEquipo.toLowerCase().contains("movil")
                );
                generarArchivoTecnico(rutaTecnico, idOrden, cliente, equipoLimpio, problema, esCelular, nombreTecnico, claveExtraida);
            }

            File archivoCliente = new File(rutaCliente);
            File archivoTecnico = new File(rutaTecnico);
            boolean impresoCorrectamente = false;

            javax.print.PrintService impresoraDefault = javax.print.PrintServiceLookup.lookupDefaultPrintService();

            if (impresoraDefault != null) {
                try {

                    String appData = System.getenv("LOCALAPPDATA");
                    
                    File ruta1 = new File("C:\\Program Files\\SumatraPDF\\SumatraPDF.exe");
                    File ruta2 = new File("C:\\Program Files (x86)\\SumatraPDF\\SumatraPDF.exe");
                    File ruta3 = new File(appData + "\\SumatraPDF\\SumatraPDF.exe");
                    
                    String ejecutableSumatra = null;
                    if (ruta1.exists()) ejecutableSumatra = ruta1.getAbsolutePath();
                    else if (ruta2.exists()) ejecutableSumatra = ruta2.getAbsolutePath();
                    else if (ruta3.exists()) ejecutableSumatra = ruta3.getAbsolutePath();

                    if (ejecutableSumatra != null) {
                        
                        if (archivoCliente.exists()) {
                            Runtime.getRuntime().exec(new String[]{ejecutableSumatra, "-print-to-default", "-silent", archivoCliente.getAbsolutePath()});
                        }
                        if (esRecepcion && archivoTecnico.exists()) {
                            Thread.sleep(500); 
                            Runtime.getRuntime().exec(new String[]{ejecutableSumatra, "-print-to-default", "-silent", archivoTecnico.getAbsolutePath()});
                        }
                    } else {
                        
                        javax.swing.JOptionPane.showMessageDialog(null, "Aviso técnico: SumatraPDF no encontrado. Usando impresión clásica.");
                        if (archivoCliente.exists()) Desktop.getDesktop().print(archivoCliente);
                        if (esRecepcion && archivoTecnico.exists()) Desktop.getDesktop().print(archivoTecnico);
                    }
                    
                    impresoCorrectamente = true;
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "La impresora está lista, pero falta asociar un lector PDF en Windows.\nAbriendo documentos en pantalla...", 
                        "Aviso de Sistema", javax.swing.JOptionPane.WARNING_MESSAGE);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "No se detectó ninguna impresora conectada.\nGenerando tickets en pantalla...", 
                    "Modo Visual", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }

            if (abrirAlFinal || !impresoCorrectamente) {
                //quitalo para probar
                if (archivoCliente.exists()) Desktop.getDesktop().open(archivoCliente);
                if (esRecepcion && archivoTecnico.exists()) Desktop.getDesktop().open(archivoTecnico);
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error crítico en GeneradorPDF: " + e.getMessage());
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

        if (esRecepcion) {
            tablaInfo.addCell(crearCeldaInvalida("SEGURIDAD:", fuenteEtiqueta));
            if (claveExtraida.trim().equalsIgnoreCase("patron") || claveExtraida.trim().equalsIgnoreCase("p")) {
                tablaInfo.addCell(crearCeldaInvalida("DIBUJAR PATRÓN:\n  O   O   O\n  O   O   O\n  O   O   O", fuenteDato));
            } else {
                tablaInfo.addCell(crearCeldaInvalida(claveExtraida, fuenteDato));
            }
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
            Paragraph cobro = new Paragraph("TOTAL PAGADO: L. " + total, fuenteTitulo);
            cobro.setAlignment(Element.ALIGN_RIGHT);
            cobro.setSpacingBefore(10f);
            cobro.setSpacingAfter(10f);
            documento.add(cobro);
            
            PdfPTable tablaGarantia = new PdfPTable(1);
            tablaGarantia.setWidthPercentage(100);
            tablaGarantia.setSpacingBefore(10f);
            
            PdfPCell celdaGar = new PdfPCell();
            celdaGar.setBorderColor(BaseColor.BLACK);
            celdaGar.setPadding(8f);

            Paragraph tituloGar = new Paragraph("PÓLIZA DE GARANTÍA\n\n", fuenteEtiqueta);
            tituloGar.setAlignment(Element.ALIGN_CENTER);
            celdaGar.addElement(tituloGar);

            String terminosGarantia = "NOTA: " + politicaGarantia + "\n\n" +
                              "1. COBERTURA: Válida exclusivamente por defectos de fábrica del repuesto instalado o en la mano de obra realizada.\n\n" +
                              "2. EXCLUSIONES: Se anula automáticamente la garantía por rastros de humedad, golpes, presión excesiva o uso de cargadores genéricos.\n\n" +
                              "3. SELLOS: La remoción, ruptura o alteración de los sellos de seguridad del taller invalidan cualquier reclamo.\n\n" +
                              "4. SOFTWARE: Los trabajos de sistema, cuentas o liberación no tienen garantía contra bloqueos futuros por actualizaciones del usuario.\n\n" +
                              "5. REQUISITO: Es estrictamente necesario presentar este ticket para procesar cualquier validación de garantía.";

            Paragraph cuerpoGar = new Paragraph(terminosGarantia, fuenteMini);
            cuerpoGar.setAlignment(Element.ALIGN_JUSTIFIED);
            celdaGar.addElement(cuerpoGar);

            Paragraph aceptacionGar = new Paragraph("\n* Revise su equipo antes de retirarse. Su firma confirma que recibe el equipo reparado y funcionando a entera satisfacción.", fuenteLegal);
            aceptacionGar.setAlignment(Element.ALIGN_CENTER);
            celdaGar.addElement(aceptacionGar);

            tablaGarantia.addCell(celdaGar);
            documento.add(tablaGarantia);
            
        } else {
            PdfPTable tablaCondiciones = new PdfPTable(1);
            tablaCondiciones.setWidthPercentage(100);
            tablaCondiciones.setSpacingBefore(15f);
            
            PdfPCell celdaCond = new PdfPCell();
            celdaCond.setBorderColor(BaseColor.BLACK);
            celdaCond.setPadding(8f);

            Paragraph tituloCond = new Paragraph("TÉRMINOS Y CONDICIONES DEL SERVICIO\n\n", fuenteEtiqueta);
            tituloCond.setAlignment(Element.ALIGN_CENTER);
            celdaCond.addElement(tituloCond);

            String terminos = "1. ABANDONO: Todo equipo no reclamado pasados 30 días calendario desde su ingreso, pasará a ser propiedad del taller para cubrir costos de revisión y almacenaje.\n\n" +
                              "2. REVISIÓN: El diagnóstico de equipos tiene un cargo no reembolsable de L. 100.00 en caso de no autorizar la reparación.\n\n" +
                              "3. RETIRO: Es estrictamente obligatorio presentar este comprobante (físico o foto legible) para la entrega de su equipo.\n\n" +
                              "4. DATOS: No nos responsabilizamos por la pérdida de información o archivos durante el servicio.\n\n" +
                              "5. RIESGOS: Equipos mojados, muy golpeados o que no encienden, se reciben bajo el propio riesgo del cliente.";

            Paragraph cuerpoCond = new Paragraph(terminos, fuenteMini);
            cuerpoCond.setAlignment(Element.ALIGN_JUSTIFIED);
            celdaCond.addElement(cuerpoCond);

            Paragraph aceptacion = new Paragraph("\n* Al entregar su equipo y recibir este documento, usted acepta todas las condiciones anteriormente descritas.", fuenteLegal);
            aceptacion.setAlignment(Element.ALIGN_CENTER);
            celdaCond.addElement(aceptacion);

            tablaCondiciones.addCell(celdaCond);
            documento.add(tablaCondiciones);
        }

        DottedLineSeparator lineaPunteada = new DottedLineSeparator();
        lineaPunteada.setGap(3f);
        
        documento.add(new Paragraph("\n\n"));
        documento.add(new Chunk(lineaPunteada));
        Paragraph firma = new Paragraph("Firma de Conformidad del Cliente\n\n", fuenteEtiqueta);
        firma.setAlignment(Element.ALIGN_CENTER);
        documento.add(firma);
        
        documento.close();
    }

    private void generarArchivoTecnico(String ruta, String idOrden, String cliente, String equipoLimpio, 
                                       String problema, boolean esCelular, String nombreTecnico, String claveExtraida) throws Exception {
        
        float alto = esCelular ? 140f : 250f; 
        Document documento = new Document(new Rectangle(190, alto), 5, 5, 5, 5); 
        PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(ruta));
        documento.open();

        Font fuenteMiniDato = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.BLACK);
        Font fuenteMiniEtiqueta = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, BaseColor.DARK_GRAY);

        PdfPTable tablaInfo = new PdfPTable(2);
        tablaInfo.setWidthPercentage(100);
        tablaInfo.setWidths(new float[]{50f, 50f}); 

        tablaInfo.addCell(crearCeldaInvalida("ORDEN: " + idOrden, fuenteMiniEtiqueta));
        tablaInfo.addCell(crearCeldaInvalida("EQ: " + equipoLimpio, fuenteMiniDato));

        tablaInfo.addCell(crearCeldaInvalida("CLI: " + (cliente.length() > 12 ? cliente.substring(0, 12) : cliente), fuenteMiniDato));
        tablaInfo.addCell(crearCeldaInvalida("TEC: " + nombreTecnico, fuenteMiniDato));

        tablaInfo.addCell(crearCeldaInvalida("SEGURIDAD: ", fuenteMiniEtiqueta));
        if (claveExtraida.trim().equalsIgnoreCase("patron") || claveExtraida.trim().equalsIgnoreCase("p")) {
            tablaInfo.addCell(crearCeldaInvalida("O  O  O\nO  O  O\nO  O  O", fuenteMiniDato));
        } else {
            tablaInfo.addCell(crearCeldaInvalida(claveExtraida, fuenteMiniDato));
        }

        documento.add(tablaInfo);
        
        Paragraph falla = new Paragraph("F: " + problema, fuenteMiniDato);
        falla.setSpacingBefore(0f);
        falla.setSpacingAfter(2f);
        documento.add(falla);

        if (esCelular) {
            Barcode128 barcode = new Barcode128();
            barcode.setCode(idOrden);
            barcode.setBarHeight(20f); 
            barcode.setSize(7f);      
            
            Image imgBarcode = barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
            imgBarcode.setAlignment(Element.ALIGN_CENTER);
            imgBarcode.scalePercent(110); 
            
            documento.add(imgBarcode);
            
            Paragraph notas = new Paragraph("NOTAS:____________________", fuenteMiniEtiqueta);
            notas.setAlignment(Element.ALIGN_CENTER);
            documento.add(notas);
            
        } else {
            documento.add(new Chunk(new LineSeparator(0.5f, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, 0)));
            documento.add(new Paragraph("TRABAJO/REPUESTOS:", fuenteMiniEtiqueta));
            documento.add(new Paragraph("\n\n\n\n________________________", fuenteMiniDato));
        }

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
        
        String rutaBase = System.getProperty("user.dir") + File.separator + "Tickets_Sairtech";
        File archivo = new File(rutaBase + File.separator + subCarpeta + File.separator + nombreArchivo);

        if (archivo.exists()) {
            try {
                Desktop.getDesktop().open(archivo);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
    
    // =========================================================
    // GENERADOR DE ETIQUETAS DE INVENTARIO
    // =========================================================
    public boolean crearEtiquetasInventario(String nombreProducto, String codigoBarras, int cantidad) {
        try {
            String rutaBase = System.getProperty("user.dir") + File.separator + "Tickets_Sairtech";
            File dirEtiquetas = new File(rutaBase + File.separator + "Etiquetas_Inventario");
            if (!dirEtiquetas.exists()) dirEtiquetas.mkdirs();

            String codigoLimpio = codigoBarras.replaceAll("[^a-zA-Z0-9_\\-]", "");
            String rutaPdf = dirEtiquetas.getAbsolutePath() + File.separator + "Etiquetas_" + codigoLimpio + ".pdf";

            // CORRECCIÓN 1: Aumentamos el alto a 110 para que no salte a una segunda página por error
            Rectangle tamañoEtiqueta = new Rectangle(160, 110);
            Document documento = new Document(tamañoEtiqueta, 5, 5, 5, 5);
            PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(rutaPdf));
            
            documento.open();

            Font fNombre = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, BaseColor.BLACK);
            Font fContador = new Font(Font.FontFamily.HELVETICA, 7, Font.NORMAL, BaseColor.DARK_GRAY);

            for (int i = 1; i <= cantidad; i++) {
                if (i > 1) {
                    documento.newPage(); 
                }

                String nombreMostrar = nombreProducto.length() > 25 ? nombreProducto.substring(0, 25) + "..." : nombreProducto;
                Paragraph pNombre = new Paragraph(nombreMostrar, fNombre);
                pNombre.setAlignment(Element.ALIGN_CENTER);
                pNombre.setSpacingAfter(3f); // Damos un pequeño respiro hacia abajo
                documento.add(pNombre);

                Barcode128 barcode = new Barcode128();
                barcode.setCode(codigoBarras);
                barcode.setBarHeight(25f); 
                barcode.setSize(7f);       
                
                Image imgBarcode = barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
                imgBarcode.setAlignment(Element.ALIGN_CENTER);
                imgBarcode.scalePercent(105); 
                
                // CORRECCIÓN 2: Eliminamos el "Paragraph espacio" que causaba el salto de página extra
                documento.add(imgBarcode);

                // CORRECCIÓN 3: Solo mostramos el número
                Paragraph pContador = new Paragraph(String.valueOf(i), fContador);
                pContador.setAlignment(Element.ALIGN_CENTER);
                documento.add(pContador);
            }

            documento.close();

            File archivo = new File(rutaPdf);
            if (archivo.exists()) {
                java.awt.Desktop.getDesktop().open(archivo);
            }

            return true;

        } catch (Exception e) {
            System.err.println("Error al generar etiquetas de inventario: " + e.getMessage());
            return false;
        }
    }
}