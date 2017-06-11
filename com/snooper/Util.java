package com.snooper;

import com.snooper.tray.*;

import java.util.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;

import com.google.gson.Gson;

public class Util {
	
	//create a file in the disk
	public static File createFile(String filename) {
		File file = new File(filename);
		checkParentFile(file);
		try {
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
	
	public static void checkParentFile(File file) {
		File parentFile = file.getParentFile();
		try {
			if (parentFile != null) {
				parentFile.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//get the current date within the given format
	public static String getCurrentDate(String format) {
		return toString(new Date(), format);
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
	
	public static void goLink(String link) {
		try {
			Desktop.getDesktop().browse(new URI(link));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static boolean serialize_json(Serializable serializable, String filepath) {
		Gson gson = new Gson();
		File file = new File(filepath + (filepath.contains(".json") ? "" : ".json"));
		checkParentFile(file);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			gson.toJson(serializable, writer);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static <T>T deserialize_json(Class<T> c, String filepath) {
		Gson gson = new Gson();
		try (BufferedReader reader = new BufferedReader(new FileReader(filepath + (filepath.contains(".json") ? "" : ".json")))) {
			return gson.fromJson(reader,c);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static Pref loadPref() {
		Pref pref = deserialize_json(Pref.class, Snooper.PREF_FILEPATH);
		if (pref == null) {
			pref = createPref();
		}
		return pref;
	}
	
	public static boolean savePref(Pref pref) {
		if (pref == null) return false;
		
		serialize_json(pref,Snooper.PREF_FILEPATH);
		return true;
	}
	
	public static Pref createPref() {
		Pref pref = new Pref();
		serialize_json(pref,Snooper.PREF_FILEPATH);
		return pref;
	}
	
	public static void notif(String title, String message) {
		Snooper snooper = Snooper.getInstance();
		Pref pref = snooper.getPref();
		pref.notif(snooper.getTrayIcon(), title, message);
	}
	
	public static File[] getSLogFiles(File parentDir) {
		return parentDir.listFiles((dir,name) -> {
			return name.toLowerCase().endsWith(Snooper.EXTENSION_NAME);
		});
	}
	
	public static File[] getSLogFiles(String parentDir) {
		return getSLogFiles(new File(parentDir));
	}
	
	//optional to reverse array
	public static <T> void reverse(T[] array) {
		Collections.reverse(Arrays.asList(array));
	}
	
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean withinRange(int bound1, int bound2, int test) {
		if (test >= bound1 && test <= bound2) return true;
		if (test <= bound1 && test >= bound2) return true;
		return false;
	}
	
	public static Date toDate(String dateString, String format) {
		Date date = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			date = dateFormat.parse(dateString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return date;
	}
	
	public static String toString(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	//date in string to another format in string
	public static String toString(String dateString, String formatParse, String formatTo) {
		Date date = toDate(dateString,formatParse);
		return toString(date,formatTo);
	}
	
	public static Image createAwtImage(String path, String description) {
		return (new ImageIcon(path,description)).getImage();
	}
	
	public static javafx.scene.image.Image createJavaFXImage(String path) {
		return new javafx.scene.image.Image("file:" + path);
	}
}