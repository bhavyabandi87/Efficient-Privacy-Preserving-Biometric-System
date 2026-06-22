package com.biometric.actions;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.bio.db.GetDBConnection;
import javax.servlet.annotation.WebServlet;

@WebServlet("/DownloadOriginalAction")
public class DownloadOriginalAction extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int imageId = Integer.parseInt(request.getParameter("id"));

        try {
            Connection con = GetDBConnection.getDbConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT file_name, original_file FROM ownerimages_original WHERE image_id=?"
            );
            ps.setInt(1, imageId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String fileName = rs.getString("file_name");
                Blob fileBlob = rs.getBlob("original_file");

                response.setContentType("application/octet-stream");
                response.setHeader(
                        "Content-Disposition",
                        "attachment; filename=\"" + fileName + "\""
                );

                InputStream is = fileBlob.getBinaryStream();
                OutputStream os = response.getOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                is.close();
                os.flush();
                os.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
