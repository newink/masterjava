package ru.javaops.masterjava.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@AllArgsConstructor
public class MailObject implements Serializable {

    public static final long serialVersionUID = 13L;

    private String adressees;

    private String body;

    private String subject;
}
