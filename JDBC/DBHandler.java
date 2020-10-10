package JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.DriverManager;
import java.sql.Statement;

public class DBHandler implements DBHandlerInterface {

    private Connection conn;

    public static final String PATH = String.format("/virtual/%s/URLShortner", System.getProperty("user.name"));
    Statement stmt;

    public static Connection connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(true);
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
            sql = "INSERT INTO URLSHORTNER (SHORT,LONG) " + "VALUES ('ali', 'http://ali-raza.me' );";
            stmt.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    /**
     * find a shortURL return the corresponding long.
     *
     * @param shortURL
     * @param longURL
     * @throws SQLException
     */
    @Override
    public void saveURL(String shortURL, String longURL) throws SQLException {

        String sql = "REPLACE INTO URLSHORTNER (SHORT,LONG) VALUES(?,?)";

        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, shortURL);
        pstmt.setString(2, longURL);

        pstmt.executeUpdate();

    }

    /**
     * find a shortURL return the corresponding long.
     *
     * @param shortURL
     * @throws SQLException
     * @return corresponding long
     */
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

    /**
     * Get the short count in the DB.
     *
     * @throws SQLException
     */
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

    /**
     * Delete a shortURL
     *
     * @param shortURL
     * @throws SQLException
     */
    @Override
    public void delete(String shortURL) throws SQLException {
        String sql = "DELETE FROM URLSHORTNER WHERE short = ?";

        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, shortURL);
        pstmt.executeUpdate();
    }

    /**
     * Get the db connection
     *
     */
    public Connection getConnection() {
        return this.conn;
    }

}
