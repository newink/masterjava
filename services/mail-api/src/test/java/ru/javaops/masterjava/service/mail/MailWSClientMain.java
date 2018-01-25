package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.web.WebStateException;

import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class MailWSClientMain {
    public static void main(String[] args) throws WebStateException, IOException {
        byte[] attachment = Files.readAllBytes(Configs.getConfigFile("wsdl/common.xsd").toPath());
        String state = MailWSClient.sendToGroup(
                ImmutableSet.of(new Addressee("To <masterjava@javaops.ru>")),
                ImmutableSet.of(new Addressee("Copy <masterjava@javaops.ru>")), "Subject", "Body", attachment);
        System.out.println(state);
    }
}