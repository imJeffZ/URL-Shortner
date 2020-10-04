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

    private String getCurrentHost() {
        return this.hosts.get(this.currentHost);
    }

    public Socket forwardReadRequest(byte[] request, int bytesRead, int nodePort) {
        System.out.println(String.format("Shard %d received read request", this.shardNumber));
        OutputStream streamToNode;
        Socket nodeSocket;
        String host;

        // Iterate through hosts until able to make successful connection
        while (true) {
            this.setNextHost();
            try {
                host = this.getCurrentHost();
                nodeSocket = new Socket(host, nodePort);
                streamToNode = nodeSocket.getOutputStream();
                System.out.println(String.format(" ✅ ACCEPTED: %s\n", host));
                break;
            } catch (IOException e) {
                System.out.println(String.format(" ❌ UNAVAILABLE: %s\n", this.getCurrentHost()));
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
        System.out.println(String.format("Shard %d received write request, forwarding to hosts:", this.shardNumber));
        for (String host : this.hosts) {
            System.out.print(String.format("  • %s", host));
            try {
                nodeSockets.add(new Socket(host, nodePort));
                System.out.print(": ✅ ACCEPTED\n");
            } catch (IOException e) {
                System.out.print(": ❌ UNAVAILABLE\n");
            }
        }
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