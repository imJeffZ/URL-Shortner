package proxy;

import java.io.*;
import java.net.*;
import java.util.*;

public class Proxy {
    private static final int DEPRECATED_NODE_PORT = 8026;
    private static final int PROXY_PORT = 8030;
    private static LoadBalancer loadBalancer;

    public static void main(final String[] args) throws IOException {

        loadBalancer = new LoadBalancer();
        try {
            String proxyHost = loadBalancer.getHost();
            // Print a start-up message
            System.out.println("Starting proxy for " + proxyHost + " on port " + PROXY_PORT);
            // And start running the server
            runServer(proxyHost, PROXY_PORT); // never returns
        } catch (final Exception e) {
            System.err.println(e);
        }
    }

    /**
     * runs a single-threaded proxy server on the specified local port. It never
     * returns.
     * 
     * 
     * @return
     */
    public static void runServer(final String host, final int localport) throws IOException {
        // Create a ServerSocket to listen for connections with
        final ServerSocket ss = new ServerSocket(localport);

        // while true
        // serversocket.accept (dont block)
        // if serversocket has data
        // choose socket from socket array to write to
        // write to socket (dont block on response)
        // for socket in socket array
        // check if socket has data
        // write to respective client
        // repeat loop

        while (true) {
            Socket clientSocket = null;
            try {
                // Wait for a connection on the local port
                System.out.println("Waiting for a client ...");
                clientSocket = ss.accept();
                System.out.println("Accepted new connection. " + ss);

                // Start the client-to-server request thread running
                String nodeHost = loadBalancer.getHost();
                final Thread t = new Thread(new ConnectionHandler(clientSocket, nodeHost, DEPRECATED_NODE_PORT));
                t.start();
                System.out.println("thread spawned for new client.");
            } catch (final Exception e) {
                clientSocket.close(); 
                e.printStackTrace();
            }
        }
    }
}
