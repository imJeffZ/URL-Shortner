package proxy;

import java.util.HashMap;
import java.io.BufferedOutputStream;
import java.io.File;
import java.util.Date;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class CacheHandler {

    private byte[] RedirectPage;
    private int RedirectPageLength;
    static final File WEB_ROOT = new File(".");
    static final String REDIRECT = "redirect.html";
    private static final int SIZE = 1000;
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
        String longResource = find(shortResource);
        if (longResource != null) {
            System.out.println("data found on cache.");
            return longResource;
            // return null;
        } else {
            return null;
        }
    }

    public void replyToClient(String longResource, OutputStream streamToClient) {
        PrintWriter out = new PrintWriter(streamToClient);
        BufferedOutputStream dataOut = new BufferedOutputStream(streamToClient);
        try {
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
        if (this.cache.size() < SIZE)
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
