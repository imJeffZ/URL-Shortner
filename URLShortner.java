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

<<<<<<< HEAD
	static void runServer(int localport) throws IOException {
		// Create a ServerSocket to listen for connections with
		ServerSocket serverConnect = new ServerSocket(localport);
		// we listen until user halts server execution
		while (true) {
			Socket clientSocket = null;
=======
	public static void handle(Socket connect) {
		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			String input = in.readLine();
			
			if(verbose)System.out.println("first line: "+input);

			Pattern pingpattern = Pattern.compile("^(\\S+)\\s+/\\?\\?\\s+(\\S+)$");
			Matcher pingmatcher = pingpattern.matcher(input);
			if (pingmatcher.matches()){
				byte[] response = "OK".getBytes();

				out.println("HTTP/1.1 200 OK");
				out.println("Content-Length: 2");
				out.println("Content-Type: text/plain");
				out.println();
				out.flush();

				dataOut.write(response, 0, response.length);
				dataOut.flush();
				// in.close();
				// out.close();
				// connect.close(); // we close socket connection

				// return;
			}

			Pattern pput = Pattern.compile("^PUT\\s+/\\?short=(\\S+)&long=(\\S+)\\s+(\\S+)$");
			Matcher mput = pput.matcher(input);
			if(mput.matches()){
				String shortResource=mput.group(1);
				String longResource=mput.group(2);
				String httpVersion=mput.group(3);

				save(shortResource, longResource);

				File file = new File(WEB_ROOT, REDIRECT_RECORDED);
				int fileLength = (int) file.length();
				String contentMimeType = "text/html";
				//read content to return to client
				byte[] fileData = readFileData(file, fileLength);
					
				out.println("HTTP/1.1 200 OK");
				out.println("Server: Java HTTP Server/Shortner : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + contentMimeType);
				out.println("Content-length: " + fileLength);
				out.println(); 
				out.flush(); 

				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
			} else {
				Pattern pget = Pattern.compile("^(\\S+)\\s+/(\\S+)\\s+(\\S+)$");
				Matcher mget = pget.matcher(input);
				if(mget.matches()){
					String method=mget.group(1);
					String shortResource=mget.group(2);
					String httpVersion=mget.group(3);

					String longResource = find(shortResource);
					if(longResource!=null){
						File file = new File(WEB_ROOT, REDIRECT);
						int fileLength = (int) file.length();
						String contentMimeType = "text/html";
	
						//read content to return to client
						byte[] fileData = readFileData(file, fileLength);
						
						// out.println("HTTP/1.1 301 Moved Permanently");
						out.println("HTTP/1.1 307 Temporary Redirect");
						out.println("Location: "+longResource);
						out.println("Server: Java HTTP Server/Shortner : 1.0");
						out.println("Date: " + new Date());
						out.println("Content-type: " + contentMimeType);
						out.println("Content-length: " + fileLength);
						out.println(); 
						out.flush(); 
	
						dataOut.write(fileData, 0, fileLength);
						dataOut.flush();
					} else {
						File file = new File(WEB_ROOT, FILE_NOT_FOUND);
						int fileLength = (int) file.length();
						String content = "text/html";
						byte[] fileData = readFileData(file, fileLength);
						
						out.println("HTTP/1.1 404 File Not Found");
						out.println("Server: Java HTTP Server/Shortner : 1.0");
						out.println("Date: " + new Date());
						out.println("Content-type: " + content);
						out.println("Content-length: " + fileLength);
						out.println(); 
						out.flush(); 
						
						dataOut.write(fileData, 0, fileLength);
						dataOut.flush();
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Server error");
		} finally {
>>>>>>> 048eb65d425574c0e31f97a03a5a52a4ba96f849
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
