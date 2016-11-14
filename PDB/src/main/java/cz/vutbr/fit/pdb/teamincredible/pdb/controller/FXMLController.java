package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.Database;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    
    private Database db;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
        
    }

    public void setDb(Database db) {
        this.db = db;
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
