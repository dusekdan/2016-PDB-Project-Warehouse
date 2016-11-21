package cz.vutbr.fit.pdb.teamincredible.pdb;

import cz.vutbr.fit.pdb.teamincredible.pdb.controller.FXMLController;
import cz.vutbr.fit.pdb.teamincredible.pdb.view.LoginDialog;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;


public class MainApp extends Application {

    private Database db;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        db = new Database();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        
        FXMLController controller = new FXMLController();
        controller.setDb(db);
        
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        
        stage.setTitle("Skladiště");
        
        stage.setScene(scene);
        stage.show();
        
        LoginDialog dialog = new LoginDialog();
        
        Optional<Pair<String, String>> cred = dialog.showAndWait();
        
        while(!cred.isPresent()) {
            cred = dialog.showAndWait();
        }
        
        String user = cred.get().getKey();
        String pass = cred.get().getValue();
        
        System.out.println(user + " " + pass);
        db.setPasswd(pass);
        db.setUsername(user);
        
        
        
        
        
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
