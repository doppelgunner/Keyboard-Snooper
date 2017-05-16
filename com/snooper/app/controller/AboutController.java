package com.snooper.app.controller;

import com.snooper.*;

import javafx.fxml.FXML;

public class AboutController {
	
	@FXML
	public void initialize() {
		System.out.println("INIT: AboutController.java");
	}
	
	@FXML
	private void goGithub() {
		Util.goLink("https://github.com/doppelgunner");
	}
	
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
	
	@FXML
	private void goSourceCode() {
		Util.goLink("https://github.com/doppelgunner/Keyboard-Snooper");
	}
}