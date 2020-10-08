import java.sql.*;
import java.io.File;

public class SQLiteJDBC {

   public static void main( String args[] ) {
      Connection c = null;
      Statement stmt = null;
      String PATH = String.format("/virtual/%s/URLShortner", System.getProperty("user.name"));
      File directory = new File(PATH);
      if (! directory.exists()){
         directory.mkdirs();
      }
      
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection(String.format("jdbc:sqlite:%s/urlshortner.db", PATH));
         c.setAutoCommit(false);
         System.out.println("Opened database successfully");

         stmt = c.createStatement();
         String sql;
         try {
            sql = "CREATE TABLE URLSHORTNER " +
                        "(SHORT TEXT PRIMARY KEY     NOT NULL," +
                        " LONG           TEXT    NOT NULL)"; 
            stmt.executeUpdate(sql);
         } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }
         
         try {
            sql = "INSERT INTO URLSHORTNER (SHORT,LONG) " +
                  "VALUES ('gg', 'http://www.google.com' );"; 
            stmt.executeUpdate(sql);
         } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         }

         stmt = c.createStatement();
         
         ResultSet rs = stmt.executeQuery( "SELECT * FROM URLSHORTNER;" );
         
         while ( rs.next() ) {
            String shortURL   = rs.getString("short");
            String longURL    = rs.getString("long");
            
            System.out.println( "short = " + shortURL );
            System.out.println( "long  = " + longURL );
            System.out.println();
         }
         rs.close();
         stmt.close();
         c.close();
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         System.exit(0);
      }
      System.out.println("Operation done successfully");
   }
}