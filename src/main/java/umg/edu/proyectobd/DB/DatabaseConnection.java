package umg.edu.proyectobd.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://192.168.92.131:1433;databaseName=Inventario;encrypt=false";
    private static final String USER = "steven";
    private static final String PASSWORD = "msqr0101";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}