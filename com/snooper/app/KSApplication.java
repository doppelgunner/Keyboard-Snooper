package com.snooper.app;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.application.*;

public class KSApplication extends Application {
	
	public static final int WIDTH = 640, HEIGHT = 400;
	
	private static Stage primaryStage;
	private static Scene mainScene;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		//load fxmls
		
		Parent rootMain = FXMLLoader.load(getClass().getResource("/fxml/Main.fxml"));
		
		//make scenes
		mainScene = new Scene(rootMain, WIDTH, HEIGHT);
		
		//stage configs
		primaryStage.getIcons().add(new Image("/images/snooper.png"));
		primaryStage.setResizable(false);
		
		//assigne default scene
		setScene(mainScene);
		
		Platform.setImplicitExit(false); //use to hide only when x is pressed in the menu bar
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
}