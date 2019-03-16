package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import models.Client;
import util.ISODate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StatsController {

    @FXML
    private TextField nom;
    @FXML
    private TextField adresseIP;
    @FXML
    private TextField portClient;
    @FXML
    private TextField portServeur;
    @FXML
    private TextField nbMessagesEnvoyes;
    @FXML
    private TextField derniereConnexion;
    @FXML
    private LineChart historiqueMessageLineChart;
    @FXML
    private LineChart historiqueNbUtilisateurConnecte;

    private XYChart.Series<String,Number> serieMessageEnvoye;
    private XYChart.Series<String,Number> serieUtilisateurConnecte;
    private int nbMessageSend = 0;
    private int nbUtilisateurConnecte = 0;
    private Client client;

    public StatsController(){

    }

    public void initialize(){
        derniereConnexion.setText(new ISODate().toString());
        serieMessageEnvoye = new XYChart.Series<>();
        serieMessageEnvoye.setName("Nombre total de message envoyé");
        historiqueMessageLineChart.getData().addAll(serieMessageEnvoye);
        serieUtilisateurConnecte = new XYChart.Series<>();
        serieUtilisateurConnecte.setName("Nombre d'utilisateur connecté en même temps");
        historiqueNbUtilisateurConnecte.getData().addAll(serieUtilisateurConnecte);
    }

    public void incrementNbMessagesEnvoyes(){
        nbMessageSend++;
        nbMessagesEnvoyes.setText(Integer.toString(nbMessageSend));

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        serieMessageEnvoye.getData().add(new XYChart.Data(dateFormat.format(date),nbMessageSend));
    }

    public void refreshInformations(){
        nom.setText(client.getName());
        adresseIP.setText(client.getClientIp().toString());
        portClient.setText(Integer.toString(client.getClientPort()));
        portServeur.setText(Integer.toString(client.getServerPort()));
    }

    public void setNbUtilisateurConnecte(int nbUtilisateurConnecte){
        this.nbUtilisateurConnecte = nbUtilisateurConnecte;

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        serieMessageEnvoye.getData().add(new XYChart.Data(dateFormat.format(date),this.nbUtilisateurConnecte));
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

