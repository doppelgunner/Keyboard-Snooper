package com.snooper;

import com.snooper.tray.*;

public class SnoopKey {
	
	private int index;
	private String date;
	private String key;
	
	public SnoopKey(int index, String date, String key) {
		this.index = index;
		this.date = date;
		this.key = key;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getDate() {
		return date;
	}
	
	public String toString() {
		return "[" + index + "] - " + Util.toString(date,Snooper.fileNameFormat,Snooper.perHourFormat) + " :: " + key.toUpperCase();
	}
	
	public boolean hasSameKey(SnoopKey sKey) {
		return key.equals(sKey.getKey());
	}
}