package com.snooper.tray;

import com.snooper.*;
import com.snooper.app.*;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyAdapter;

import java.util.logging.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Snooper extends NativeKeyAdapter implements Disposable {
  
	public static final String FOLDER = "snoop_log";
	public static final String DASH = "/";
	public static final String START_NAME = "log_";
	public static final String EXTENSION_NAME = ".slog";
	public static final int SECOND_IN_MILLI = 1_000;
	public static final int HOUR_IN_MILLI = SECOND_IN_MILLI * 60 * 60;
	public static final String fileNameFormat = "yyyyMMddHHmmssSSSS";
	public static final String perHourFormat = "yyyy, MM/dd, h:mma";
	public static final String TITLE = "Keyboard Snooper";
	public static final String AUTHOR = "doppelgunner";
	
	public static final String PREF_FOLDER = "data";
	public static final String PREF_NAME = "pref";
	public static final String PREF_EXTENSION_NAME = ".json";
	public static final String PREF_FILEPATH = PREF_FOLDER + DASH + PREF_NAME + PREF_EXTENSION_NAME;

	private File folderFile;
	private File keyStrokesFile;
	private File tempFile;
	private String keyStrokesFilePath;
	private String tempFilePath;
	
	private String startDate;
	
	private SnoopLogger sLogger;
	
	private TrayIcon trayIcon;
	private SystemTray tray;
	private PopupMenu popup;
	
	private boolean paused = false;
	
	private Pref pref;
	private static Snooper currentInstance;
	
	public static Snooper getInstance() {
		return currentInstance;
	}
	
	public Pref getPref() {
		return pref;
	}
	
	public TrayIcon getTrayIcon() {
		return trayIcon;
	}
	
	public Snooper() {
		currentInstance = this;
		pref = Util.loadPref();
		pref.init();
		
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
		tempFile = new File(tempFilePath);
		
		if (Util.hasInstance(tempFilePath)) {
			System.exit(0); //exit app if already has a temp file to log into
		}
		
		sLogger = new SnoopLogger(tempFilePath);
		
		Runtime.getRuntime().addShutdownHook(new Thread(()-> {
			dispose();
		}));
	}
	
	//init of system tray
	public void initSystemTray() {
		if (!SystemTray.isSupported()) {
			System.err.println("System tray is not supported");
		}
		tray = SystemTray.getSystemTray();
		trayIcon = new TrayIcon(Util.createAwtImage("images/snooper.png","Keyboard Snooper"));
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
		MenuItem exit = new MenuItem("Exit");
		
		popup.add(about);
		popup.addSeparator();
		popup.add(pause);
		popup.add(openCurrentFolder);
		popup.add(openCurrentLog);
		popup.add(clearCurrentLog);
		popup.addSeparator();
		popup.add(exit);
		
		trayIcon.setImageAutoSize(pref.isIconImageAutoSize());
		
		pref.iconImageAutoSizeProperty().addListener((o,ov,nv) -> {
			trayIcon.setImageAutoSize(nv);
		});
		
		trayIcon.setToolTip("Keyboard Snooper");
		trayIcon.setPopupMenu(popup);
		
		trayIcon.addActionListener(event -> {
			KSApplication.show();
		});
		
		about.addActionListener(event -> {
			Util.notif(TITLE, "by " + AUTHOR);
		});
		
		pause.addActionListener(event -> {
			paused = !paused;
			if (paused) {
				Util.notif(TITLE, "Pausing...");
				pause.setLabel("Continue");
			} else {
				Util.notif(TITLE, "Continuing...");
				pause.setLabel("Pause");
			}
		});
		
		openCurrentFolder.addActionListener(event -> {
			try {
				Desktop.getDesktop().open(folderFile);
			} catch (Exception ex) {
				Util.notif(TITLE, "Error opening current log folder..." + AUTHOR);
			}
		});
		
		openCurrentLog.addActionListener(event -> {
			Util.openFileDefault(sLogger.getLogFile());
		});
		
		clearCurrentLog.addActionListener(event -> {
			sLogger.clear();
		});
		
		exit.addActionListener(e -> {
			Util.notif(TITLE, "Exiting ...");
			tray.remove(trayIcon);
			System.exit(0);
		});
	}
	
	public void start() {
		Util.notif(TITLE, "Starting ...");
	}
	
    public void nativeKeyReleased(NativeKeyEvent e) {
        try {
			if (sLogger.getWriter() == null || paused) return;
			sLogger.snoopLog(NativeKeyEvent.getKeyText(e.getKeyCode()));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
    }

    public static void main(String[] args) {
		if (args.length > 0 && args[0].equals("test")) {
			test();
			return;
		}
		
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
	
	//method for testing args = test
	public static void test() {
		//test here
		
		/* //test serialization
		Pref pref = Util.loadPref();
		System.out.println(pref);
		*/
		
		//test SnoopLogger (encapsulation of logging)
		/*
		SnoopLogger sLogger = new SnoopLogger("test/test_temp.test");
		sLogger.snoopLog("abcdef");
		sLogger.snoopLog("12345");
		sLogger.dispose();
		sLogger.renameFile("test/test_final.test");
		*/
		
		//test SnoopReader Episode 12
		SnoopReader sReader = new SnoopReader(FOLDER + DASH + "log_201706011232550817.slog");
		sReader.printSnoopKeys();
		System.out.println("has same key, index [5 and 6]: " + sReader.hasSameKey(5,6));
		System.out.println("has same key, index [3 and 6]: " + sReader.hasSameKey(3,6));
		System.out.println("has same keys, indexes [0, 10 and 57]: " + sReader.hasSameKeys(0,10,57));
	}
	
	//called when the program is aout to exit or shutdown
	@Override
	public void dispose() {
		sLogger.dispose();
		sLogger.renameFile(keyStrokesFile);
	}
}