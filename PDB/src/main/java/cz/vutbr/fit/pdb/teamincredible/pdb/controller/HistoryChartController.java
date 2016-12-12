/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.ChartDataModel;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.StoreActivityRecord;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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

    @FXML private LineChart historyChart;
    @FXML private CategoryAxis axisX;
    @FXML private NumberAxis axisY;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Calendar begin = (Calendar) StoreActivityRecord.currentDay.clone();
        Calendar end =  (Calendar) StoreActivityRecord.currentDay.clone();
        end.add(Calendar.DATE, 1);
        
        SimpleDateFormat format = new SimpleDateFormat("dd. MMMM");
        SimpleDateFormat formatTime = new SimpleDateFormat("dd. MMMM, HH:mm:ss,SSS");
        historyChart.setTitle(
                "Obsazení skladu dne " 
                        + format.format(begin.getTime())
        );
        
        HashMap<String, XYChart.Series> series = new HashMap<String, XYChart.Series>();
        ObservableList<ChartDataModel> loadData = ChartDataModel.loadData(new Timestamp(begin.getTime().getTime()), new Timestamp(end.getTime().getTime()));
        
        System.err.println(loadData.size());
        int counter = 0;
        for (ChartDataModel chartDataModel : loadData) {
            System.err.println(counter);
            System.err.println("velikost: "+chartDataModel.goods.size());
            for (GoodInRack good : chartDataModel.goods) {
                if(series.containsKey(good.getGoodID()+""+good.getRackID())) {
                    XYChart.Series get = series.get(good.getGoodID()+""+good.getRackID());
                    get.getData().add(new XYChart.Data(formatTime.format( chartDataModel.time), good.getCount()));
                } else {
                    series.put(good.getGoodID()+""+good.getRackID(), new XYChart.Series());
                    XYChart.Series get = series.get(good.getGoodID()+""+good.getRackID());
                    get.getData().add(new XYChart.Data(formatTime.format( chartDataModel.time), good.getCount()));
                }
                System.err.println("series: "+good.getGoodID()+""+good.getRackID()+ " time: "+chartDataModel.time+" count: "+good.getCount() );
            }
            counter++;
        }
        
        
        
        
        
        
        
        
        
        historyChart.getData().addAll(series.values());
        
    }    
    
    
}
