package proxy;

import java.io.*;
import java.net.*;

class ConnectionHandler implements Runnable {
    Socket clientSocket;
    InputStream streamFromClient;
    OutputStream streamToClient;
    String node;
    Socket nodeSocket;
    final byte[] request;
    byte[] reply;
    InputStream streamFromNode;
    OutputStream streamToNode;

    public ConnectionHandler(Socket clientSocket, String node) {
        this.clientSocket = clientSocket;
        this.node = node;
        this.request = new byte[1024];
        this.reply = new byte[4096];
    }

    public void run() {
        try {
            this.nodeSocket = new Socket(node, 8026);
            this.streamFromClient = clientSocket.getInputStream();
            this.streamToClient = clientSocket.getOutputStream();
        } catch (IOException e) {
            PrintWriter out = new PrintWriter(streamToClient);
            out.print("Proxy server cannot connect to " + node + ":" + 8026 + ":\n" + e + "\n");
            out.flush();
            try {
                clientSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        try {
            final OutputStream streamToNode = nodeSocket.getOutputStream();
            int bytesRead;
            System.out.println("Reading from client.");
            while ((bytesRead = streamFromClient.read(request)) != -1) {
                streamToNode.write(request, 0, bytesRead);
                streamToNode.flush();
                System.out.println("Wrote to node.");

            }
            streamToNode.close();
            System.out.println("Reading from node.");
            final InputStream streamFromNode = nodeSocket.getInputStream();
            while ((bytesRead = streamFromNode.read(reply)) != -1) {
                streamToClient.write(reply, 0, bytesRead);
                streamToClient.flush();
                System.out.println("Wrote to client.");

            }
            streamToClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.clientSocket.close();
            this.nodeSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}