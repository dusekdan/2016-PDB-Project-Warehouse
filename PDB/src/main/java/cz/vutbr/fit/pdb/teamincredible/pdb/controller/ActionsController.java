package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AddGoodDialog;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.ImportDBAlert;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Dan on 03.12.2016.
 * Definitions related to Actions tab
 */
public class ActionsController implements Initializable{

    /**
     * First method called when ActionScene is loaded
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // TODO: Remove this from final version
        // Inform about Scene being loaded
        System.out.println("D: Action controller loaded.");


        // Not much to be initialized at the moment

        // Initialize TableView data source
        List<Good> AllGoodTypes = DatabaseD.GetGoods();


        // TODO: Remove this from final version
        // Check out how can I select something
        //List<Good> goods = DatabaseD.GetGoods();
        //for (Good good : goods)
        //{
        //  System.out.println("Id:" + good.getId());
        //  System.out.println("Name: " + good.getName());
        //}
    }


    /**
     * Add good item button action
     * @param actionEvent ActionEvent information about event that is linked to the method call
     */
    public void addGoodType(ActionEvent actionEvent)
    {
        // Fire dialog and take back results after it is closed
        AddGoodDialog addDialog = new AddGoodDialog();
        Optional<Good> goodValue = addDialog.showAndWait();

        // Based on returned value either add item to database or do nothing
        if (goodValue.isPresent() && goodValue.get() != null)
            if(DatabaseD.InsertGood(goodValue.get())) {
                System.out.println("Good item added successfully.");
                DEBUG_DumpAddedItem(goodValue);
            }
            else
                System.out.println("Something failed during Good insertion. Feel free to debug the sh*t out of it.");
        else
            System.out.println("Dialog was closed without Good object returned.");
    }


    /**
     * Import database button action
     * @param actionEvent ActionEvent information about event that is linked to the method call
     */
    public void importTestDB(ActionEvent actionEvent) {

        ImportDBAlert confirm = new ImportDBAlert(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            DatabaseD.initDBStruct();
            // DatabaseD.loadInitData  // TODO: create model data

            DisplayInformation("Úspěch", "Databáze úspěšně obnovena!");
        }
    }


    /**
     * Displays information in alert
     * @param title String message in the alert title
     * @param content String message in the alert content
     */
    private void DisplayInformation(String title, String content)
    {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle(title);
        info.setContentText(content);
        info.showAndWait();
    }


    /**
     * Debug only method - prints to standard output contents of received Good object
     */
    private void DEBUG_DumpAddedItem(@SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Good> goodValue)
    {
        if(goodValue.isPresent()) {
            System.out.println("I have received new item creation request with: \nItem name: " +
                    goodValue.get().getName() + "\n" + "Item volume: " +
                    goodValue.get().getVolume() + "\n Item price: " +
                    goodValue.get().getPrice() + "\n Item path: " +
                    goodValue.get().getImgFilePath());
        }
    }
}
