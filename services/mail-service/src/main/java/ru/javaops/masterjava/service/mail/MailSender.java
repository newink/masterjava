package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.type.Result;
import ru.javaops.masterjava.service.persist.MailingResult;
import ru.javaops.masterjava.service.persist.MailingResultDao;

import java.util.List;

@Slf4j
public class MailSender {
    private static MailProperties mailProperties = MailProperties.getInstance();
    private static MailingResultDao mailingResultDao = DBIProvider.getDao(MailingResultDao.class);

    static {
        DBITestProvider.initDBI();
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
                log.error("Unable to add copy addressee in email: {}", e);
            }
        });

        to.forEach(addressee -> {
            try {
                email.addTo(addressee.getEmail());
            } catch (EmailException e) {
                log.error("Unable to add addressee in email: {}", e);
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
        return emails.get(0).getEmail() + (emails.size() > 1 ? " -- " + emails.get(emails.size() - 1).getEmail() : "");
    }
}
