package com.snooper;

import com.snooper.tray.*;

import java.util.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;

public class Util {
	
	//create a file in the disk
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
	
	//get the current date within the given format
	public static String getCurrentDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	//gets the image from the path
	public static Image createImage(String path, String description) {
		URL imageURL = Util.class.getResource(path);
		
		if (imageURL == null) {
			System.err.println("Resource not found: " + path);
			return null;
		} else {
			return (new ImageIcon(imageURL, description)).getImage();
		}
	}
	
	//converts the string to file and pass it to hasInstance(File file) method
	public static boolean hasInstance(String filePath) {
		return hasInstance(new File(filePath));
	}
	
	//checks if the file has already been created
	public static boolean hasInstance(File file) {
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
	public static String getHourly() {
		return "Hourly update as of: " + Util.getCurrentDate(Snooper.perHourFormat);
	}
}