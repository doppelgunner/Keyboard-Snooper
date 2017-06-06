package com.snooper;

import com.snooper.*;
import com.snooper.tray.*;

import java.util.*;
import java.io.*;

public class SLogFile {
	
	private String filepath;
	private String filename;
	private String dateString;
	private Date date;
	private String dateStringPretty;
	
	public SLogFile(String filepath) {
		this(new File(filepath));
	}
	
	public SLogFile(File file) {
		this.filepath = file.getPath();
		this.filename = file.getName();
		findDate();
	}
	
	private void findDate() {
		if (filename.contains("temp")) return;
		dateString = filename.substring(filename.lastIndexOf('_') + 1, filename.lastIndexOf('.'));
		date = Util.toDate(dateString,Snooper.fileNameFormat);
		
		dateStringPretty = Util.toString(date, Snooper.perHourFormat);
	}
	
	public String getFilepath() {
		return filepath;
	}
	
	public File getFile() {
		return new File(filepath);
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getDateString() {
		return dateString;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String toString() {
		if (dateStringPretty == null || dateStringPretty.isEmpty()) return "SLOG: temp";
		return "SLOG: " + dateStringPretty;
	}
}