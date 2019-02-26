package client.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import models.TypesChannel;

public class ParametresController {
    @FXML
    private ChoiceBox listTypeChannel;

    public ParametresController(){

    }
    public void initialize(){
        listTypeChannel.setItems(FXCollections.observableArrayList(TypesChannel.values()));

    }
}
