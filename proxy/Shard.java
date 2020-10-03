package proxy;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


class Shard {

    private int shardNumber;
    private ArrayList<String> hosts;

    public Shard(ArrayList<String> hosts, int shardNumber) {
        this.hosts = hosts;
        this.shardNumber = shardNumber;
    }

    
    
}