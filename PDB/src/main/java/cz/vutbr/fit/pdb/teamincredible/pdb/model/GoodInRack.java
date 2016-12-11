/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.model;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Sucha
 */
public class GoodInRack {
    int count;
    int goodID;
    int rackID;
    String name;

    public int getCount() {
        return count;
    }

    public int getGoodID() {
        return goodID;
    }
    
    
    
    public static ObservableList<GoodInRack> data = FXCollections.observableArrayList();

    public GoodInRack(int count, int goodID, int rackID, String name) {
        this.count = count;
        this.goodID = goodID;
        this.rackID = rackID;
        this.name = name;
    }
    
    
    
    public static ObservableList<GoodInRack> loadData(int rackID) {
        data = DatabaseD.getGoodsInRack(rackID);
        return data;
    }

    @Override
    public String toString() {
        return name + ", počet: " + count;
    }
    
    
    
}
