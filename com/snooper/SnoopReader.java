package com.snooper;

import com.snooper.tray.*;

import java.io.*;
import java.util.*;

public class SnoopReader {
	
	public static String DEFAULT_SEPARATOR = "::";
	
	private File logFile;
	private List<SnoopKey> sKeys;
	
	private String separator;
	
	public SnoopReader(String logFilepath) {
		this(new File(logFilepath));
	}
	
	public SnoopReader(SLogFile sLogFile) {
		this(sLogFile.getFile());
	}
	
	public SnoopReader(File file) {
		this.separator = DEFAULT_SEPARATOR;
		sKeys = new ArrayList<SnoopKey>();
		load(file);
	}
	
	private void load(File logFile) {
		this.logFile = logFile;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
			String line;
			int counter = 0;
			while((line = reader.readLine()) != null) {
				String[] s = line.split(separator);
				sKeys.add(new SnoopKey(counter, s[0], s[1]));
				
				counter++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Util.notif(Snooper.TITLE, "Problem loading file (SnoopReader): " + logFile.getPath());
		}
	}
	
	public boolean withinRange(int index) {
		if (index >= 0 && index < getSnoopKeysSize()) return true;
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
	
	public int getSnoopKeysSize() {
		return sKeys.size();
	}
	
	public int getTypesPerMin() {
		int keys = getSnoopKeysSize();
		long duration = getSnoopKey(keys-1).getDate().getTime() - getSnoopKey(0).getDate().getTime();
		long minutes = Util.getMinutes(duration);
		
		if (minutes <= 0) {
			return keys;
		}
		
		int keysPerMinute = (int)((double) keys / minutes + 0.5f);
		return keysPerMinute;
	}
	
	public Map getCounts() {
		Map<SnoopKey, Integer> map = new HashMap<>();
		for (int i = 0; i < getSnoopKeysSize(); i++) {
			SnoopKey sKey = getSnoopKey(i);
			Integer storedValue = map.get(sKey);
			if (storedValue != null) {
				map.put(sKey,++storedValue);
			}  else {
				map.put(sKey,1);
			}
		}
		
		return map;
	}
	
	public List<Map.Entry<SnoopKey,Integer>> getCountsDescending() {
		List<Map.Entry<SnoopKey,Integer>> sortedEntries = new ArrayList<Map.Entry<SnoopKey,Integer>>(getCounts().entrySet());
		
		Collections.sort(sortedEntries,
			new Comparator<Map.Entry<SnoopKey,Integer>>() {
			@Override
			public int compare(Map.Entry<SnoopKey,Integer> e1, Map.Entry<SnoopKey,Integer> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});
		
		return sortedEntries;
	}
}