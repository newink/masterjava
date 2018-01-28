package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailWSClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("/send")
@MultipartConfig
@Slf4j
public class SendServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String result;
        try {
            log.info("Start sending");
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            String users = req.getParameter("users");
            String subject = req.getParameter("subject");
            String body = req.getParameter("body");
            Part filePart = req.getPart("attachment");
            String filename;

            GroupResult groupResult;

            if (filePart != null) {
                filename = filePart.getSubmittedFileName();
                InputStream inputStream = filePart.getInputStream();
                byte[] attachment = ByteStreams.toByteArray(inputStream);
                log.info("Sending email with attachment with name: {}", filename);
                groupResult = MailWSClient.sendBulk(MailWSClient.split(users), subject, body, filename, attachment);
            } else {
                log.info("Sending email without attachment");
                groupResult = MailWSClient.sendBulk(MailWSClient.split(users), subject, body);
            }

            result = groupResult.toString();
            log.info("Processing finished with result: {}", result);
        } catch (Exception e) {
            log.error("Processing failed", e);
            result = e.toString();
        }
        resp.getWriter().write(result);
    }
}
