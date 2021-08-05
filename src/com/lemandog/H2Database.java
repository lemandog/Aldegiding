package com.lemandog;

import org.h2.jdbc.JdbcSQLNonTransientException;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.NoSuchElementException;
import java.util.Vector;

import static com.lemandog.Main.nick;

class H2Database {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/test";
    static final String USER = "sa";
    static final String PASS = "";
    static Connection conn = null;
    static Statement stmt = null;
    static JLabel state = new JLabel("CONNECTION STATE", SwingConstants.CENTER);
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
            //finally block used to close resources
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
            System.out.println("Inserted " + nick + " " + score + " " + dist);
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
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void setGlobalRecord(Vector<String> record) {

    }
    public static void CreateLeaderboardWin() {
        JFrame leaders = new JFrame("Leaders");
        leaders.setLayout(new BorderLayout());
        leaders.setSize(500, 800);
        JPanel title = new JPanel();
        JPanel control = new JPanel();

        title.setLayout(new GridLayout(1, 2));
        JLabel localLeaderT = new JLabel("LOCAL LEADERBOARD");
        JLabel globalLeaderT = new JLabel("GLOBAL LEADERBOARD");
        JButton post = new JButton("Post best result");
        post.addActionListener((event)->{
            try{
                GlobalLeaderboard.post(getLocalLeaders().firstElement());
        }   catch (NoSuchElementException e ){
                state.setText("There is nothing to send to send");
            }});
        JButton resetter = new JButton("Reset records");
        resetter.addActionListener(e -> {ResetRecords(); main();});
        JButton connectTest = new JButton("Test connection");
        connectTest.addActionListener((e -> {state.setText(GlobalLeaderboard.test());}));
        JButton update = new JButton("Update global leaderboard");
        update.addActionListener((e -> setGlobalRecord(GlobalLeaderboard.getGlobalLeaders())));
        control.setLayout(new GridLayout(2,2));

        control.add(post);
        control.add(resetter);
        control.add(connectTest);
        control.add(update);
        title.add(localLeaderT);
        title.add(globalLeaderT);

        leaders.setResizable(false);
        JPanel local = new JPanel();
        local.setBackground(Color.GRAY);
        GridLayout main = new GridLayout(10, 1);
        local.setLayout(main);
        Vector<String> records = getLocalLeaders();
        for (int i = 0; i < records.size(); i++) {
            if (i > 9) {
                break;
            }
            local.add(new JLabel((i + 1) + ") " + records.elementAt(i) + "px", SwingConstants.CENTER), i);
        }
        JPanel global = new JPanel();
        global.setBackground(Color.PINK);
        global.setLayout(main);
        JPanel scores = new JPanel();
        scores.setLayout(new GridLayout(1, 2));
        scores.add(local);
        scores.add(global);
        JPanel compose = new JPanel();
        compose.setLayout(new GridLayout(2,1));
        compose.add(control);
        compose.add(state);

        leaders.add(title, BorderLayout.NORTH);
        leaders.add(scores, BorderLayout.CENTER);
        leaders.add(compose, BorderLayout.AFTER_LAST_LINE);
        //compose.setLayout(new SpringLayout());
        leaders.setVisible(true);
    }
}
