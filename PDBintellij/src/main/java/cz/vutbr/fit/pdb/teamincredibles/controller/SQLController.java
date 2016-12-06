package  cz.vutbr.fit.pdb.teamincredibles.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Sucha on 02.12.2016.
 */
public class SQLController implements Initializable {

    @FXML
    private TextArea sqlConsole;

    @FXML
    private TextField sqlQueryField;



    @FXML
    private void sqlMakeRequest(ActionEvent event) {

    }

    public SQLController() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SQLController init");
    }


}
