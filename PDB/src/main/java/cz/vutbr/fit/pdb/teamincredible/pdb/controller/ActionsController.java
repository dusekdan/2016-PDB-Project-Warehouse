package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AddGoodDialog;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.GoodDetailDialog;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.ImportDBAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by Dan on 03.12.2016.
 * Definitions related to Actions tab
 */
public class ActionsController implements Initializable{

    @FXML private TableView<GoodTypeRecord> tbvGoodTypes;
    @FXML private TableColumn<GoodTypeRecord, String> goodId;
    @FXML private TableColumn<GoodTypeRecord, String> goodName;
    @FXML private TableColumn<GoodTypeRecord, String> goodPrice;
    @FXML private TableColumn<GoodTypeRecord, String> goodVolume;


    /**
     * Initialization function filling TableView with records from database
     */
    private void InitializeGoodTypesTableView()
    {
        System.out.println("D: Initializing Action tab's table view.");

        goodId.setCellValueFactory(new PropertyValueFactory<>("goodId"));
        goodName.setCellValueFactory(new PropertyValueFactory<>("goodName"));
        goodPrice.setCellValueFactory(new PropertyValueFactory<>("goodPrice"));
        goodVolume.setCellValueFactory(new PropertyValueFactory<>("goodVolume"));

        tbvGoodTypes.setItems(GoodTypeRecord.getData());

        HookDoubleClickEventsToTableRow();
    }




    @FXML public void ReloadTableView()
    {
        tbvGoodTypes.setItems(GoodTypeRecord.getData());
    }

    /**
     * Shows good record detail and offers the possibility to remove the item as well
     */
    private void ShowGoodDetail(int id)
    {
        GoodDetailDialog dialog = new GoodDetailDialog(id);
        Optional<Good> dialogReturned = dialog.showAndWait();

        if (dialogReturned.isPresent())
        {
            if (dialogReturned.get() != null)
            {
                System.out.println("D: Item seems to be successfully removed...");
                ReloadTableView();
            }
            else
            {
                System.out.println("D: Dialog returned null. If you tried to delete good, something went wrong, otherwise, all clear.");
            }
        }
    }


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
        //List<Good> AllGoodTypes = DatabaseD.GetGoods();
        InitializeGoodTypesTableView();




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
     * Hooks double click events coming from row of TableView to
     */
    private void HookDoubleClickEventsToTableRow()
    {
        tbvGoodTypes.setRowFactory(tv -> {
            TableRow<GoodTypeRecord> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {

                    // Action executed after double click
                    GoodTypeRecord rowData = row.getItem();

                    int GoodId = rowData.goodIdProperty().getValue();

                    ShowGoodDetail(GoodId);
                    System.out.println("D: Showing detail dialog for Good.Id=" + GoodId);

                }
            });
            return row;
        });
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
                ReloadTableView();
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
    @FXML public void importTestDB(ActionEvent actionEvent) {

        ImportDBAlert confirm = new ImportDBAlert(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            DatabaseD.initDBStruct();
            DatabaseD.loadInitData();  // TODO: insert model data

            DisplayInformation("Úspěch", "Databáze úspěšně obnovena!");
        }
    }


    /**
     * Displays information in alert
     * @param title String message in the alert title
     * @param content String message in the alert content
     */
    public static void DisplayInformation(String title, String content)
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
