package com.biometric.actions;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.bio.db.GetDBConnection;
import javax.servlet.annotation.WebServlet;

@WebServlet("/VerifySKAction")
public class VerifySKAction extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String sk = request.getParameter("sk");

        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("consumer");
 // consumer username

        try {
            Connection con = GetDBConnection.getDbConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT name, image FROM ownerimages WHERE id=? AND sk=?"
            );
            ps.setInt(1, id);
            ps.setString(2, sk);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String fileName = rs.getString("name");
                Blob image = rs.getBlob("image");

                // ✅ SEND FILE AS-IS (NO CRYPTO)
                response.setContentType("application/octet-stream");
                response.setHeader(
                        "Content-Disposition",
                        "attachment; filename=\"" + fileName + "\""
                );

                InputStream is = image.getBinaryStream();
                OutputStream os = response.getOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                is.close();
                os.flush();
                os.close();

                // ✅ LOG TRANSACTION (UNCHANGED)
                PreparedStatement ps2 = con.prepareStatement(
    "INSERT INTO transaction(user, name, sk, task, dt) VALUES(?,?,?,?,NOW())"
);
ps2.setString(1, user);      // NOW consumer name is stored
ps2.setString(2, fileName);
ps2.setString(3, sk);
ps2.setString(4, "Download");
ps2.executeUpdate();

                // ✅ SUCCESS MESSAGE
                session.setAttribute("msg", "File downloaded successfully");

            } else {
                response.sendRedirect("Invalidkey.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
