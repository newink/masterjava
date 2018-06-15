package ru.javaops.masterjava.service.mail.rest;


import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments;
import ru.javaops.masterjava.web.WebStateException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Collections;

@Path("/")
public class MailRS {
    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test";
    }

    @POST
    @Path("/send")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaType.APPLICATION_JSON)
    public GroupResult send(@NotBlank @FormDataParam("users") String users,
                            @FormDataParam("subject") String subject,
                            @NotBlank @FormDataParam("body") String body,
                            @FormDataParam("attach") InputStream fileInputStream,
                            @FormDataParam("attach") FormDataContentDisposition fileDetail) throws WebStateException {

        Attachment attachment = Attachments.getAttachment(fileDetail.getName(), fileInputStream);
        return MailServiceExecutor.sendBulk(MailWSClient.split(users), subject, body, Collections.singletonList(attachment));
    }
}