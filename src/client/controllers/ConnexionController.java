package client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Client;


public class ConnexionController {
    @FXML
    Button connexionBtn;
    @FXML
    TextField identifiant;
    @FXML
    TextField password;

    Client client;
    public ConnexionController(){

    }
    public void initialize(){
    }

    @FXML
    public void clickConnexion(ActionEvent e)
    {
        client.setConnexionCallback(this::handleAfterConnexion);
        client.connect(identifiant.getText(),password.getText());
    }

    /**
     * Function handling what happens after Client.cs receive a response for the connexion attemps
     * @param connected is the connection attempt successful ?
     */
    private Void handleAfterConnexion(boolean connected){
        if(connected){
            Stage connexionStage = (Stage)identifiant.getScene().getWindow();
            Platform.runLater(connexionStage::close);
        }
        else{
            Platform.runLater(() ->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error connexion");
                alert.showAndWait();
            });
        }
        return null;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
