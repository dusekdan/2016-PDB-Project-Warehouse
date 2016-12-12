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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AddGoodsAndCount;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.AskForGoodsAndCount;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForAvailableRacks;
import javafx.util.Pair;

import static cz.vutbr.fit.pdb.teamincredible.pdb.controller.SpatialViewerForQueryingStore.NONE;
import static cz.vutbr.fit.pdb.teamincredible.pdb.controller.SpatialViewerForQueryingStore.SELECT_POINT;
import static cz.vutbr.fit.pdb.teamincredible.pdb.view.SpatialViewerForStore.shapeList;

/**
 * Created by Sucha on 03.12.2016.
 */
public class StoreController implements Initializable {

    private static final String QUERY_STR_SHOW_ITEMS_FROM = "Zobrazit zboží z vybrané plochy.";
    private static final String QUERY_STR_SHORTEST_WAY_TO_ITEM = "Spočítat nejkratší cestu ke zboží z vybraného místa.";
    private static final String QUERY_STR_SHOW_ITEMS_WITH_PROPERTY_FROM = "Zobrazit zboží určité vlastnosti z vybrané plochy.";
    private static final String QUERY_STR_COUNT_USED_AREA = "Vypočítat celkovou zastavěnou plochu skladu.";
    private static final String QUERY_STR_COUNT_SMALLEST_BOUNDING_BOX = "Vypočítat nejmenší možnou velikost skladu s těmito stojany.";
    private static final String QUERY_STR_SHOW_RACKS_WITH_SPECIFIC_ITEMS = "Zobrazit všechny stojany obsahující zboží jisté vlastnosti.";
    private static final String QUERY_STR_JOIN_RACKS = "Sloučit dva stojany.";

    @FXML
    private ButtonBar queryButtonBar2;
    @FXML
    private javafx.scene.text.Text textMoreInfoQuery;
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

        setUIForModification();

        comboboxQuery.getItems().addAll(
                //QUERY_STR_SHOW_ITEMS_FROM,
                QUERY_STR_SHORTEST_WAY_TO_ITEM,
                //QUERY_STR_SHOW_ITEMS_WITH_PROPERTY_FROM,
                QUERY_STR_COUNT_USED_AREA,
                QUERY_STR_JOIN_RACKS
        //QUERY_STR_COUNT_SMALLEST_BOUNDING_BOX,
        //QUERY_STR_SHOW_RACKS_WITH_SPECIFIC_ITEMS
        );
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

                        JGeometry rotationPoint = new JGeometry(shapeObject.getBoundingBox().getCenterX(), shapeObject.getBoundingBox().getCenterY(), JGeometry.GTYPE_POINT);

                        //posun rackGeometry tak, aby splnovalo translation v shapeObject
                        rackGeometry = rackGeometry.affineTransforms(true, shapeObject.getTranslation().getX(), shapeObject.getTranslation().getY(), 0,
                                false, null, 0, 0, 0,
                                true, rotationPoint, null, shapeObject.getRotation() * Math.toRadians(45), -1,
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

        Optional<Pair<Integer, GoodTypeRecord>> res = new AddGoodsAndCount(rackId, true).showAndWait();

        if (!res.isPresent()) {
            return;
        } else {
            goodId = res.get().getValue().goodIdProperty().getValue();
            count = res.get().getKey();
        }

        if (DatabaseD.InsertGoodIntoStorage(goodId, rackId, count)) {
            DisplayInformation("Úspěch", "Do stojanu č. " + rackId + " bylo přidáno " + count + " kus/ů zboží " + goodId + ".");
        }
        else {
             DisplayInformation("Pozor", "Zboží nelze přidat, překročena kapacita skladu!");
        }

    }

    public void removeGoodFromRack() {
        int rackId = isSomethingSelected();
        if (rackId == 0) {
            DisplayInformation("Info", "Je nutno zvolit umístění vloženého zboží!");
            return;
        }
        int goodId;
        int count;

        Optional<Pair<Integer, GoodInRack>> res = new AskForGoodsAndCount(rackId).showAndWait();
        if (!res.isPresent()) {
            return;
        } else {
            goodId = res.get().getValue().getGoodID();
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

            Optional<Pair<Integer, GoodInRack>> res = new AskForGoodsAndCount(rackFromId).showAndWait();
            if (!res.isPresent()) {
                return;
            } else {
                goodFromId = res.get().getValue().getGoodID();
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
            DatabaseD.MoveGoodFromTo(goodFromId, rackFromId, goodFromId, rackToId, count);
            movePrepare = true;

            DisplayInformation("Úspěch", "Zboží přesunuto.");
        }

    }

    public void modificationMode(ActionEvent actionEvent) {

        refreshStore(false);
        setUIForModification();
        SpatialViewerForQueryingStore.status = NONE;
    }

    public void queryMode(ActionEvent actionEvent) {
        saveStore();
        showQueryMode();
        setUIforQuerying();
        SpatialViewerForQueryingStore.unselectAllShapes();
        SpatialViewerForQueryingStore.status = NONE;
    }

    private void showQueryMode() {

        StoreACP.getChildren().remove(0);

        final SwingNode swingNode = new CustomSwingNode();
        swingNode.resize(500, 500);

        createSwingContentForQueries(swingNode);

        StoreACP.getChildren().add(swingNode);

    }

    private void createSwingContentForQueries(SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JPanel panel = null;
                panel = new SpatialViewerForQueryingStore();

                panel.setPreferredSize(new Dimension(500, 500));

                swingNode.setContent(panel);
                panel.repaint();
            }
        });
    }

    public void executeQuery(ActionEvent actionEvent) {

        System.out.println("Selected query: " + comboboxQuery.getValue());

        switch (comboboxQuery.getValue().toString()) {
            case QUERY_STR_SHOW_ITEMS_FROM:
                break;
            case QUERY_STR_SHORTEST_WAY_TO_ITEM:

                if (SpatialViewerForQueryingStore.getSelectedShapeObject() != null) {
                    Optional<Pair<Integer, GoodTypeRecord>> res = new AddGoodsAndCount(rackFromId, false).showAndWait();
                    if (!res.isPresent()) {
                        DisplayInformation("Něco se pokazilo.", "Zkuste prosím opakovat akci.");
                    } else {
                        int goodId = res.get().getValue().goodIdProperty().getValue();
                        if (executeShortestWayToItem(goodId)) {
                            showQueryMode();
                            showMoreInformationForQuery("");
                            SpatialViewerForQueryingStore.status = NONE;
                        }
                    }
                } else {
                    DisplayInformation("Něco se pokazilo.", "Vyberte prosím bod, od kterého chcete měřit vzdálenost.");
                }
                break;
            case QUERY_STR_SHOW_ITEMS_WITH_PROPERTY_FROM:
                break;
            case QUERY_STR_COUNT_USED_AREA:
                showMoreInformationForQuery("Počítám celkovou rozlohu stojanů ve skladu...");
                double area = countUsedArea();
                DisplayInformation("Výsledek dotazu", "Celková zastavěná plocha skladu činí " + area + "m krychlových");
                showMoreInformationForQuery("");
                SpatialViewerForQueryingStore.status = NONE;
                break;
            case QUERY_STR_COUNT_SMALLEST_BOUNDING_BOX:
                break;
            case QUERY_STR_SHOW_RACKS_WITH_SPECIFIC_ITEMS:
                break;

        }

    }

    private boolean executeShortestWayToItem(int goodId) {

        CustomShape selectedPoint = null;
        selectedPoint = SpatialViewerForQueryingStore.getSelectedShapeObject();

        if (selectedPoint == null) {
            return false;
        } else {

            int nearestRackID = -1;

            try (Connection dbConnection = DatabaseD.getConnection()) {

                dbConnection.setAutoCommit(false);
                try (
                        PreparedStatement selectStatement = dbConnection.prepareStatement("SELECT r.racks_id, SDO_GEOM.SDO_DISTANCE(r.racks_geometry, SDO_GEOMETRY(2001, null, SDO_POINT_TYPE(?,?,NULL), NULL, NULL) , 0.005) as distance FROM racks r join rack_goods rg on r.racks_id=rg.racks_id where rg.goods_id=? order by distance fetch first 1 rows only");) {

                    selectStatement.setInt(1, selectedPoint.getCenterPoint().x);
                    selectStatement.setInt(2, selectedPoint.getCenterPoint().y);
                    selectStatement.setInt(3, goodId);
                    ResultSet resultSet = selectStatement.executeQuery();

                    while (resultSet.next()) {
                        nearestRackID = resultSet.getInt("racks_id");

                    }
                    resultSet.close();

                    CustomShape nearestRack = null;
                    if (nearestRackID != -1) {
                        nearestRack = SpatialViewerForQueryingStore.getShapeById(nearestRackID);
                        if (nearestRack != null) {
                            SpatialViewerForQueryingStore.unselectAllShapes();
                            nearestRack.setSelected();
                            return true;
                        }
                    } else {
                        DisplayInformation("Něco se pokazilo.", "Toto zboží se nenachází v žádném stojanu, zkuste prosím opakovat akci.");
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        DisplayInformation("Něco se pokazilo.", "Zkuste prosím opakovat akci.");
        return false;
    }

    private void setUIForModification() {
        modificationModeButton.setSelected(true);
        queryModeButton.setSelected(false);

        queryButtonBar.setPrefHeight(0.0);
        queryButtonBar.setDisable(true);
        queryButtonBar.setVisible(false);
        queryButtonBar2.setPrefHeight(0.0);
        queryButtonBar2.setDisable(true);
        queryButtonBar2.setVisible(false);
        modificationButtonBar.setPrefHeight(40.0);
        modificationButtonBar.setVisible(true);
        modificationButtonBar.setDisable(false);
        modificationButtonBar2.setPrefHeight(40.0);
        modificationButtonBar2.setVisible(true);
        modificationButtonBar2.setDisable(false);
    }

    private void setUIforQuerying() {
        queryButtonBar.setPrefHeight(40.0);
        queryButtonBar.setVisible(true);
        queryButtonBar.setDisable(false);
        queryButtonBar2.setPrefHeight(40.0);
        queryButtonBar2.setVisible(true);
        queryButtonBar2.setDisable(false);
        modificationButtonBar.setPrefHeight(0.0);
        modificationButtonBar.setVisible(false);
        modificationButtonBar.setDisable(true);
        modificationButtonBar2.setPrefHeight(0.0);
        modificationButtonBar2.setVisible(false);
        modificationButtonBar2.setDisable(true);

        modificationModeButton.setSelected(false);
        queryModeButton.setSelected(true);
    }

    public void comboboxQuerySelected(ActionEvent actionEvent) {

        SpatialViewerForQueryingStore.unselectAllShapes();
        SpatialViewerForQueryingStore.deleteAllTemporalShapes();
        showMoreInformationForQuery("");

        switch (comboboxQuery.getValue().toString()) {
            case QUERY_STR_SHOW_ITEMS_FROM:
                break;
            case QUERY_STR_SHORTEST_WAY_TO_ITEM:
                showMoreInformationForQuery("Klikněte do prostoru skadu odkud chcete měřit vzdálenost a potvrďte odesláním dotazu.");
                SpatialViewerForQueryingStore.status = SELECT_POINT;
                break;
            case QUERY_STR_SHOW_ITEMS_WITH_PROPERTY_FROM:
                break;
            case QUERY_STR_COUNT_USED_AREA:
                break;
            case QUERY_STR_COUNT_SMALLEST_BOUNDING_BOX:
                break;
            case QUERY_STR_SHOW_RACKS_WITH_SPECIFIC_ITEMS:
                break;

        }

    }

    private void showMoreInformationForQuery(String message) {
        textMoreInfoQuery.setText(message);
        textSelectQuery.setVisible(true);
    }

    private double countUsedArea() {

        double area = -1;

        try (Connection dbConnection = DatabaseD.getConnection()) {
            dbConnection.setAutoCommit(false);
            try (
                    PreparedStatement selectStatement = dbConnection.prepareStatement("select sum(sdo_geom.sdo_area(racks_geometry,1)) plocha from racks")) {
                //selectStatement.setInt(1, shapeObject.getRackTypeId());
                ResultSet resultSet = selectStatement.executeQuery();

                while (resultSet.next()) {
                    area = resultSet.getDouble(1);
                }
                resultSet.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return area;
    }
}
