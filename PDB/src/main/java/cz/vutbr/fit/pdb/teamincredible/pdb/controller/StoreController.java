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
import javafx.scene.text.Text;
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

import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AskForGoodsAndCount;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForAvailableRacks;
import javafx.util.Pair;
import static cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore.shapeList;

/**
 * Created by Sucha on 03.12.2016.
 */
public class StoreController implements Initializable {


    @FXML
    private ButtonBar modificationButtonBar2;
    @FXML
    private ButtonBar modificationButtonBar;
    @FXML
    private ButtonBar queryButtonBar;
    @FXML
    private Button executeQueryButton;
    @FXML
    private ComboBox comboboxQuery;
    @FXML
    private Text textSelectQuery;
    @FXML
    private AnchorPane StoreACP;
    @FXML
    private ToggleButton modificationModeButton;
    @FXML
    private ToggleButton queryModeButton;
    @FXML
    private Button addGoodToRackBtn;
    @FXML
    private Button removeGoodFromRackBtn;
    public int itemsCount;

    private static int rackIdSelected;
    private int count;
    private boolean movePrepare = true;
    private int rackFromId;
    private int goodFromId;
    private int rackToId;
    private int goodToId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("StoreController init");

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500, 500);

        createSwingContent(swingNode);

        StoreACP.getChildren().add(swingNode);
        StoreACP.resize(500, 500);
        StoreACP.getStyleClass().add("storeAnchorPane");

        itemsCount = 5;

        modificationModeButton.setSelected(true);
        queryModeButton.setSelected(false);

        queryButtonBar.setPrefHeight(0.0);
        queryButtonBar.setDisable(true);
        queryButtonBar.setVisible(false);
        modificationButtonBar.setPrefHeight(40.0);
        modificationButtonBar.setVisible(true);
        modificationButtonBar.setDisable(false);
        modificationButtonBar2.setPrefHeight(40.0);
        modificationButtonBar2.setVisible(true);
        modificationButtonBar2.setDisable(false);
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

                panel.setPreferredSize(new Dimension(500, 500));

                swingNode.setContent(panel);
                panel.repaint();
            }
        });
    }

    public boolean addNewRack(ActionEvent actionEvent) throws SpatialConverters.JGeometry2ShapeException {

        if (saveStore()) {
            Rectangle rightUpCorner = new Rectangle(500 - 50, 0, 50, 50);

            for (CustomShape shape : shapeList) {
                System.out.println("rightup corner: " + rightUpCorner.getBounds().toString());
                System.out.println("shape: " + shape.getBoundingBox().toString());
                if (rightUpCorner.getBounds().intersects(shape.getBoundingBox())) {
                    DisplayInformation("Není místo pro vložení nového stojanu.", "Uvolněte prosím místo v pravém horním rohu.");
                    return false;
                }
            }

            AvailableRacksView addNewRackDialog = new AvailableRacksView(itemsCount);
            Optional<CustomRackDefinition> newRack = addNewRackDialog.showAndWait();

            if (insertNewRack(newRack)) {
                return true;
            } else {
                DisplayInformation("Chyba při vkládání nového stojanu..", "Zkuste to prosím znovu.");
                return false;
            }

        } else {
            DisplayInformation("Chyba při vkládání nového stojanu.", "Zkuste to prosím znovu.");
            return false;
        }

    }

    public boolean saveStore(ActionEvent actionEvent) {

        if (saveStore()) {
            return refreshStore(false);
        } else {
            DisplayInformation("Chyba při ukládání", "Sklad se nepodařilo uložit do databáze, zkuste to prosím znovu.");
            return false;
        }
    }

    public boolean refreshStore(ActionEvent actionEvent) {

        if (!SpatialViewerForStore.hasChanges()) {
            DisplayInformation("Úspěch", "Obsah skladu je aktuální!");
            return true;
        }

        SaveChangesDialog confirm = new SaveChangesDialog(Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            return refreshStore(true);
        }

        return true;

    }

    private boolean insertNewRack(Optional<CustomRackDefinition> newRack) {

        int rackType = -1;
        if (newRack.isPresent()) {
            rackType = newRack.get().getId();
        } else {
            return false;
        }
        int id = -1;

        JGeometry rackGeometry = null;

        try (Connection dbConnection = DatabaseD.getConnection()) {

            dbConnection.setAutoCommit(false);
            try (
                    PreparedStatement getRackDefinitionStatement = dbConnection.prepareStatement("select * from rack_definitions where rack_defs_id=?");) {

                getRackDefinitionStatement.setInt(1, rackType);
                ResultSet resultSet = getRackDefinitionStatement.executeQuery();

                while (resultSet.next()) {
                    byte[] image = resultSet.getBytes("rack_defs_shape");

                    // save this to racks
                    rackGeometry = JGeometry.load(image);

                }
                resultSet.close();

                if (rackGeometry != null) {
                    //insert to database
                    //posun rackGeometry
                    String returnCols[] = {"RACKS_ID"};
                    try (
                            PreparedStatement insertStatement = dbConnection.prepareStatement("insert into racks (racks_type, racks_geometry, racks_rotation) values (?, ?, ?)", returnCols);) {
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
        } catch (SQLException e) {
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
                    PreparedStatement updateStatement = dbConnection.prepareStatement("UPDATE racks set racks_type=?, racks_geometry=?, racks_rotation=? where racks_id=?");) {
                // update jeden po druhém
                for (CustomShape shapeObject : shapeList) {
                    try (
                            PreparedStatement selectStatement = dbConnection.prepareStatement("select * from racks where racks_type=?");) {
                        int affectedRows;

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

                        affectedRows = updateStatement.executeUpdate();
                        System.out.println("Number of affected rows: " + affectedRows);

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

        return true;
        //return refreshStore(false);
    }

    private boolean refreshStore(boolean showMessage) {

        StoreACP.getChildren().remove(0);

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500, 500);

        createSwingContent(swingNode);

        StoreACP.getChildren().add(swingNode);

        if (showMessage) {
            DisplayInformation("Úspěch", "Databáze úspěšně obnovena!");
        }

        return true;
    }

    private int isSomethingSelected() {
        for (CustomShape rack : SpatialViewerForStore.shapeList) {
            if (rack.isSelected()) {
                // return 1;
                return rack.getId();
            }
        }
        return 0;

    }

    public void addGoodInRack() {
        int rackId = isSomethingSelected();
        if (rackId == 0) {
            DisplayInformation("Info", "Je nutno zvolit umístění vloženého zboží!");
            return;
        }
        int goodId;
        int count;

        Optional<Pair<Integer, GoodTypeRecord>> res = new AskForGoodsAndCount(rackId).showAndWait();

        if (!res.isPresent()) {
            return;
        } else {
            goodId = res.get().getValue().goodIdProperty().getValue();
            count = res.get().getKey();
        }

        DatabaseD.InsertGoodIntoStorage(goodId, rackId, count);
        DisplayInformation("Úspěch", "Do stojanu č. " + rackId + " bylo přidáno " + count + " kus/ů zboží " + goodId + ".");

    }

    public void removeGoodFromRack() {
        int rackId = isSomethingSelected();
        if (rackId == 0) {
            DisplayInformation("Info", "Je nutno zvolit umístění vloženého zboží!");
            return;
        }
        int goodId;
        int count;

        Optional<Pair<Integer, GoodTypeRecord>> res = new AskForGoodsAndCount(rackId).showAndWait();
        if (!res.isPresent()) {
            return;
        } else {
            goodId = res.get().getValue().goodIdProperty().getValue();
            count = res.get().getKey();
        }

        DatabaseD.RemoveGoodFromStorage(goodId, rackId, count);
        DisplayInformation("Úspěch", "Ze stojanu č. " + rackId + " bylo odebráno " + count + " kus/ů zboží " + goodId + ".");
    }

    public void moveGoodFromToRack() {
        if (movePrepare) {
            this.rackFromId = isSomethingSelected();
            this.goodFromId = 0;

            if (rackFromId == 0) {
                DisplayInformation("Info", "Je nutno zvolit umístění vloženého zboží!");
                return;
            }

            Optional<Pair<Integer, GoodTypeRecord>> res = new AskForGoodsAndCount(rackFromId).showAndWait();
            if (!res.isPresent()) {
                return;
            } else {
                goodFromId = res.get().getValue().goodIdProperty().getValue();
                count = res.get().getKey();
            }

            //    DatabaseD.RemoveGoodFromStorage(goodId, rackId, count);
            DisplayInformation("Úspěch", "Zvolte kam zboží přesunout");

            movePrepare = false;
        } else {
            this.rackToId = isSomethingSelected();
            if (rackToId == 0) {
                DisplayInformation("Info", "Je nutno zvolit umístění vloženého zboží!");
                return;
            }
         DatabaseD.MoveGoodFromTo(goodFromId,  rackFromId, goodFromId, rackToId, count);
         movePrepare = true;

         DisplayInformation("Úspěch", "Zboží přesunuto.");
    }

    }


    public void modificationMode(ActionEvent actionEvent) {

        refreshStore(false);


        queryButtonBar.setPrefHeight(0.0);
        queryButtonBar.setDisable(true);
        queryButtonBar.setVisible(false);
        modificationButtonBar.setPrefHeight(40.0);
        modificationButtonBar.setVisible(true);
        modificationButtonBar.setDisable(false);
        modificationButtonBar2.setPrefHeight(40.0);
        modificationButtonBar2.setVisible(true);
        modificationButtonBar2.setDisable(false);


        modificationModeButton.setSelected(true);
        queryModeButton.setSelected(false);
    }

    public void queryMode(ActionEvent actionEvent) {
        showQueryMode();

        queryButtonBar.setPrefHeight(40.0);
        queryButtonBar.setVisible(true);
        queryButtonBar.setDisable(false);
        modificationButtonBar.setPrefHeight(0.0);
        modificationButtonBar.setVisible(false);
        modificationButtonBar.setDisable(true);
        modificationButtonBar2.setPrefHeight(0.0);
        modificationButtonBar2.setVisible(false);
        modificationButtonBar2.setDisable(true);

        modificationModeButton.setSelected(false);
        queryModeButton.setSelected(true);
    }

    private void showQueryMode() {

        StoreACP.getChildren().remove(0);

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500,500);

        createSwingContentForQueries(swingNode);

        StoreACP.getChildren().add(swingNode);

    }

    private void createSwingContentForQueries(SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JPanel panel = null;
                panel = new SpatialViewerForQueryingStore();

                panel.setPreferredSize(new Dimension(500,500));

                swingNode.setContent(panel);
                panel.repaint();
            }
        });
    }

    public void executeQuery(ActionEvent actionEvent) {
    }

}
