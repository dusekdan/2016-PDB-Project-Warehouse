package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

import java.util.List;

/**
 * Created by Dan on 12/11/2016
 * Contains definition of dialog displayed when "find similar images" option is selected
 */
class SimilarImagesDialog extends Dialog {

    // List of Good objects that has similar image to the one currently displayed in this dialog
    private List<Good> similarGoods;

    // Controls declaration
    private GridPane grid;
    private Label similarity01;
    private Label similarity02;
    private Label similarity03;
    private ImageView photoPlaceholder01;
    private ImageView photoPlaceholder02;
    private ImageView photoPlaceholder03;

    /**
     * Constructor creating Dialog
     * @param similarGoods List of Good objects that are similar to the item from which this dialog was invoked
     */
    SimilarImagesDialog(List<Good> similarGoods)
    {
        this.similarGoods = similarGoods;

        InitDialog();

        CreateDialogLayout();

        PrepareGrid();

        getDialogPane().setContent(grid);
    }


    /**
     * Encapsulated basic initialization of a dialog
     */
    private void InitDialog()
    {
        setTitle("Podobné obrázky");
        initModality(Modality.APPLICATION_MODAL);
    }


    /**
     * Encapsulated grid definition and controls placement
     */
    private void PrepareGrid()
    {
        grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add labels and imageviews to the grid
        grid.add(similarity01, 0, 0);
        grid.add(photoPlaceholder01, 0, 1);
        grid.add(similarity02, 1, 0);
        grid.add(photoPlaceholder02, 1, 1);
        grid.add(similarity03, 2, 0);
        grid.add(photoPlaceholder03, 2, 1);
    }


    /**
     * Encapsulated dialog layout creation
     */
    private void CreateDialogLayout()
    {
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        LoadImagesControls();
    }


    /**
     * Loads images from similarGoods list to imageViews (and resize them if needed)
     */
    private void LoadImagesControls()
    {
        int i = 0;
        for (Good item : similarGoods) {

            if (i == 0)
            {
                similarity01 = new Label("Podobnost: " + item.getSimilarity());
                photoPlaceholder01 = new ImageView();
                photoPlaceholder01.setImage(item.getRealImageData());

                if (item.getRealImageData().getWidth() > 500 || item.getRealImageData().getHeight() > 500)
                {
                    photoPlaceholder01.setFitHeight(500);
                    photoPlaceholder01.setFitWidth(500);
                    photoPlaceholder01.setPreserveRatio(true);
                }
            }
            else if (i == 1)
            {
                similarity02 = new Label("Podobnost: " + item.getSimilarity());
                photoPlaceholder02 = new ImageView();
                photoPlaceholder02.setImage(item.getRealImageData());

                if (item.getRealImageData().getWidth() > 500 || item.getRealImageData().getHeight() > 500)
                {
                    photoPlaceholder02.setFitHeight(500);
                    photoPlaceholder02.setFitWidth(500);
                    photoPlaceholder02.setPreserveRatio(true);
                }
            }
            else if (i == 2)
            {
                similarity03 = new Label("Podobnost: " + item.getSimilarity());
                photoPlaceholder03 = new ImageView();
                photoPlaceholder03.setImage(item.getRealImageData());

                if (item.getRealImageData().getWidth() > 500 || item.getRealImageData().getHeight() > 500)
                {
                    photoPlaceholder03.setFitHeight(500);
                    photoPlaceholder03.setFitWidth(500);
                    photoPlaceholder03.setPreserveRatio(true);
                }

                // Interested only in up to three most similar items
                break;
            }

            i++;
        }
    }
}
