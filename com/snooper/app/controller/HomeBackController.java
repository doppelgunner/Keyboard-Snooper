package com.snooper.app.controller;

import com.snooper.app.*;

import javafx.scene.control.*;
import javafx.fxml.FXML;

public class HomeBackController extends Controller {
	
	@FXML private Button homeButton;
	
	@FXML
	public void initialize() {
		System.out.println("INIT: HomeBackController.java");
	}
	
	@FXML
	private void goHome() {
		KSApplication.switchHome();
	}
	
}