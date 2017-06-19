package com.snooper.app.controller;

import com.snooper.*;
import com.snooper.tray.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.value.*;

public class PrefController extends Controller {
	
	private Pref pref;
	
	@FXML private CheckBox iconAutoSizeCheckBox;
	@FXML private CheckBox notificationBalloonCheckBox;
	
	public CheckBox getIconAutoSizeCheckBox() {
		return iconAutoSizeCheckBox;
	}
	
	public CheckBox getNotificationBalloonCheckBox() {
		return notificationBalloonCheckBox;
	}
	
	@FXML
	public void initialize() {
		System.out.println("INIT: PrefController.java");
	}
	
	public void setPref(Pref pref) {
		this.pref = pref;
	}
	
	@FXML
	private void savePref() {
		Util.notif(Snooper.TITLE, "Saving pref...");
		boolean saved = Util.savePref(pref);
		System.out.println("saved pref: " + saved);
	}

}