package ru.javaops.masterjava.web;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.MailWSClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet(urlPatterns = "/send-mail", loadOnStartup = 1)
@Slf4j
public class SendMailServlet extends HttpServlet {
    private final UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Getting users list to send mail");
        List<User> users = userDao.getWithLimit(100);

        WebContext webContext = new WebContext(req, resp, req.getServletContext());
        webContext.setVariable("users", users);
        engine.process("send-mail", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<Addressee> addressees = stringsToAddressees(req.getParameterValues("to[]"));
        Set<Addressee> copies = stringsToAddressees(req.getParameterValues("cc[]"));

        String message = req.getParameter("message");
        String subject = req.getParameter("subject");
        log.info("Addressees: {}, Message: {}, Subject: {}", addressees, message, subject);

        MailWSClient.sendToGroup(addressees, copies, subject, message);
    }

    private Set<Addressee> stringsToAddressees(String[] strings) {
        return StreamEx.of(strings).nonNull().filter(s -> !s.isEmpty()).map(s -> {
            String[] split = s.split(" - ");
            return new Addressee(split[1], split[0]);
        }).toSet();
    }
}
