package client.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class ParametresController {
    @FXML
    private ChoiceBox listTypeChannel;
    public void initialize(){
        listTypeChannel.setItems(FXCollections.observableArrayList(models.TypeChannel.values()));
    }
}
