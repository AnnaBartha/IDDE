package edu.bbte.idde.baim2115.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import edu.bbte.idde.baim2115.backend.model.IngatlanUgynok;
import edu.bbte.idde.baim2115.backend.repository.AbstractDaoFactory;
import edu.bbte.idde.baim2115.backend.repository.IngatlanDao;
import edu.bbte.idde.baim2115.backend.repository.IngatlanUgynokDao;
import edu.bbte.idde.baim2115.backend.repository.RepositoryExeption;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/ugynok")
public class IngatlanUgynokServlet extends HttpServlet {

    private final IngatlanUgynokDao ingatlanMuveletek = AbstractDaoFactory.getInstance().getAbstractIngatlanUgynokDao();
    private final IngatlanDao ingatlanMuveletei = AbstractDaoFactory.getInstance().getAbstractIngatlanDao();

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("GET /ugynok");
        resp.setHeader("Content-Type", "application/json");

        try {

            // string-ben tarolom kezdetben, hogy ellenorizzem null-e (meg van e adva)
            String idParamString = req.getParameter("id");
            String telefonszam = req.getParameter("telefonszam");

            if (idParamString == null && telefonszam == null) {
                // hogy nincs id sem telefonszam parameterkent visszateritek minden elemet
                Collection<IngatlanUgynok> ingatlanLista = ingatlanMuveletek.findAll();
                objectMapper.writeValue(resp.getOutputStream(), ingatlanLista);
            } else if (idParamString != null && telefonszam == null) {
                // ha biztosra tudom, hogy van id mezom megadva es nncs telefonszam , LONG-a alakitom
                Long idParam1 = Long.valueOf(idParamString);

                // ha van id megadva parameterkent megkeresem az adott id-val ellatott ingatlant
                IngatlanUgynok ingatlan = ingatlanMuveletek.findById(idParam1);

                if (ingatlan != null) {
                    // h avan ilyen id-val rendelkezo ingatlan
                    objectMapper.writeValue(resp.getOutputStream(), ingatlan);
                } else {
                    hibafuggveny(resp, HttpServletResponse.SC_NOT_FOUND, "Az on altal"
                            + " megadott id-val rendelkezo ingatlan nem letezik. ");
                }
            } else if (idParamString == null) {
                // ha van telefonszam parameter megadva
                log.info("TELOSZAM" + telefonszam);

                // ha van id megadva parameterkent megkeresem az adott id-val ellatott ingatlant
                IngatlanUgynok ingatlan1 = ingatlanMuveletek.findByPhoneNumber(telefonszam);
                if (ingatlan1 != null) {
                    objectMapper.writeValue(resp.getOutputStream(), ingatlan1);
                } else {
                    hibafuggveny(resp, HttpServletResponse.SC_NOT_FOUND, "Az on altal megadott "
                            + "telefonszammal rendelkezo ingatlan nem letezik.");
                }

            }
        } catch (NumberFormatException | InvalidFormatException | RepositoryExeption e) {
            hibafuggveny(resp, HttpServletResponse.SC_BAD_REQUEST, "Ervenytelen Id tipus vagy sikertelen modositas");
        }
    }

    private void hibafuggveny(HttpServletResponse resp, int statusCode, String errorMessage) throws IOException {
        // ha nincs ilyen ingatlan
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        // Objektum létrehozása az üzenet számára
        String jsonResponse = objectMapper.writeValueAsString(Map.of("error", errorMessage));
        objectMapper.writeValue(resp.getOutputStream(), jsonResponse);
    }

    private boolean isValid(String info) {
        return info != null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        log.info("POST /ugynok");
        resp.setHeader("Content-Type", "application/json");
        resp.setCharacterEncoding("UTF-8");


        try {
            IngatlanUgynok ingatlan = objectMapper.readValue(req.getInputStream(), IngatlanUgynok.class);
            // lekerem az emailt es a telefonszamot
            String email = ingatlan.getEmail();
            String telefonszam = ingatlan.getTelefonszam();

            // lekerem az id-kat az inagtlanokhoz
            List<Ingatlan> ingatlanok = ingatlan.getIngatlanok();
            // ide tarolom az adott ugynokhoz tartozo ingatlanok id-jat amit json-bol olvasok ki

            // ebben a listaban tarolom majd a megadott id-val rendelkezo ingatlanokat
            List<Ingatlan> mentett = new ArrayList<>();
            for (Ingatlan i : ingatlanok) {
                // lekerem az adott id-t
                long ingatlanId = i.getId();
                //lekerem az adott id-val rendelkezo ingatlant
                Ingatlan ujabb = null;

                try {
                    ujabb = ingatlanMuveletei.findById(ingatlanId);
                } catch (RepositoryExeption e) {
                    log.error("Sikeretelen adatbazis muvelet");
                    hibafuggveny(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sikeretelen adatbazis muvelet");
                }

                //mentem a keresett ingatlant
                mentett.add(ujabb);
            }

            log.info(mentett.toString());


            if (isValid(email) && isValid(telefonszam)) {
                log.info("HELYES VALIDACIO");
                // Ha minden rendben van, beszúrja az entitást az adatbázisba
                IngatlanUgynok createUgynok = new IngatlanUgynok(email, telefonszam, mentett);
                IngatlanUgynok ingatlanokCreate = ingatlanMuveletek.create(createUgynok);

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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            log.info("PUT /ugynok");
            resp.setHeader("Content-Type", "application/json");
            resp.setCharacterEncoding("UTF-8");

            IngatlanUgynok ingatlan = objectMapper.readValue(req.getInputStream(), IngatlanUgynok.class);
            Long idParam = ingatlan.getId();
            String email = ingatlan.getEmail();
            String telefonszam = ingatlan.getTelefonszam();
            // lekerem az id-kat az inagtlanokhoz
            List<Ingatlan> ingatlanokLista = ingatlan.getIngatlanok();

            // ellenorzom az adatokat
            if (idParam != null && isValid(email) && isValid(telefonszam)) {

                IngatlanUgynok ingatlanIdAlapjan = null;
                try {
                    ingatlanIdAlapjan = ingatlanMuveletek.findById(idParam);
                } catch (RepositoryExeption e) {
                    log.error("Sikeretelen adatbazis muvelet");
                    hibafuggveny(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Sikeretelen adatbazis muvelet");
                }
                if (ingatlanIdAlapjan != null) {
                    // Ha minden rendben van, frissitjuk az entitást az adatbázisba
                    IngatlanUgynok ingatlanok = new IngatlanUgynok(email, telefonszam, ingatlanokLista);

                    IngatlanUgynok ingatlanokUpdate = ingatlanMuveletek.update(ingatlanok, idParam);

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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("DELETE /ugynok");
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

                IngatlanUgynok ingatlanIdAlapjan = null;
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


}
