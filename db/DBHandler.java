package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHandler {

  static class DBConn {

    /**
     * Connect to database
     *
     * @return the Connection object
     */
    static Connection connect(String url) {
      // SQLite connection string
      Connection conn = null;
      try {
        conn = DriverManager.getConnection(url);
      } catch (SQLException e) {
        System.out.println(e.getMessage());
      }
      System.out.println("Opened database successfully");
      return conn;
    }
  }

  public void saveURL(String shortURL, String longURL) {
    // TODO Auto-generated method stub

  }

  public String findURL(String shortURL) {
    String sql = "SELECT short " + "FROM URLSHORTNER WHERE short = ?";

    // PreparedStatement pstmt  = conn.prepareStatement(sql)

    return null;
  }

  public int dbCount() {
    // TODO Auto-generated method stub
    return 0;
  }

  public static void main(String args[]) {

    Statement stmt = null;
    String PATH = String.format("/virtual/%s/URLShortner", System.getProperty("user.name"));

    File directory = new File(PATH);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DBConn.connect(String.format("jdbc:sqlite:%s/urlshortner.db", PATH));
      conn.setAutoCommit(false);

      stmt = conn.createStatement();
      String sql;
      try {
        sql = "CREATE TABLE URLSHORTNER " + "(SHORT TEXT PRIMARY KEY     NOT NULL,"
            + " LONG           TEXT    NOT NULL)";
        stmt.executeUpdate(sql);
      } catch (Exception e) {
      }

      try {
        sql = "INSERT INTO URLSHORTNER (SHORT,LONG) " + "VALUES ('gg', 'http://www.google.com' );";
        stmt.executeUpdate(sql);
      } catch (Exception e) {
      }

      stmt = conn.createStatement();

      ResultSet rs = stmt.executeQuery("SELECT * FROM URLSHORTNER;");

      while (rs.next()) {
        String shortURL = rs.getString("short");
        String longURL = rs.getString("long");

        System.out.println(String.format("short = %s   long = %s", shortURL, longURL));
        System.out.println();
      }
      rs.close();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Operation done successfully");
  }
}