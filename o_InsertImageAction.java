package com.biometric.actions;

import com.bio.db.GetDBConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.bouncycastle.util.encoders.Base64;

@MultipartConfig
public class o_InsertImageAction extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter out = response.getWriter();

        Connection con = null;
        PreparedStatement ps = null;

        HttpSession session = request.getSession();

        String title = null, name = null, desc = null, mac = null, key = null;

        try {
            // ===== READ FORM DATA =====
            title = new String(Base64.encode(request.getParameter("title").getBytes()));
            name  = new String(Base64.encode(request.getParameter("name").getBytes()));
            desc  = new String(Base64.encode(request.getParameter("desc").getBytes()));

            mac = request.getParameter("mac");
            key = request.getParameter("key");

            Part filePart = request.getPart("pic");

            con = GetDBConnection.getDbConnection();

            // ===== DATE & TIME =====
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
            Date now = new Date();
            String dt = sdfDate.format(now) + "   " + sdfTime.format(now);

            int rank = 0;
            String task = "Upload";
            String owner = (String) session.getAttribute("owner");

            // ===== TRANSACTION LOG =====
            String strQuery2 = "insert into transaction(user,name,sk,task,dt) values(?,?,?,?,?)";
            ps = con.prepareStatement(strQuery2);
            ps.setString(1, owner);
            ps.setString(2, name);
            ps.setString(3, key);
            ps.setString(4, task);
            ps.setString(5, dt);
            ps.executeUpdate();

            // ===== METADATA =====
            String strQuery3 = "insert into matadata(owner,title,name,digitalsign) values(?,?,?,?)";
            ps = con.prepareStatement(strQuery3);
            ps.setString(1, owner);
            ps.setString(2, title);
            ps.setString(3, name);
            ps.setString(4, mac);
            ps.executeUpdate();

            // ===== OWNERIMAGES (ENCRYPTED FILE) =====
            PreparedStatement ps1 = con.prepareStatement(
                "insert into ownerimages(title,name,owner,description,digitalsign,sk,dt,image,rank) values(?,?,?,?,?,?,?,?,?)",
                PreparedStatement.RETURN_GENERATED_KEYS
            );

            ps1.setString(1, title);
            ps1.setString(2, name);
            ps1.setString(3, owner);
            ps1.setString(4, desc);
            ps1.setString(5, mac);
            ps1.setString(6, key);
            ps1.setString(7, dt);
            ps1.setBinaryStream(8, filePart.getInputStream());
            ps1.setInt(9, rank);
            ps1.executeUpdate();

            // ===== GET GENERATED IMAGE ID =====
            int imageId = 0;
            ResultSet rsKey = ps1.getGeneratedKeys();
            if (rsKey.next()) {
                imageId = rsKey.getInt(1);
            }

            // ===== SERVERIMAGES =====
            ps = con.prepareStatement(
                "insert into serverimages(title,name,owner,description,digitalsign,sk,dt,rank) values(?,?,?,?,?,?,?,?)"
            );
            ps.setString(1, title);
            ps.setString(2, name);
            ps.setString(3, owner);
            ps.setString(4, desc);
            ps.setString(5, mac);
            ps.setString(6, key);
            ps.setString(7, dt);
            ps.setInt(8, rank);
            ps.executeUpdate();

            // ===== STORE ORIGINAL FILE (NEW ADDITION) =====
            String fileName = filePart.getSubmittedFileName();
            InputStream originalFileInputStream = filePart.getInputStream();

            PreparedStatement psOrg = con.prepareStatement(
                "INSERT INTO ownerimages_original(image_id, file_name, original_file) VALUES(?,?,?)"
            );
            psOrg.setInt(1, imageId);
            psOrg.setString(2, fileName);
            psOrg.setBlob(3, originalFileInputStream);
            psOrg.executeUpdate();

            out.println("Image Uploaded Successfully");

        } catch (Exception e) {
            out.println("Error during image upload");
            e.printStackTrace();
        }
    }
}
