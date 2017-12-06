package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.Data;
import ru.javaops.masterjava.config.Configs;

@Data
public class MailProperties {
    private static volatile MailProperties instance;

    private String host;
    private String username;
    private String password;
    private String fromName;
    private Integer port;
    private Boolean useSsl;
    private Boolean useTls;
    private Boolean debug;

    private MailProperties() {
        Config mailConfig = Configs.getConfig("mail.conf", "mail");

        this.host = mailConfig.getString("host");
        this.port = mailConfig.getInt("port");
        this.username = mailConfig.getString("username");
        this.password = mailConfig.getString("password");
        this.fromName = mailConfig.getString("fromName");
        this.useSsl = mailConfig.getBoolean("useSSL");
        this.useTls = mailConfig.getBoolean("useTLS");
        this.debug = mailConfig.getBoolean("debug");
    }

    public static MailProperties getInstance() {
        if (instance == null) {
            synchronized (MailProperties.class) {
                if (instance == null) {
                    instance = new MailProperties();
                }
            }
        }
        return instance;
    }
}
