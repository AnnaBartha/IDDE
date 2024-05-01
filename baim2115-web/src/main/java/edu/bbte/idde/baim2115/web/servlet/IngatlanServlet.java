package edu.bbte.idde.baim2115.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import edu.bbte.idde.baim2115.backend.repository.AbstractDaoFactory;
import edu.bbte.idde.baim2115.backend.repository.IngatlanDao;
import edu.bbte.idde.baim2115.backend.repository.RepositoryExeption;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Slf4j
@WebServlet("/ingatlan")
public class IngatlanServlet extends HttpServlet {

    // private final IngatlanDaoImplementation ingatlanMuveletek = IngatlanDaoImplementation.getInstance();
    private final IngatlanDao ingatlanMuveletek = AbstractDaoFactory.getInstance().getAbstractIngatlanDao();

    ObjectMapper objectMapper = new ObjectMapper();

    private void hibafuggveny(HttpServletResponse resp, int statusCode, String errorMessage) throws IOException {
        // ha nincs ilyen ingatlan
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        // Objektum létrehozása az üzenet számára
        String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
        objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
    }

    // get hivasra visszateritem az osszes tarolt entitast
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("GET /ingatlan");
        resp.setHeader("Content-Type", "application/json");

        try {

            // string-ben tarolom kezdetben, hogy ellenorizzem null-e (meg van e adva)
            String idParamString = req.getParameter("id");
            String priceParam = req.getParameter("termekAra");

            if (idParamString == null && priceParam == null) {
                // hogy nincs id parameterkent visszateritek minden elemet
                Collection<Ingatlan> ingatlanLista = ingatlanMuveletek.findAll();
                objectMapper.writeValue(resp.getOutputStream(), ingatlanLista);
                return;
            }
            if (idParamString != null && priceParam == null) {
                // ha biztosra tudom, hogy van id mezom megadva, LONG-a alakitom
                Long idParam1 = Long.valueOf(idParamString);

                // ha van id megadva parameterkent megkeresem az adott id-val ellatott ingatlant
                Ingatlan ingatlan = ingatlanMuveletek.findById(idParam1);

                if (ingatlan != null) {
                    objectMapper.writeValue(resp.getOutputStream(), ingatlan);
                }
                hibafuggveny(resp, HttpServletResponse.SC_NOT_FOUND, "Az on altal megadott id-val "
                        + "rendelkezo ingatlan nem letezik.");
                return;
            }
            if (idParamString == null) {
                Integer price = Integer.parseInt(priceParam);
                Ingatlan ingatlan = ingatlanMuveletek.findByPrice(price);
                if (ingatlan != null) {
                    objectMapper.writeValue(resp.getOutputStream(), ingatlan);
                }
                hibafuggveny(resp, HttpServletResponse.SC_NOT_FOUND, "Az on altal megadott id-val "
                        + "rendelkezo ingatlan nem letezik.");
            }
        } catch (NumberFormatException | InvalidFormatException | RepositoryExeption e) {
            hibafuggveny(resp, HttpServletResponse.SC_BAD_REQUEST, "Ervenytelen id tipus vagy helytelen SQL parancs");
        }

    }

    // a bemeneti string ne tartalmazzon szamjegyet
    public static boolean stringE(String input) {
        for (char karakter : input.toCharArray()) {
            if (Character.isDigit(karakter)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidIngatlan(String orszag,
                                    String varos,
                                    Integer negyzetmeter,
                                    Integer termekAra,
                                    String tulajNeve,
                                    String elerhetoseg) {
        if (orszag == null) {
            return false;
        } else if (varos == null) {
            return false;
        } else if (negyzetmeter == null) {
            return false;
        } else if (elerhetoseg == null) {
            return false;
        } else if (termekAra == null) {
            return false;
        } else if (!stringE(orszag) || !stringE(varos)) {
            return false;
        }
        return tulajNeve != null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.info("POST /ingatlan");
        resp.setHeader("Content-Type", "application/json");
        resp.setCharacterEncoding("UTF-8");


        try {
            Ingatlan ingatlan = objectMapper.readValue(req.getInputStream(), Ingatlan.class);

            String orszag = ingatlan.getOrszag();
            String varos = ingatlan.getVaros();
            Integer negyzetmeter = ingatlan.getNegyzetmeter();
            Integer termekAra = ingatlan.getTermekAra();
            String tulajNeve = ingatlan.getTulajNeve();
            String elerhetoseg = ingatlan.getElerhetoseg();

            if (isValidIngatlan(orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg)) {
                // Ha minden rendben van, beszúrja az entitást az adatbázisba
                Ingatlan createdIngatlan = new Ingatlan(orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg);
                Ingatlan ingatlanokCreate = null;

                try {
                    ingatlanokCreate = ingatlanMuveletek.create(createdIngatlan);
                } catch (RepositoryExeption e) {
                    log.error("Sikeretelen adatbazis muvelet");
                    hibafuggveny(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sikeretelen adatbazis muvelet");
                }

                // Válasz küldése a létrehozott entitással
                objectMapper.writeValue(resp.getOutputStream(), ingatlanokCreate);

            } else {
                // Hiányzó vagy érvénytelen tulajdonságok esetén 400 Bad Request válasz küldése
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                String errorMessage = "Hianyzo adattagok. ";
                // Objektum létrehozása az üzenet számára
                String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
                objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
            }

        } catch (NumberFormatException | UnrecognizedPropertyException | InvalidFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            String errorMessage = "Hianyzo adattagok. ";
            // Objektum létrehozása az üzenet számára
            String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
            objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
        }

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("DELETE /ingatlan");
        resp.setHeader("Content-Type", "application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParamString = req.getParameter("id");
            // a Long.valueOf() nem terit vissza sose null-t ezert a string erteket is mentettem
            // => hogy ellenorizhessem ha ninc s megadva ID
            if (idParamString == null) {
                // ha nincs id parameter
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                String errorMessage = "Kerem adjon meg egy id-t";
                // Objektum létrehozása az üzenet számára
                String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
                objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
            } else {
                Long idParam = Long.valueOf(idParamString);

                Ingatlan ingatlanIdAlapjan = null;

                try {
                    ingatlanIdAlapjan = ingatlanMuveletek.findById(idParam);
                } catch (RepositoryExeption e) {
                    log.error("Sikeretelen adatbazis muvelet");
                    hibafuggveny(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sikeretelen adatbazis muvelet");
                }

                log.info("torles talalat: " + ingatlanIdAlapjan);
                if (ingatlanIdAlapjan != null) {
                    // sikeres torles
                    ingatlanMuveletek.delete(idParam);
                    // objectMapper.writeValue(resp.getOutputStream(), "Torolve: " + idParam);
                    String errorMessage = "Torolve a(z) " + idParam + ". ingatlan. ";
                    // Objektum létrehozása az üzenet számára
                    String jsonResponse = objectMapper.writeValueAsString(Map.of("info", errorMessage));
                    objectMapper.writeValue(resp.getOutputStream(), jsonResponse);

                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.setContentType("application/json");
                    String errorMessage = "Iyen id-val ellatott ingatlan nem letezik";
                    // Objektum létrehozása az üzenet számára
                    String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
                    objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
                }
            }
        } catch (NumberFormatException | InvalidFormatException e) {
            // parseLong exeption-t dob
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            String errorMessage = "Az id tipusa nem megfelelo";
            // Objektum létrehozása az üzenet számára
            String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
            objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            log.info("PUT /ingatlan");
            resp.setHeader("Content-Type", "application/json");
            resp.setCharacterEncoding("UTF-8");

            Ingatlan ingatlan = objectMapper.readValue(req.getInputStream(), Ingatlan.class);
            Long idParam = ingatlan.getId();
            String orszag = ingatlan.getOrszag();
            String varos = ingatlan.getVaros();
            Integer negyzetmeter = ingatlan.getNegyzetmeter();
            Integer termekAra = ingatlan.getTermekAra();
            String tulajNeve = ingatlan.getTulajNeve();
            String elerhetoseg = ingatlan.getElerhetoseg();

            // ellenorzom az adatokat
            if (isValidIngatlan(orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg) && idParam != null) {

                Ingatlan ingatlanIdAlapjan = ingatlanMuveletek.findById(idParam);
                if (ingatlanIdAlapjan != null) {
                    // Ha minden rendben van, frissitjuk az entitást az adatbázisba
                    Ingatlan ingatlanok = new Ingatlan(orszag, varos, negyzetmeter, termekAra, tulajNeve, elerhetoseg);
                    Ingatlan ingatlanokUpdate = null;

                    try {
                        ingatlanokUpdate = ingatlanMuveletek.update(ingatlanok, idParam);
                    } catch (RepositoryExeption e) {
                        log.error("Sikeretelen adatbazis muvelet");
                        hibafuggveny(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sikeretelen"
                                + " adatbazis muvelet");
                    }

                    // Válasz küldése
                    objectMapper.writeValue(resp.getOutputStream(), "Modositott ingatlan:" + ingatlanokUpdate);
                } else {
                    // Hiányzó vagy érvénytelen tulajdonságok esetén 400 Bad Request válasz küldése
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.setContentType("application/json");
                    String errorMessage = "Ilyen id-ju ingatlan nincs";
                    // Objektum létrehozása az üzenet számára
                    String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
                    objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
                }
            } else {
                // Hiányzó vagy érvénytelen tulajdonságok esetén 400 Bad Request válasz küldése
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                String errorMessage = "Hianyzo vagy ervenytelen adatok";
                // Objektum létrehozása az üzenet számára
                String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
                objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
            }
            // ha nem integgerek
        } catch (NumberFormatException | InvalidFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json");
            String errorMessage = "Helytelen adatok";
            // Objektum létrehozása az üzenet számára
            String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
            objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
        }

    }
}
