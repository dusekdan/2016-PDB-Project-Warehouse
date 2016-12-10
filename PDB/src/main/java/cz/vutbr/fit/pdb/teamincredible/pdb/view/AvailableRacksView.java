package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.CustomSwingNode;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomRackDefinition;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Created by popko on 10/12/2016.
 */
public class AvailableRacksView extends Dialog<CustomRackDefinition>
{

    public Button confirmButton;
    public Button cancelButton;
    public int rackCount;
    private BorderPane borderPane;
    private AnchorPane racksACP;
    private ButtonBar buttonsGroup;


    private ButtonType btnConfirmType;

    public AvailableRacksView(int rackCount)
    {
        setTitle("Adding new rack to the store.");
        this.rackCount = rackCount;

        getDialogPane().setPrefSize(rackCount*125+100+100, 125+100+600);

        borderPane = new BorderPane();

        borderPane.setPadding(new javafx.geometry.Insets(50, 50, 50, 50));
        borderPane.setPrefSize(rackCount*125+100,125+100+500);

        // Adding buttons here
        btnConfirmType = new ButtonType("Přidat", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(btnConfirmType, ButtonType.CANCEL);

        // Return results back to the caller
        setResultConverter(
                dialogButton -> {
                    if (dialogButton == btnConfirmType)
                        return new CustomRackDefinition(1);
                    else
                        return null;
                }
        );

        racksACP = new AnchorPane();
        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(rackCount*125+100,125+100+100);
        createSwingContent(swingNode);

        racksACP.getChildren().add(swingNode);
        racksACP.resize(rackCount*125+100,125+100+100);
        racksACP.getStyleClass().add("racksAnchorPane");

        borderPane.setCenter(racksACP);

        getDialogPane().getChildren().add(borderPane);

        //setResultConverter(null);

    }

    private void initializeAvailableRacks() {





    }

    private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {

            JPanel panel = null;
            //JButton button = new JButton("buu");


            try {
                panel = new SpatialViewerForAvailableRacks();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //panel.add(button);


            panel.setPreferredSize(new Dimension(rackCount*125+100, 125+100+100));
            swingNode.setContent(panel);

        });

    }


}