package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodInRack;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.GoodTypeRecord;
import cz.vutbr.fit.pdb.teamincredible.pdb.controller.ActionsController;
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
 * @author Anna
 */
public class QueryingAreaDialog extends Dialog<String> {

    // Text constants used across the dialog
    public static final String REQ_QUERY_SUM = "Zobraz celkovou hodnotu zboží ve vybrané oblasti skladu.";
    //public static final String REQ_QUERY_MOST_EXPENSIVE = "Zobraz cenu a typ nejdražšího zboží v oblasti.";
    //public static final String REQ_QUERY_EMPTY = "Spočítej počet volných míst ve stojanech vybrané oblasti";
    public static final String REQ_QUERY_ITEMS = "Spočítej počet umístěných položek zboží do stojanů ve vybrané oblasti";

    // Controls & nodes declarations
    private ButtonType confirmButtonType;
    private GridPane grid;
    private ComboBox cb;
    private Node confirmButton;

    public QueryingAreaDialog() {

        InitDialog();

        CreateDialogLayout();

        SetConverters();
    }

    /**
     *
     * Set window title and texts
     *
     */
    private void InitDialog() {

        setTitle("Vyberte dotaz, který vás zajímá.");
        setHeaderText("Vyberte dotaz, který vás zajímá.");

        initModality(Modality.APPLICATION_MODAL);

    }

    /**
     * Create dialog layout grid
     *
     *
     */
    private void CreateDialogLayout() {

        confirmButtonType = new ButtonType("Odešli dotaz", ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        CreateControlsPreloadData();

        // set grid
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Dotaz:"), 0, 1);
        grid.add(cb, 1, 1);

        confirmButton = getDialogPane().lookupButton(confirmButtonType);
        getDialogPane().setContent(grid);

    }

    /**
     * create dialog controls
     *
     */
    private void CreateControlsPreloadData() {

        cb = new ComboBox();
        cb.getItems().addAll(
                REQ_QUERY_SUM,
                //REQ_QUERY_MOST_EXPENSIVE,
                //REQ_QUERY_EMPTY,
                REQ_QUERY_ITEMS
        );

        cb.getSelectionModel().selectFirst();

    }

    /**
     * set converters and listeners
     *
     */
    private void SetConverters() {

        setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return cb.getValue().toString();
            }
            return null;
        });

    }
}
