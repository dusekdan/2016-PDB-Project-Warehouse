package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.DatabaseD;
import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import java.util.List;


/**
 * Created by Dan on 12/10/2016.
 * Class describing dialog to display Good type details
 */
public class GoodDetailDialog extends Dialog<Good> {

    private static final String DETAILS_TITLE = "Zobrazit detail zboží";
    private static final String DETAILS_HEADER_TEXT = "Zboží";
    private static final String DETAILS_REMOVE_BUTTON = "Odstranit zboží";
    private static final String DETAILS_CLOSE_BUTTON = "Zavřít";


    private ButtonType btnDeleteType = new ButtonType(DETAILS_REMOVE_BUTTON, ButtonBar.ButtonData.OK_DONE);
    private ButtonType btnCancelType = new ButtonType(DETAILS_CLOSE_BUTTON, ButtonBar.ButtonData.CANCEL_CLOSE);
    private GridPane grid;

    private Good GoodItem;
    private Label namePlaceholder;
    private Label idPlaceholder;
    private Label volumePlaceholder;
    private Label pricePlaceholder;
    private ImageView photoPlaceholder;
    private Button findSimilar;
    private Button rotateImage;


    public GoodDetailDialog(int id)
    {
        GoodItem = DatabaseD.GetGoodById(id);

        InitDialog();

        CreateDialogLayout();

        SetDialogClosingActions();
    }


    private void SetDialogClosingActions()
    {
        setResultConverter(
                dialogButton -> {
                    if (dialogButton == btnDeleteType) {
                        boolean success = DatabaseD.RemoveGood(GoodItem.getId());
                        if (!success)
                            return null;
                        else return new Good();
                    }
                    else
                    {
                        return null;
                    }
                }
        );
    }

    private void InitDialog()
    {
        setTitle(DETAILS_TITLE);
        setHeaderText(DETAILS_HEADER_TEXT);
        initModality(Modality.NONE);
    }


    private void CreateDialogLayout()
    {
        getDialogPane().getButtonTypes().addAll(btnCancelType, btnDeleteType);

        DefineControls();

        PrepareGrid();

        getDialogPane().setContent(grid);
    }


    /**
     * Assigning action to find similar button
     */
    private void AssignActionToFindSimilarButton()
    {
        findSimilar.setOnAction(
                event -> {
                    System.out.println("Find similar photos event fired...");

                    // Open dialog with displayed similarities in TableView
                    List<Good> similarGoods = DatabaseD.GetSimilarGoods(GoodItem.getId());  // call made here only for debug purposes
                    System.out.println("Found: " + similarGoods.size());

                    Dialog similarityDialog = new SimilarImagesDialog(similarGoods);
                    similarityDialog.show();

                }
        );
    }


    /**
     * Assigning action to rotate image by 90°
     */
    private void AssignActionToRotateImageButton()
    {
        rotateImage.setOnAction(
                event -> {
                    System.out.println("D: Rotate image action fired...");

                    if (DatabaseD.RotateImage(GoodItem.getId())) {
                        System.out.println("Image rotated successfully.");

                        // Update image placeholder to look like it updated from database
                        photoPlaceholder.setRotate((photoPlaceholder.getRotate() + 90));

                    }
                    else {
                        System.out.println("Unable to rotate image");
                    }
                }
        );
    }



    private void DefineControls()
    {
        namePlaceholder = new Label(GoodItem.getName());
        idPlaceholder = new Label("ID: " + String.valueOf(GoodItem.getId()));
        volumePlaceholder = new Label("Objem: " + String.valueOf(GoodItem.getVolume()));
        pricePlaceholder = new Label("Cena: " + String.valueOf(GoodItem.getPrice()));


        photoPlaceholder = new ImageView();
        Image tmpImage = GoodItem.getRealImageData();

        double height = tmpImage.getHeight();
        double width = tmpImage.getHeight();

        photoPlaceholder.setImage(tmpImage);

        // Images that are too big should be hard-code minimized.
        if (height > 500 || width > 500)
        {
            photoPlaceholder.setFitHeight(500);
            photoPlaceholder.setFitWidth(500);
            photoPlaceholder.setPreserveRatio(true);

            System.out.println("D: Image had to be resized, because it is " + width + "x" + height + ".");
        }

        // Prepare find similarities button action
        findSimilar = new Button();
        findSimilar.setText("Najdi podobné obrázky");
        AssignActionToFindSimilarButton();

        // Prepare rotate image button
        rotateImage = new Button();
        rotateImage.setText("Orotovat obrázek o 90°");
        AssignActionToRotateImageButton();
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
        grid.add(idPlaceholder, 0, 1);
        grid.add(volumePlaceholder, 0, 2);
        grid.add(pricePlaceholder, 0, 3);

        // Span OrdImage over these grid cells
        grid.add(photoPlaceholder, 2,3, 2, 1);
        grid.add(findSimilar, 3, 1);
        grid.add(rotateImage, 3,2);
    }


}
