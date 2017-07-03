package com.snooper;

import com.snooper.tray.*;

import java.util.*;

public class SnoopKey {
	
	private int index;
	private String dateString;
	private String key;
	
	public SnoopKey(int index, String dateString, String key) {
		this.index = index;
		this.dateString = dateString;
		this.key = key;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getDateString() {
		return dateString;
	}
	
	public Date getDate() {
		return Util.toDate(getDateString(), Snooper.fileNameFormat);
	}
	
	public String toString() {
		return "[" + index + "] - " + Util.toString(dateString,Snooper.fileNameFormat,Snooper.perHourFormat) + " :: " + key.toUpperCase();
	}
	
	public boolean hasSameKey(SnoopKey sKey) {
		return key.equals(sKey.getKey());
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		return ((SnoopKey) o).key.equals(this.key);
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
}