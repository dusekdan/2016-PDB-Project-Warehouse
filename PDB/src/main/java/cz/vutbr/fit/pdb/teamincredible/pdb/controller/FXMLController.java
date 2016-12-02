package cz.vutbr.fit.pdb.teamincredible.pdb.controller;

import cz.vutbr.fit.pdb.teamincredible.pdb.Database;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController implements Initializable {

    @FXML
    private TextArea sqlConsole;

    @FXML
    private TextField sqlQueryField;


    private Database db;

    @FXML
    private void sqlMakeRequest(ActionEvent event) {
        String query = sqlQueryField.getText() + "\n";
        sqlConsole.appendText(query);
        sqlQueryField.setText("");


        ResultSet resultSet;


        try {
            System.out.print("Making a request... ");
            resultSet = db.executeQuery(query);

            if (resultSet != null)
            {
                StringBuilder builder = new StringBuilder();
                System.out.print("Result is not null");
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int nCols = rsmd.getColumnCount();

                while (resultSet.next())
                {
                    for (int i = 1;i<= nCols; i++) {
                        builder.append(resultSet.getString(i));

                    }
                    sqlConsole.appendText(builder.toString());
                }
            }
            else
                System.out.print("Result is null");
        } catch (SQLException ex) {
            System.out.print("aaaand it failed!");
            sqlConsole.appendText(ex.getMessage());
        }




    }

    public Database getDb() {
        return this.db;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Check whether database already contains our objects -> then color the canvas

    }

    public FXMLController() {
        try {
            db = new Database();
        } catch (SQLException e) {
            sqlConsole.appendText(e.getSQLState());
        }
    }
}
