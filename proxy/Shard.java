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

    public Socket forwardReadRequest(byte[] request, int bytesRead, int nodePort) {
        String host = this.hosts.get(currentHost);
        System.out.println(String.format("Shard %d received read request, forwarding to host %s\n", this.shardNumber, host));
        this.currentHost = (this.currentHost + 1) % this.hosts.size();
        OutputStream streamToNode; Socket nodeSocket;
        try {
            nodeSocket = new Socket(host, nodePort);
            streamToNode = nodeSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            System.out.println(String.format("Shard %d writing to host %s\n", this.shardNumber, host));
            streamToNode.write(request, 0, bytesRead);
            streamToNode.flush();
            streamToNode.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return nodeSocket;
    }

    public Socket forwardWriteRequest(byte[] request, int bytesRead, int nodePort) {
        System.out.print(String.format("Shard %d received write request, forwarding to hosts", this.shardNumber));
        for (String host : this.hosts)
            System.out.print(String.format("%s ", host));
        System.out.print("\n");
        return null;
    }

    
    
}