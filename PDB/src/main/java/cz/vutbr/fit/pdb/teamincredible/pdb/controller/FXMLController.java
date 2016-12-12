package cz.vutbr.fit.pdb.teamincredible.pdb.controller;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class FXMLController implements Initializable {


    /**
     * Initialization method for FXML controller
     * @param url URL unused
     * @param rb ResourceBundle unused
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Check whether database already contains our objects -> then color the canvas
        System.out.println("FXMLController init");
    }

    /**
     * Empty constructor
     */
    public FXMLController() {

    }
}
