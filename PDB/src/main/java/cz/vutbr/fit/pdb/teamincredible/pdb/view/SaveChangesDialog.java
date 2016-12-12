package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import javafx.scene.control.Alert;

/**
 * Created by popko on 10/12/2016
 * Dialog for saving changes
 */
public class SaveChangesDialog extends Alert{

    /**
     * Constructor building dialog
     * @param alertType AlertType type of the dialog
     */
    public SaveChangesDialog(Alert.AlertType alertType)
    {
        super(alertType);

        setTitle("Potvrdit znovunačtení skladu z databáze");
        setHeaderText("Zahodit změny?");
        setContentText("Touto akcí zahodíte změny a obsah skladu se načte znovu z databáze.");
    }

}
