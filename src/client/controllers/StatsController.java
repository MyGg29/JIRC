package client.controllers;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import models.Client;
import models.TypesChannel;

public class StatsController {

    @FXML
    private TextField nom;
    @FXML
    private TextField prenom;
    @FXML
    private TextField age;
    @FXML
    private TextField pseudo;
    @FXML
    private TextField password;
    @FXML
    private TextField dateInscription;
    @FXML
    private TextField nbMessagesEnvoyes;
    @FXML
    private Button desinscriptionBtn;

    int nbMessageSend = 0;

    Database db;

    public StatsController(){

    }
    public void initialize(){
    }

    public void incrementNbMessagesEnvoyes(){
        nbMessageSend++;
        nbMessagesEnvoyes.setText(Integer.toString(nbMessageSend));
    }

    public void refreshInformations(){
        //TODO : récup dans la base
        nom.setText("nom");
        prenom.setText("prenom");
        age.setText("age");
        pseudo.setText("pseudo");
        password.setText("password");

    }

    @FXML
    public void clickDesinscription(ActionEvent e)
    {
        if(!nom.getText().isEmpty() && !prenom.getText().isEmpty() && !pseudo.getText().isEmpty()
                && !password.getText().isEmpty() && !age.getText().isEmpty()){

            //suppression de l'utilisateur de la base de données
            db.supprimerUtilisateurBDD(nom.getText(), prenom.getText(), pseudo.getText(),
                    password.getText(), age.getText());
        }
    }



}

