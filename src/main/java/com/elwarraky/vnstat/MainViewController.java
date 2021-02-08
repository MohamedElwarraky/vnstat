package com.elwarraky.vnstat;

import com.jfoenix.controls.JFXComboBox;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

public class MainViewController {

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private JFXComboBox<String> connectionTypeComboBox;

    @FXML
    private JFXComboBox<String> displayComboBox;

    @FXML
    private Label TotalTrafficLbl;

    private Service service;

    @FXML
    void initialize() {
        this.xAxis.setAutoRanging(true);
        this.yAxis.setAutoRanging(true);
        this.yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return service.humanReadableByteCount(object.longValue());
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
        displayComboBox.getItems().addAll("fiveminute", "hour", "day", "month", "year", "top");
        this.displayComboBox.setValue("fiveminute");
    }

    public void setModel(Service service) {
        this.service = service;
    }

    @FXML
    void connectionTypeComboBoxOnAction(ActionEvent event) {
        String cat = this.displayComboBox.getValue();
        String type = this.connectionTypeComboBox.getValue();
        if (this.lineChart.getData() != null) {
            this.lineChart.getData().clear();
        }
        this.lineChart.setData(service.getLineChartData(type, cat));
        this.TotalTrafficLbl.setText(service.getTotals(type));
    }

    @FXML
    void displayComboBoxOnAction(ActionEvent event) {
        String cat = this.displayComboBox.getValue();
        String type = this.connectionTypeComboBox.getValue();
        if (this.lineChart.getData() != null) {
            this.lineChart.getData().clear();
        }
        this.lineChart.setData(service.getLineChartData(type, cat));
        this.TotalTrafficLbl.setText(service.getTotals(type));
    }

    public void SetLineChartData(ObservableList<XYChart.Series<String, Number>> lineChartData) {
        this.lineChart.setData(lineChartData);
    }

    public void setConnectionTypeComboBoxData(ObservableList<String> connectionTypes) {
        this.connectionTypeComboBox.setItems(connectionTypes);
    }

    public void setTotalTraffic(String totalTraffic) {
        TotalTrafficLbl.setText(totalTraffic);
    }
}
