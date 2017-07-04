package com.snooper.app.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.stage.*;
import javafx.scene.*;

import com.snooper.*;

public class TitleBarController extends Controller {
	
	@FXML private HBox titleBarHBox;
	@FXML private ImageView iconImageView;
	@FXML private Label titleLabel;
	@FXML private HBox buttonsHBox;
	@FXML private Button xButton;
	
	@FXML public void initialize() {
		System.out.println("INIT: TitleBarController.java");
		
		setTitle("Keyboard Snooper");
		drag();//add drag functionality to custom title bar
		
		//set icon - we want 16x16
		iconImageView.setFitWidth(16);
		setIcon(Util.createJavaFXImage("images/snooper.png"));
		
		//later we will use css instead of this
		//setXButton(); //TODO delete
	}
	
	private double mx, my;
	private void drag() {
		titleBarHBox.setOnMousePressed(me -> {
			Window window = titleBarHBox.getScene().getWindow();
			mx = window.getX() - me.getScreenX();
			my = window.getY() - me.getScreenY();
		});
		
		titleBarHBox.setOnMouseDragged(me -> {
			Window window = titleBarHBox.getScene().getWindow();
			window.setX(me.getScreenX() + mx);
			window.setY(me.getScreenY() + my);
		});
	}
	
	public void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	public void setIcon(Image image) {
		iconImageView.setImage(image);
	}
	
	private void setXButton() {
		ImageView xIV = new ImageView(Util.createJavaFXImage("images/x_PLAIN.png"));
		xIV.setPreserveRatio(true);
		xIV.setFitWidth(16); //we want it to be 16x16
		xButton.setGraphic(xIV);
	}
	
	@FXML private void x() {
		Scene scene = xButton.getScene();
		if (scene == null) return;
		Stage stage = (Stage)scene.getWindow();
		if (stage == null) return;
		stage.close();
	}
}