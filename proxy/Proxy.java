package proxy;

import java.io.*;
import java.net.*;
import java.util.*;

import proxy.ConnectionHandler;

public class Proxy {
    static final String HOST_FILE = "./proxy/hosts.txt";
    static String[] hosts;

    public static void main(final String[] args) throws IOException {

        hosts = readHosts(HOST_FILE);
        try {
            final int remoteport = 8026;
            final int localport = 8030;
            String tempHost = hosts[0];
            // Print a start-up message
            System.out.println("Starting proxy for " + tempHost + ":" + remoteport + " on port " + localport);
            // And start running the server
            runServer(tempHost, remoteport, localport); // never returns
        } catch (final Exception e) {
            System.err.println(e);
        }
    }

    /**
     * runs a single-threaded proxy server on the specified local port. It never
     * returns.
     * 
     * @return
     */
    public static void runServer(final String host, final int remoteport, final int localport) throws IOException {
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

                final Thread t = new Thread(new ConnectionHandler(clientSocket, host));
                t.start();
                System.out.println("thread spawned for new client.");
            } catch (final Exception e) {
                clientSocket.close(); 
                e.printStackTrace();
            }
        }
    }

    private static String[] readHosts(final String hostFile) throws IOException {
        String s;
        ArrayList<String> hosts = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(hostFile))) {
            while((s = br.readLine()) != null) {
                hosts.add(s);
            }
        } catch (final IOException exc) {
            System.err.println("I/O Error in reading host file: " + exc);
        }
        return (String[]) hosts.toArray(new String[0]);
    }
}