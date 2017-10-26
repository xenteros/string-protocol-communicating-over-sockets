package com.github.xenteros.handler;

import com.github.xenteros.exception.EdgeNotFoundException;
import com.github.xenteros.exception.VertexNotAllowedException;
import com.github.xenteros.exception.VertexNotFoundException;
import com.github.xenteros.graph.GraphHolder;
import com.github.xenteros.server.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.xenteros.handler.ClientMessages.*;
import static com.github.xenteros.handler.ClientMessages.BYE;
import static com.github.xenteros.handler.ClientMessages.HI;
import static com.github.xenteros.handler.ServerMessages.*;
import static java.lang.String.format;

public class ServerMessageHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServerMessageHandler.class);

    public static String handle(String message, Session session, GraphHolder graphHolder) {
        String response = prepareResponse(message, session, graphHolder);
        LOG.debug(format("Respond to %s with %s.", message, response));
        return response;
    }

    private static String prepareResponse(String message, Session session, GraphHolder graphHolder) {

        if (message == null) {
            return UNSUPPORTED;
        }
        if (message.startsWith(HI)) {
            String name = message.substring(HI.length());
            session.setName(name);
            return format(ServerMessages.HI, session.getName());
        }
        if (message.equals(BYE)) {
            return format(ServerMessages.BYE, session.getName(), (System.currentTimeMillis() - session.getStartTime()));
        }
        if (message.startsWith(ADD_NODE)) {
            String node = message.substring(ADD_NODE.length());
            try {
                graphHolder.addNode(node);
            } catch (VertexNotAllowedException e) {
                return ServerMessages.NODE_ADD_FAILED;
            }
            return ServerMessages.NODE_ADDED;
        }
        if (message.startsWith(ADD_EDGE)) {
            String[] edge = message.substring(ADD_EDGE.length()).split(" ");
            try {
                graphHolder.addEdge(edge[0], edge[1], Integer.parseInt(edge[2]));
            } catch (VertexNotFoundException e) {
                return NODE_NOT_FOUND;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return UNSUPPORTED;
            }
            return ServerMessages.EDGE_ADDED;
        }
        if (message.startsWith(REMOVE_NODE)) {
            try {
                graphHolder.removeNode(message.substring(REMOVE_NODE.length()));
            } catch (VertexNotFoundException e) {
                return NODE_NOT_FOUND;
            }
            return ServerMessages.NODE_REMOVED;
        }
        if (message.startsWith(REMOVE_EDGE)) {
            try {
                String[] edge = message.substring(REMOVE_EDGE.length()).split(" ");
                graphHolder.removeEdge(edge[0], edge[1]);
            } catch (VertexNotFoundException | EdgeNotFoundException e) {
                return NODE_NOT_FOUND;
            } catch (IndexOutOfBoundsException e) {
                return UNSUPPORTED;
            }
            return EDGE_REMOVED;
        }
        if (message.startsWith(SHORTEST_PATH)) {
            try {
                String[] path = message.substring(SHORTEST_PATH.length()).split(" ");
                return format(ServerMessages.PATH_WEIGHT, graphHolder.shortestPath(path[0], path[1]));
            } catch (VertexNotFoundException e) {
                return NODE_NOT_FOUND;
            } catch (IndexOutOfBoundsException e) {
                return UNSUPPORTED;
            }
        }
        if (message.startsWith(CLOSER_THAN)) {
            try {
                String[] params = message.substring(CLOSER_THAN.length()).split(" ");
                return graphHolder.findNodesCloserThan(Integer.parseInt(params[0]), params[1]);
            } catch (VertexNotFoundException e) {
                return NODE_NOT_FOUND;
            } catch (IndexOutOfBoundsException e) {
                return UNSUPPORTED;
            }
        }
        return UNSUPPORTED;
    }
}
