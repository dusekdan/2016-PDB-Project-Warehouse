package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class represents goods stored in rack
 *
 * @author Sucha
 */
public class GoodInRack {

    /**
     * Count of good in rack
     */
    int count;

    public int getCount() {
        return count;
    }

    /**
     * id of good type
     */
    int goodID;

    public int getGoodID() {
        return goodID;
    }

    /**
     * rack id
     */
    int rackID;

    public int getRackID() {
        return rackID;
    }
    
    
    /**
     * name of good
     */
    String name;

    /**
     * list for all goods in rack
     */
    public static ObservableList<GoodInRack> data = FXCollections.observableArrayList();

    /**
     * Object constructor, sets all properties.
     * 
     * @param count     count of this type of good in rack
     * @param goodID    id of type
     * @param rackID    id of rack
     * @param name      good string identifier
     */
    public GoodInRack(int count, int goodID, int rackID, String name) {
        this.count = count;
        this.goodID = goodID;
        this.rackID = rackID;
        this.name = name;
    }

    /**
     * For selected rack,
     * loads data from database and store them in data list.
     * @param rackID    selected rackID
     * @return          list of good in rack
     */
    public static ObservableList<GoodInRack> loadData(int rackID) {
        data = DatabaseD.getGoodsInRack(rackID);
        return data;
    }

    /**
     * Prettier string representation
     * @return object representation string
     */
    @Override
    public String toString() {
        return  "rack: " + rackID + ", zboží:" + name + ", počet: " + count;
    }

}
