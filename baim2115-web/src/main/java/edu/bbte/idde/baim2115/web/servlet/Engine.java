package edu.bbte.idde.baim2115.web.servlet;

import edu.bbte.idde.baim2115.backend.model.Ingatlan;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import org.thymeleaf.web.IWebExchange;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Engine {
    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private static TemplateEngine enginetemplate;
    private static JakartaServletWebApplication application;

    public static synchronized void buildEngine(ServletContext servletContext) {
        LOG.info("Building Thymeleaf renderer");

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/templates/");

        // Összekötjük a sablonmotorral
        enginetemplate = new TemplateEngine();
        enginetemplate.setTemplateResolver(templateResolver);
        application = JakartaServletWebApplication.buildApplication(servletContext);
    }

    public static void process(
            HttpServletRequest req,
            HttpServletResponse resp,
            String view,
            //  Map<String, Object> model
            List<Ingatlan> model
    ) throws IOException {
        IWebExchange webExchange = application.buildExchange(req, resp);
        // webcontext: tartalmazza az osszes obj-ot amit en AT AKAROK ADNI A HTML-NEK !!!
        WebContext context = new WebContext(webExchange, Locale.getDefault());
        context.setVariable("ingatlanok", model);
        enginetemplate.process(view, context, resp.getWriter());
    }
}
