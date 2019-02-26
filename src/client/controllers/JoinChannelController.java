package client.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.TypesChannel;

import java.util.Map;

public class JoinChannelController {

    @FXML
    private ChoiceBox channelType;
    @FXML
    private TextField channelName;
    @FXML
    private Button validBtn;
    @FXML
    private Button cancelBtn;

    public JoinChannelController(){
    }

    public void initialize(){
        channelType.setItems(FXCollections.observableArrayList(TypesChannel.values()));
        channelType.getSelectionModel().select(TypesChannel.PUBLIC);//valeur par defaut
    }

    @FXML
    public void validDialog(ActionEvent event){
        Stage stage = (Stage)validBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelDialog(ActionEvent event){
        Stage stage = (Stage)cancelBtn.getScene().getWindow();
        stage.close();
    }

    public boolean isPresent(){
        boolean result;
        result = getChannelName() != null
                && !getChannelName().trim().isEmpty()
                && getChannelType() != null;
        return result;
    }

    public TypesChannel getChannelType(){
        TypesChannel channel = (TypesChannel)channelType.getSelectionModel().getSelectedItem();
        return channel;
    }
    public String getChannelName(){
        return channelName.getText();
    }

}
