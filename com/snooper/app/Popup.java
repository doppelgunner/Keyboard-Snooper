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
	
	public static final String EMAIL_POPUP_FXML = "/fxml/EmailPopup.fxml";
	
	public Popup(FXMLLoader loader) {
		try {
			stage = new Stage(StageStyle.UTILITY);
			root = loader.load();
			controller = loader.getController();
			controller.setStage(stage);
			
			Scene scene = new Scene(root);
			
			stage.setScene(scene);
			stage.sizeToScene();
			
			stage.setAlwaysOnTop(true);
			stage.initOwner(KSApplication.getStage());
			stage.initModality(Modality.WINDOW_MODAL);
		} catch (Exception e) {
			Util.notif(Snooper.TITLE, "Problem with popup...");
		}
	}
	
	public void show() {
		stage.showAndWait();
	}
	
	public void close() {
		stage.close();
	}
	
	public void sendToController(Object object) {
		controller.send(object);
	}
}