package com.snooper;

import java.io.*;

public class Pref implements Serializable {
	
	private static final long serialVersionUID = 0x10L;
	
	private String nickname;
	private boolean iconImageAutoSize;
	private boolean notifBalloon;
	
	public Pref() {
		nickname = System.getProperty("user.name");
		iconImageAutoSize = true;
		notifBalloon = true;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void setIconImageAutoSize(boolean b) {
		this.iconImageAutoSize = b;
	}
	
	public void setNotifBalloon(boolean b) {
		this.notifBalloon = b;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public boolean isIconImageAutoSize() {
		return iconImageAutoSize;
	}
	
	public boolean isNotifBalloon() {
		return notifBalloon;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Pref:{\n");
		sb.append("\tnickname: " + nickname + "\n");
		sb.append("\ticonImageAutoSize: " + iconImageAutoSize + "\n");
		sb.append("\tnotifBalloon: " + notifBalloon + "\n");
		sb.append("}");
		return sb.toString();
	}
}