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

    public InputStream forwardReadRequest(byte[] request, int bytesRead, Socket nodeSocket) {
        String host = this.hosts.get(currentHost);
        System.out.println(String.format("Shard %d received request, forwarding to host %s\n", this.shardNumber, host));
        this.currentHost = (this.currentHost + 1) % this.hosts.size();
        InputStream streamFromNode; OutputStream streamToNode;
        try {
            streamFromNode = nodeSocket.getInputStream();
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

        return streamFromNode;
    }

    public Byte[] forwardWriteRequest(byte[] request, int bytesRead, int nodePort) {
        return null;
    }

    
    
}