package org.example.Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import org.example.Db.DbException;


public class Db {
private static Connection conn = null;

    public static  Connection getConection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection("jdbc:mysql://:3306/hardware_security", "root", "180118");
            }
                catch (SQLException e) {
                    System.out.println("Erro ao conectar com o banco de dados: " + e.getMessage());
                }


}
return conn;
    }

    public static void cloneConection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }


}
