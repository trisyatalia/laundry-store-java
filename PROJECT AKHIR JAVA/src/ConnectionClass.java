/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package laundryproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author Trisy
 */
public class ConnectionClass {
    private static String ClassName = "ConnectionClass";
    
    public static Connection getConnection() {
        
        Connection connection = null;
        String driver = "org.mariadb.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/laundry";
        String user = "root";
        String password = "1234";
        if (connection == null) {
            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException | SQLException error) {
                System.err.println("Terjadi kesalahan pada class " + ClassName + ", fungsi getConnection \n Detail : " + error);
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan pada database,silahkan cek pengaturan database anda");
                System.exit(0);
            }

        }
        return connection;
    }
    
    
     public static void main(String[] args) {
       
         
     }
}

