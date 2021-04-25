package org.shijh.myframework.framework.servlet;


import org.shijh.myframework.framework.ModelAndView;
import org.shijh.myframework.framework.util.Str;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/*")
public class DispatcherServlet extends HttpServlet {
    private final ServletHandler handler = new ServletHandler();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURL().toString();
        ModelAndView res = handler.execute(url, req.getParameterMap());
        HttpSession session = req.getSession();
        if (res == null) {
            session.setAttribute("success", false);
            resp.sendRedirect("/error.jsp");
        } else {
            session.setAttribute("success", res.getSuccess());
            session.setAttribute("result", res.getModel());
            if (!Str.empty(res.getView())) {
                resp.sendRedirect(res.getView());
            }
        }
    }
}
