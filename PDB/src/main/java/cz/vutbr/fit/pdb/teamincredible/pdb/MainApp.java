package cz.vutbr.fit.pdb.teamincredible.pdb;

import cz.vutbr.fit.pdb.teamincredible.pdb.controller.FXMLController;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.LoginDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Pair;


public class MainApp extends Application {


    private String userName;
    private String password;

    @Override
    public void start(Stage stage) throws Exception {
        

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));

        FXMLController controller = new FXMLController();

        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        
        stage.setTitle("Skladiště");
        
        stage.setScene(scene);
        stage.show();
        
        LoginDialog dialog = new LoginDialog();

        // Check whether previously used credentials are stored
        if(AreCredentialsPresent())
        {
            ExtractCredentialsFromConfig();
        }
        else
        {

            Optional<Pair<String, String>> cred = dialog.showAndWait();

            while (!cred.isPresent()) {
                cred = dialog.showAndWait();
            }

            userName = cred.get().getKey();
            password = cred.get().getValue();

        }

        System.out.println(userName + ":" + password);
        controller.getDb().setPasswd(password);
        controller.getDb().setUsername(userName);
        
        controller.getDb().testConnection();
        
        
        
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
