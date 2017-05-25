package com.snooper;

import com.snooper.app.controller.*;

import javafx.beans.property.*;
import java.io.*;
import java.awt.*;

public class Pref implements Serializable {
	
	private static final long serialVersionUID = 0x10L;
	
	private String nickname;
	private boolean iconImageAutoSize;
	private boolean notifBalloon;
	
	private transient PrefController prefController;
	private transient BooleanProperty iconImageAutoSizeProperty;
	private transient BooleanProperty notifBalloonProperty;
	
	public Pref() {
		nickname = System.getProperty("user.name");
		iconImageAutoSize = true;
		notifBalloon = true;
	}
	
	public void init() {
		iconImageAutoSizeProperty = new SimpleBooleanProperty(iconImageAutoSize);
		notifBalloonProperty = new SimpleBooleanProperty(notifBalloon);
	}
	
	public void setController(PrefController prefController) {
		this.prefController = prefController;
		prefController.setPref(this);
		
		bind();
	}
	
	public BooleanProperty iconImageAutoSizeProperty() {
		return iconImageAutoSizeProperty;
	}
	
	public BooleanProperty notifBalloonProperty() {
		return notifBalloonProperty;
	}
	
	public void bind() {
		//remember the order of binding bidirectional as it is important in this program, the start values
		prefController.getIconAutoSizeCheckBox().selectedProperty().bindBidirectional(iconImageAutoSizeProperty);
		prefController.getNotificationBalloonCheckBox().selectedProperty().bindBidirectional(notifBalloonProperty);
		
		//TODO delete
		iconImageAutoSizeProperty.addListener((o,ov,nv) -> {
			System.out.println("icon image: " + nv);
			iconImageAutoSize = nv;
		});
		
		//TODO delete
		notifBalloonProperty.addListener((o,ov,nv) -> {
			System.out.println("notif balloon: " + nv);
			notifBalloon = nv;
		});
	}
	
	public void notif(TrayIcon trayIcon, String title, String message) {
		notif(trayIcon,title,message,TrayIcon.MessageType.NONE);
	}
	
	public void notif(TrayIcon trayIcon, String title, String message, TrayIcon.MessageType messageType) {
		if (notifBalloon) {
			trayIcon.displayMessage(title,message,messageType);
		} else {
			System.out.println("notif: " + title + " - " + message);
		}
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