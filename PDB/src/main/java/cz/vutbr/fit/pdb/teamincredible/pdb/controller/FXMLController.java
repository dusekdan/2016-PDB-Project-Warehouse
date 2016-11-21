package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.Database;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController implements Initializable {
    
    @FXML
    private TextArea sqlConsole;
    
    @FXML
    private TextField sqlQueryField;
    
    
    private Database db;
    
    @FXML
    private void sqlMakeRequest(ActionEvent event) {
       String query = sqlQueryField.getText() + "\n";
        sqlConsole.appendText(query);
        sqlQueryField.setText("");
        
        
        try {
            db.executeQuery(query);
        } catch (SQLException ex) {
            sqlConsole.appendText(ex.getSQLState());
        }
        
       
    }

    public void setDb(Database db) {
        this.db = db;
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
