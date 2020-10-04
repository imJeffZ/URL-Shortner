package proxy;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Shard {

    private int shardNumber;
    private ArrayList<String> hosts;
    private int currentHost;

    public Shard(ArrayList<String> hosts, int shardNumber) {
        this.hosts = hosts;
        this.shardNumber = shardNumber;
        this.currentHost = 0;
    }

    private void setNextHost() {
        this.currentHost = (this.currentHost + 1) % this.hosts.size();
    }

    public Socket forwardReadRequest(byte[] request, int bytesRead, int nodePort) {
        System.out.print(String.format("Shard %d received read request, ", this.shardNumber));
        OutputStream streamToNode;
        Socket nodeSocket;
        String host;

        // Iterate through hosts until able to make successful connection
        while (true) {
            try {
                this.setNextHost();
                host = this.hosts.get(currentHost);
                nodeSocket = new Socket(host, nodePort);
                streamToNode = nodeSocket.getOutputStream();
                System.out.print(String.format("forwarding to host %s\n", this.shardNumber, host));
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            System.out.println(String.format("Shard %d reading from host %s\n", this.shardNumber, host));
            streamToNode.write(request, 0, bytesRead);
            streamToNode.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return nodeSocket;
    }

    public Socket forwardWriteRequest(byte[] request, int bytesRead, int nodePort) {
        ArrayList<Socket> nodeSockets = new ArrayList<Socket>();
        System.out.print(String.format("Shard %d received write request, forwarding to hosts", this.shardNumber));
        for (String host : this.hosts) {
            System.out.print(String.format(" %s", host));
            try {
                nodeSockets.add(new Socket(host, nodePort));
            } catch (UnknownHostException e) {
                System.out.print(":UNAVAILABLE");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.print(":UNAVAILABLE");
                e.printStackTrace();
            }
        }
        System.out.print(".\n");

        for (Socket nodeSocket : nodeSockets) {
            try {
                OutputStream streamToNode = nodeSocket.getOutputStream();
                streamToNode.write(request, 0, bytesRead);
                streamToNode.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return nodeSockets.get(0);
    }

}