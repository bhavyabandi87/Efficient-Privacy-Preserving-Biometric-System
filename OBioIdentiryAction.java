/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.biometric.actions;

import com.bio.db.GetDBConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author welcome
 */
@MultipartConfig
public class OBioIdentiryAction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Part filepart = request.getPart("pic2");
        String thbname = filepart.getSubmittedFileName();
        try {
            Connection con = GetDBConnection.getDbConnection();
            String sql1 = "SELECT * FROM owner where  thumbname='" + thbname + "' ";
            Statement stmt1 = con.createStatement();
            ResultSet rs1 = stmt1.executeQuery(sql1);
            if (rs1.next() == true) {

                response.sendRedirect("ownerMain.jsp");

            }else{
            out.println("Finger Print Mismatch, Please Provide Correct Finger Print!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
