package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import cz.vutbr.fit.pdb.teamincredible.pdb.model.Good;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Dan on 12/7/2016.
 */
public class AddGoodDialog extends Dialog<Good>
{

    // Text constants used across the dialog
    private static final String ADD_GOOD_DIALOG_TITLE = "Vytvořte nový typ zboží!";
    private static final String ADD_GOOD_DIALOG_HEADERTEXT = "Nové zboží:";
    private static final String ADD_GOOD_DIALOG_PHOTOPICKED_DEFAULTTEXT = "Nevybráno";
    private static final String ADD_GOOD_BUTTON = "Přidat zboží";
    private static final String FILE_CHOOSER_HEADER_TEXT = "Vyberte fotku zboží";
    private static final String ADD_GOOD_NAME_PLACEHOLDER = "Název zboží";
    private static final String ADD_GOOD_VOLUME_PLACEHOLDER = "Objem zboží";
    private static final String ADD_GOOD_PRICE_PLACEHOLDER = "Cena zboží";
    private static final String ADD_GOOD_PHOTO_BUTTON_PLACEHOLDER = "Vybrat fotku zboží";
    private static final String ADD_GOOD_PHOTO_PLACEHOLDER = "Fotka zboží";
    private static final String ERR_VOLUME_PARSE = "Unable to parse volume, maybe it is in invalid format?";
    private static final String ERR_PRICE_PARSE = "Unable to parse price, maybe it is in invalid format?";

    // Controls & nodes declarations
    private Node addGoodButton;
    private TextField name;
    private TextField volume;
    private Button photoChooser;
    private TextField price;
    private TextField pickedPhoto;
    private ButtonType btnConfirmType;
    private GridPane grid;

    /**
     * Creates the new Add Good dialog
     */
    public AddGoodDialog()
    {
        InitDialog();

        CreateDialogLayout();

        AssignActionToFileChooserButton();

        AssignControlListeners();

        ReturnDialogValues();
    }


    /**
     *  Basic initialization of dialog appearance (modality, header text and title)
     */
    private void InitDialog()
    {
        setTitle(ADD_GOOD_DIALOG_TITLE);
        setHeaderText(ADD_GOOD_DIALOG_HEADERTEXT);
        initModality(Modality.APPLICATION_MODAL);
    }


    /**
     * Puts the layout for dialog together
     */
    private void CreateDialogLayout()
    {
        // Creation of confirmation button for the dialog
        btnConfirmType = new ButtonType(ADD_GOOD_BUTTON, ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(btnConfirmType, ButtonType.CANCEL);

        DefineControls();

        PrepareGrid();

        getDialogPane().setContent(grid);
    }


    /**
     * Prepares definitions of controls for the dialog
     */
    private void DefineControls()
    {
        // Defining textfields
        name = new TextField();
        name.setPromptText(ADD_GOOD_NAME_PLACEHOLDER);

        volume = new TextField();
        volume.setPromptText(ADD_GOOD_VOLUME_PLACEHOLDER);

        photoChooser = new Button();
        photoChooser.setText(ADD_GOOD_PHOTO_BUTTON_PLACEHOLDER);

        pickedPhoto = new TextField();
        pickedPhoto.setText(ADD_GOOD_DIALOG_PHOTOPICKED_DEFAULTTEXT);
        pickedPhoto.setEditable(true); // TODO: Decide whether we should let user edit the path by hand

        price = new TextField();
        price.setPromptText(ADD_GOOD_PRICE_PLACEHOLDER);

        // Disable confirm button as values are not filled in at load time
        addGoodButton = getDialogPane().lookupButton(btnConfirmType);
        addGoodButton.setDisable(true);
    }


    /**
     * Prepares grid to which is the dialog rendered
     */
    private void PrepareGrid()
    {
        // Define grid parameters
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Add labels & controls to grid
        grid.add(new Label(ADD_GOOD_NAME_PLACEHOLDER), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label(ADD_GOOD_VOLUME_PLACEHOLDER), 0, 1);
        grid.add(volume, 1, 1);
        grid.add(new Label(ADD_GOOD_PHOTO_PLACEHOLDER), 0, 2);
        grid.add(photoChooser, 1, 2);
        grid.add(pickedPhoto, 2, 2);
        grid.add(new Label(ADD_GOOD_PRICE_PLACEHOLDER), 0, 3);
        grid.add(price, 1, 3);
    }


    /**
     * Describes action that is done after file chooser dialog is opened
     */
    private void AssignActionToFileChooserButton()
    {
        photoChooser.setOnAction(
                event -> {
                    System.out.println("Select photo event fired...");

                    // Extract stage from control that fired event
                    Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());

                    // Open file chooser
                    FileChooser fc = new FileChooser();
                    fc.setTitle(FILE_CHOOSER_HEADER_TEXT);

                    // Set picked path to TextView control
                    File pickedFile = fc.showOpenDialog(stage);
                    pickedPhoto.setText(pickedFile.getAbsolutePath());
                }
        );
    }


    /**
     * Based on the way dialog was closed returns either new Good object or null
     */
    private void ReturnDialogValues()
    {
        // Returns new instance of goods for the dialog closed with add goods option and null for the cancelled dialog
        setResultConverter(
                dialogButton -> {
                    if (dialogButton == btnConfirmType)
                        return PrepareGoodObject(name, volume, pickedPhoto, price);
                    else
                        return null;
                }
        );
    }


    /**
     * Assigns validation listeners to controls in dialog
     */
    private void AssignControlListeners()
    {
        // Validation class for controls
        final ControlValidationStates controls = new ControlValidationStates();

        name.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.trim().isEmpty())
                        controls.setNameValid(true);
                    else
                        controls.setNameValid(false);
                    SetAddGoodButtonState(controls, addGoodButton);
                }
        );

        volume.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.trim().isEmpty())
                        controls.setVolumeValid(true);
                    else
                        controls.setVolumeValid(false);
                    SetAddGoodButtonState(controls, addGoodButton);
                }
        );

        price.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.trim().isEmpty())
                        controls.setPriceValid(true);
                    else
                        controls.setPriceValid(false);
                    SetAddGoodButtonState(controls, addGoodButton);
                }
        );

        pickedPhoto.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue.trim().isEmpty() && !newValue.equals(ADD_GOOD_DIALOG_PHOTOPICKED_DEFAULTTEXT))
                        controls.setImagePathValid(true);
                    else
                        controls.setImagePathValid(false);
                    SetAddGoodButtonState(controls, addGoodButton);
                }
        );
    }


    /**
     * Toggles AddGood button based on the validation output of controls
     * @param controls ControlValidationStates holds information about validation states for all the controls
     * @param addGoodButton Node reference to button to be toggled
     */
    private void SetAddGoodButtonState(ControlValidationStates controls, Node addGoodButton)
    {
        if (controls.isAllValid())
        {
            addGoodButton.setDisable(false);
        }
        else
        {
            addGoodButton.setDisable(true);
        }
    }


    /**
     * Creates Good object to be returned on dialog closing
     * @param name TextField control containing name of the good to be created
     * @param volume TextField control containing volume of the good to be created
     * @param pickedPhoto TextField control containing path to the picked photo of the good to be created
     * @param price TextField control containing price of the good to be created
     * @return Good object of the good to be created
     */
    private Good PrepareGoodObject(TextField name, TextField volume, TextField pickedPhoto, TextField price)
    {
        String goodName = name.getText();
        String goodFilePath = pickedPhoto.getText();

        double goodPrice = -1.0;
        try
        {
            goodPrice = Double.parseDouble(price.getText());
        }
        catch (Exception e)
        {
            System.out.println(ERR_PRICE_PARSE);
        }

        double goodVolume = -1.0;
        try
        {
            goodVolume = Double.parseDouble(volume.getText());
        }
        catch (Exception e)
        {
            System.out.println(ERR_VOLUME_PARSE);
        }


        return new Good(goodName, goodVolume, goodFilePath, goodPrice);
    }


    /**
     * Auxiliary class keeping validation states of controls in dialog
     */
    private class ControlValidationStates
    {
        boolean isNameValid = false;
        boolean isVolumeValid = false;
        boolean isPriceValid = false;
        boolean isImagePathValid = false;

        void setPriceValid(boolean priceValid) { isPriceValid = priceValid; }
        void setImagePathValid(boolean imagePathValid) { isImagePathValid = imagePathValid; }
        void setVolumeValid(boolean volumeValid) { isVolumeValid = volumeValid; }
        void setNameValid(boolean nameValid) { isNameValid = nameValid; }

        boolean isAllValid()
        {
            return isNameValid && isVolumeValid && isPriceValid && isImagePathValid;
        }
    }
}
