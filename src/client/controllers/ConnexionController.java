package client.controllers;

import client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import sun.plugin.JavaRunTime;


public class ConnexionController {
    @FXML
    Button connexionBtn;
    @FXML
    TextField identifiant;
    @FXML
    TextField password;

    public ConnexionController(){

    }
    public void initialize(){
    }

    @FXML
    public void clickConnexion(ActionEvent e)
    {
        //Test de connexion avec des pass en dur
        if(identifiant.getCharacters().toString().equals("bob") && password.getCharacters().toString().equals("bob"))
        {
            System.out.println("CONNEXION REUSSIE");
            //Remplacer les conditions par une vérification des informations dans la collection users de la BDD

            //Permettre la visualisation de la fenêtre de discussion
        }

    }


}
