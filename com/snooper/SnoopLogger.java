package com.snooper;

import com.snooper.tray.*;
import com.snooper.*;

import java.io.*;

public class SnoopLogger implements Disposable {
	
	private File logFile;
	private BufferedWriter writer;
	
	private String separator;
	
	private javax.swing.Timer flushTimer;
	private boolean logged;
	
	public SnoopLogger(String logFilepath) {
		this.separator = "::";
		
		this.flushTimer = new javax.swing.Timer(Snooper.SECOND_IN_MILLI * 5, e-> {
			try {
				if (logged) {
					logged = false;
					flush();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		load(logFilepath);
		this.flushTimer.start();
	}
	
	private void load(String logFilepath) {
		this.logFile = Util.createFile(logFilepath);
		try {
			writer = new BufferedWriter(new FileWriter(logFile));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public BufferedWriter getWriter() {
		return writer;
	}
	
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	public File getLogFile() {
		return logFile;
	}
	
	private void logged() {
		logged = true;
	}
	
	public void snoopLog(String s) {
		try {
			writer.write(Util.getCurrentDate(Snooper.fileNameFormat) + separator + s);
			writer.newLine();
			logged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//extra
	public void logln(String s) {
		try {
			writer.write(s);
			writer.newLine();
			logged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void log(String s) {
		try {
			writer.write(s);
			logged();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void flush() {
		try {
			writer.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void clear() {
		try {
			Util.notif(Snooper.TITLE, "Overwriting temp log...");
			writer.close();
			load(logFile.getPath());
		} catch (Exception ex) {
			Util.notif(Snooper.TITLE, "Error clearing current log...");
		}
	}
	
	@Override
	public void dispose() {
		System.out.println("SnoopLogger.java Disposing...");
		if (writer == null) return;
		try {
			writer.close();
		} catch (Exception ex) {
			Util.notif(Snooper.TITLE, "Error closing writer...");
		}
	}
	
	public void renameFile(File file) {
		if (Util.hasInstance(file)) {
			file.delete();
		}
		
		logFile.renameTo(file);
	}
	
	public void renameFile(String filepath) {
		renameFile(new File(filepath));
	}
}