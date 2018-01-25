package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.web.WebStateException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class MailWSClientMain {
    public static void main(String[] args) throws WebStateException, IOException {
        File configFile = Configs.getConfigFile("wsdl/common.xsd");
        byte[] attachment = Files.readAllBytes(configFile.toPath());
        String state = MailWSClient.sendToGroup(
                ImmutableSet.of(new Addressee("To <masterjava@javaops.ru>")),
                ImmutableSet.of(new Addressee("Copy <masterjava@javaops.ru>")), "Subject", "Body", configFile.getName(), attachment);
        System.out.println(state);
    }
}