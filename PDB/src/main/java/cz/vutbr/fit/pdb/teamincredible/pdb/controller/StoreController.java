package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.SpatialConverters;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomRackDefinition;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.CustomShape;
import cz.vutbr.fit.pdb.teamincredible.pdb.CustomSwingNode;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AvailableRacksView;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.ImportDBAlert;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SaveChangesDialog;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import oracle.jdbc.internal.OraclePreparedStatement;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.List;

import static cz.vutbr.fit.pdb.teamincredible.pdb.controller.ActionsController.DisplayInformation;
import static cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore.shapeList;

/**
 * Created by Sucha on 03.12.2016.
 */
public class StoreController implements Initializable {

    @FXML
    private AnchorPane StoreACP;
    private Button AddNewRackButton;
    public int itemsCount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("StoreController init");

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500,500);

        createSwingContent(swingNode);

        StoreACP.getChildren().add(swingNode);
        StoreACP.resize(500,500);
        StoreACP.getStyleClass().add("storeAnchorPane");

        itemsCount = 5;

    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JPanel panel = null;
                try {
                    panel = new SpatialViewerForStore();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                panel.setPreferredSize(new Dimension(500,500));

                swingNode.setContent(panel);
                panel.repaint();
            }
        });
    }

    public boolean addNewRack(ActionEvent actionEvent) throws SpatialConverters.JGeometry2ShapeException {

        if (saveStore())
        {
            Rectangle rightUpCorner = new Rectangle(500-50, 0, 50, 50);

            for (CustomShape shape : shapeList)
            {
                System.out.println("rightup corner: "+rightUpCorner.getBounds().toString());
                System.out.println("shape: "+shape.getBoundingBox().toString());
                if (rightUpCorner.getBounds().intersects(shape.getBoundingBox()))
                {
                    DisplayInformation("Není místo pro vložení nového stojanu.", "Uvolněte prosím místo v pravém horním rohu.");
                    return false;
                }
            }

            AvailableRacksView addNewRackDialog = new AvailableRacksView(itemsCount);
            Optional<CustomRackDefinition> newRack = addNewRackDialog.showAndWait();

            if(insertNewRack(newRack))
                return true;
            else
            {
                DisplayInformation("Chyba při vkládání nového stojanu..", "Zkuste to prosím znovu.");
                return false;
            }

        }
        else
        {
            DisplayInformation("Chyba při vkládání nového stojanu.", "Zkuste to prosím znovu.");
            return false;
        }

    }


    public boolean saveStore(ActionEvent actionEvent) {

        if (saveStore())
        {
            return refreshStore(false);
        }
        else {
            DisplayInformation("Chyba při ukládání", "Sklad se nepodařilo uložit do databáze, zkuste to prosím znovu.");
            return false;
        }
    }

    public boolean refreshStore(ActionEvent actionEvent) {

        if (!SpatialViewerForStore.hasChanges())
        {
            DisplayInformation("Úspěch", "Obsah skladu je aktuální!");
            return true;
        }

        SaveChangesDialog confirm = new SaveChangesDialog(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            return refreshStore(true);
        }

        return true;

    }

    private boolean insertNewRack(Optional<CustomRackDefinition> newRack) {

        int rackType = -1;
        if (newRack.isPresent())
            rackType = newRack.get().getId();
        else
            return false;
        int id = -1;

        JGeometry rackGeometry = null;

        try (Connection dbConnection = DatabaseD.getConnection()) {

            dbConnection.setAutoCommit(false);
            try (
                    PreparedStatement getRackDefinitionStatement = dbConnection.prepareStatement("select * from rack_definitions where rack_defs_id=?");
            )
            {

                getRackDefinitionStatement.setInt(1, rackType);
                ResultSet resultSet = getRackDefinitionStatement.executeQuery();

                while (resultSet.next()) {
                    byte[] image = resultSet.getBytes("rack_defs_shape");

                    // save this to racks
                    rackGeometry = JGeometry.load(image);

                }
                resultSet.close();

                if (rackGeometry!= null)
                {
                    //insert to database
                    //posun rackGeometry
                    try (
                            PreparedStatement insertStatement = dbConnection.prepareStatement("insert into racks (racks_type, racks_geometry, racks_rotation) values (?, ?, ?)");
                    )
                    {
                        Point translationPoint = new Point(500 - rackGeometry.createShape().getBounds().width, 0);
                        rackGeometry = rackGeometry.affineTransforms(true, translationPoint.getX(), translationPoint.getY(), 0,
                                false, null, 0, 0, 0,
                                false, null, null, 0, 0,
                                false, 0, 0, 0, 0, 0, 0,
                                false, null, null, 0,
                                false, null, null);

                        STRUCT rackGeometrySDO = JGeometry.store(rackGeometry, dbConnection);

                        insertStatement.setInt(1, rackType);
                        insertStatement.setObject(2, rackGeometrySDO);
                        insertStatement.setInt(3, 0);

                        int affectedRows = insertStatement.executeUpdate();

                        if (affectedRows == 0) {
                            throw new SQLException("Creating user failed, no rows affected.");
                        }

                        id = DatabaseD.GetInsertedRowID(insertStatement);

                        Shape newShape = SpatialConverters.jGeometry2Shape(rackGeometry);

                        CustomShape shapeObject = new CustomShape(newShape, rackType, id);

                        shapeList.add(shapeObject);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            dbConnection.commit();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private boolean saveStore() {

        JGeometry rackGeometry = null;
        int id = -1;

        try (Connection dbConnection = DatabaseD.getConnection()) {

            dbConnection.setAutoCommit(false);
            // update všechny stojany

            try (
                    PreparedStatement updateStatement = dbConnection.prepareStatement("UPDATE racks set racks_type=?, racks_geometry=?, racks_rotation=? where racks_id=?");
            ) {
                // update jeden po druhém
                for (CustomShape shapeObject : shapeList) {
                    try (
                            PreparedStatement selectStatement = dbConnection.prepareStatement("select * from racks where racks_type=?");
                    ) {
                        selectStatement.setInt(1, shapeObject.getRackTypeId());
                        ResultSet resultSet = selectStatement.executeQuery();

                        System.out.println("Select statement: " + selectStatement.toString());
                        System.out.println("Result set: " + resultSet.toString());

                        while (resultSet.next()) {
                            byte[] image = resultSet.getBytes("racks_geometry");
                            System.out.println("Image: " + image.toString());
                            rackGeometry = JGeometry.load(image);
                        }
                        resultSet.close();

                        if (rackGeometry == null) {
                            return false;
                        }

                        //posun rackGeometry tak, aby splnovalo translation v shapeObject
                        rackGeometry = rackGeometry.affineTransforms(true, shapeObject.getTranslation().getX(), shapeObject.getTranslation().getY(), 0,
                                false, null, 0, 0, 0,
                                false, null, null, 0, 0,
                                false, 0, 0, 0, 0, 0, 0,
                                false, null, null, 0,
                                false, null, null);

                        STRUCT rackGeometrySDO = JGeometry.store(rackGeometry, dbConnection);

                        updateStatement.setInt(1, shapeObject.getRackTypeId());
                        updateStatement.setObject(2, rackGeometrySDO);
                        updateStatement.setInt(3, shapeObject.getRotation());
                        updateStatement.setInt(4, shapeObject.getId());

                        int affectedRows = updateStatement.executeUpdate();

                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            dbConnection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return refreshStore(false);
    }


    private boolean refreshStore(boolean showMessage) {

        StoreACP.getChildren().remove(0);

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500,500);

        createSwingContent(swingNode);

        StoreACP.getChildren().add(swingNode);

        if (showMessage)
            DisplayInformation("Úspěch", "Databáze úspěšně obnovena!");

        return true;
    }

}
