import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLShortner { 
	// port to listen connection
	private static final int PORT = 8026;
	
	// verbose mode
	private static final boolean verbose = true;

	public static void main(String[] args) {
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			
			// we listen until user halts server execution
			while (true) {
				if (verbose) { System.out.println("Connecton opened. (" + new Date() + ")"); }
				URLShortnerConnectionHandler connectionHandler = new URLShortnerConnectionHandler(serverConnect.accept());
			}
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

}
