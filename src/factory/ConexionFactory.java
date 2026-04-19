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
    
    // Este bloque se ejecuta automáticamente al iniciar el programa
    static {
        Properties config = new Properties();
        try {
            // Intenta leer el archivo de configuración
            config.load(new FileInputStream("config.properties"));
            
            // Sobrescribe las variables con lo que encuentre en el texto
            host = config.getProperty("IP_SERVIDOR", host);
            port = config.getProperty("PUERTO", port);
            database = config.getProperty("BASE_DATOS", database);
            user = config.getProperty("USUARIO", user);
            pass = config.getProperty("PASSWORD", pass);
            
            System.out.println("✅ Configuración cargada: Apuntando a " + host);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "⚠️ No se encontró config.properties en la raíz. Usando localhost por defecto.");
        }
    }

    /**
     * Obtiene una conexión activa a la base de datos.
     * @return Connection
     */
    public Connection getConexion() {
        // Armamos la URL con las variables que cargamos
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: No se encontró el Driver de MySQL: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error crítico conectando a la base de datos en: " + url);
            JOptionPane.showMessageDialog(null, "Mensaje: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}