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
import javafx.stage.StageStyle;
import models.Client;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Client client = new Client();

        /** Initialisation de la fenêtre principale **/
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Client.fxml"));
        final Parent root = loader.load();
        MainController mainController = loader.getController();
        Scene scene = new Scene(root);
        mainController.setClient(client);//important
        primaryStage.setTitle("JMessenger");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(mainController::shutdown);
        scene.getStylesheets().add(getClass().getResource("css/common.css").toExternalForm());
        primaryStage.show();

        /** Initialisation de la fenêtre de connexion **/
        FXMLLoader loaderConnexion = new FXMLLoader(getClass().getResource("views/Connexion.fxml"));//
        final Parent rootConnexion = loaderConnexion.load();//
        ConnexionController connexionController = loaderConnexion.getController();
        Stage connexionStage = new Stage();
        Scene sceneConnexion = new Scene(rootConnexion); //
        connexionController.setClient(client); //important
        connexionStage.setTitle("Connexion");
        connexionStage.initModality(Modality.APPLICATION_MODAL);
        connexionStage.initOwner(scene.getWindow());//Le model de connexion est celui de la scene principale
        connexionStage.setScene(sceneConnexion);
        sceneConnexion.getStylesheets().add(getClass().getResource("css/common.css").toExternalForm());
        connexionStage.setResizable(false);//empêche le resize de la fenêtre et donc fixe la taille simplement
        connexionStage.showAndWait();
    }

    @Override
    public void stop(){
        System.out.println("stoping...");
    }

    public static void main(String[] args) {
        launch(args);
    }

}

