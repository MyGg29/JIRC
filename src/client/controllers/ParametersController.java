package client.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import models.Client;
import models.TypesChannel;

public class ParametersController {
    private Client client;
    @FXML
    private ChoiceBox listTypeChannel;
    @FXML
    private TextField inviterChannel;
    @FXML
    private TextField inviterUser;

    public ParametersController(){

    }
    public void initialize(){
        listTypeChannel.setItems(FXCollections.observableArrayList(TypesChannel.values()));

    }

    @FXML
    public void inviterBtn(ActionEvent e){
        String name = inviterUser.getText();
        String channel = inviterChannel.getText();
        client.addToWhitelist(name,channel);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
