package utilidades;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class BackupAutomatico {

    public static String obtenerRutaBackup() {
        String rutaDefecto = "F:\\SairTech_Backups";
        try {
            Properties config = new Properties();
            File archivoConfig = new File("config.properties");
            if (archivoConfig.exists()) {
                config.load(new FileInputStream(archivoConfig));
                return config.getProperty("RUTA_BACKUPS", rutaDefecto);
            }
        } catch (Exception e) {}
        return rutaDefecto;
    }

    public static boolean guardarNuevaRutaBackup(String nuevaRuta) {
        try {
            Properties config = new Properties();
            File archivoConfig = new File("config.properties");
            if (archivoConfig.exists()) { config.load(new FileInputStream(archivoConfig)); }
            config.setProperty("RUTA_BACKUPS", nuevaRuta);
            config.store(new FileOutputStream(archivoConfig), "Configuración de SairTech (Modificado por Admin)");
            return true;
        } catch (Exception e) { return false; }
    }

    public static void realizarRespaldoSilencioso(String tipoRespaldo) {
        try {
            // --- NUEVO ESCUDO DE RED: SOLO EL SERVIDOR HACE RESPALDOS ---
            Properties config = new Properties();
            File archivoConfig = new File("config.properties");
            String ipServidor = "localhost"; 
            String usuarioBD = "root";
            String passBD = "";
            
            if (archivoConfig.exists()) {
                config.load(new FileInputStream(archivoConfig));
                ipServidor = config.getProperty("IP_SERVIDOR", "localhost");
                usuarioBD = config.getProperty("USUARIO", "root");
                passBD = config.getProperty("PASSWORD", "");
            }

            // Si la IP no es localhost ni 127.0.0.1, significa que es una máquina cliente.
            // Cancelamos el respaldo silenciosamente.
            if (!ipServidor.equals("localhost") && !ipServidor.equals("127.0.0.1")) {
                System.out.println("💻 Máquina cliente detectada (" + ipServidor + "). El respaldo solo se hace en el Servidor.");
                return; 
            }
            // -----------------------------------------------------------

            String rutaDestino = obtenerRutaBackup();
            File carpeta = new File(rutaDestino);
            
            if (!carpeta.exists()) {
                if (!carpeta.mkdirs()) {
                    mostrarAlerta("⚠️ ALERTA DE SEGURIDAD ⚠️\n\nNo se pudo acceder a la ruta de respaldos en el servidor:\n" + rutaDestino);
                    return; 
                }
            }

            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String nombreArchivo = "SairTech_BD_" + fechaHoy + "_" + tipoRespaldo + ".sql";
            File archivoRespaldo = new File(carpeta, nombreArchivo);

            if (tipoRespaldo.equals("INICIO") && archivoRespaldo.exists()) {
                return;
            }

            // Construimos el comando dinámico por si alguna vez le pones contraseña al root de MySQL
            String comandoPass = passBD.isEmpty() ? "" : " -p\"" + passBD + "\"";
            String comando = "C:\\xampp\\mysql\\bin\\mysqldump.exe -u " + usuarioBD + comandoPass + " db_sairtech -r \"" + archivoRespaldo.getAbsolutePath() + "\"";
            
            Process proceso = Runtime.getRuntime().exec(comando);
            int resultado = proceso.waitFor();

            if (resultado != 0) {
                mostrarAlerta("❌ ERROR AL CREAR EL RESPALDO DE " + tipoRespaldo + " ❌\nCódigo de error: " + resultado + "\nVerifique que XAMPP esté corriendo.");
            }

        } catch (Exception e) {
            mostrarAlerta("❌ ERROR DE RESPALDO ❌\n" + e.getMessage());
        }
    }
    
    private static void mostrarAlerta(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, mensaje, "Falla en Respaldo Automático", JOptionPane.ERROR_MESSAGE);
        });
    }
}