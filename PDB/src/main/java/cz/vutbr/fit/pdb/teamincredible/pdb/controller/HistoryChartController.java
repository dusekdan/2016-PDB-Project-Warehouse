/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.StoreActivityRecord;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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

    @FXML private LineChart historyChart;
    @FXML private NumberAxis axisX;
    @FXML private NumberAxis axisY;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Calendar begin = (Calendar) StoreActivityRecord.currentDay.clone();
        
        
        SimpleDateFormat format = new SimpleDateFormat("dd. MMMM");
        
        historyChart.setTitle(
                "Obsazení skladu dne " 
                        + format.format(begin.getTime())
        );
        
        historyChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);

        XYChart.Series history = new XYChart.Series();
        history.setName("Obsazenost");
        
       HashMap<String, XYChart.Series> series = new HashMap();
        
        for (StoreActivityRecord record : StoreActivityRecord.data) {
            series.put(record.stockIDProperty().getValue()+record.goodIDProperty().getValue(),
                            new XYChart.Series()
            );
            
        
        }
        axisX.clipProperty();
        
        SortedList<StoreActivityRecord> sorted = StoreActivityRecord.data.sorted((StoreActivityRecord t, StoreActivityRecord t1) -> (t.fromTS.compareTo(t1.fromTS)));

        sorted.forEach((record) -> {
            XYChart.Series get = series.get(record.stockIDProperty().getValue()+record.goodIDProperty().getValue());
            get.getData().add(new XYChart.Data(record.fromTS.getTime(), record.countProperty().getValue()));
        });
        
        
        
            historyChart.getData().addAll(series.values());
        
    }    
    
    
}
