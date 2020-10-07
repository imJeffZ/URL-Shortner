import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;

class URLDataAccessObject {
    final String DATABASE = "/virtual/database.txt";

    String find(String shortURL) {
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

	void save(String shortURL, String longURL) {
		try {
			File file = new File(DATABASE);
			// Write to beginning of file
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(shortURL + "\t" + longURL);
			pw.close();
		} catch (IOException e) {
			
		} 
		return;
	}
	
	byte[] readFileData(File file, int fileLength) throws IOException {
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
