package edu.bbte.idde.baim2115.web.servlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//@WebFilter(urlPatterns = {"/"})
@WebFilter(urlPatterns = {"/sablonservlet"})
public class LoginFilter extends HttpFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Ellenőrizzük, hogy a felhasználó be van-e jelentkezve a session segítségével.
        if (request.getSession().getAttribute("username") == null) {
            // Ha a felhasználó nincs bejelentkezve, átirányítjuk a login oldalra.
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            // Ha a felhasználó be van jelentkezve, folytatjuk a kérés továbbítását a szűrőláncban.
            chain.doFilter(request, response);
        }
    }
}
