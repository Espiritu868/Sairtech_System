package factory;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.awt.TrayIcon.MessageType;

/**
 * Factory para la conexión a la base de datos
 * Proyecto: SairTech - Sistema de Control de Equipos
 */
public class ConexionFactory {
    
    private static String host = "localhost";
    private static String port = "3306";
    private static String database = "db_sairtech";
    private static String user = "root";
    private static String pass = "";
    
    private static long tiempoUltimoError = 0;
    
    // --- VARIABLES DE ESTADO ---
    private static boolean huboFalloConexion = false;
    private static boolean hiloVigilanteActivo = false;
    
    static {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream("config.properties"));
            host = config.getProperty("IP_SERVIDOR", host);
            port = config.getProperty("PUERTO", port);
            database = config.getProperty("BASE_DATOS", database);
            user = config.getProperty("USUARIO", user);
            pass = config.getProperty("PASSWORD", pass);
        } catch (Exception e) { }
    }

    /**
     * Obtiene una conexión activa. 
     * Ahora lanza SQLException para que los DAOs manejen el error sin colapsar la UI.
     */
    public Connection getConexion() throws SQLException {
        // Timeout de 3 segundos para que el programa no se quede "pensando" demasiado
        String parametros = "?connectTimeout=3000&socketTimeout=5000&useSSL=false&allowPublicKeyRetrieval=true";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + parametros;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            
            // Si logra conectar y veníamos de un fallo, avisamos y reseteamos
            if (huboFalloConexion) {
                huboFalloConexion = false;
                utilidades.NotificadorWindows.mostrarAlerta(
                    "Conexión con la BD Restablecida", 
                    "El servidor está de vuelta. Ya puedes continuar.", 
                    MessageType.INFO
                );
            }
            return con;
            
        } catch (ClassNotFoundException e) {
            mostrarErrorControlado("Falta el Driver de MySQL.");
            throw new SQLException("Error Crítico: Driver no encontrado", e);
        } catch (SQLException e) {
            // Si es la primera vez que detecta la caída, lanzamos el vigilante en segundo plano
            if (!huboFalloConexion) {
                huboFalloConexion = true;
                mostrarErrorControlado("Se perdió la conexión. SairTech buscará el servidor automáticamente.");
                iniciarVigilanteFantasma(url); 
            }
            // Lanzamos la excepción para que el DAO la capture y el panel no se cuelgue
            throw e; 
        }
    }
    
    private void iniciarVigilanteFantasma(String url) {
        if (hiloVigilanteActivo) return; 
        hiloVigilanteActivo = true;

        new Thread(() -> {
            while (huboFalloConexion) {
                try {
                    Thread.sleep(3000); // Intento cada 3 segundos
                    Connection testCon = DriverManager.getConnection(url, user, pass);
                    
                    if (testCon != null) {
                        testCon.close();
                        // El flag se resetea en el siguiente getConexion() exitoso para evitar doble notificación
                        hiloVigilanteActivo = false;
                    }
                } catch (Exception ex) {
                    // XAMPP sigue apagado, el vigilante sigue esperando...
                }
            }
        }).start();
    }

    private void mostrarErrorControlado(String mensaje) {
        long tiempoActual = System.currentTimeMillis();
        if (tiempoActual - tiempoUltimoError > 10000) { // Spam control de 10 segundos
            utilidades.NotificadorWindows.mostrarAlerta("Falla de Conexión", mensaje, MessageType.ERROR);
            tiempoUltimoError = tiempoActual;
        }
    }
}