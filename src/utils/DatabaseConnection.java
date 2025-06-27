package utils;

import java.sql.*;

public class DatabaseConnection {
    private static Connection connetion;
    
    public static Connection getConnection(){
        String url = "jdbc:sqlserver://localhost:1433;databaseName=qlch_2025;encrypt=true;trustServerCertificate=true;";
        String username = "sa";
        String password = "hoi04012005";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connetion = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connetion;
    }
    
    public static void closeConnection(){
        try {
            connetion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        DatabaseConnection.getConnection();
    }
    
}
