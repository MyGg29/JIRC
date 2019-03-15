package client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Client;

import java.io.IOException;


public class ConnexionController {
    @FXML
    Button connexionBtn;
    @FXML
    Button inscriptionBtn;
    @FXML
    TextField identifiant;
    @FXML
    TextField password;

    Stage registerStage;
    RegisterController registerController;

    Client client;

    public ConnexionController(){
        try{
            FXMLLoader loaderRegister = new FXMLLoader(getClass().getResource("../views/Register.fxml"));//
            final Parent rootRegister= loaderRegister.load();//
            registerController = loaderRegister.getController();
            registerStage = new Stage();
            registerStage.setTitle("Inscription");
            registerStage.setScene(new Scene(rootRegister));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void initialize(){
    }

    @FXML
    public void clickConnexion(ActionEvent e)
    {
        client.setConnexionCallback(this::handleAfterConnexion);
        client.connect(identifiant.getText(),password.getText());
    }

    @FXML
    private void showRegister(ActionEvent event){
        registerStage.show();
    }

    /**
     * Function handling what happens after Client.cs receive a response for the connexion attemps
     * @param connected is the connection attempt successful ?
     */
    private Void handleAfterConnexion(boolean connected){
        if(connected){
            Stage connexionStage = (Stage)identifiant.getScene().getWindow();
            Platform.runLater(connexionStage::close);
            client.setName(identifiant.getText());
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

    /**
     * Set the client "context" of the window. Which client will use this window basically
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
    }
}
