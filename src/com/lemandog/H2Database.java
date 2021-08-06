package com.lemandog;

import org.h2.jdbc.JdbcSQLNonTransientException;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

import static com.lemandog.Main.nick;

class H2Database {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/test";
    static final String USER = "sa";
    static final String PASS = "";
    static Connection conn = null;
    static Statement stmt = null;
    public static void main() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS RECORD " +
                    "(ID INTEGER AUTO_INCREMENT, " +
                    " NICKNAME VARCHAR(255), " +
                    " SCORE LONG, " +
                    " DISTANCE LONG, " +
                    " PRIMARY KEY ( ID ))";
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception se) {
            se.printStackTrace();
        }
        finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException ignored) {}
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } //end finally try
        }
    }

    public static void setNewScore(long score, long dist) {
        String sql = "INSERT INTO RECORD (NICKNAME,SCORE,DISTANCE)" +
                "VALUES ('" + nick + "', '" + score + "', '" + dist + "')";
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Vector<String> getLocalLeaders() {
        Vector<String> result = new Vector<>(0);
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            ResultSet output = stmt.executeQuery("SELECT NICKNAME,SCORE,DISTANCE FROM RECORD ORDER BY SCORE DESC");
            while (output.next()) {
                try {
                    result.add(output.getNString("NICKNAME") + " - " + output.getString("SCORE") + " s - " + output.getString("DISTANCE"));
                } catch (JdbcSQLNonTransientException e) {
                    break;
                }
            }
            stmt.close();
            conn.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
        }
    }
    public static void ResetRecords() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql = "DROP TABLE RECORD";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        }
        catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }
    public static void CreateLeaderboardWin() {
        JFrame leaders = new JFrame("LEADERBOARD");
        leaders.setLocation(500,200);
        leaders.setLayout(new BorderLayout());
        leaders.setSize(400, 700);
        JPanel title = new JPanel();

        title.setLayout(new GridLayout(1, 1));
        JLabel localLeaderT = new JLabel("LOCAL LEADERBOARD", SwingConstants.CENTER);
        localLeaderT.setForeground(Color.WHITE);
        localLeaderT.setBackground(Color.black);
        localLeaderT.setOpaque(true);
        title.add(localLeaderT);
        leaders.setResizable(false);
        JPanel local = new JPanel();
        local.setBackground(Color.black);
        GridLayout main = new GridLayout(20, 1);
        local.setLayout(main);
        Vector<String> records = getLocalLeaders();
        JButton resetLeaders = new JButton("RESET LEADERBOARD");

        resetLeaders.setForeground(Color.WHITE);
        resetLeaders.setBackground(Color.black);
        resetLeaders.setOpaque(true);
        resetLeaders.addActionListener(e -> {
            ResetRecords(); //Delete Table
            main();         //Create empty table
        });
        for (int i = 0; i < records.size() && i < 19; i++) {
            JLabel exemplar = new JLabel((i + 1) + ") " + records.elementAt(i) + "px", SwingConstants.CENTER);
            exemplar.setForeground(Color.WHITE);
            local.add(exemplar, i);
        }
        leaders.add(title, BorderLayout.NORTH);
        leaders.add(local, BorderLayout.CENTER);
        leaders.add(resetLeaders, BorderLayout.AFTER_LAST_LINE);
        leaders.setVisible(true);
    }
}
