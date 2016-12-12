/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import java.sql.Timestamp;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Sucha
 */
public class ChartDataModel {
  public static ObservableList<ChartDataModel> data = FXCollections.observableArrayList();

  public Timestamp time;
  public int totalStoreCapacity;
  public List<GoodInRack> goods;

    public ChartDataModel(Timestamp time, List<GoodInRack> goods) {
        this.time = time;
        this.goods = goods;
    }
  
  public static ObservableList<ChartDataModel> loadData(Timestamp from, Timestamp to) {
      data = DatabaseD.getGraphData(from, to);
      
      return data;
  }
  
}
