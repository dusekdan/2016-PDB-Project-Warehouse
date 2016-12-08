package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.CustomSwingNode;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by Sucha on 03.12.2016.
 */
public class StoreController implements Initializable {

    @FXML
    private AnchorPane StoreACP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("StoreController init");

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500,500);

        createSwingContent(swingNode);

        StoreACP.getChildren().add(swingNode); // Co myslíš, pojede to? :D
        StoreACP.resize(500,500);
        StoreACP.getStyleClass().add("storeAnchorPane");
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {


//                JPanel imageView = new JPanel(new GridBagLayout());
//                imageView.setPreferredSize(new Dimension(500,500));
//                swingNode.setContent(imageView);


                JPanel panel = null;
                try {
                    panel = new Ex2SpatialViewer();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //JButton button = new JButton();
                //button.setSize(500,500);
                //panel.add(button);
                panel.setPreferredSize(new Dimension(500,500));

                swingNode.setContent(panel);

            }
        });
    }
}
