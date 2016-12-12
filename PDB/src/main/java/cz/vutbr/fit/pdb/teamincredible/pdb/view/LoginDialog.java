package cz.vutbr.fit.pdb.teamincredible.pdb.view;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.util.Pair;

/**
 * Represents login dialog
 * @author Sucha
 */
public class LoginDialog extends Dialog<Pair<String, String>> {

     // Text constants used across the dialog
    private static final String LOGIN_DIALOG_TITLE = "Přihlašte se, prosím";
    private static final String LOGIN_DIALOG_HEADERTEXT = "Zadejte své přístupové údaje";
    private static final String LOGIN_DIALOG_CONFIRM_BUTTON = "Přihlásit";
    private static final String LOGIN_DIALOG_USERNAME_TEXTFIELD = "Uživatelské jméno:";
    private static final String LOGIN_DIALOG_USERNAME_PASSWORD = "Heslo:";

    // Controls & nodes declarations
    private ButtonType loginButtonType;
    private GridPane grid;
    private TextField username;
    private PasswordField password;
    private Node loginButton;

    
    public LoginDialog() {

        // init dialog window
        setTitle(LOGIN_DIALOG_TITLE);
        setHeaderText(LOGIN_DIALOG_HEADERTEXT);
        initModality(Modality.APPLICATION_MODAL);

        loginButtonType = new ButtonType(LOGIN_DIALOG_CONFIRM_BUTTON, ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        
        // setup grid
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // setup controls
        username = new TextField();
        username.setPromptText("login");

        password = new PasswordField();
        password.setPromptText("Heslo");

        // add controls to grid
        grid.add(new Label(LOGIN_DIALOG_USERNAME_TEXTFIELD), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label(LOGIN_DIALOG_USERNAME_PASSWORD), 0, 1);
        grid.add(password, 1, 1);

        loginButton = getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // add listener for check username
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // add result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });
  
        getDialogPane().setContent(grid);

    }

}
