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
    public final SimpleIntegerProperty goodIdProperty() { return goodId; };

    private final SimpleStringProperty goodName;
    public final SimpleStringProperty goodNameProperty() { return goodName; };

    private final SimpleStringProperty goodVolume;
    public final SimpleStringProperty goodVolumeProperty() { return goodVolume; };

    private final SimpleStringProperty goodPrice;
    public final SimpleStringProperty goodPriceProperty() { return goodPrice; };

    public static void LoadData()
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


    public GoodTypeRecord(int Id, String name, double volume, double price)
    {
        goodId = new SimpleIntegerProperty(Id);
        goodName = new SimpleStringProperty(name);
        goodVolume = new SimpleStringProperty(String.valueOf(volume));
        goodPrice = new SimpleStringProperty(String.valueOf(price));
    }

    @Override
    public String toString() {
         return "("+goodId+")"+goodName;
       //return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
