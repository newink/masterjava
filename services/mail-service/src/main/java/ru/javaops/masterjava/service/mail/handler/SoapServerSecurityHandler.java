package ru.javaops.masterjava.service.mail.handler;

import com.google.common.net.HttpHeaders;
import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.api.message.Message;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.ExceptionType;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.web.AuthUtil;
import ru.javaops.masterjava.web.WebStateException;
import ru.javaops.masterjava.web.handler.SoapBaseHandler;

import java.util.List;
import java.util.Map;

import static ru.javaops.masterjava.web.SoapUtil.getMessageText;

@Slf4j
public class SoapServerSecurityHandler extends SoapBaseHandler {

    private final static String AUTH_HEADER;

    static {
        Config config = Configs.getConfig("hosts.conf", "hosts.mail");
        AUTH_HEADER = AuthUtil.encodeBasicAuthHeader(config.getString("user"), config.getString("password"));
    }

    @Override
    public boolean handleMessage(MessageHandlerContext messageHandlerContext) {
        if (!isOutbound(messageHandlerContext)) {
            Map<String, List<String>> httpHeaders = (Map<String, List<String>>) messageHandlerContext.get(MessageHandlerContext.HTTP_REQUEST_HEADERS);
            int authStatus = AuthUtil.checkBasicAuth(httpHeaders, AUTH_HEADER);
            if (authStatus != 0) {
                log.error("Error while authenticating. Wrong username or password.");
                throw new SecurityException("Bad credentials");
            }
        }
        log.info("Successfully authenticated request!");
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext messageHandlerContext) {
        log.error("Exception thrown while authenticating. Message body: {}", getMessageText(messageHandlerContext.getMessage().copy()));
        return false;
    }
}
