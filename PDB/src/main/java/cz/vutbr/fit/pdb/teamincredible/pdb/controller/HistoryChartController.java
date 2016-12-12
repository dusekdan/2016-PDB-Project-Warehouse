/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.Database;
import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.ChartDataModel;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.StoreActivityRecord;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

/**
 * FXML Controller class
 *
 * @author Sucha
 */
public class HistoryChartController implements Initializable {
    
    /**
     * FXML injects
     */
    @FXML
    private LineChart historyChart;
    @FXML
    private CategoryAxis axisX;
    @FXML
    private NumberAxis axisY;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Calendar begin = (Calendar) StoreActivityRecord.currentDay.clone();
        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND,0);
        begin.set(Calendar.MILLISECOND, 0);
        Calendar end = (Calendar) StoreActivityRecord.currentDay.clone();
        end.add(Calendar.DATE, 1);

        SimpleDateFormat format = new SimpleDateFormat("dd. MMMM");
        SimpleDateFormat formatTime = new SimpleDateFormat("dd. MMMM, HH:mm:ss,SSS");
        historyChart.setTitle(
                "Obsazení skladu dne "
                + format.format(begin.getTime())
        );

        HashMap<String, XYChart.Series> series = new HashMap<>();
        series.put("0", new XYChart.Series());
        series.put("00", new XYChart.Series());
        
        XYChart.Series total;
        total = series.get("0");
        total.setName("Celková kapacita");
        int kap = DatabaseD.getCapacity();
        
        XYChart.Series rackSum;
        rackSum = series.get("00");
        rackSum.setName("Celková obsazená kapacita");
        
        
        while (begin.getTimeInMillis() <= end.getTimeInMillis()) {
            ChartDataModel loadData = ChartDataModel.loadData(new Timestamp(begin.getTime().getTime()));

            HashMap<Integer, Integer> sum = new HashMap<>();
            int sumTotal = 0;
            for (GoodInRack good : loadData.goods) {
                if (sum.containsKey(good.getRackID())) {
                    int get = sum.get(good.getRackID());
                    sum.put(good.getRackID(), get + good.getCount());
                  
                } else {
                    sum.put(good.getRackID(), good.getCount());
                }
                  sumTotal += good.getCount();
            }

            for (Integer integer : sum.keySet()) {
                if (!series.containsKey(integer.toString()))
                {
                    series.put(integer.toString(), new XYChart.Series());
                }
                series.get(integer.toString()).setName("rack " + integer);

                XYChart.Series get = series.get(integer.toString());
                get.getData().add(
                        new XYChart.Data(formatTime.format(begin.getTimeInMillis()), sum.get(integer))
                );
                total.getData().add(new XYChart.Data(formatTime.format(begin.getTimeInMillis()), kap));
                rackSum.getData().add(new XYChart.Data(formatTime.format(begin.getTimeInMillis()), sumTotal));
            }
      //      System.err.println(loadData.time + "::" + sum);
            
            begin.add(Calendar.MINUTE, 10);
        }

        historyChart.getData().addAll(series.values());

    }

}
