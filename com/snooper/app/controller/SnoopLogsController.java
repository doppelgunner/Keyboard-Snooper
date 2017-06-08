package com.snooper.app.controller;

import com.snooper.*;
import com.snooper.tray.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import java.io.*;
import java.util.*;

public class SnoopLogsController {
	
	@FXML ListView<SLogFile> sLogListView;
	@FXML ListView<SnoopKey> keyLogsListView;
	
	@FXML Button sLogLeftButton;
	@FXML Button sLogRightButton;
	@FXML Label sLogMaxPageLabel;
	@FXML TextField sLogPageTextField;
	
	@FXML Button keyLogLeftButton;
	@FXML Button keyLogRightButton;
	@FXML Label keyLogMaxPageLabel;
	@FXML Label sLogFileSelectedLabel;
	@FXML TextField keyLogPageTextField;
	
	public static final int MAX_CONTENT_PER_PAGE = 10;
	
	private ObservableList<SLogFile> sLogFilesContents;
	private SLogFile[] sLogFiles;
	private int sLogPages;
	
	private int selectedSLogIndex;
	private int selectedKeyLogIndex;
	
	private int currentSLogPage;
	private int currentKeyLogPage;
	
	private SnoopReader sReader;
	private ObservableList<SnoopKey> keyLogsContents;
	private int keyLogPages;
	
	private boolean sameSLogIndex;
	
	@FXML
	public void initialize() {
		System.out.println("INIT: SnoopLogController.java");
		
		currentSLogPage = 1;
		currentKeyLogPage = 1;
		selectedSLogIndex = 0;
		sameSLogIndex = false;
		
		sLogListView.getSelectionModel().selectedItemProperty().addListener((o,ov,nv) -> {
			if (nv == null) return;
			int index = getSelectedSLogIndex();
			if (index == -1) return;
			System.out.println("slog selected[" + index + "]:" + nv);
			sameSLogIndex = false;
			selectedSLogIndex = index;
			
			loadKeyLogs(selectedSLogIndex);
		});
		
		sLogPageTextField.textProperty().addListener((o,ov,nv) -> {
			int page = -1;
			if (Util.tryParseInt(nv)) {
				page = Integer.parseInt(nv);
				if (Util.withinRange(1,sLogPages,page)) {
					setSLogPage(page);
					return;
				}
			}
			
			sLogPageTextField.setText(ov); //set old value if not valid page number
		});
		
		keyLogPageTextField.textProperty().addListener((o,ov,nv) -> {
			int page = -1;
			if (Util.tryParseInt(nv)) {
				page = Integer.parseInt(nv);
				if (Util.withinRange(1,keyLogPages, page)) {
					setKeyLogPage(page);
					return;
				}
			}
			keyLogPageTextField.setText(ov); //set old value if not valid page number
		});
		
		loadSLogs();
	}
	
	public void loadSLogs() {
		File[] files = Util.getSLogFiles(Snooper.FOLDER);
		int length = files.length;
		sLogFiles = new SLogFile[length];
		for (int i = 0; i < length; i++) {
			sLogFiles[i] = new SLogFile(files[length - i - 1]); //to reverse while copying - latest to old
		}
		
		sLogFilesContents = FXCollections.observableArrayList();
		
		this.sLogPages = (int)Math.ceil((double)length / MAX_CONTENT_PER_PAGE);
		sLogMaxPageLabel.setText("/" + sLogPages);
		setSLogPage(currentSLogPage);
	}
	
	public void setSLogPage(int page) {
		this.currentSLogPage = page;
		sLogPageTextField.setText("" + page);
		
		if (sLogPages == 0 || currentSLogPage == 1) {
			sLogLeftButton.setDisable(true);
		} else {
			sLogLeftButton.setDisable(false);
		}
		
		if (sLogPages == 0 || currentSLogPage == sLogPages) {
			sLogRightButton.setDisable(true);
		} else {
			sLogRightButton.setDisable(false);
		}
		
		int start = (currentSLogPage - 1) * MAX_CONTENT_PER_PAGE;
		int end = start + MAX_CONTENT_PER_PAGE;
		sLogFilesContents.clear(); //remove previous on list view
		for (int i = start; i < sLogFiles.length && i < end; i++) {
			sLogFilesContents.add(sLogFiles[i]);
		}
		
		displaySLogs();
		sameSLogIndex = true;
		
		loadKeyLogs(selectedSLogIndex);
	}
	
	public void displaySLogs() {
		sLogListView.setItems(sLogFilesContents);
	}
	
	private int getSelectedSLogIndex() {
		int start = (currentSLogPage - 1) * MAX_CONTENT_PER_PAGE;
		int index = sLogListView.getSelectionModel().getSelectedIndex();
		return start + index;
	}
	
	@FXML
	private void sLogLeft() {
		if (currentSLogPage <= 1) return;
		setSLogPage(--currentSLogPage);
	}
	
	@FXML
	private void sLogRight() {
		if (currentSLogPage >= sLogPages) return;
		setSLogPage(++currentSLogPage);
	}
	
	@FXML
	private void refreshSLogs() {
		loadSLogs();
	}
	
	public void loadKeyLogs(int index) {
		final SLogFile sLogFile = sLogFiles[index];
		sReader = new SnoopReader(sLogFile);
		sLogFileSelectedLabel.setText(sLogFile.toString());
		
		keyLogsContents = FXCollections.observableArrayList();
		this.keyLogPages = (int)Math.ceil((double)sReader.getSnoopKeysSize() / MAX_CONTENT_PER_PAGE);
		keyLogMaxPageLabel.setText("/" + keyLogPages);
		setKeyLogPage((sameSLogIndex) ? currentKeyLogPage : 1);
	}
	
	public void setKeyLogPage(int page) {
		this.currentKeyLogPage = page;
		keyLogPageTextField.setText("" + page);
		
		if (keyLogPages == 0 || currentKeyLogPage == 1) {
			keyLogLeftButton.setDisable(true);
		} else {
			keyLogLeftButton.setDisable(false);
		}
		
		if (keyLogPages == 0 || currentKeyLogPage == keyLogPages) {
			keyLogRightButton.setDisable(true);
		} else {
			keyLogRightButton.setDisable(false);
		}
		
		int start = (currentKeyLogPage - 1) * MAX_CONTENT_PER_PAGE;
		int end = start + MAX_CONTENT_PER_PAGE;
		keyLogsContents.clear();
		for (int i = start; i < sReader.getSnoopKeysSize() && i < end; i++) {
			keyLogsContents.add(sReader.getSnoopKey(i));
		}
		
		displayKeyLogs();
	}
	
	private void displayKeyLogs() {
		keyLogsListView.setItems(keyLogsContents);
	}
	
	@FXML
	private void keyLogLeft() {
		if (currentKeyLogPage <= 1) return;
		setKeyLogPage(--currentKeyLogPage);
	}
	
	@FXML
	private void keyLogRight() {
		if (currentKeyLogPage >= keyLogPages) return;
		setKeyLogPage(++currentKeyLogPage);
	}
	
}