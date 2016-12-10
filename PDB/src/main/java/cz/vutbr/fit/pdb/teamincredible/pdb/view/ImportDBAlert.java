package cz.vutbr.fit.pdb.teamincredible.pdb.view;

import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Import;
import javafx.scene.control.Alert;

/**
 * Created by Dan on 12/8/2016.
 * Definition of Import Database Alert and related constants
 */
public class ImportDBAlert extends Alert {

    private static final String IMPORT_DB_ALERT_TITLE = "Reset databáze";
    private static final String IMPORT_DB_ALERT_HEADER_TEXT = "Opravdu chcete vrátit databázi do původního stavu?";
    private static final String IMPORT_DB_ALERT_CONTENT_TEXT = "Touto akcí smažete všechna aktuální data. Databáze bude vyčištěna a uvedena do původního stavu.";

    public ImportDBAlert(AlertType alertType)
    {
        super(alertType);

        setTitle(IMPORT_DB_ALERT_TITLE);
        setHeaderText(IMPORT_DB_ALERT_HEADER_TEXT);
        setContentText(IMPORT_DB_ALERT_CONTENT_TEXT);
    }
}
