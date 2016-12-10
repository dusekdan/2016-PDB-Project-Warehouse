package cz.vutbr.fit.pdb.teamincredible.pdb;

import cz.vutbr.fit.pdb.teamincredible.pdb.view.LoginDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;


public class MainApp extends Application {

    private String userName;
    private String password;


    @Override
    public void start(Stage stage) throws Exception {

        EnsureDatabaseCredentials();

        // Establish connection to database
        DatabaseD.setUserName(userName);
        DatabaseD.setPassword(password);
        DatabaseD.init();

        // Load FXML layout
        FXMLLoader base = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        Parent root = base.load();

        // Display Scene
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("Skladiště");
        stage.setScene(scene);
        stage.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void stop() throws Exception{
        DatabaseD.closeConnection();
    }

    private void EnsureDatabaseCredentials()
    {
        LoginDialog dialog = new LoginDialog();

        if(AreCredentialsPresent())
        {
            ExtractCredentialsFromConfig();
        }
        else
        {
            // Get credentials for database from logon dialog
            Optional<Pair<String, String>> cred = dialog.showAndWait();

            while (!cred.isPresent()) {
                cred = dialog.showAndWait();
            }

            if (cred.isPresent())
            {
                userName = cred.get().getKey();
                password = cred.get().getValue();
            }
        }
    }

    private void ExtractCredentialsFromConfig()
    {
        BufferedReader bReader = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/config/credentials")));

        try
        {
            String line;
            int lineNumber = 0;
            while ((line = bReader.readLine()) != null)
            {
                if (lineNumber == 0)
                {
                    userName = line.substring(line.indexOf("=")+1);

                }
                else if (lineNumber == 1 )
                {
                    password = line.substring(line.indexOf("=")+1);
                }
                else
                {
                    break;
                }

                lineNumber++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean AreCredentialsPresent()
    {
        BufferedReader bReader = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream("/config/credentials")));

        boolean returnValue = true;

        try
        {
            String line;
            int lineNumber = 0;
            while ((line = bReader.readLine()) != null)
            {
                if (lineNumber == 0)
                {
                    if (line.substring(line.indexOf("=")+1).equals("XXXXX"))
                        returnValue = false;
                }
                else if (lineNumber == 1 )
                {
                    if (line.substring(line.indexOf("=")+1).equals("XXXXX"))
                        returnValue = false;
                }
                else
                {
                    break;
                }

                lineNumber++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
