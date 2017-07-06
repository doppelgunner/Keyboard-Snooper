package com.snooper.app.controller;

import com.snooper.*;
import com.snooper.tray.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.value.*;

import java.io.*;
import com.snooper.app.*;

public class PrefController extends Controller {
	
	private Pref pref;
	
	@FXML private CheckBox iconAutoSizeCheckBox;
	@FXML private CheckBox notificationBalloonCheckBox;
	@FXML private ComboBox stylesheetFileComboBox;
	
	public CheckBox getIconAutoSizeCheckBox() {
		return iconAutoSizeCheckBox;
	}
	
	public CheckBox getNotificationBalloonCheckBox() {
		return notificationBalloonCheckBox;
	}
	
	@FXML
	public void initialize() {
		System.out.println("INIT: PrefController.java");
		
		stylesheetFileComboBox.getSelectionModel().selectedItemProperty().addListener((o,ov,nv) -> {
			File cssFile = new File(KSApplication.STYLE_FOLDER + "/" + nv + KSApplication.STYLE_EXTENSION);
			pref.setStylesheetFile(cssFile);
			
			//reload stylesheets set in pref
			KSApplication.removeStylesheets();
			KSApplication.loadStylesheets();
		});
	}
	
	public void setPref(Pref pref) {
		this.pref = pref;
		
		stylesheet();//load
	}
	
	@FXML
	private void savePref() {
		Util.notif(Snooper.TITLE, "Saving pref...");
		boolean saved = Util.savePref(pref);
		System.out.println("saved pref: " + saved);
	}
	
	private void stylesheet() {
		File cssFile = pref.getStylesheetFile();
		String chosen = "None";
		if (Util.hasInstance(cssFile)) {
			chosen = Util.removeExtension(cssFile.getName());
			stylesheetFileComboBox.getItems().add(chosen);
		}
		
		stylesheetFileComboBox.getItems().add("None");
		stylesheetFileComboBox.getSelectionModel().select(chosen);
	}
	
	@FXML
	private void openStylesheetComboBox() {
		File[] files = Util.getFiles(KSApplication.STYLE_FOLDER, KSApplication.STYLE_EXTENSION);
		for (int i = 0; i < files.length; i++) {
			String name = Util.removeExtension(files[i].getName());
			if (stylesheetFileComboBox.getItems().contains(name)) {
				continue;
			}
			stylesheetFileComboBox.getItems().add(name);
		}
	}

}