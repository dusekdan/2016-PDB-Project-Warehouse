/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.StoreActivityRecord;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

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
        Calendar date = (Calendar) StoreActivityRecord.currentDay.clone();
        Calendar end = (Calendar) StoreActivityRecord.currentDay.clone();
        end.add(Calendar.DATE, 1);
        
        
        SimpleDateFormat format = new SimpleDateFormat("dd. MMMM, HH:mm");
        
        historyChart.setTitle(
                "Obsazení skladu dne " 
                        + format.format(begin.getTime())
        );
        
        XYChart.Series history = new XYChart.Series();
        history.setName("Obsazenost");
      
        for (StoreActivityRecord record : StoreActivityRecord.data) {
            history.getData().add(new XYChart.Data(record.fromProperty().getValue(), record.countProperty().getValue()));
        }
        
        
        
        
        
        historyChart.getData().add(history);
        
    }    
    
}
