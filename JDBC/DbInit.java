package JDBC;

import java.io.File;

public class DbInit {

  public static void main(String args[]) {

    String PATH = String.format("/virtual/%s/URLShortner", System.getProperty("user.name"));

    File directory = new File(PATH);
    if (!directory.exists()) {
      directory.mkdirs();
    }

    try {

      DBHandler dbHandler = new DBHandler(String.format("%s/urlshortner.db", PATH));

      System.out.println("short = " + "gg" + " long = " + dbHandler.findURL("gg"));
      System.out.println(String.format("DB Rows count = %d", dbHandler.dbCount()));
      dbHandler.saveURL("gg", "http://www.bing.com");
      System.out.println("short = " + "gg" + " long = " + dbHandler.findURL("gg"));
      System.out.println(String.format("DB Rows count = %d", dbHandler.dbCount()));

      dbHandler.getConnection().close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Operation done successfully");
  }
}
