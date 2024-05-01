package edu.bbte.idde.baim2115.web.servlet;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import edu.bbte.idde.baim2115.backend.repository.AbstractDaoFactory;
import edu.bbte.idde.baim2115.backend.repository.IngatlanDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//@WebServlet("/")
@WebServlet("/sablonservlet")
public class SablonMotorServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SablonMotorServlet.class);
    // private final IngatlanDaoImplementation ingatlanMuveletek = IngatlanDaoImplementation.getInstance();
    private final IngatlanDao ingatlanMuveletek = AbstractDaoFactory.getInstance().getAbstractIngatlanDao();

    @Override
    public void init() throws ServletException {
        super.init();
        Engine.buildEngine(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        LOG.info("get / SABLONMOTOR");
        resp.setCharacterEncoding("UTF-8");
        Collection<Ingatlan> ingatlanLista1 = ingatlanMuveletek.findAll();
        List<Ingatlan> ingatlanLista = new ArrayList<>(ingatlanLista1);
        // model.put(ingatlanLista);
        LOG.info("LISTA: " + ingatlanLista);
        Engine.process(req, resp, "index.html", ingatlanLista);
    }
}


