package  cz.vutbr.fit.pdb.teamincredibles.controller;


import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Check whether database already contains our objects -> then color the canvas
        System.out.println("FXMLController init");
    }

    public FXMLController() {

    }
}
