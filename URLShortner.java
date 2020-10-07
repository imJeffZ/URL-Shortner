import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class URLShortner { 
	// port to listen connection
	// 8026
	private static final int LOCALPORT = 8031;

	public static void main(String[] args) throws IOException {
		try {
			System.out.println("Server started.\nListening for connections on port : " + LOCALPORT + " ...\n");
			runServer(LOCALPORT); // never return
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}

	static void runServer(int localport) throws IOException {
		// Create a ServerSocket to listen for connections with
		ServerSocket serverConnect = new ServerSocket(localport);
		// we listen until user halts server execution
		while (true) {
			Socket clientSocket = null;
			try {
				// Wait for a connection on the local port
				System.out.println("Waiting for a client ...");
				clientSocket = serverConnect.accept();
				System.out.println("Accepted new connection: " + serverConnect);
				Thread t = new Thread(new URLConnectionHandler(clientSocket));
				t.start();
				System.out.println("thread spawned for new client.");
			} catch (Exception e) {
				clientSocket.close();
				e.printStackTrace();
			}
		}

	}

}
