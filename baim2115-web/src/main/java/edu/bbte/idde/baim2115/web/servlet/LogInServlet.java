package edu.bbte.idde.baim2115.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LogInServlet extends HttpServlet {
    private static final String EXPECTED_USERNAME = "mylittleuser";
    private static final String EXPECTED_PASSWORD = "mylittlepw";

    @Override
    public void init() throws ServletException {
        super.init();
        Engine.buildEngine(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username != null && password != null
                && username.equals(EXPECTED_USERNAME) && password.equals(EXPECTED_PASSWORD)) {
            HttpSession session = req.getSession();
            session.setAttribute("username", username);
            // Sikeres hitelesítés esetén átirányítás egy biztonságos oldalra
            resp.sendRedirect(req.getContextPath() + "/sablonservlet");
        } else {
            // Sikertelen hitelesítés esetén hibaüzenet
            resp.getWriter().println("Sikertelen bejelentkezes");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Engine.process(req, resp, "login.html", null);
    }

}
