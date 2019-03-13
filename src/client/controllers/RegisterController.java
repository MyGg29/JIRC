package client.controllers;

import com.sun.org.glassfish.external.statistics.Stats;
import database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import models.Client;

public class RegisterController {


    @FXML
    private Button validerInscriptionBtn;
    @FXML
    private TextField inputNom;
    @FXML
    private TextField inputPrenom;
    @FXML
    private TextField inputPseudo;
    @FXML
    private TextField inputPassword;
    @FXML
    private TextField inputAge;

    Client client;

    StatsController statsController;

    Database db;


    public RegisterController(){

    }
    public void initialize(){
    }


    @FXML
    public void clickValiderInscription(ActionEvent e)
    {
        if(!inputNom.getText().isEmpty() && !inputPrenom.getText().isEmpty() && !inputPseudo.getText().isEmpty()
            && !inputPassword.getText().isEmpty() && !inputAge.getText().isEmpty()){

            //ajout à la base de données de l'utilisateur
            db.ajouterUtilisateurBDD(inputNom.getText(), inputPrenom.getText(), inputPseudo.getText(),
                    inputPassword.getText(), inputAge.getText());
        }
        if(statsController!=null){
            statsController.refreshInformations();
        }


    }


    public void setClient(Client client) {
        this.client = client;
    }

}
