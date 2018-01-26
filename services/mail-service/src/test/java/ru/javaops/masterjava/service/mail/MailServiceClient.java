package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;
import ru.javaops.web.WebStateException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class MailServiceClient {

    public static void main(String[] args) throws MalformedURLException, WebStateException {
        Service service = Service.create(
                new URL("http://localhost:8080/mail/mailService?wsdl"),
                new QName("http://mail.javaops.ru/", "MailServiceImplService"));

        MailService mailService = service.getPort(MailService.class);

        String state = mailService.sendToGroup(ImmutableSet.of(new Addressee("trashink@yandex.ru", null)), null,
                "Group mail subject", "Group mail body");
        System.out.println("Group mail state: " + state);

        GroupResult groupResult = mailService.sendBulk(ImmutableSet.of(
                new Addressee("Мастер Java <trashink@yandex.ru>"),
                new Addressee("Bad Email <bad_email.ru>")), "Bulk mail subject", "Bulk mail body");

        GroupResult groupResultWithAttachment = mailService.sendBulkWithAttachment(ImmutableSet.of(
                new Addressee("Мастер Java <trashink@yandex.ru>"),
                new Addressee("Bad Email <bad_email.ru>")), "Bulk mail subject", "Bulk mail body", "filename", new byte[1]);
        System.out.println("\nBulk mail groupResult:\n" + groupResult);
        System.out.println("\nBulk mail with attachment groupResult:\n" + groupResultWithAttachment);
    }
}
