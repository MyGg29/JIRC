package client.controllers;

import client.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Client;
import sun.plugin.JavaRunTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class ConnexionController {
    @FXML
    Button connexionBtn;
    @FXML
    TextField identifiant;
    @FXML
    TextField password;
    @FXML
    private Client client;

    public boolean validationConnexion = false;

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
            validationConnexion = true;
            
            //Permettre la visualisation de la fenêtre de discussion


        }


        if(!identifiant.getCharacters().toString().equals("") && !password.getCharacters().toString().equals(""))
        {
            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy' à 'HH:mm:ss");
            String s = f.format(date);
            System.out.println("--------------------- \n Connexion le "+s);

        }

    }

}
