package org.shijh.myframework.framework.servlet;


import org.shijh.myframework.framework.bean.BeanFactory;
import org.shijh.myframework.framework.bean.ModelAndView;
import org.shijh.myframework.framework.util.Str;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/*")
public class DispatcherServlet extends HttpServlet {
    private final ServletHandler handler = BeanFactory.I.getBean(ServletHandler.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        assert handler != null;
        ModelAndView res = handler.execute(req, resp);
        if (res == null) {
            resp.sendRedirect("/error.jsp");
        } else {
            req.setAttribute("success", res.getSuccess());
            req.setAttribute("result", res.getModel());
            if (!Str.empty(res.getView())) {
                req.getRequestDispatcher(res.getView()).forward(req,resp);
            }
        }
    }
}
