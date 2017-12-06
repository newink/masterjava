package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.MailingResultDao;
import ru.javaops.masterjava.persist.model.MailingResult;
import ru.javaops.masterjava.persist.model.type.Result;

import java.sql.DriverManager;
import java.util.List;

@Slf4j
public class MailSender {
    private static MailProperties mailProperties = MailProperties.getInstance();
    private static MailingResultDao mailingResultDao = DBIProvider.getDao(MailingResultDao.class);

    static {
        initDBI();
    }

    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));

        SimpleEmail email = new SimpleEmail();
        email.setHostName(mailProperties.getHost());
        email.setSmtpPort(mailProperties.getPort());
        email.setAuthentication(mailProperties.getUsername(), mailProperties.getPassword());
        email.setSSLOnConnect(mailProperties.getUseSsl());
        email.setSubject(subject);

        cc.forEach(addressee -> {
            try {
                email.addCc(addressee.getEmail());
            } catch (EmailException e) {
                log.error("Unable to add CC in email: {}", e);
            }
        });

        to.forEach(addressee -> {
            try {
                email.addCc(addressee.getEmail());
            } catch (EmailException e) {
                log.error("Unable to add CC in email: {}", e);
            }
        });

        try {
            email.setFrom(mailProperties.getFromName());
            email.setMsg(body);
            String send = email.send();
            log.info("Email has been successfully sent: {}", send);
            mailingResultDao.insert(new MailingResult(listToEmailRange(to), Result.SUCCESS, null));
        } catch (EmailException e) {
            log.error("Unable to set From field or body in email: {}", e);
            mailingResultDao.insert(new MailingResult(listToEmailRange(to), Result.FAIL, e.toString()));
        }
    }

    private static String listToEmailRange(List<Addressee> emails) {
        if (emails.isEmpty()) return "";
        return emails.get(0).getEmail() + " -- " + (emails.size() > 1 ? emails.get(emails.size() - 1).getEmail() : "");
    }


    public static void initDBI() {
        Config db = Configs.getConfig("persist.conf", "db");
        initDBI(db.getString("url"), db.getString("user"), db.getString("password"));
    }

    public static void initDBI(String dbUrl, String dbUser, String dbPassword) {
        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        });
    }
}
