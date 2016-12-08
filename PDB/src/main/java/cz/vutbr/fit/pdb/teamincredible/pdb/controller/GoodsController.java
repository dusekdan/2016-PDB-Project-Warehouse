package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.StoreActivityRecord;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Created by Sucha on 03.12.2016.
 */
public class GoodsController implements Initializable {
    
    
    @FXML
    private TableView<StoreActivityRecord> goodsTableView;
    
    @FXML private TableColumn<StoreActivityRecord, String> goodsID;
    @FXML private TableColumn<StoreActivityRecord, String> stockID;
    @FXML private TableColumn<StoreActivityRecord, String> action;
    @FXML private TableColumn<StoreActivityRecord, String> count;
    @FXML private TableColumn<StoreActivityRecord, String> date;
    @FXML private TableColumn<StoreActivityRecord, String> detail;
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("GoodsController init");
        
        goodsID.setCellValueFactory(new PropertyValueFactory<>("goodID") );
        stockID.setCellValueFactory(new PropertyValueFactory<>("stockID") );
        action.setCellValueFactory(new PropertyValueFactory<>("action") );
        count.setCellValueFactory(new PropertyValueFactory<>("count") );
        date.setCellValueFactory(new PropertyValueFactory<>("date") );
        
        
        
        StoreActivityRecord.testData();
        goodsTableView.setItems(StoreActivityRecord.getData());
        
        
    }
}
