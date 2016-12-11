/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.util.Pair;

/**
 *
 * @author Sucha
 */
public class AskForGoodsAndCount extends Dialog<Pair<Integer, GoodInRack>> {

    public AskForGoodsAndCount(int rack, boolean inserting) {
        setTitle("Počet zboží");
        setHeaderText("Zadejte počet kusů a druh zboží");

        initModality(Modality.APPLICATION_MODAL);

        ButtonType loginButtonType = new ButtonType("Proveď", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("počet");
        ComboBox cb;
        if (!inserting) {
            ObservableList<GoodInRack> data;
            data = GoodInRack.loadData(rack);
            while (data.isEmpty()) {// Fujky fuj
                data = GoodInRack.loadData(rack);
            }

            cb = new ComboBox(data);
        } else {
            ObservableList<GoodTypeRecord> data = GoodTypeRecord.getData();
            while (data.isEmpty()) {// Fujky fuj
                data = GoodTypeRecord.getData();
            }

            cb = new ComboBox(data);
        }
        //     ComboBox cb = new ComboBox(GoodTypeRecord.getData());
         cb.getSelectionModel().selectFirst();
        grid.add(new Label("Počet"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Zboží"), 0, 1);
        grid.add(cb, 1, 1);

        final Node loginButton = getDialogPane().lookupButton(loginButtonType);
        

        if (!inserting) {
           
            username.textProperty().addListener((observable, oldValue, newValue) -> {
               //  System.err.println("disablee: "+(Integer.parseInt(newValue) <= ((GoodInRack) cb.getValue()).getCount())+Integer.parseInt(newValue)+"<="+((GoodInRack) cb.getValue()).getCount());
                loginButton.setDisable(Integer.parseInt(newValue) > ((GoodInRack) cb.getValue()).getCount());
            });
            loginButton.setDisable(true);
        }
        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(Integer.parseInt(username.getText()), (GoodInRack) cb.getValue());
            }
            return null;
        });

    }

}
