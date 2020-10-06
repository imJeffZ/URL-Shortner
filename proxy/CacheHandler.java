package proxy;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class CacheHandler {

    private byte[] RedirectPage;
    private int RedirectPageLength;
    static final File WEB_ROOT = new File(".");
    static final String REDIRECT = "redirect.html";
    HashMap<String, String> cache = null;
    static final String contentMimeType = "text/html";

    public CacheHandler() {
        this.cache = new HashMap<String, String>();
        try {
            this.initFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initFiles() throws IOException {
        // REDIRECT file init
        File file = new File(WEB_ROOT, REDIRECT);
        this.RedirectPageLength = (int) file.length();
        this.RedirectPage = readFileData(file, this.RedirectPageLength);
    }

    public String checkLocalCache(String shortResource) {
        System.out.println("Checking local cache");
        String longResource = find(shortResource);
        if (longResource != null) {
            System.out.println("data found on cache.");
            return longResource;
        } else {
            return null;
        }
    }

    public void replyToClient(String longResource, OutputStream streamToClient) {
        PrintWriter out = new PrintWriter(streamToClient);
        BufferedOutputStream dataOut = new BufferedOutputStream(streamToClient);
        try {
            out.println("HTTP/1.1 301 Moved Permanently");
            out.println("HTTP/1.1 307 Temporary Redirect");
            out.println(longResource);
            out.println("Server: Java HTTP Server/Shortner : 1.0");
            out.println("Date: " + new Date());
            out.println("Content-type: " + contentMimeType);
            out.println("Content-length: " + this.RedirectPageLength);
            out.println();
            out.flush();

            dataOut.write(this.RedirectPage, 0, this.RedirectPageLength);

            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String shortURL, String longURL) {
        this.cache.put(shortURL, longURL);
    }

    public void remove(String shortURL) {
        this.cache.remove(shortURL);
    }

    private String find(String shortURL) {
        if (this.cache.containsKey(shortURL)) {
            return this.cache.get(shortURL);
        } else {
            return null;
        }
    }

    private static byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }

}
