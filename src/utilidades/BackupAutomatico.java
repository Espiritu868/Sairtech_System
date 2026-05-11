package utilidades;

import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class BackupAutomatico {

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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
            config.store(new FileOutputStream(archivoConfig), "Configuración de SairTech");
            return true;
        } catch (Exception e) { return false; }
    }

    public static void iniciarProgramadorRespaldo() {
        scheduler.scheduleAtFixedRate(() -> {
            Calendar ahora = Calendar.getInstance();
            int hora = ahora.get(Calendar.HOUR_OF_DAY);
            int minuto = ahora.get(Calendar.MINUTE);

            if (hora == 8 && minuto == 30) {
                realizarRespaldoSilencioso("PROG_8AM");
            } else if (hora == 13 && minuto == 30) {
                realizarRespaldoSilencioso("PROG_1PM");
            } else if (hora == 16 && minuto == 50) {
                realizarRespaldoSilencioso("PROG_4PM");
            }

            try (java.sql.Connection con = new factory.ConexionFactory().getConexion();
                 java.sql.PreparedStatement ps = con.prepareStatement("SELECT id FROM peticiones_backup LIMIT 1");
                 java.sql.ResultSet rs = ps.executeQuery()) {
                
                if (rs.next()) {
                    int idPeticion = rs.getInt("id");
                    try(java.sql.PreparedStatement psDel = con.prepareStatement("DELETE FROM peticiones_backup WHERE id = ?")) {
                        psDel.setInt(1, idPeticion);
                        psDel.executeUpdate();
                    }
                    realizarRespaldoSilencioso("MANUAL_REMOTO");
                }
            } catch (Exception e) { }
            
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Compara la IP del archivo de configuración con todas las IPs de la máquina actual
     * para determinar si esta PC es el Servidor.
     */
    private static boolean soyElServidor(String ipConfig) {
        try {
            if (ipConfig.equalsIgnoreCase("localhost") || ipConfig.equals("127.0.0.1")) return true;
            
            // Revisamos todas las interfaces de red (Wi-Fi, Ethernet, etc.)
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.getHostAddress().equals(ipConfig)) {
                        return true; // ¡Coincidencia encontrada! Esta PC es el servidor.
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void realizarRespaldoSilencioso(String tipoRespaldo) {
        try {
            Properties config = new Properties();
            File archivoConfig = new File("config.properties");
            String ipServidor = "localhost"; 
            
            if (archivoConfig.exists()) {
                config.load(new FileInputStream(archivoConfig));
                ipServidor = config.getProperty("IP_SERVIDOR", "localhost");
            }

            // --- VALIDACIÓN DE IDENTIDAD ---
            if (!soyElServidor(ipServidor)) {
                return; // Si no soy el servidor, no hago nada.
            }

            File mysqldump = new File("C:\\xampp\\mysql\\bin\\mysqldump.exe");
            if (!mysqldump.exists()) {
                NotificadorWindows.mostrarAlerta("Error de Configuración", "No se encontró mysqldump.exe en el servidor.", MessageType.ERROR);
                return;
            }

            String rutaDestino = obtenerRutaBackup();
            File carpeta = new File(rutaDestino);
            if (!carpeta.exists() && !carpeta.mkdirs()) {
                NotificadorWindows.mostrarAlerta("Error de Respaldo", "No se pudo acceder a la ruta: " + rutaDestino, MessageType.ERROR);
                return;
            }

            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String timestamp = tipoRespaldo.contains("MANUAL") ? new SimpleDateFormat("HH-mm-ss").format(new Date()) : tipoRespaldo;
            File archivoRespaldo = new File(carpeta, "SairTech_BD_" + fechaHoy + "_" + timestamp + ".sql");

            if (tipoRespaldo.contains("PROG") && archivoRespaldo.exists()) return;

            // --- CREDENCIALES MAESTRAS DE XAMPP (Para evitar el Error 1045) ---
            String usuarioMaster = "root";
            String passwordMaster = "root25";

            List<String> comando = new ArrayList<>();
            comando.add(mysqldump.getAbsolutePath());
            comando.add("--host=localhost"); 
            comando.add("--port=3306");
            comando.add("--user=" + usuarioMaster);
            comando.add("--password=" + passwordMaster);
            comando.add("--result-file=" + archivoRespaldo.getAbsolutePath());
            comando.add("db_sairtech");

            ProcessBuilder pb = new ProcessBuilder(comando);
            Process proceso = pb.start();
            
            java.io.BufferedReader errorReader = new java.io.BufferedReader(new java.io.InputStreamReader(proceso.getErrorStream()));
            StringBuilder errorSalida = new StringBuilder();
            String linea;
            while ((linea = errorReader.readLine()) != null) errorSalida.append(linea).append("\n");
            
            int resultado = proceso.waitFor();

            if (resultado == 0) {
                String msg = tipoRespaldo.contains("MANUAL") ? "Orden remota ejecutada: Respaldo guardado en Disco F." : "Respaldo automático completado.";
                NotificadorWindows.mostrarAlerta("Seguridad SairTech", msg, MessageType.INFO);
            } else {
                NotificadorWindows.mostrarAlerta("Error de MySQL", "Fallo al crear archivo:\n" + errorSalida.toString(), MessageType.ERROR);
            }

        } catch (Exception e) {
            NotificadorWindows.mostrarAlerta("Falla en Respaldo", "Excepción: " + e.getMessage(), MessageType.ERROR);
        }
    }
}