package com.snooper.app.controller;

import com.snooper.app.*;

import javafx.fxml.FXML;

public class MainController {
	
	@FXML
	public void initialize() {
		System.out.println("INIT: MainController.java");
	}
	
	@FXML
	private void goPref() {
		KSApplication.switchPref();
	}
	
	@FXML
	private void goAbout() {
		KSApplication.switchAbout();
	}
	
	@FXML
	private void goSnoopLogs() {
		KSApplication.switchSnoopLogs();
	}
}