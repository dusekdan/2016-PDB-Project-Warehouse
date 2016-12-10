/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;



/**
 *
 * @author Sucha
 */
public class StoreActivityRecord {
    
    public static Calendar currentDay = Calendar.getInstance();

    public static void loadData() {
        data = DatabaseD.GetStoreHistory();
        currentDay = Calendar.getInstance();
        currentDay.set(Calendar.HOUR_OF_DAY, 0);
        currentDay.set(Calendar.MINUTE, 0);
        currentDay.set(Calendar.SECOND, 0);
        currentDay.set(Calendar.MILLISECOND, 0);
     
        
    }

    
    public static ObservableList<StoreActivityRecord> loadData(Calendar date) {
        data = DatabaseD.GetStoreHistory(date);
        currentDay = date;
        currentDay.set(Calendar.HOUR_OF_DAY, 0);
        currentDay.set(Calendar.MINUTE, 0);
        currentDay.set(Calendar.SECOND, 0);
        currentDay.set(Calendar.MILLISECOND, 0);
        return data;
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

    private final SimpleStringProperty from;
    public final SimpleStringProperty fromProperty() {
        return from;
    }
    
    private final SimpleStringProperty to;
    public final SimpleStringProperty toProperty() {
        return to;
    }
    
    
    
    
    private static final DateFormat format = new SimpleDateFormat("d. MMMM yyyy, H:mm");

    public StoreActivityRecord(int goodID, int stockID, int numOfGood, Timestamp from, Timestamp to) {
        this.goodID = new SimpleStringProperty(String.valueOf(goodID)); // for now
        this.stockID = new SimpleStringProperty(String.valueOf(stockID)); // for now
        this.numOfGood = new SimpleIntegerProperty(numOfGood);
   
        
        Date obj = new Date(from.getTime());
        this.from = new SimpleStringProperty(format.format(obj));
        obj = new Date(to.getTime());
        this.to = new SimpleStringProperty(format.format(obj));
    }
    
    
      public static ObservableList<StoreActivityRecord> data = FXCollections.observableArrayList();
  
    public static void testData() {
        // add some dummy data
        
        
    }

    public static ObservableList<StoreActivityRecord> getData() {
        return data;
    }
    
    
}
