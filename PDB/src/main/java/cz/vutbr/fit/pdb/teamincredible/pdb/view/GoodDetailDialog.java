package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;


/**
 * Created by Dan on 12/10/2016.
 * Class describing dialog to display Good type details
 */
public class GoodDetailDialog extends Dialog {

    private static final String DETAILS_TITLE = "Zobrazit detail zboží";
    private static final String DETAILS_HEADER_TEXT = "Zboží";
    private static final String DETAILS_REMOVE_BUTTON = "Odstranit zboží";
    private static final String DETAILS_CLOSE_BUTTON = "Zavřít";


    private ButtonType btnDeleteType = new ButtonType(DETAILS_REMOVE_BUTTON, ButtonBar.ButtonData.OK_DONE);
    private ButtonType btnCancelType = new ButtonType(DETAILS_CLOSE_BUTTON, ButtonBar.ButtonData.CANCEL_CLOSE);
    private GridPane grid;

    private int referenceGid;
    private Good GoodItem;
    private Label namePlaceholder;
    private Label idPlaceholder;
    private Label volumePlaceholder;
    private Label pricePlaceholder;


    public GoodDetailDialog(int id)
    {
        this.referenceGid = id;
        GoodItem = DatabaseD.GetGoodById(id);

        InitDialog();

        CreateDialogLayout();

    }


    private void InitDialog()
    {
        setTitle(DETAILS_TITLE);
        setHeaderText(DETAILS_HEADER_TEXT);
        initModality(Modality.NONE);
    }


    private void CreateDialogLayout()
    {
        getDialogPane().getButtonTypes().addAll(btnDeleteType, btnCancelType);

        DefineControls();

        PrepareGrid();

        getDialogPane().setContent(grid);
    }

    private void DefineControls()
    {
        namePlaceholder = new Label(GoodItem.getName());
        idPlaceholder = new Label("ID: " + String.valueOf(GoodItem.getId()));
        volumePlaceholder = new Label("Objem: " + String.valueOf(GoodItem.getVolume()));
        pricePlaceholder = new Label("Cena: " + String.valueOf(GoodItem.getPrice()));

        // TODO: Image view or something with similar functionality
    }

    private void PrepareGrid()
    {
        // Define grid parameters
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add labels & controls to grid
        grid.add(namePlaceholder, 0, 0);
        grid.add(new Label("1,0"), 1, 0);
        grid.add(idPlaceholder, 0, 1);
        grid.add(new Label("1,1"), 1, 1);
        grid.add(volumePlaceholder, 0, 2);
        grid.add(new Label("1,2"), 1, 2);
        grid.add(pricePlaceholder, 0, 3);
        grid.add(new Label("1,3"), 1, 3);

        // Span OrdImage over these grid cells
        grid.add(new Label("2,0"), 2,0);
        grid.add(new Label("2,1"), 2,1);
        grid.add(new Label("2,2"), 2,2);
        grid.add(new Label("2,3"), 2,3);
    }


}
