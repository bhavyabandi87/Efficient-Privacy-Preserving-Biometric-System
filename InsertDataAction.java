package com.biometric.actions;

import com.bio.db.GetDBConnection;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.bouncycastle.util.encoders.Base64;

@MultipartConfig
public class InsertDataAction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        String uname = request.getParameter("userid");
        String pass = request.getParameter("pass");
        String email = request.getParameter("email");
        String mno = request.getParameter("mobile");
        String addr = request.getParameter("address");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String pincode = request.getParameter("pincode");

        Part filepart = request.getPart("pic");
        Part fileBio = request.getPart("pic2");

        try {
            Connection con = GetDBConnection.getDbConnection();

            /* ========= BASE64 ENCODING FIX ========= */
            //String encUname = new String(Base64.encode(uname.getBytes()));
            /* ====================================== */

            String status = "Waiting";

            PreparedStatement ps = con.prepareStatement(
                "insert into consumer(username,password,email,mobile,address,dob,gender,pincode,status,image,thumbimage,thumbname) values(?,?,?,?,?,?,?,?,?,?,?,?)"
            );

            ps.setString(1, uname);   // ✅ encoded username
            ps.setString(2, pass);
            ps.setString(3, email);
            ps.setString(4, mno);
            ps.setString(5, addr);
            ps.setString(6, dob);
            ps.setString(7, gender);
            ps.setString(8, pincode);
            ps.setString(9, status);

            ps.setBinaryStream(10, filepart.getInputStream());
            ps.setBinaryStream(11, fileBio.getInputStream());
            ps.setString(12, fileBio.getSubmittedFileName());

            int x = ps.executeUpdate();
            if (x > 0) {
                out.print("Registered Successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("Registration Failed");
        }
    }
}
