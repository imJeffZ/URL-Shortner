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
                clientSocket = ss.accept();
                System.out.println("Accepted new connection.");

                // Make a connection to the real server.
                // If we cannot connect to the server, send an error to the
                // client, disconnect, and continue waiting for connections.
                // try {
                // server = new Socket(host, remoteport);
                // } catch (IOException e) {
                // PrintWriter out = new PrintWriter(streamToClient);
                // out.print("Proxy server cannot connect to " + host + ":" + remoteport + ":\n"
                // + e + "\n");
                // out.flush();
                // client.close();
                // continue;
                // }
                // Going to send off client stream/socket to handler

                // Get server streams.
                // final InputStream streamFromServer = server.getInputStream();
                // final OutputStream streamToServer = server.getOutputStream();

                // a thread to read the client's requests and pass them
                // to the server. A separate thread for asynchronous.
                // Thread t = new Thread() {
                // public void run() {
                // int bytesRead;
                // try {
                // while ((bytesRead = streamFromClient.read(request)) != -1) {
                // streamToServer.write(request, 0, bytesRead);
                // streamToServer.flush();
                // }
                // } catch (IOException e) {
                // }

                // // the client closed the connection to us, so close our
                // // connection to the server.
                // try {
                // streamToServer.close();
                // } catch (IOException e) {
                // }
                // }
                // };

                // Start the client-to-server request thread running

                Thread t = new Thread(new ConnectionHandler(clientSocket, "dh2026pc05"));
                t.start();

                // Read the server's responses
                // and pass them back to the client.
                // int bytesRead;
                // try {
                // while ((bytesRead = streamFromServer.read(reply)) != -1) {
                // streamToClient.write(reply, 0, bytesRead);
                // streamToClient.flush();
                // }
                // } catch (IOException e) {
                // }

                // The server closed its connection to us, so we close our
                // connection to our client.
                // streamToClient.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

}