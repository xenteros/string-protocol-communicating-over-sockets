package com.github.xenteros.handler;

import com.github.xenteros.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.xenteros.handler.ClientMessages.BYE;
import static com.github.xenteros.handler.ClientMessages.HI;
import static java.lang.String.format;

public class ServerMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServerMessageHandler.class);

    public static String handle(String message, Session session) {
        String response = prepareResponse(message, session);
        LOG.debug(format("Respond with %s to %s.", message, response));
        return response;
    }

    private static String prepareResponse(String message, Session session) {

        if (message == null) {
            return ServerMessages.UNSUPPORTED;
        }
        if (message.startsWith(HI)) {
            String name = message.substring(HI.length());
            session.setName(name);
            return format(ServerMessages.HI, session.getName());
        }
        if (message.equals(BYE)) {
            return format(ServerMessages.BYE, session.getName(), (System.currentTimeMillis() - session.getStartTime()));
        }
        return ServerMessages.UNSUPPORTED;
    }
}
