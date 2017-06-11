package com.snooper;

import com.snooper.*;
import com.snooper.tray.*;

import java.util.*;
import java.io.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.*;

public class SLogFile {
	
	private String filepath;
	private String filename;
	private String dateString;
	private Date date;
	private String dateStringPretty;
	
	public SLogFile(String filepath) {
		this(new File(filepath));
	}
	
	public SLogFile(File file) {
		this.filepath = file.getPath();
		this.filename = file.getName();
		findDate();
	}
	
	private void findDate() {
		if (filename.contains("temp")) return;
		dateString = filename.substring(filename.lastIndexOf('_') + 1, filename.lastIndexOf('.'));
		date = Util.toDate(dateString,Snooper.fileNameFormat);
		
		dateStringPretty = Util.toString(date, Snooper.perHourFormat);
	}
	
	public String getFilepath() {
		return filepath;
	}
	
	public File getFile() {
		return new File(filepath);
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getDateString() {
		return dateString;
	}
	
	public Date getDate() {
		return date;
	}
	
	public String toString() {
		if (dateStringPretty == null || dateStringPretty.isEmpty()) return "SLOG: temp";
		return "SLOG: " + dateStringPretty;
	}
	
	public static Image getImage() {
		return Util.createJavaFXImage("images/slog.png");
	}
	
	public static Image getSelectedImage() {
		return Util.createJavaFXImage("images/slog_selected.png");
	}
	
	public static ImageView getImageView() {
		return new ImageView(getImage());
	}
	
	public static ImageView getSelectedImageView() {
		return new ImageView(getSelectedImage());
	}
	
	//for setting the layout of the cells (to be used in cell factory to create cells with image and label)
	public HBox getHBox(boolean selected) {
		ImageView imageView = (selected) ? getSelectedImageView() : getImageView();
		HBox bar = new HBox(imageView, new Label(toString()));
		bar.setAlignment(Pos.CENTER_LEFT);
		return bar;
	}
}