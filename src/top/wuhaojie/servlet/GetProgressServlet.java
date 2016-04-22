package top.wuhaojie.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Author: wuhaojie
 * E-mail: w19961009@126.com
 * Date: 2016/4/22 15:30
 * Version: 1.0
 */
@WebServlet(name = "GetProgressServlet")
public class GetProgressServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String percent = (String) session.getAttribute("percent");
        PrintWriter writer = response.getWriter();
        writer.print(percent);
        writer.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
