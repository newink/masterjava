package ru.javaops.masterjava.upload;

import one.util.streamex.IntStreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;

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

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
@MultipartConfig
public class UploadServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(UploadServlet.class);

    private final UserProcessor userProcessor = new UserProcessor();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        engine.process("upload", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        String message;
        try {
//            http://docs.oracle.com/javaee/6/tutorial/doc/glraq.html
            Part filePart = req.getPart("fileToUpload");
            Integer chunkSize = Integer.parseInt(req.getParameter("chunk-size"));
            try (InputStream is = filePart.getInputStream()) {
                List<User> users = userProcessor.process(is);
                UserDao dao = DBIProvider.getDao(UserDao.class);
                int[] insertedUserIds = dao.batchInsert(users, chunkSize);
                List<User> insertedUserList = IntStreamEx.range(0, users.size())
                        .filter(i -> insertedUserIds[i] != 0)
                        .mapToObj(users::get)
                        .toList();
                webContext.setVariable("users", insertedUserList);
                log.warn("File successfully processed.");
                engine.process("result", webContext, resp.getWriter());
                return;
            }
        } catch (Exception e) {
            log.error("File processing failed: {}", e);
            message = e.toString();
        }
        renderMessage(req, resp, message);
    }

    private void renderMessage(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        final WebContext webContext =
                new WebContext(request, response, request.getServletContext(), request.getLocale());
        webContext.setVariable("message", message);
        engine.process("upload", webContext, response.getWriter());
    }
}
