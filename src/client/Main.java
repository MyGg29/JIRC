package client;

import client.controllers.ConnexionController;
import client.controllers.MainController;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{


        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Client.fxml"));

        final Parent root = loader.load();

        /* ---------- Initialisation de la fenêtre principale ---------- */
        primaryStage.setTitle("JMessenger");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/common.css").toExternalForm());
        primaryStage.setScene(scene);

        MainController mainController = loader.getController();
        primaryStage.setOnCloseRequest(mainController::shutdown);
        primaryStage.show();

        /* ---------- Initialisation de la fenêtre de connexion ---------- */
        Stage connexionStage = new Stage();
        FXMLLoader loaderConnexion = new FXMLLoader(getClass().getResource("views/Connexion.fxml"));//
        final Parent rootConnexion = loaderConnexion.load();//
        Scene sceneConnexion = new Scene(rootConnexion); //

        sceneConnexion.getStylesheets().add(getClass().getResource("css/common.css").toExternalForm());
        ConnexionController connexionController = loaderConnexion.getController();

        connexionStage.setTitle("Connexion");
        connexionStage.initModality(Modality.APPLICATION_MODAL);
        connexionStage.initOwner(scene.getWindow());//Le model de connexion est celui de la scene principale
        connexionStage.setScene(sceneConnexion);


        connexionStage.showAndWait();
    }

    @Override
    public void stop(){
        System.out.println("Arrêt...");
    }

    public static void main(String[] args) {
        /* ---------- Lancement ---------- */
        launch(args);
    }
}

