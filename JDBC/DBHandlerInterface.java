package JDBC;

import java.sql.SQLException;

public interface DBHandlerInterface {
  public void saveURL(String shortURL, String longURL) throws SQLException;

  public String findURL(String shortURL) throws SQLException;

  public int dbCount() throws SQLException;
}