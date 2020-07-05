package com.harunaga.games.common.sql;

/*
 * common.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * 
 * @author Harunaga.
 * 
 */

/*
 * This class for take the common user-name and password.
 */
public class MySQL {

    public static final Logger log = Logger.getLogger(MySQL.class);
    public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
    public static String USER = "root";
    public static String PASSWORD = "";

    /**
     * this will connect to DBMS via user "root" and password ""
     * @return a connection with the database
     */
    public static Connection getConnection(String database) {
        return getConnection(database, USER, PASSWORD);
    }

    public static Connection getConnection(String database, String user, String password) {
        String url = "jdbc:mysql://localhost:3306/";
        try {
            //Class.forName(DEFAULT_DRIVER);
            return DriverManager.getConnection(url + database, user, password);
        } catch (Exception e) {
            log.error("Error while connect to DBMS", e);
            return null;
        }
    }
    //-----------------------Test----------------------
//
//    public static void main(String[] args) {
//        BasicConfigurator.configure();
//        Connection conn = MySQL.getConnection("tetris");
//        if (conn == null) {
//            System.out.println("error while connect");
//            return;
//        }
//        try {
//            String username = "harunaga";
//            Statement stm = conn.createStatement();
//            String sql = "select password from client where username='" + username + "'";
//            stm = conn.createStatement();
//            ResultSet rs = stm.executeQuery(sql);
//            if (rs.next()) {
//                String password = rs.getString(1);
//                System.out.println("Pass: " + password);
//                stm.close();
//            }
//
//        } catch (SQLException ex) {
//            log.error("error while create statement", ex);
//        }
//
//    }
}
