package top.wuhaojie.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import top.wuhaojie.constant.Constant;
import top.wuhaojie.entities.PointItem;
import top.wuhaojie.utils.Converter;
import top.wuhaojie.utils.DataDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Author: wuhaojie
 * E-mail: w19961009@126.com
 * Date: 2016/4/20 20:52
 * Version: 1.0
 */
@WebServlet(name = "UploadServlet")
public class UploadServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setCharacterEncoding("utf-8");

        ReceivingFile receivingFile = new ReceivingFile(request).invoke();
        ServletFileUpload servletFileUpload = receivingFile.getServletFileUpload();
        HttpSession session = receivingFile.getSession();

        writeFile(request, servletFileUpload, session);
    }

    private void writeFile(HttpServletRequest request, ServletFileUpload servletFileUpload, HttpSession session) {
        try {
            String fileName = null;
            List<FileItem> list = servletFileUpload.parseRequest(request);
            for (FileItem item : list) {
                if (!item.isFormField()) {
                    if (item.getName() != null && !item.getName().isEmpty()) {
                        session.setAttribute(Constant.ATTR_FILE_NAME, item.getName());
                        fileName = item.getName();
                        System.out.println(item.getName());
                    }
                    InputStream is = item.getInputStream();
                    File file = new File(Constant.FILE_PATH + fileName);
                    if (!file.exists()) {
                        new File(Constant.FILE_PATH).mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    System.out.println("写入文件完成！");
                    session.setAttribute(Constant.ATTR_UPLOADING, Constant.ATTR_FALSE);
                    dataOperation(Constant.FILE_PATH, fileName, session);
                    is.close();
                    fos.close();
                }
                item.delete();
                System.out.println("文件上传成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dataOperation(String filePath, String fileName, HttpSession session) {
        System.out.println("文件路径" + filePath + fileName);
        File srcFile = new File(filePath + fileName);
        try {
            Converter converter = Converter.getConverter();
            List<PointItem> pointItemList = converter.readXml(srcFile);
            System.out.println("开始");
            converter.setOnConverterChangedListener(() -> System.out.println("转换完成"));
            converter.xml2Gpx(srcFile, new File(filePath + fileName.substring(0, fileName.length() - 3) + "gpx"));
            converter.xml2Kml(srcFile, new File(filePath + fileName.substring(0, fileName.length() - 3) + "kml"));

            DataDao dataDao = DataDao.getInstance();
            dataDao.setOnStatusChangedListener(new DataDao.OnStatusChangedListener() {
                                                   @Override
                                                   public void onEventCompleted(long deltaTime, long eventMount) {
                                                       System.out.println("插入完成, 耗时" + deltaTime + ", 数量" + eventMount);
                                                   }

                                                   @Override
                                                   public void onProgress(long curr, long size) {
                                                       session.setAttribute("ConvertMount", curr + "#" + size);
                                                   }
                                               }
            );
            dataDao.insertNewPointList(pointItemList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private class ReceivingFile {
        private HttpServletRequest request;
        private HttpSession session;
        private ServletFileUpload servletFileUpload;

        public ReceivingFile(HttpServletRequest request) {
            this.request = request;
        }

        public HttpSession getSession() {
            return session;
        }

        public ServletFileUpload getServletFileUpload() {
            return servletFileUpload;
        }

        public ReceivingFile invoke() {
            DiskFileItemFactory factory = new DiskFileItemFactory();

            session = request.getSession();

            servletFileUpload = new ServletFileUpload(factory);

            servletFileUpload.setFileSizeMax(100 * 1024 * 1024);


            servletFileUpload.setProgressListener(new ProgressListener() {
                Long beginTime = System.currentTimeMillis();

                public void update(long bytesRead, long contentLength, int items) {

//                    BigDecimal br = new BigDecimal(bytesRead).divide(
//                            new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
//                    BigDecimal cl = new BigDecimal(contentLength).divide(
//                            new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP);
//                    System.out.print("当前读取的是第" + items + "个上传项,总大小" + cl
//                            + "KB,已经读取" + br + "KB");
//                    // 剩余字节数
//                    BigDecimal ll = cl.subtract(br);
//                    System.out.print("剩余" + ll + "KB");
//                    // 上传百分比
//                    BigDecimal per = br.multiply(new BigDecimal(100)).divide(cl, 2,
//                            BigDecimal.ROUND_HALF_UP);
//                    System.out.print("已经完成" + per + "%");
//                    // 上传用时
//                    Long nowTime = System.currentTimeMillis();
//                    Long useTime = (nowTime - beginTime) / 1000;
//                    System.out.print("已经用时" + useTime + "秒");
//                    // 上传速度
//                    BigDecimal speed = new BigDecimal(0);
//                    if (useTime != 0) {
//                        speed = br.divide(new BigDecimal(useTime), 2,
//                                BigDecimal.ROUND_HALF_UP);
//                    }
//                    System.out.print("上传速度为" + speed + "KB/S");
//                    // 大致剩余时间
//                    BigDecimal leftTime = new BigDecimal(0);
//                    if (!speed.equals(new BigDecimal(0))) {
//                        leftTime = ll.divide(speed, 0, BigDecimal.ROUND_HALF_UP);
//                    }
//                    System.out.print("大致剩余时间为" + leftTime + "秒");
//
//                    System.out.println();
//                    session.setAttribute("percent", per + "%");

                    session.setAttribute(Constant.ATTR_UPLOADING, Constant.ATTR_TRUE);

                }


            });
            return this;
        }
    }
}
