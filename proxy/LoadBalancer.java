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

    LoadBalancer() throws IOException {
        ArrayList<String> hostsArray = readHosts(HOSTS_FILE);
        this.hostsQueue = new ArrayDeque<String>(hostsArray);
    }
    
    // Switch load balancer to eventually use the 'usage' script output to select the best host.
    // Always have 1/5 hosts as 

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
}
