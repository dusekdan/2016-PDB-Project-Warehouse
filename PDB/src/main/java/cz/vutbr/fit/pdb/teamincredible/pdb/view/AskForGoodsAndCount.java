package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.util.Pair;

/**
 *
 * @author Sucha
 */
public class AskForGoodsAndCount extends Dialog<Pair<Integer, GoodInRack>> {

    // Text constants used across the dialog
    private static final String ASK_GOOD_DIALOG_TITLE = "Určete počet a typ zboží";
    private static final String ASK_GOOD_DIALOG_HEADERTEXT = "Zvolte:";
    private static final String ASK_GOOD_CONFIRM_BUTTON = "Proveď";
    private static final String ASK_GOOD_COUNT_TEXTFIELD = "Počet zboží";
    private static final String ASK_GOOD_COUNT_LABEL = "Počet";
    private static final String ASK_GOOD_CB_LABEL = "Název zboží:";

    // Controls & nodes declarations
    private ButtonType confirmButtonType;
    private GridPane grid;
    private TextField count;
    private ComboBox cb;
    private Node confirmButton;

    /**
     * Constructs object which represents one good type in specified rack
     *
     * @param rack specified rack id
     */
    public AskForGoodsAndCount(int rack) {
        InitDialog();

        CreateDialogLayout(rack);

        SetConverters();

    }

    /**
     *
     * Set window title and texts
     *
     */
    private void InitDialog() {
        setTitle(ASK_GOOD_DIALOG_TITLE);
        setHeaderText(ASK_GOOD_DIALOG_HEADERTEXT);

        initModality(Modality.APPLICATION_MODAL);

    }

    /**
     * Create dialog layout grid
     *
     * @param rack
     */
    private void CreateDialogLayout(int rack) {

        confirmButtonType = new ButtonType(ASK_GOOD_CONFIRM_BUTTON, ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        CreateControlsPreloadData(rack);

        // set grid
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label(ASK_GOOD_COUNT_LABEL), 0, 0);
        grid.add(count, 1, 0);
        grid.add(new Label(ASK_GOOD_CB_LABEL), 0, 1);
        grid.add(cb, 1, 1);

        confirmButton = getDialogPane().lookupButton(confirmButtonType);

        getDialogPane().setContent(grid);

    }

    /**
     * create dialog controls and load data from database
     *
     * @param rack id of rack
     */
    private void CreateControlsPreloadData(int rack) {
        count = new TextField();
        count.setPromptText(ASK_GOOD_COUNT_TEXTFIELD);

        ObservableList<GoodInRack> data;
        data = GoodInRack.loadData(rack);
    
        if (data.isEmpty()) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setHeaderText("Info");
            info.setTitle("Nic nenalezeno");
            info.setContentText("Rack č. "+ rack+ " je prázdný");
            info.showAndWait();
            close();
        }
        
        cb = new ComboBox(data);
        //     ComboBox cb = new ComboBox(GoodTypeRecord.getData());
        cb.getSelectionModel().selectFirst();

    }

    /**
     * set converters and listeners
     *
     */
    private void SetConverters() {

        count.textProperty().addListener((observable, oldValue, newValue) -> {
            //  System.err.println("disablee: "+(Integer.parseInt(newValue) <= ((GoodInRack) cb.getValue()).getCount())+Integer.parseInt(newValue)+"<="+((GoodInRack) cb.getValue()).getCount());
            confirmButton.setDisable(Integer.parseInt(newValue) > ((GoodInRack) cb.getValue()).getCount());
        });
        confirmButton.setDisable(true);

        setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return new Pair<>(Integer.parseInt(count.getText()), (GoodInRack) cb.getValue());
            }
            return null;
        });

    }
}
