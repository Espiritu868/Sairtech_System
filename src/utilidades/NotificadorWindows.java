package utilidades;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class NotificadorWindows {

    public static void mostrarAlerta(String titulo, String mensaje, MessageType tipo) {
        // Verificamos si el Windows/Sistema Operativo soporta estas notificaciones
        if (!SystemTray.isSupported()) {
            System.err.println("Este sistema operativo no soporta notificaciones nativas.");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            
            // Cargamos tu logo (Asegúrate de que esta ruta sea la misma que usas en tu VentanaPrincipal)
            java.net.URL rutaLogo = NotificadorWindows.class.getResource("/image/logo.png");
            if (rutaLogo == null) {
                System.err.println("No se encontró el logo para la notificación.");
                return;
            }
            
            Image image = Toolkit.getDefaultToolkit().createImage(rutaLogo);
            
            TrayIcon trayIcon = new TrayIcon(image, "SairTech");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("SairTech - Sistema de Gestión");
            
            tray.add(trayIcon);
            
            // ¡Lanzamos la notificación de Windows!
            trayIcon.displayMessage(titulo, mensaje, tipo);
            
            // Hilo fantasma: Espera 5 segundos y borra el icono de la barra para no dejar basura
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    tray.remove(trayIcon);
                } catch (InterruptedException e) { }
            }).start();

        } catch (AWTException e) {
            System.err.println("Error al lanzar notificación de Windows: " + e.getMessage());
        }
    }
}