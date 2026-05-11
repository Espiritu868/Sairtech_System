package utilidades;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServidorCamara extends NanoHTTPD {

    private String idDespieceActual = "";
    private final String RUTA_DESTINO = "C:\\SairTech_System\\Evidencias\\";

    public ServidorCamara() throws java.io.IOException {
        super("0.0.0.0", 8080); 
        
        File directorio = new File(RUTA_DESTINO);
        if (!directorio.exists()) { directorio.mkdirs(); }
        
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    // Ahora solo recibe el ID, ya no necesita el int numeroFoto
    public void prepararRecepcion(String idDespiece) {
        this.idDespieceActual = idDespiece;
    }

    @Override
    public Response serve(IHTTPSession session) {
        // Preguntamos a SairTech si hay espacio (1, 2 o 3). Si devuelve -1, está lleno.
        int espacioDisponible = gui.PanelDespiece.getEspacioDisponible();

        if (session.getMethod() == Method.GET) {
            if (espacioDisponible == -1) {
                return newFixedLengthResponse(Response.Status.OK, "text/html", construirPaginaLleno());
            }
            return newFixedLengthResponse(Response.Status.OK, "text/html", construirPaginaWeb(espacioDisponible));
        }

        if (session.getMethod() == Method.POST) {
            try {
                if (espacioDisponible == -1) {
                     return newFixedLengthResponse(Response.Status.OK, "text/html", construirPaginaLleno());
                }

                Map<String, String> archivosTemporales = new HashMap<>();
                session.parseBody(archivosTemporales);
                String archivoTempPath = archivosTemporales.get("foto");
                
                if (archivoTempPath != null) {
                    // La guardamos con el número de espacio que encontramos vacío
                    String nombreFinal = "Despiece_" + idDespieceActual + "_Img" + espacioDisponible + ".jpg";
                    File archivoDestino = new File(RUTA_DESTINO + nombreFinal);

                    java.nio.file.Path origen = java.nio.file.Paths.get(archivoTempPath);
                    java.nio.file.Path destino = java.nio.file.Paths.get(archivoDestino.getAbsolutePath());
                    java.nio.file.Files.copy(origen, destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    // Avisamos a SairTech
                    actualizarInterfazJava(archivoDestino.getAbsolutePath(), espacioDisponible);
                    
                    // Verificamos si aún queda espacio para recargar la cámara
                    int nuevoEspacio = gui.PanelDespiece.getEspacioDisponible();
                    if (nuevoEspacio == -1) {
                        return newFixedLengthResponse(Response.Status.OK, "text/html", construirPaginaLleno());
                    } else {
                        // Recarga la página sola para la siguiente foto
                        String htmlSiguiente = construirPaginaWeb(nuevoEspacio);
                        return newFixedLengthResponse(Response.Status.OK, "text/html", htmlSiguiente);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al recibir imagen: " + e.getMessage());
            }
        }
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "Error procesando solicitud.");
    }

    // --- PÁGINA "PANTALLA COMPLETA" SIN BOTONES VISIBLES ---
    private String construirPaginaWeb(int espacio) {
        return "<!DOCTYPE html>"
             + "<html>"
             + "<head>"
             + "<meta charset='UTF-8'>"
             + "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>"
             + "<title>SairTech - Cámara</title>"
             + "<style>"
             + "  body, html { margin: 0; padding: 0; height: 100%; background-color: #1E272E; color: white; font-family: 'Segoe UI', sans-serif; overflow: hidden; }"
             + "  /* Convertimos el label en un botón gigante que cubre toda la pantalla */"
             + "  .pantalla-boton { display: flex; flex-direction: column; justify-content: center; align-items: center; width: 100vw; height: 100vh; cursor: pointer; text-align: center; }"
             + "  .pantalla-boton.subiendo { background-color: #f39c12; pointer-events: none; }"
             + "  .icono { font-size: 80px; margin-bottom: 20px; }"
             + "  h1 { margin: 0; font-size: 30px; }"
             + "  p { color: #DCDDE1; font-size: 18px; }"
             + "  input[type='file'] { display: none; }"
             + "</style>"
             + "</head>"
             + "<body>"
             + "    <form method='POST' enctype='multipart/form-data' id='formFoto'>"
             + "      <label for='fotoInput' class='pantalla-boton' id='zonaTacto'>"
             + "         <div class='icono' id='iconoWeb'>📸</div>"
             + "         <h1 id='tituloWeb'>TOCA LA PANTALLA</h1>"
             + "         <p id='subtituloWeb'>Para tomar la foto #" + espacio + " del equipo " + idDespieceActual + "</p>"
             + "      </label>"
             + "      <input type='file' name='foto' id='fotoInput' accept='image/*' capture='environment'>"
             + "    </form>"
             + "  <script>"
             + "    document.getElementById('fotoInput').addEventListener('change', function() {"
             + "      if(this.files.length > 0) {"
             + "        document.getElementById('zonaTacto').classList.add('subiendo');"
             + "        document.getElementById('iconoWeb').innerText = '⏳';"
             + "        document.getElementById('tituloWeb').innerText = 'ENVIANDO A SAIRTECH...';"
             + "        document.getElementById('subtituloWeb').innerText = 'Espera un segundo';"
             + "        document.getElementById('formFoto').submit();"
             + "      }"
             + "    });"
             + "  </script>"
             + "</body>"
             + "</html>";
    }

    private String construirPaginaLleno() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
             + "<style>body{display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;background-color:#2ecc71;color:white;font-family:sans-serif;margin:0;}</style></head>"
             + "<body><div style='font-size:80px;'>✅</div><h1>¡Todo Listo!</h1><p>3 de 3 fotos recibidas.</p><p>Ya puedes cerrar el navegador.</p></body></html>";
    }

    private void actualizarInterfazJava(String rutaImagen, int numFoto) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            gui.PanelDespiece.imagenRecibida(rutaImagen, numFoto);
        });
    }
    
    public static java.awt.image.BufferedImage generarQR(String datos, int ancho, int alto) throws Exception {
        com.google.zxing.qrcode.QRCodeWriter writer = new com.google.zxing.qrcode.QRCodeWriter();
        com.google.zxing.common.BitMatrix matrix = writer.encode(datos, com.google.zxing.BarcodeFormat.QR_CODE, ancho, alto);
        return com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(matrix);
    }
}