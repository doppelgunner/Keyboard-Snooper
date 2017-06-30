package com.snooper.app.controller;

import com.snooper.*;
import com.snooper.tray.*;

import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.util.*;
import javafx.collections.*;
import javafx.geometry.*;

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
		}
	}
	
	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
		
		stage.setOnShown(event -> {
			if (ok()) {
				snoopReader = new SnoopReader(sLogFile);
				filenameLabel.setText(sLogFile.toString() + " --- KEY COUNT: " + snoopReader.getSnoopKeysSize());
				keysPerMinuteLabel.setText(String.format("%,d", snoopReader.getTypesPerMin()) + " keys/minute");
				loadKeysLineChart();
				loadTopCountsAnalytics();
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
		
		ObservableList<XYChart.Data<Number,Number>> keysACContents = FXCollections.observableArrayList();
		for (int i = 0; i < keysLength; i++) {
			SnoopKey sKey = snoopReader.getSnoopKey(i);
			
			long now = sKey.getDate().getTime();
			int minute = (int)Util.getMinutesStarting(start, now);
			if (minute == bound) {
				counter++;
			} else {
				XYChart.Data data = new XYChart.Data<Number,Number>(bound,counter);
				keysACContents.add(data);
				bound++;
				counter = 0;
			}
		}
		XYChart.Data lastData = new XYChart.Data<Number,Number>(bound,counter);
		keysACContents.add(lastData);
		keysACSeries.setData(keysACContents);
		keysAreaChart.getData().add(keysACSeries); //must do this before adding tooltip (to create nodes on data)
		for (int i = 0; i < keysACContents.size(); i++) {
			XYChart.Data data = keysACContents.get(i);
			Util.addTooltip(data.getNode(), "Keys: " + data.getYValue().toString() + "\nMinute: " + data.getXValue());
		}
	}
	
	private void loadTopCountsAnalytics() {
		List<Map.Entry<SnoopKey,Integer>> topList = snoopReader.getCountsDescending();
		int limit = 5;
		
		ObservableList<SnoopKeyCount> keyCountsContents = FXCollections.observableArrayList();
		
		double totalKeyCounts = snoopReader.getSnoopKeysSize();
		int top5 = 0;
		ObservableList<PieChart.Data> dataContents = FXCollections.observableArrayList();
		for (int i = 0; i < 5 && i < topList.size(); i++) {
			Map.Entry<SnoopKey,Integer> entry = topList.get(i);
			SnoopKeyCount skc = new SnoopKeyCount(entry.getKey().getKey(), entry.getValue()); //get key of entry then get key of snoopkey
			keyCountsContents.add(skc);
			PieChart.Data data = new PieChart.Data(skc.getKey(), (skc.getCount() / totalKeyCounts) * 100);
			dataContents.add(data);
			top5 += skc.getCount();
		}
		PieChart.Data otherData = new PieChart.Data("Others", ((totalKeyCounts - top5) / totalKeyCounts) * 100);
		dataContents.add(otherData);
		
		topTypedKeysPieChart.setData(dataContents);
		for (int i = 0; i < dataContents.size(); i++) {
			PieChart.Data data = dataContents.get(i);
			Util.addTooltip(data.getNode(), String.format("%.2f%%", data.getPieValue()));
		}
		
		topTypedKeysListView.setItems(keyCountsContents);
		topTypedKeysPieChart.setStartAngle(180);
		topTypedKeysPieChart.setLabelsVisible(false);
		topTypedKeysPieChart.setLegendSide(Side.LEFT);
	}
}