package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Goods;
import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.ResultSet;
import java.util.List;
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
        List<Goods> goods = DatabaseD.GetGoods();
        for (Goods good : goods)
        {
            System.out.println("Id:" + good.getId());
            System.out.println("Name: " + good.getTitle());
        }



    }
}
