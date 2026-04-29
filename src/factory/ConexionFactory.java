package factory;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Factory para la conexión a la base de datos
 * Proyecto: SairTech - Sistema de Control de Equipos
 */
public class ConexionFactory {
    
    // Variables por defecto en caso de que borres el archivo sin querer
    private static String host = "localhost";
    private static String port = "3306";
    private static String database = "db_sairtech";
    private static String user = "root";
    private static String pass = "";
    
    // --- NUEVO: Temporizador para evitar la "tormenta de popups" ---
    private static long tiempoUltimoError = 0;
    
    // Este bloque se ejecuta automáticamente al iniciar el programa
    static {
        Properties config = new Properties();
        try {
            config.load(new FileInputStream("config.properties"));
            
            host = config.getProperty("IP_SERVIDOR", host);
            port = config.getProperty("PUERTO", port);
            database = config.getProperty("BASE_DATOS", database);
            user = config.getProperty("USUARIO", user);
            pass = config.getProperty("PASSWORD", pass);
            
        } catch (Exception e) {
            // Este es el único error que dejamos silencioso porque el usuario no necesita saber
            // que se usó la configuración por defecto de localhost.
        }
    }

    /**
     * Obtiene una conexión activa a la base de datos con protecciones anti-cuelgues.
     * @return Connection
     */
    public Connection getConexion() {
        // connectTimeout=3000 : Aborta en 3 segundos si el servidor no responde.
        // socketTimeout=5000  : Cancela consultas trabadas de más de 5 segundos.
        // autoReconnect=true  : Reconecta sola ante micro-cortes.
        String parametrosSeguridad = "?connectTimeout=3000&socketTimeout=5000&autoReconnect=true";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + parametrosSeguridad;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);
            
        } catch (ClassNotFoundException e) {
            mostrarErrorControlado("Error Crítico: No se encontró el Driver de MySQL.\nComuníquese con soporte técnico.");
            throw new RuntimeException(e);
        } catch (SQLException e) {
            mostrarErrorControlado("⚠️ No se pudo conectar al servidor de base de datos.\nRevise su conexión de red o verifique si el servidor está encendido.\n\nDetalle: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Muestra un mensaje de error visual, pero evita que salgan 50 ventanas al mismo tiempo.
     */
    private void mostrarErrorControlado(String mensaje) {
        long tiempoActual = System.currentTimeMillis();
        // Si han pasado más de 5000 milisegundos (5 segundos) desde el último error, mostramos el popup
        if (tiempoActual - tiempoUltimoError > 5000) {
            JOptionPane.showMessageDialog(null, mensaje, "Falla de Conexión", JOptionPane.ERROR_MESSAGE);
            tiempoUltimoError = tiempoActual;
        }
    }
}