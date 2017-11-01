package ru.javaops.masterjava.webapp.web;

import com.google.common.io.Files;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.webapp.config.TemplateEngineHolder;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

@WebServlet("/")
@MultipartConfig(location = "/tmp")
public class UploadServlet extends HttpServlet {

    private static final String SAVE_DIR = "uploads";

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        renderHtml(req, resp, "index.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String appPath = req.getServletContext().getRealPath("");
        String savePath = appPath + File.separator + SAVE_DIR;
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }

        File savedFile = null;
        Collection<Part> parts = req.getParts();
        for (Part part : parts) {
            System.out.println("Size: ");
            System.out.println(part.getSize());
            String fileName = extractFileName(part);


            fileName = new File(fileName).getName();
            String fullName = savePath + File.separator + fileName;
            savedFile = new File(fullName);
            part.write(fullName);
        }

        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.asByteSource(savedFile).openStream())) {
            while (processor.startElement("User", "Users")) {
                System.out.printf("Username: %s, Email: %s\n", processor.getText(), processor.getAttribute("email"));
            }
        } catch (XMLStreamException e) {
            renderHtml(req, resp, "error.html");
        }

        renderHtml(req, resp, "success.html");
    }

    private void renderHtml(HttpServletRequest req, HttpServletResponse resp, String template) throws IOException {
        TemplateEngine engine = TemplateEngineHolder.getEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        engine.process(template, context, resp.getWriter());
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
}
