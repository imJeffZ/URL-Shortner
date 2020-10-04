package proxy;

import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConnectionHandler extends Thread {
    Socket clientSocket;
    InputStream streamFromClient;
    OutputStream streamToClient;
    String node;
    int nodePort;
    Socket nodeSocket;
    byte[] request;
    byte[] reply;
    InputStream streamFromNode;
    OutputStream streamToNode;
    private LoadBalancer loadBalancer;

    public ConnectionHandler(Socket clientSocket, LoadBalancer loadBalancer, int nodePort) {
        this.clientSocket = clientSocket;
        this.nodePort = nodePort;
        this.request = new byte[1024];
        this.reply = new byte[4096];
        this.loadBalancer = loadBalancer;
    }

    @Override
    public void run() {
        try {
            // init streams from client
            streamFromClient = clientSocket.getInputStream();
            streamToClient = clientSocket.getOutputStream();

            // create a socket connection to the node.
            try {
                nodeSocket = new Socket(this.node, this.nodePort);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(streamToClient);
                out.print("Proxy server cannot connect to " + node + ":" + this.nodePort + ":\n" + e + "\n");
                out.flush();
                try {
                    clientSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }

            // create the streams to the node.
            streamFromNode = nodeSocket.getInputStream();
            streamToNode = nodeSocket.getOutputStream();

            // a new thread to read from client and send to node.
            new Thread() {
                public void run() {
                    int bytesRead;
                    System.out.println("Reading from client.");
                    int totalBytes = 0;
                    try {
                        while ((bytesRead = streamFromClient.read(request)) != -1) {
                            totalBytes += bytesRead;
                        }
                        handleRequest(totalBytes);
                        System.out.println("Reading from client complete, closing streamToNode.");
                    } catch (IOException e) {
                    }

                }
            }.start();

            // this current thread reads response from node and forwards to client.
            int bytesRead;
            try {

                System.out.println("Reading from node.");
                while ((bytesRead = streamFromNode.read(reply)) != -1) {
                    streamToClient.write(reply, 0, bytesRead);
                    streamToClient.flush();
                }
                System.out.println("Wrote to client.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnections();
            }
            streamToClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnections() throws IOException {
        if (nodeSocket != null)
            nodeSocket.close();
        System.out.println("Close nodesocket.");
        if (clientSocket != null)
            clientSocket.close();
        System.out.println("Close clientsocket.");
    }

    public byte[] handleRequest(int bytesRead) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(request);
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(streamReader);

            String input = bufferedReader.readLine();
            System.out.println(input);

            Pattern pput = Pattern.compile("^PUT\\s+/\\?short=(\\S+)&long=(\\S+)\\s+(\\S+)$");
            Matcher mput = pput.matcher(input);

            Pattern pget = Pattern.compile("^(\\S+)\\s+/(\\S+)\\s+(\\S+)$");
            Matcher mget = pget.matcher(input);

            // out = new PrintWriter(streamToClient);
            // dataOut = new BufferedOutputStream(streamToClient);
            byte[] response;
            if (mput.matches()) {
                System.out.println("PUT REQUEST CAME");
                String shortResource = mput.group(1);
                String longResource = mput.group(2);
                String httpVersion = mput.group(3);

                Shard shard = this.loadBalancer.getShard(shortResource);
                response = shard.forwardWriteRequest(request, bytesRead, nodePort, streamToClient);
            } else if (mget.matches()) {
                String method = mget.group(1);
                String shortResource = mget.group(2);
                String httpVersion = mget.group(3);

                Shard shard = this.loadBalancer.getShard(shortResource);
                response = shard.forwardReadRequest(request, bytesRead, );
            } else {
                response = null;
            }
            return response;
        } catch (Exception e) {
            System.err.println("request handler error");
            return null;
        }
    }
}