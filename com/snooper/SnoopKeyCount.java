package com.snooper;

public class SnoopKeyCount {
	
	private String key;
	private int count;
	
	public SnoopKeyCount(String key, int count) {
		this.key = key;
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "Key: " + key + ", Count: " + count; 
	}
	
	public String getKey() {
		return key;
	}
	
	public int getCount() {
		return count;
	}
}