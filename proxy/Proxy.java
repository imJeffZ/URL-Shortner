package proxy;

import java.io.*;
import java.net.*;
import proxy.ConnectionHandler;

public class Proxy {
    static String host = "dh2026pc05";

    public static void main(String[] args) throws IOException {

        // this.host = "dh2026pc05"; // Choose a host from host.txt

        try {
            int remoteport = 8026;
            int localport = 8030;
            // Print a start-up message
            System.out.println("Starting proxy for " + host + ":" + remoteport + " on port " + localport);
            // And start running the server
            runServer(host, remoteport, localport); // never returns
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * runs a single-threaded proxy server on the specified local port. It never
     * returns.
     * 
     * @return
     */
    public static void runServer(String host, int remoteport, int localport) throws IOException {
        // Create a ServerSocket to listen for connections with
        ServerSocket ss = new ServerSocket(localport);

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

                Thread t = new Thread(new ConnectionHandler(clientSocket, host));
                t.start();
                System.out.println("thread spawned for new client.");
            } catch (Exception e) {
                clientSocket.close(); 
                e.printStackTrace();
            }
        }
    }

}