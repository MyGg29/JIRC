package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;


public class Controller {
    @FXML
    private TextField textInput;
    @FXML
    private TextArea chatBox;
    @FXML
    private Button send;
    @FXML
    private TabPane tabs;

    private Client client;

    public Controller(){
        client = new Client();
    }

    @FXML
    private void sendText(ActionEvent e){
        client.sendText(textInput.getText());
        chatBox.appendText(textInput.getText());
        textInput.clear();
    }

    @FXML
    private void addTab(){
        System.out.println("Adding a tab...");
        Tab newTab = new Tab("Tab " + (tabs.getTabs().size() + 1));
        tabs.getTabs().add(tabs.getTabs().size() - 1, newTab);
        //tabs.getTabs().add(newTab);
        tabs.getSelectionModel().select(newTab);
        System.out.println("Tab added");
    }

    @FXML
    private void createStage(MouseEvent event){
        final TextInputDialog textDialog = new TextInputDialog();
        textDialog.setContentText("Entrer l'invitation");
        textDialog.setTitle("Rejoindre un channel");
        textDialog.setHeaderText(null);//on ne veux pas afficher le header n'y d'image pour pas surcharger le truc
        textDialog.setGraphic(null);
        Optional<String> invitation =  textDialog.showAndWait();
        if(invitation.isPresent()){
            tabs.getSelectionModel().getSelectedItem().setText(invitation.get());
        }
        chatBox.setOnMouseClicked(null);//on veux afficher cette dialog qu'une fois
    }

}
