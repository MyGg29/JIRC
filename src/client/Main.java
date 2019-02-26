package client;

import client.controllers.MainController;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/Client.fxml"));
        final Parent root = loader.load();
        primaryStage.setTitle("JMessenger");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/common.css").toExternalForm());
        primaryStage.setScene(scene);

        MainController controller = loader.getController();
        primaryStage.setOnCloseRequest(controller::shutdown);
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.out.println("stoping...");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

