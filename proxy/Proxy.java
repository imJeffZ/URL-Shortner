package proxy;

import java.io.*;
import java.net.*;

public class Proxy {
    private static final int DEPRECATED_NODE_PORT = 8026;
    private static final int PROXY_PORT = 8030;
    private static LoadBalancer loadBalancer;
    private static CacheHandler cacheHandler;

    public static void main(final String[] args) throws IOException {

        loadBalancer = new LoadBalancer();
        cacheHandler = new CacheHandler();

        // shardHandlers = new ShardHandler();
        try {
            // Print a start-up message
            System.out.println(
                    "✅ Proxy started on " + InetAddress.getLocalHost().getHostName() + " on port " + PROXY_PORT);
            // And start running the server
            runServer(PROXY_PORT); // never returns
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
    public static void runServer(final int localport) throws IOException {
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
                clientSocket = ss.accept();
                System.out.println("Accepted new connection. " + ss);

                // Start the client-to-server request thread running
                // String nodeHost = loadBalancer.getHost();
                final Thread t = new Thread(
                        new ConnectionHandler(clientSocket, loadBalancer, cacheHandler, DEPRECATED_NODE_PORT));
                t.start();
            } catch (final Exception e) {
                clientSocket.close();
                e.printStackTrace();
            }
        }
    }
}
