package client.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Pair;
import models.Client;

public class ParametersController {
    private Client client;
    @FXML
    private ChoiceBox inviterChannel;
    @FXML
    private TextField inviterUser;
    @FXML
    private ListView userList;
    @FXML
    private Text labelUserList;
    private ObservableList<String> channelList;
    private Pair<String,ObservableList<String>> userListPair;

    public ParametersController(){

    }
    public void initialize(){
        Platform.runLater(()->inviterChannel.setItems(channelList));
        Platform.runLater(()->userList.setItems(userListPair.getValue()));
        Platform.runLater(()->labelUserList.setText(userListPair.getKey()));
    }

    /**
     * sends the invitation request to the server. Fires when clicking to the invite button
     * @param e
     */
    @FXML
    public void inviterBtn(ActionEvent e){
        String name = inviterUser.getText();
        String channel = (String)inviterChannel.getSelectionModel().getSelectedItem();
        client.addToWhitelist(name,channel);
    }

    /**
     * sets the client context
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
        client.setUserlistCallback(this::setUsernameList);
    }

    /**
     * set the list of channels dropdown in the "invite user" tab
     * @param channelList
     */
    public void setChannelList(ObservableList<String> channelList) {
        this.channelList = channelList;
    }

    /**
     * set the listview of the  user logged in the active channel tab
     * @param usernameList
     * @return
     */
    public Void setUsernameList(Pair<String,ObservableList<String>> usernameList) {
        this.userListPair = usernameList;
        return null;
    }
}
