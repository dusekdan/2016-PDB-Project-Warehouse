package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Created by Dan on 12/10/2016.
 * Class represent 1..N rows in TableView in Actions tab and contains methods and functionality relevant to it
 */
public class GoodTypeRecord {

    public static ObservableList<GoodTypeRecord> data = FXCollections.observableArrayList();
    public static ObservableList<GoodTypeRecord> getData() { LoadData(); return data; }     // Data fetch and refresh is ensured

    private final SimpleIntegerProperty goodId;
    public final SimpleIntegerProperty goodIdProperty() { return goodId; }

    private final SimpleStringProperty goodName;
    public final SimpleStringProperty goodNameProperty() { return goodName; }

    private final SimpleStringProperty goodVolume;
    public final SimpleStringProperty goodVolumeProperty() { return goodVolume; }

    private final SimpleStringProperty goodPrice;
    public final SimpleStringProperty goodPriceProperty() { return goodPrice; }


    /**
     * Loads data for table view
     */
    private static void LoadData()
    {
        if (!data.isEmpty())
            data.clear();

        List<Good> allGoods = DatabaseD.GetGoods();

        for (Good item : allGoods)
        {
            data.add(
              new GoodTypeRecord(item.getId(), item.getName(), item.getVolume(), item.getPrice())
            );
        }
    }


    /**
     * Constructor for GoodType record
     * @param Id int identification of the record
     * @param name String name of the record
     * @param volume Double volume of the record
     * @param price Double price of the record
     */
    private GoodTypeRecord(int Id, String name, double volume, double price)
    {
        goodId = new SimpleIntegerProperty(Id);
        goodName = new SimpleStringProperty(name);
        goodVolume = new SimpleStringProperty(String.valueOf(volume));
        goodPrice = new SimpleStringProperty(String.valueOf(price));
    }


    /**
     * Overide of the basic toString() method to retrieve data in specific format
     * @return String formatted in a specific matter relevant to the implementation of the application
     */
    @Override
    public String toString() {
         return goodName.getValue();
    }
}