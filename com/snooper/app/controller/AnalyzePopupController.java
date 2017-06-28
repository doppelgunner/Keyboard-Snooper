package com.snooper.app.controller;

import com.snooper.*;
import com.snooper.tray.*;

import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.util.*;

public class AnalyzePopupController extends Controller {
	
	@FXML private Label filenameLabel;
	@FXML private Label keysPerMinuteLabel;
	
	@FXML private AreaChart<Number,Number> keysAreaChart;
	@FXML private NumberAxis keysACQuantityNumberAxis;
	@FXML private NumberAxis keysACMinutesNumberAxis;
	private XYChart.Series<Number,Number> keysACSeries;
	
	@FXML private ListView topTypedKeysListView;
	@FXML private PieChart topTypedKeysPieChart;
	
	private SLogFile sLogFile;
	private SnoopReader snoopReader;
	
	@FXML
	public void initialize() {
		System.out.println("INIT: AnalyzePopupController.java");
	}
	
	@Override
	public void send(Object data) {
		if (data instanceof SLogFile) {
			sLogFile = (SLogFile) data;
			filenameLabel.setText(sLogFile.toString());
		}
	}
	
	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
		
		stage.setOnShown(event -> {
			if (ok()) {
				snoopReader = new SnoopReader(sLogFile);
				keysPerMinuteLabel.setText(String.format("%,d", snoopReader.getTypesPerMin()) + " keys/minute");
				loadKeysLineChart();
			}
		});
	}
	
	public boolean ok() {
		if (sLogFile == null) {
			return false;
		}
		
		return true;
	}
	
	private void loadKeysLineChart() {
		int keysLength = snoopReader.getSnoopKeysSize();
		keysACSeries = new XYChart.Series<>();
		
		long start = snoopReader.getSnoopKey(0).getDate().getTime();
		long end = snoopReader.getSnoopKey(keysLength - 1).getDate().getTime();
		long lasts = end - start;
		int minutesLast = (int)Util.getMinutes(lasts);
		
		keysACMinutesNumberAxis.setAutoRanging(false);
		keysACMinutesNumberAxis.setTickUnit(1);
		keysACMinutesNumberAxis.setLowerBound(0);
		keysACMinutesNumberAxis.setUpperBound(minutesLast);
		keysACMinutesNumberAxis.setTickLabelFormatter(new StringConverter<Number>() {
			@Override
			public String toString(Number n) {
				return n.intValue() + "m";
			}
			
			@Override
			public Number fromString(String string) {
				return Integer.parseInt(string.replace("m",""));
			}
		});
		
		int bound = 0;
		int counter = 0;
		for (int i = 0; i < keysLength; i++) {
			SnoopKey sKey = snoopReader.getSnoopKey(i);
			
			long now = sKey.getDate().getTime();
			int minute = (int)Util.getMinutesStarting(start, now);
			if (minute == bound) {
				counter++;
			} else {
				keysACSeries.getData().add(new XYChart.Data<Number,Number>(
					bound,
					counter
				));
				bound++;
				counter = 0;
			}
		}
		keysACSeries.getData().add(new XYChart.Data<Number,Number>(
			bound,
			counter
		));
		
		keysAreaChart.getData().add(keysACSeries);
	}
}