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
import java.util.zip.CRC32;


class URLConnectionHandler extends Thread {
    final File WEB_ROOT = new File(".");
	final String DEFAULT_FILE = "index.html";
	final String FILE_NOT_FOUND = "404.html";
	final String METHOD_NOT_SUPPORTED = "not_supported.html";
	final String REDIRECT_RECORDED = "redirect_recorded.html";
	final String REDIRECT = "redirect.html";
	final String NOT_FOUND = "notfound.html";
	final String DATABASE = "/virtual/database.txt";
	final boolean VERBOSE = true;

    Socket clientSocket;
    public URLConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        Socket connect = this.clientSocket;
        BufferedReader in = null;

		
		try {
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			String input = in.readLine();

			if (VERBOSE) {
				System.out.println("first line: "+input);
			}
			Pattern pput = Pattern.compile("^PUT\\s+/\\?short=(\\S+)&long=(\\S+)\\s+(\\S+)$");
			Matcher mput = pput.matcher(input);
			Pattern pget = Pattern.compile("^(\\S+)\\s+/(\\S+)\\s+(\\S+)$");
			Matcher mget = pget.matcher(input);
			
			if (mput.matches()) {
				putHandler(mput, connect);
			} else if (mget.matches()) {
				getHandler(mget, connect);
			} else {
				notFoundHandler(connect);
			}
		} catch (Exception e) {
			System.err.println("Server error");
		} finally {
			try {
				in.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}
			
			if (VERBOSE) {
				System.out.println("Connection closed.\n");
			}
		}

	}

	
	private void putHandler(Matcher mput, Socket connect) {
		String shortResource=mput.group(1);
		String longResource=mput.group(2);
		String httpVersion=mput.group(3);
        PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		
		try {
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());
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
		} catch (Exception e) {
			System.err.println("Server error");
		} finally {
			try {
				out.close();
				connect.close(); // we close socket connection
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			if (VERBOSE) {
				System.out.println("Connection closed.\n");
			}
		}
		
	}

	private void getHandler(Matcher mget, Socket connect) {
		String method = mget.group(1);
		String shortResource = mget.group(2);
		String httpVersion = mget.group(3);
		String longResource = find(shortResource);

		if (longResource == null) {
			notFoundHandler(connect);
			return;
		}
		
		
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;

		try {

			File file = new File(WEB_ROOT, REDIRECT);
			int fileLength = (int) file.length();
			String contentMimeType = "text/html";


			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());
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
		}  catch (Exception e) {
			System.err.println("Server error");
			out.close();
		}
	}
		
	private void notFoundHandler(Socket connect) {
		PrintWriter out = null;
        BufferedOutputStream dataOut = null;
		try {

			File file = new File(WEB_ROOT, FILE_NOT_FOUND);
			int fileLength = (int) file.length();
			String content = "text/html";
			byte[] fileData = readFileData(file, fileLength);
				
			out = new PrintWriter(connect.getOutputStream());
			dataOut = new BufferedOutputStream(connect.getOutputStream());

			out.println("HTTP/1.1 404 File Not Found");
			out.println("Server: Java HTTP Server/Shortner : 1.0");
			out.println("Date: " + new Date());
			out.println("Content-type: " + content);
			out.println("Content-length: " + fileLength);
			out.println(); 
			out.flush(); 
			
			dataOut.write(fileData, 0, fileLength);
			dataOut.flush();
		}  catch (Exception e) {
			System.err.println("Server error");
			out.close();
		}
	}


	// private String longToShort(String longURL) {
    //     CRC32 crc = new CRC32();
    //     crc.update(longURL.getBytes());
	// 	String shortURL = Long.toHexString(crc.getValue());
    //     return shortURL;
	// }
    
	private String find(String shortURL){
		String longURL = null;
		try {
			File file = new File(DATABASE);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String [] map = line.split("\t");
				if(map[0].equals(shortURL)){
					longURL = map[1];
					break;
				}
			}
			fileReader.close();
		} catch (IOException e) {
			
		} 
		return longURL;
	}

	private void save(String shortURL, String longURL) {
		try {
			File file = new File(DATABASE);
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(shortURL + "\t" + longURL);
			pw.close();
		} catch (IOException e) {
			
		} 
		return;
	}
	
	private byte[] readFileData(File file, int fileLength) throws IOException {
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
