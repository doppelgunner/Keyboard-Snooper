package com.snooper.app.controller;

import com.snooper.*;

import javafx.fxml.FXML;
import javafx.scene.image.*;
import javafx.scene.shape.*;

public class AboutController extends Controller {
	
	@FXML private ImageView sourceCodeImageView;
	@FXML private ImageView paypalImageView;
	@FXML private ImageView patreonImageView;
	
	@FXML
	public void initialize() {
		System.out.println("INIT: AboutController.java");
		
		patreonImageView.setClip(new Circle(32,32,32));
		paypalImageView.setClip(new Circle(32,32,32));
		sourceCodeImageView.setClip(new Circle(32,32,32));
		
		patreonImageView.setImage(Util.createJavaFXImage("images/patreon.png"));
		paypalImageView.setImage(Util.createJavaFXImage("images/paypal.jpg"));
		sourceCodeImageView.setImage(Util.createJavaFXImage("images/github.png"));
	}
	
	@FXML
	private void goPatreon() {
		Util.goLink("https://www.patreon.com/doppelgunner");
	}
	
	@FXML
	private void goPaypal() {
		Util.goLink("https://www.paypal.me/doppelgunner");
	}
	
	@FXML
	private void goGithub() {
		Util.goLink("https://github.com/doppelgunner");
	}
	
	/*
	@FXML
	private void goBlog() {
		Util.goLink("https://doppelgunner.blogspot.com/");
	}
	
	@FXML
	private void goYoutube() {
		Util.goLink("https://www.youtube.com/channel/UCjd_DY1LawVuZuLteDbVabQ");
	}
	
	@FXML
	private void goTwitter() {
		Util.goLink("https://twitter.com/doppelgunner");
	}
	
	@FXML
	private void goInstagram() {
		Util.goLink("https://www.instagram.com/doppelgunner/");
	}
	
	@FXML
	private void goItch() {
		Util.goLink("https://doppelgunner.itch.io/");
	}
	*/
	
	@FXML
	private void goSourceCode() {
		Util.goLink("https://github.com/doppelgunner/Keyboard-Snooper");
	}
}