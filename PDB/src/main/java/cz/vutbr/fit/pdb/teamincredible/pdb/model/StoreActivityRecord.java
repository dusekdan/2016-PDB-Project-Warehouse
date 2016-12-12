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
 * Class represents one record in temporal DB.
 *
 * @author Sucha
 */
public class StoreActivityRecord {

    /**
     * information about current day, or currently set day
     */
    public static Calendar currentDay = Calendar.getInstance();

    /**
     * load data from database for current day
     */
    public static void loadData() {
        data = DatabaseD.GetStoreHistory();
        currentDay = Calendar.getInstance();
        currentDay.set(Calendar.HOUR_OF_DAY, 0);
        currentDay.set(Calendar.MINUTE, 0);
        currentDay.set(Calendar.SECOND, 0);
        currentDay.set(Calendar.MILLISECOND, 0);

    }

    /**
     * Set currentDay to date and load data for this day
     *
     * @param date
     * @return loaded data
     */
    public static ObservableList<StoreActivityRecord> loadData(Calendar date) {
        data = DatabaseD.GetStoreHistory(date);
        currentDay = date;
        currentDay.set(Calendar.HOUR_OF_DAY, 0);
        currentDay.set(Calendar.MINUTE, 0);
        currentDay.set(Calendar.SECOND, 0);
        currentDay.set(Calendar.MILLISECOND, 0);
        return data;
    }
    /**
     * timestamp of record validity begin time
     */
    public final Timestamp fromTS;

    /**
     * string property with id of goods type
     */
    private final SimpleStringProperty goodID;

    public final SimpleStringProperty goodIDProperty() {
        return goodID;
    }

    /**
     * string property with id of rack
     */
    private final SimpleStringProperty stockID;

    public final SimpleStringProperty stockIDProperty() {
        return stockID;
    }

    /**
     * integer property represents count of goods in rack
     */
    private final SimpleIntegerProperty numOfGood;

    public final SimpleIntegerProperty countProperty() {
        return numOfGood;
    }

    /**
     * string prop represents begin valid time
     */
    private final SimpleStringProperty from;

    public final SimpleStringProperty fromProperty() {
        return from;
    }

    /**
     * string prop represents end valid time
     */
    private final SimpleStringProperty to;

    public final SimpleStringProperty toProperty() {
        return to;
    }

    /**
     * constant date format string
     */
    private static final DateFormat format = new SimpleDateFormat("d. MMMM yyyy, H:mm");

    /**
     * Constructs instance with all data set
     *
     * @param goodID
     * @param stockID
     * @param numOfGood
     * @param from timestamp of valid time, transformed to string
     * @param to timestamp of valid time, transformed to string
     */
    public StoreActivityRecord(int goodID, int stockID, int numOfGood, Timestamp from, Timestamp to) {
        this.goodID = new SimpleStringProperty(String.valueOf(goodID)); // for now
        this.stockID = new SimpleStringProperty(String.valueOf(stockID)); // for now
        this.numOfGood = new SimpleIntegerProperty(numOfGood);

        this.fromTS = from;

        Date obj = new Date(from.getTime());
        this.from = new SimpleStringProperty(format.format(obj));
        obj = new Date(to.getTime());
        this.to = new SimpleStringProperty(format.format(obj));
    }

    /**
     * list of all records in chosen time
     */
    public static ObservableList<StoreActivityRecord> data = FXCollections.observableArrayList();

    public static ObservableList<StoreActivityRecord> getData() {
        return data;
    }

}
