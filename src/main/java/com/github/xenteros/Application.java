package com.github.xenteros;


import com.github.xenteros.server.MultiServer;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {
        BasicConfigurator.configure();
        LOG.info("Starting server...");
        MultiServer multiServer = new MultiServer();
        multiServer.start(50000);
    }
}
