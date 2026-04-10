package factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionFactory {

    // Aquí está el cambio: apuntando a db_sairtech
    private final String URL = "jdbc:mysql://localhost:3306/db_sairtech?useSSL=false&serverTimezone=UTC";
    private final String USUARIO = "root"; 
    private final String PASSWORD = "";      

    public Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error: No se pudo conectar a la BD.");
            System.err.println("Motivo: " + e.getMessage());
            return null; 
        }
    }

//    public static void main(String[] args) {
//        
//        modelo.Cliente clientePrueba = new modelo.Cliente("Juan Hernandez", "98765432", "juanh@email.com");
//        
//        dao.ClienteDAO dao = new dao.ClienteDAO();
//        boolean exito = dao.insertar(clientePrueba);
//        
//        
//        if(exito){
//            System.out.println("Cliente guardado exitosamente en la base de datos!");
//        } else {
//            System.out.println("Ocurrio un error");
//        }
//    }
}