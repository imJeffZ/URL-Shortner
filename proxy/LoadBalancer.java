package proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import javax.imageio.IIOException;

public class LoadBalancer {
    private final String HOSTS_FILE = "./proxy/hosts.txt";
    Deque<String> hostsQueue;
    public ArrayList<String> hostsArray;
    private static ArrayList<Shard> shards;

    LoadBalancer() throws IOException {
        this.hostsArray = readHosts(HOSTS_FILE);
        // this.hostsQueue = new ArrayDeque<String>(hostsArray);
        assignHostsToShard(hostsArray);
    }
    
    // 1. Switch load balancer to eventually use the 'usage' script output to select the best host.

    // Always have 1/5 hosts as backup

    Shard getShard(String shortURL) {
        int hash = Hash.getHash(shortURL, shards.size());
        return shards.get(hash);
    }

    /***
     * get load balanced host using round robin
     * @return host
     */
    String getHost() {
        String host = this.hostsQueue.removeFirst();
        this.hostsQueue.addLast(host);
        return host;
    }

    void addHost(String host) {}
    void removeHost(String host) {}
    
    private ArrayList<String> readHosts(final String hostFile) throws IOException {
        String s;
        ArrayList<String> hosts = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(hostFile))) {
            while ((s = br.readLine()) != null) {
                hosts.add(s);
            }
        } catch (final IIOException exc) {
            System.err.println("I/O Error in reading host file: " + exc);
        }
        return hosts;
    }

    private void assignHostsToShard(ArrayList<String> hosts) {
        int numHosts = hosts.size();
        shards = new ArrayList<Shard>();
        ArrayList<ArrayList<String>> shardLists = new ArrayList<ArrayList<String>>();
        int numShards = (int) Math.floor(numHosts / 2);
        System.out.println(String.format("Assigning %d nodes to %d shards."));

        for (int i = 0; i < numShards; i++)
            shardLists.add(new ArrayList<String>());

        for (int i = 0; i < numHosts; i++) {
            shardLists.get(i % numShards).add(hosts.get(i));
            System.out.println(String.format("Node %d assigned to Shard %d", i, i % numShards));
        }

        for (int i = 0; i < numShards; i++)
            shards.add(new Shard(shardLists.get(i), i));
    }

}
