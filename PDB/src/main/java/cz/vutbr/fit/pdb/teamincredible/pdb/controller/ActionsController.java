package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AddGoodDialog;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;



/**
 * Created by Dan on 03.12.2016.
 */
public class ActionsController implements Initializable{
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Action controller loaded.");
        // Testing whether DatabaseD with established connection is accessible here
        if (DatabaseD.testConnection())
            System.out.println("Connection is working...");
        else
            System.out.println("Connection is NOT working...");

        // Check out how can I select something
        List<Good> goods = DatabaseD.GetGoods();
        for (Good good : goods)
        {
            System.out.println("Id:" + good.getId());
            System.out.println("Name: " + good.getName());
        }



    }


    public void addGoodType(ActionEvent actionEvent)
    {

        AddGoodDialog addDialog = new AddGoodDialog();

        Optional<Good> goodValue = addDialog.showAndWait();






        System.out.println("Something pressed in add goods dialog...");


    }

    public void importTestDB(ActionEvent actionEvent) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset databáze");
        confirm.setHeaderText("Opravdu chcete obnovit data?");
        confirm.setContentText("Touto akcí smažete všechna aktuální data, databáze se vyčistí a nahraje se modelový příklad.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.get() == ButtonType.OK) {
            DatabaseD.initDBStruct();
            // DatabaseD.loadInitData  TODO: create model data
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Úspěch");
            info.setContentText("Databáze obnovena");
            info.showAndWait();
            
        }else {

        }

    }
}
