package proxy;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicReference;
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
            System.out.println("inside run method.");
            // init streams from client
            streamFromClient = clientSocket.getInputStream();
            streamToClient = clientSocket.getOutputStream();

            int bytesReadIncoming = streamFromClient.read(request);
            nodeSocket = handleRequest(bytesReadIncoming);
            System.out.println("read from client complete..");
            streamFromNode = nodeSocket.getInputStream();

            int bytesRead;
            // this current thread reads response from node and forwards to client.
            try {
                System.out.println("Reading from node.");
                while (streamFromNode == null)
                    continue;
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    /*
     * close client and socket connection.
     */
    private void closeConnections() {
        if (nodeSocket != null)
            try {
                nodeSocket.close();
                System.out.println("Close nodesocket.");
                if (clientSocket != null)
                    clientSocket.close();
                System.out.println("Close clientsocket.");
            } catch (IOException e) {
                System.out.println("error closing socket connections.");
            }
    }

    /*
     * handle incoming request from client.
     */
    public Socket handleRequest(int bytesRead) {
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

            if (mput.matches()) {
                return putHandler(mput, bytesRead);
            } else if (mget.matches()) {
                System.out.println("sending to get handler.");
                return getHandler(mget, bytesRead);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("request handler error");
            return null;
        }
    }

    /*
     * handle put requests from client and send them of to the correct shrad to be
     * written.
     */
    private Socket putHandler(Matcher mput, int bytesRead) {
        System.out.println("PUT REQUEST CAME");
        String shortResource = mput.group(1);

        Shard shard = this.loadBalancer.getShard(shortResource);
        return shard.forwardWriteRequest(request, bytesRead, nodePort);
    }

    /*
     * handle get requests from client and send them of to the correct shrad to be
     * read from.
     */
    private Socket getHandler(Matcher mget, int bytesRead) {
        System.out.println("GET REQUEST CAME");
        String shortResource = mget.group(2);
        Shard shard = this.loadBalancer.getShard(shortResource);
        return shard.forwardReadRequest(request, bytesRead, nodePort);
    }
}