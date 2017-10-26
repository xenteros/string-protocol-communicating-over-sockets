package com.github.xenteros.server;

import com.github.xenteros.graph.GraphHolder;
import com.github.xenteros.handler.ServerMessages;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.github.xenteros.handler.ClientMessages.BYE;
import static com.github.xenteros.handler.ServerMessageHandler.handle;
import static java.lang.String.format;

public class MultiServer {

    private static final Logger LOG = LoggerFactory.getLogger(MultiServer.class);

    private ServerSocket serverSocket;
    private GraphHolder graphHolder;


    public void start(int port) {
        try {
            graphHolder = new GraphHolder();
            serverSocket = new ServerSocket(port);
            LOG.info("Server started at port " + port);
            while (true) {
                new ClientHandler(serverSocket.accept(), graphHolder).run();
                LOG.info("Client connected to the server.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private void stop() {
        try {
            LOG.error("Stoping server.");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static class ClientHandler extends Thread {

        private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private TimeLimiter timeLimiter;
        private Session session;
        private GraphHolder graphHolder;

        public ClientHandler(Socket socket, GraphHolder graphHolder) {
            this.clientSocket = socket;
            this.graphHolder = graphHolder;
            timeLimiter = SimpleTimeLimiter.create(Executors.newSingleThreadExecutor());
            session = new Session();
            LOG.debug("Session established with UUID " + session.getUuid());
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                out.println(format(ServerMessages.HI_I_AM, session.getUuid().toString()));
                try {
                    while (true) {
                        inputLine = timeLimiter.callWithTimeout(in::readLine, 30, TimeUnit.SECONDS);
                        LOG.info(inputLine);
                        String response = handle(inputLine, session, graphHolder);
                        out.println(response);
                        if (BYE.equals(inputLine)) {
                            break;
                        }
                    }
                } catch (TimeoutException | InterruptedException | ExecutionException e) {
                    out.println(format(ServerMessages.BYE, session.getName(), (System.currentTimeMillis() - session.getStartTime())));
                    LOG.info(format("Connection with %s has timed out.", session.getName()));
                }

                in.close();
                out.close();
                clientSocket.close();
                LOG.info(format("Connection with %s is closed.", session.getName()));
            } catch (IOException e) {
                LOG.debug(e.getMessage());
            }
        }
    }
}
