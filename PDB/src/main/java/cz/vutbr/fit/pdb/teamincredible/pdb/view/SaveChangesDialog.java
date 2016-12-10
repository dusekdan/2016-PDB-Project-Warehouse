package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import javafx.scene.control.Alert;

/**
 * Created by popko on 10/12/2016.
 */
public class SaveChangesDialog extends Alert{

    public SaveChangesDialog(Alert.AlertType alertType)
    {
        super(alertType);

        setTitle("Potvrdit znovunačtení skladu z databáze");
        setHeaderText("Zahodit změny?");
        setContentText("Touto akcí zahodíte změny a obsah skladu se načte znovu z databáze.");
    }

}
