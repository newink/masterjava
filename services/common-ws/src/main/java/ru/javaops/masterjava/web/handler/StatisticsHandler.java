package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.Statistics;

import static ru.javaops.masterjava.web.SoapUtil.getMessageText;


@Slf4j
public class StatisticsHandler extends SoapBaseHandler {


    @Override
    public boolean handleMessage(MessageHandlerContext messageHandlerContext) {
        Statistics.count(getMessageText(messageHandlerContext.getMessage().copy()), System.currentTimeMillis(), Statistics.RESULT.SUCCESS);
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext messageHandlerContext) {
        Statistics.count(getMessageText(messageHandlerContext.getMessage().copy()), System.currentTimeMillis(), Statistics.RESULT.FAIL);
        return true;
    }
}
