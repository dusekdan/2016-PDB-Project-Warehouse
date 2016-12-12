package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
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
public class AddGoodsAndCount extends Dialog<Pair<Integer, GoodTypeRecord>> {

    // Text constants used across the dialog
    private static final String ASK_GOOD_DIALOG_TITLE = "Určete počet a typ zboží";
    private static final String ASK_GOOD_DIALOG_TITLE_2 = "Vyberte hledaný typ zboží.";
    private static final String ASK_GOOD_DIALOG_HEADERTEXT = "Zvolte:";
    private static final String ASK_GOOD_DIALOG_HEADERTEXT_2 = "Vyberte hledané zboží ze seznamu:";
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
    private boolean askForCount;

    /**
     * Constructs object which represents one good type in specified rack
     *
     * @param rack specified rack id
     * @param inserting switch between inserting in rack and removing from rack
     */
    public AddGoodsAndCount(int rack, boolean askForCount) {

        this.askForCount = askForCount;
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
        if (askForCount)
        {
            setTitle(ASK_GOOD_DIALOG_TITLE);
            setHeaderText(ASK_GOOD_DIALOG_HEADERTEXT);
        }
        else
        {
            setTitle(ASK_GOOD_DIALOG_TITLE_2);
            setHeaderText(ASK_GOOD_DIALOG_HEADERTEXT_2);
        }

        initModality(Modality.APPLICATION_MODAL);

    }

    /**
     * Create dialog layout grid
     *
     * @param rack
     * @param inserting
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

        if (askForCount)
        {
            grid.add(new Label(ASK_GOOD_COUNT_LABEL), 0, 0);
            grid.add(count, 1, 0);
        }

        grid.add(new Label(ASK_GOOD_CB_LABEL), 0, 1);
        grid.add(cb, 1, 1);

        confirmButton = getDialogPane().lookupButton(confirmButtonType);

        getDialogPane().setContent(grid);

    }

    /**
     * create dialog controls and load data from database
     *
     * @param rack id of rack
     * @param inserting is dialog used for insert new amount
     */
    private void CreateControlsPreloadData(int rack) {
        count = new TextField();
        if (askForCount)
        {
            count.setPromptText(ASK_GOOD_COUNT_TEXTFIELD);
        }
        else
        {
            count.setVisible(false);
        }

        ObservableList<GoodTypeRecord> data = GoodTypeRecord.getData();
        while (data.isEmpty()) {// Fujky fuj
            data = GoodTypeRecord.getData();
        }

        cb = new ComboBox(data);
        //     ComboBox cb = new ComboBox(GoodTypeRecord.getData());
        cb.getSelectionModel().selectFirst();

    }

    /**
     * set converters and listeners
     *
     * @param inserting add listener for check amount of goods in rack
     */
    private void SetConverters() {

        count.textProperty().addListener((observable, oldValue, newValue) -> {
            //  System.err.println("disablee: "+(Integer.parseInt(newValue) <= ((GoodInRack) cb.getValue()).getCount())+Integer.parseInt(newValue)+"<="+((GoodInRack) cb.getValue()).getCount());
            confirmButton.setDisable(newValue.isEmpty());
        });
        if (!askForCount)
        {
            confirmButton.setDisable(false);
        }
        else
        {
            confirmButton.setDisable(true);
        }


        setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {

                if (askForCount)
                {return new Pair<>(Integer.parseInt(count.getText()), (GoodTypeRecord) cb.getValue());}
                else
                {return new Pair<>(-1, (GoodTypeRecord) cb.getValue());}
            }
            return null;
        });

    }
}
