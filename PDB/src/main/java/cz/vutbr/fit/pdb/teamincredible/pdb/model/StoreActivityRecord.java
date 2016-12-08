/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Sucha
 */
public class StoreActivityRecord {
    
    public enum Action {
        income, outcome;
    }
    
    private final SimpleStringProperty goodID;
    public final SimpleStringProperty goodIDProperty() {
        return goodID;
    }
    
    private final SimpleStringProperty stockID;
    public final SimpleStringProperty stockIDProperty() {
        return stockID;
    }
    
    private final SimpleIntegerProperty numOfGood;
    public final SimpleIntegerProperty countProperty() {
        return numOfGood;
    }

    private final SimpleStringProperty action;
    public final SimpleStringProperty actionProperty() {
        return action;
    }

    private final SimpleStringProperty date;
    public final SimpleStringProperty dateProperty() {
        return date;
    }
    
    private static final DateFormat format = new SimpleDateFormat("d. MMMM yyyy, H:mm");

    public StoreActivityRecord(int goodID, int stockID, int numOfGood, Action action, long dateTimestamp) {
        this.goodID = new SimpleStringProperty(String.valueOf(goodID)); // for now
        this.stockID = new SimpleStringProperty(String.valueOf(stockID)); // for now
        this.numOfGood = new SimpleIntegerProperty(numOfGood);
        this.action = new SimpleStringProperty(action.toString());
   
        
        Date obj = new Date(dateTimestamp);
        this.date = new SimpleStringProperty(format.format(obj));
   
    }
    
    
      public static ObservableList<StoreActivityRecord> data = FXCollections.observableArrayList();
  
    public static void testData() {
        // add some dummy data
        
        data.add(new StoreActivityRecord(0, 0, 0, Action.outcome, System.currentTimeMillis()));
        data.add(new StoreActivityRecord(0, 0, 0, Action.income, System.currentTimeMillis()));
        data.add(new StoreActivityRecord(0, 0, 0, Action.outcome, System.currentTimeMillis()));
        data.add(new StoreActivityRecord(0, 0, 0, Action.outcome, System.currentTimeMillis()));
        data.add(new StoreActivityRecord(0, 0, 0, Action.outcome, System.currentTimeMillis()));
        data.add(new StoreActivityRecord(0, 0, 0, Action.income, System.currentTimeMillis()));
        data.add(new StoreActivityRecord(0, 0, 0, Action.outcome, System.currentTimeMillis()));
        data.add(new StoreActivityRecord(0, 0, 0, Action.outcome, System.currentTimeMillis()));
        
    }

    public static ObservableList<StoreActivityRecord> getData() {
        return data;
    }
    
    
}
