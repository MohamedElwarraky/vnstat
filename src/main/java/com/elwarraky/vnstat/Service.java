/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elwarraky.vnstat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mohamed_elwarraky
 */


public class Service {

    MainViewController mainViewController;
    ObservableList<String> connectionTypes;
    Map<String, String> totals = new HashMap<>();
    Map<String, Map<String, ObservableList<XYChart.Series<String, Number>>>> allLineChartData;

    public Service() {
        this.allLineChartData = new HashMap<>();
        this.connectionTypes = FXCollections.observableArrayList();

        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        InputStream intputStream = null;
        try {
            Process process = Runtime.getRuntime()
                    .exec("vnstat --json");
            intputStream = process.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(intputStream))) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject vnstatJson = (JSONObject) obj;
            parseVnstatObject(vnstatJson);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseVnstatObject(JSONObject vnstat) {
        //Get employee object within list
        JSONArray connetctionInterfaces = (JSONArray) vnstat.get("interfaces");
        for (Object connetctionInterface : connetctionInterfaces) {
            JSONObject currentConnetctionInterface = (JSONObject) connetctionInterface;

            //Get interface name
            String interfaceName = (String) currentConnetctionInterface.get("name");
            this.connectionTypes.add(interfaceName);
            Map<String, ObservableList<XYChart.Series<String, Number>>> subLineChartData = new HashMap<>();
            //Get traffic
            JSONObject traffic = (JSONObject) currentConnetctionInterface.get("traffic");

            JSONObject total = (JSONObject) traffic.get("total");
            this.totals.put(interfaceName, getTotalData(total));

            JSONArray fiveminute = (JSONArray) traffic.get("fiveminute");
            subLineChartData.put("fiveminute", getData(fiveminute));

            JSONArray hour = (JSONArray) traffic.get("hour");
            subLineChartData.put("hour", getData(hour));

            JSONArray day = (JSONArray) traffic.get("day");
            subLineChartData.put("day", getData(day));

            JSONArray month = (JSONArray) traffic.get("month");
            subLineChartData.put("month", getData(month));

            JSONArray year = (JSONArray) traffic.get("year");
            subLineChartData.put("year", getData(year));

            JSONArray top = (JSONArray) traffic.get("top");
            subLineChartData.put("top", getData(top));

            this.allLineChartData.put(interfaceName, subLineChartData);
        }

    }

    public String getTotalData(JSONObject total) {
        String totalData = "";

        long rx = (long) total.get("rx");
        totalData += "rx: " + humanReadableByteCount(rx);
        long tx = (long) total.get("tx");
        totalData += " , tx: " + humanReadableByteCount(tx);
        return totalData;
    }

    public ObservableList<XYChart.Series<String, Number>> getData(JSONArray data) {
        ObservableList<XYChart.Series<String, Number>> lineChartData = FXCollections.observableArrayList();
        XYChart.Series<String, Number> seriesRX = new XYChart.Series();
        seriesRX.setName("RX");
        XYChart.Series<String, Number> seriesTX = new XYChart.Series();
        seriesTX.setName("TX");
        for (Object myData : data) {
            JSONObject currentData = (JSONObject) myData;
            String dateString = getDateFromJSONObject(currentData);
            Long rx = (Long) currentData.get("rx");
            Long tx = (Long) currentData.get("tx");

            seriesRX.getData().add(new XYChart.Data<>(dateString, rx));
            seriesTX.getData().add(new XYChart.Data<>(dateString, tx));
        }
        lineChartData.addAll(seriesRX, seriesTX);
        return lineChartData;
    }

    public String getDateFromJSONObject(JSONObject traffic) {
        String myDate = "";
        //Get date
        JSONObject date = (JSONObject) traffic.get("date");

        //Get year
        Long year = (Long) date.get("year");
        if (year != null) {
            myDate += year;
        }

        //Get month
        Long month = (Long) date.get("month");
        if (month != null) {
            myDate += "-" + month;
        }
        //Get day
        Long day = (Long) date.get("day");
        if (day != null) {
            myDate += "-" + day;
        }
        //Get time
        JSONObject time = (JSONObject) traffic.get("time");

        if (time != null) {
            //Get hour
            boolean isAM = false;
            Long hour = (Long) time.get("hour");
            if (hour != null) {
                if (hour <= 12) {
                    isAM = true;
                    myDate += " " + hour;
                } else {
                    isAM = false;
                    myDate += " " + (hour - 12);
                }

            }
            //Get minute
            Long minute = (Long) time.get("minute");
            if (minute != null) {
                myDate += ":" + minute;
            }
            if (isAM) {
                myDate += " AM";
            }else{
                myDate += " PM";
            }
        }

        return myDate;
    }

    public String humanReadableByteCount(long bytes) {

        int factor = 1024;
        String units = "KMGTPE";
        if (bytes < factor) return bytes + " B";

        int exp = (int) (Math.log(bytes) / Math.log(factor));

        char pre = units.charAt(exp - 1);

        return String.format("%.1f %sB", bytes / Math.pow(factor, exp), pre);
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        this.mainViewController.setConnectionTypeComboBoxData(this.connectionTypes);
    }

    public ObservableList<String> getConnectionTypes() {
        return connectionTypes;
    }

    public String getTotals(String type) {
        return totals.get(type);
    }

    public ObservableList<XYChart.Series<String, Number>> getLineChartData(String currentInterface, String currentCategory) {
        System.out.println(allLineChartData.get(currentInterface).get(currentCategory));
        return allLineChartData.get(currentInterface).get(currentCategory);
    }
}
