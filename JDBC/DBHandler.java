package JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.DriverManager;
import java.sql.Statement;

public class DBHandler implements DBHandlerInterface {

    private Connection conn;

    private static final String PATH = String.format("/virtual/%s/URLShortner", System.getProperty("user.name"));
    Statement stmt;

    static Connection connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Opened database successfully");
        return conn;
    }

    public DBHandler() {
        this.conn = connect(String.format("jdbc:sqlite:%s/urlshortner.db", PATH));

        try {
            this.stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        String sql;
        try {
            sql = "CREATE TABLE IF NOT EXISTS URLSHORTNER " + "(SHORT TEXT PRIMARY KEY     NOT NULL,"
                    + " LONG           TEXT    NOT NULL)";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        } finally {
            System.out.println("Table initialization complete.");
        }

        try {
            sql = "INSERT INTO URLSHORTNER (SHORT,LONG) " + "VALUES ('gg', 'http://www.google.com' );";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    @Override
    public void saveURL(String shortURL, String longURL) throws SQLException {

        String sql = "REPLACE INTO URLSHORTNER (SHORT,LONG) VALUES(?,?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, shortURL);
        pstmt.setString(2, longURL);

        pstmt.executeUpdate();

    }

    @Override
    public String findURL(String shortURL) throws SQLException {
        String longURL = null;
        String sql = "SELECT short, long " + "FROM URLSHORTNER WHERE short = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, shortURL);

        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            longURL = rs.getString("long");
        }
        rs.close();
        pstmt.close();
        return longURL;
    }

    @Override
    public int dbCount() throws SQLException {
        String sql = "SELECT COUNT(short) AS count FROM URLSHORTNER;";
        int count = -1;
        ResultSet rs = stmt.executeQuery(sql);

        if (rs.next()) {
            count = rs.getInt("count");
        }

        return count;
    }

    public Connection getConnection() {
        return this.conn;
    }

}
