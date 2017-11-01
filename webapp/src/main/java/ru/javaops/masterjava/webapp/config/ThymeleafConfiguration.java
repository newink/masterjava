package ru.javaops.masterjava.webapp.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ThymeleafConfiguration implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        TemplateEngine engine = templateEngine(servletContextEvent.getServletContext());
        TemplateEngineHolder.storeEngine(servletContextEvent.getServletContext(), engine);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private TemplateEngine templateEngine(ServletContext context) {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver(context));
        return engine;
    }

    private ITemplateResolver templateResolver(ServletContext context) {
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        return templateResolver;
    }
}
