package com.snooper.app.controller;

import javafx.stage.*;

public abstract class Controller {
	
	protected Stage stage;
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	//sending data from outside to the controller
	public void send(Object data) {
		//do nothing - override to do something
	}
}