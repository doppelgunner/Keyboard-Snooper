package com.snooper.app;

import com.snooper.app.controller.*;
import com.snooper.*;
import com.snooper.tray.*;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.application.*;
import java.net.*;

public class KSApplication extends Application {
	
	public static final int WIDTH = 640, HEIGHT = 400;
	
	private static Stage primaryStage;
	private static Scene mainScene, prefScene, aboutScene, snoopLogsScene;
	
	private static MainController mainController;
	private static PrefController prefController;
	private static AboutController aboutController;
	private static SnoopLogsController snoopLogsController;
	
	private static Snooper snooper;
	
	public static MainController getMainController() {
		return mainController;
	}
	
	public static PrefController getPrefController() {
		return prefController;
	}
	
	public static AboutController getAboutController() {
		return aboutController;
	}
	
	public static SnoopLogsController getSnoopLogsController() {
		return snoopLogsController;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		//load fxmls
		
		//Parent rootMain = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
		//Parent rootPref = FXMLLoader.load(getClass().getResource("/fxml/Pref.fxml"));
		//Parent rootAbout = FXMLLoader.load(getClass().getResource("/fxml/About.fxml"));
		
		FXMLLoader loader = createLoader(getClass().getResource("/fxml/Main.fxml"));
		Parent rootMain = loader.load();
		mainController = loader.getController();
		
		loader = createLoader(getClass().getResource("/fxml/Pref.fxml"));
		Parent rootPref = loader.load();
		prefController = loader.getController();
		
		loader = createLoader(getClass().getResource("/fxml/About.fxml"));
		Parent rootAbout = loader.load();
		aboutController = loader.getController();
		
		loader = createLoader(getClass().getResource("/fxml/SnoopLogs.fxml"));
		Parent rootSnoopLogs = loader.load();
		snoopLogsController = loader.getController();
		
		//make scenes
		mainScene = new Scene(rootMain, WIDTH, HEIGHT);
		prefScene = new Scene(rootPref, WIDTH, HEIGHT);
		aboutScene = new Scene(rootAbout, WIDTH, HEIGHT);
		snoopLogsScene = new Scene(rootSnoopLogs, WIDTH, HEIGHT);
		
		//stage configs
		primaryStage.getIcons().add(Util.createJavaFXImage("images/snooper.png"));
		primaryStage.setResizable(false);
		
		//assigne default scene
		setScene(mainScene);
		
		Platform.setImplicitExit(false); //use to hide only when x is pressed in the menu bar
		
		setSnooper();
	}
	
	public void setSnooper() {
		snooper = snooper.getInstance();
		snooper.getPref().setController(getPrefController());
	}
	
	public static FXMLLoader createLoader(URL url) {
		FXMLLoader loader = new FXMLLoader(url);
		return loader;
	}
	
	public static void show() {
		run(() -> primaryStage.show());
	}
	
	public static void hide() {
		run(() -> primaryStage.hide());
	}
	
	public static void run(Runnable runnable) {
		Platform.runLater(runnable);
	}
	
	public static void setScene(Scene scene) {
		if (scene == null) return;
		run(() -> primaryStage.setScene(scene));
	}
	
	public static void switchHome() {
		setScene(mainScene);
	}
	
	public static void switchPref() {
		setScene(prefScene);
	}
	
	public static void switchAbout() {
		setScene(aboutScene);
	}
	
	public static void switchSnoopLogs() {
		setScene(snoopLogsScene);
	}
}