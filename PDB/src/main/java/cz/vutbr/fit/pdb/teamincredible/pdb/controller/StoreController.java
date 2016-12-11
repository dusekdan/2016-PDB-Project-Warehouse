package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomRackDefinition;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import cz.vutbr.fit.pdb.teamincredible.pdb.CustomSwingNode;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AvailableRacksView;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.ImportDBAlert;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SaveChangesDialog;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

import static cz.vutbr.fit.pdb.teamincredible.pdb.controller.ActionsController.DisplayInformation;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AskForGoodsAndCount;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForAvailableRacks;
import javafx.util.Pair;

/**
 * Created by Sucha on 03.12.2016.
 */
public class StoreController implements Initializable {

    @FXML
    private AnchorPane StoreACP;
    private Button AddNewRackButton;
    @FXML
    private Button addGoodToRackBtn;
    @FXML
    private Button removeGoodFromRackBtn;
    public int itemsCount;
    
    private static int rackIdSelected;

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("StoreController init");

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500,500);

        createSwingContent(swingNode);

        StoreACP.getChildren().add(swingNode);
        StoreACP.resize(500,500);
        StoreACP.getStyleClass().add("storeAnchorPane");

        itemsCount = 5;

    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JPanel panel = null;
                try {
                    panel = new SpatialViewerForStore();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                panel.setPreferredSize(new Dimension(500,500));

                swingNode.setContent(panel);

            }
        });
    }

    public void addNewRack(ActionEvent actionEvent) {

        AvailableRacksView addNewRackDialog = new AvailableRacksView(itemsCount);
        Optional<CustomRackDefinition> newRack = addNewRackDialog.showAndWait();

    }

    public void saveStore(ActionEvent actionEvent) {

        List<CustomShape> shapeList = SpatialViewerForStore.returnAllRacksFromStore();

        //TODO
        /// writing a geometry back to database
        //PreparedStatement ps = connection.prepareStatement("UPDATE states set geometry=? where name='Florida'");
        //convert JGeometry instance to DB STRUCT
        //STRUCT obj = JGeometry.store(j_geom, connection);
        //ps.setObject(1, obj);
        //ps.execute();
    }

    public void refreshStore(ActionEvent actionEvent) {

        if (!SpatialViewerForStore.hasChanges())
        {
            DisplayInformation("Úspěch", "Obsah skladu je aktuální!");
            return;
        }

        SaveChangesDialog confirm = new SaveChangesDialog(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            StoreACP.getChildren().remove(0);

            final SwingNode swingNode = new CustomSwingNode();
            swingNode.resize(500,500);

            createSwingContent(swingNode);

            StoreACP.getChildren().add(swingNode);

            DisplayInformation("Úspěch", "Databáze úspěšně obnovena!");
        }

        else if (result.isPresent() && result.get() == ButtonType.CANCEL)
        {
            return;
        }

    }
    
    private int isSomethingSelected() {
        for (CustomShape rack : SpatialViewerForStore.shapeList) {
            if(rack.isSelected()) {
                return 1; 
                // return rack.getId();
            }
        }
        return 0;
    }
    
    public void addGoodInRack() {
        int rackId = isSomethingSelected();
        if (rackId == 0) {
            DisplayInformation("Info", "Je nutno zvolit umístění vloženého zboží!");
            return;
        }
        int goodId;
        int count;
        
        Optional<Pair<Integer, GoodTypeRecord>> res = new AskForGoodsAndCount(rackId).showAndWait();
        
        if (!res.isPresent()) {
            return;
        } else {
            goodId = res.get().getValue().goodIdProperty().getValue();
            count = res.get().getKey();
        }
        
        DatabaseD.InsertGoodIntoStorage(goodId, rackId, count);
        DisplayInformation("Úspěch", "Do stojanu č. "+rackId+" bylo přidáno "+count+" kus/ů zboží "+goodId+".");
        
    }
    
    public void removeGoodFromRack() {
          int rackId = isSomethingSelected();
        if (rackId == 0) {
            DisplayInformation("Info", "Je nutno zvolit umístění vloženého zboží!");
            return;
        }
        int goodId;
        int count;
        
        Optional<Pair<Integer, GoodTypeRecord>> res = new AskForGoodsAndCount(rackId).showAndWait();
        if (!res.isPresent()) {
            return;
        } else {
            goodId = res.get().getValue().goodIdProperty().getValue();
            count = res.get().getKey();
        }
        
        DatabaseD.RemoveGoodFromStorage(goodId, rackId, count);
        DisplayInformation("Úspěch", "Ze stojanu č. "+rackId+" bylo odebráno "+count+" kus/ů zboží "+goodId+".");
    }
    
    

}
