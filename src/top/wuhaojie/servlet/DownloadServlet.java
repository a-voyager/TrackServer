package top.wuhaojie.servlet;

import top.wuhaojie.constant.Constant;
import top.wuhaojie.utils.ConfigUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Author: wuhaojie
 * E-mail: w19961009@126.com
 * Date: 2016/5/6 12:10
 * Version: 1.0
 */
@WebServlet(name = "DownloadServlet")
public class DownloadServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!Constant.ATTR_TRUE.equals(ConfigUtils.readConfig(Constant.CONFIG_FINISHED))) {
            response.sendError(0, "NOT PREPARED FILE");
            response.setHeader("refresh", "3;url=" + request.getContextPath() + "/upload.jsp");
        }


        String filePostfix = ".kml";
        switch (request.getHeader(Constant.FILE_TYPE_HEADER)) {
            case Constant.KML:
                filePostfix = ".kml";
                break;
            case Constant.XML:
                filePostfix = ".xml";
                break;
            case Constant.GPX:
                filePostfix = ".gpx";
                break;
        }


        ServletOutputStream outputStream = response.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        String filePath = ConfigUtils.readConfig(Constant.CONFIG_FINISHED_FILE_PATH_WITH_OUT_POSTFIX) + filePostfix;
        File file = new File(filePath);
        String fileName = file.getName();
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = bis.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
        }

        bos.flush();
        bos.close();
        bis.close();


    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
