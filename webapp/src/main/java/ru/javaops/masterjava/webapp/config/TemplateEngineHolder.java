package ru.javaops.masterjava.webapp.config;

import org.thymeleaf.TemplateEngine;

import javax.servlet.ServletContext;

public class TemplateEngineHolder {

    private final static String TEMPLATE_ENGINE_ATTRIBUTE = "com.thymeleafexamples.thymeleaf3.TemplateEngineInstance";

    private TemplateEngineHolder() {
    }

    public static void storeEngine(ServletContext context, TemplateEngine engine) {
        context.setAttribute(TEMPLATE_ENGINE_ATTRIBUTE, engine);
    }

    public static TemplateEngine getEngine(ServletContext context) {
        return (TemplateEngine) context.getAttribute(TEMPLATE_ENGINE_ATTRIBUTE);
    }
}
