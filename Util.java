import java.util.*;
import java.io.*;
import java.text.*;

public class Util {
	public static File createFile(String filename) {
		File file = new File(filename);
		File parentFile = file.getParentFile();
		try {
			if (parentFile != null) {
				parentFile.mkdirs();
			}
			if (file.createNewFile()) {
				System.out.println("File created: " + filename);
			} else {
				System.out.println("Error creating file: " + filename);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return file;
	}
	
	public static String getCurrentDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}
}