package client.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import models.Client;

public class ParametersController {
    private Client client;
    @FXML
    private ChoiceBox inviterChannel;
    @FXML
    private TextField inviterUser;
    @FXML
    private ListView userList;
    private ObservableList<String> channelList;
    private ObservableList<String> usernameList;

    public ParametersController(){

    }
    public void initialize(){
        Platform.runLater(()->inviterChannel.setItems(channelList));
        Platform.runLater(()->userList.setItems(usernameList));
    }

    @FXML
    public void inviterBtn(ActionEvent e){
        String name = inviterUser.getText();
        String channel = (String)inviterChannel.getSelectionModel().getSelectedItem();
        client.addToWhitelist(name,channel);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        client.setUserlistCallback(this::setUsernameList);
    }

    public void setChannelList(ObservableList<String> channelList) {
        this.channelList = channelList;
    }

    public Void setUsernameList(ObservableList<String> usernameList) {
        this.usernameList = usernameList;
        return null;
    }
}
