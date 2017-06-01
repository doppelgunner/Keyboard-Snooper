package com.snooper;

import com.snooper.tray.*;

import java.io.*;
import java.util.*;

public class SnoopReader {
	
	private File logFile;
	private List<SnoopKey> sKeys;
	
	private String separator;
	
	public SnoopReader(String logFilepath) {
		this.separator = "::";
		sKeys = new ArrayList<SnoopKey>();
		load(logFilepath);
	}
	
	private void load(String logFilepath) {
		this.logFile = new File(logFilepath);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
			String line = reader.readLine();
			int counter = 0;
			while(line != null) {
				String[] s = line.split(separator);
				sKeys.add(new SnoopKey(counter, s[0], s[1]));
				
				counter++;
				line = reader.readLine();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Util.notif(Snooper.TITLE, "Problem loading file (SnoopReader): " + logFilepath);
		}
	}
	
	public boolean withinRange(int index) {
		if (index >= 0 && index < sKeys.size()) return true;
		return false;
	}
	
	public SnoopKey getSnoopKey(int index) {
		if (!withinRange(index)) return null;
		return sKeys.get(index);
	}
	
	public String getSnoopKeysString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sKeys.size(); i++) {
			sb.append(sKeys.get(i) + "\n");
		}
		return sb.toString();
	}
	
	public void printSnoopKeys() {
		System.out.println(getSnoopKeysString());
	}
	
	public boolean hasSameKey(int i1, int i2) {
		if (!withinRange(i1) && !withinRange(i2)) return false;
		return getSnoopKey(i1).hasSameKey(getSnoopKey(i2));
	}
	
	public boolean hasSameKeys(int ... indexes) {
		for (int i = 1; i < indexes.length; i++) {
			if (!hasSameKey(indexes[i],indexes[i-1])) return false;
		}
		
		return true;
	}
}