package com.snooper.app;

import com.snooper.app.controller.*;
import com.snooper.tray.*;
import com.snooper.*;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

public class Popup {
	
	private Stage stage;
	private Parent root;
	private Controller controller;
	
	public static final String ANALYZE_POPUP_FXML = "/fxml/AnalyzePopup.fxml";
	public static final String EMAIL_POPUP_FXML = "/fxml/EmailPopup.fxml";
	
	public Popup(FXMLLoader loader) {
		try {
			stage = new Stage(StageStyle.TRANSPARENT);
			root = loader.load();
			controller = loader.getController();
			controller.setStage(stage);
			
			Scene scene = new Scene(root);
			
			stage.setScene(scene);
			stage.setResizable(false);
			stage.sizeToScene();
			
			stage.initOwner(KSApplication.getStage());
		} catch (Exception e) {
			Util.notif(Snooper.TITLE, "Problem with popup...");
		}
	}
	
	public void showAndWait() {
		stage.setAlwaysOnTop(true);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
	
	public void show() {
		stage.setAlwaysOnTop(false);
		stage.initModality(Modality.NONE);
		stage.show();
	}
	
	public void close() {
		stage.close();
	}
	
	public void sendToController(Object object) {
		controller.send(object);
	}
}