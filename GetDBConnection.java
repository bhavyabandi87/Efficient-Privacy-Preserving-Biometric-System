/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bio.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author welcome
 */
public class GetDBConnection {

    public static Connection con = null;

    public static Connection getDbConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Bio_Indentification", "root", "root");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public static Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
