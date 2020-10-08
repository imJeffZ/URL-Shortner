package db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    /**
     * Connect to a sample database
     */

    private static final String DATABASE = "jdbc:sqlite:~/workspace/a1/db/URLTable.db";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        connect();
    }

    public static void createNewDatabase(String fileName) {

        String url = DATABASE;
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void connect() {  
        Connection conn = null;  
        try {  
            // db parameters  
            String url = DATABASE;  
            // create a connection to the database  
            conn = DriverManager.getConnection(url);  
              
            System.out.println("Connection to SQLite has been established.");  
              
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        } finally {  
            try {  
                if (conn != null) {  
                    conn.close();  
                }  
            } catch (SQLException ex) {  
                System.out.println(ex.getMessage());  
            }  
        }  
    }  

}  