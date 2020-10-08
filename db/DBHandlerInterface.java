package db;

public interface DBHandlerInterface {
  public void saveURL(String shortURL, String longURL);

  public String findURL(String shortURL);

  public int dbCount();
}