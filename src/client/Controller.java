package client;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import org.bson.Document;

import java.io.IOException;
import java.util.Optional;


public class Controller {
    @FXML
    private TextField textInput;
    @FXML
    private Button send;
    @FXML
    private TabPane tabs;

    private Client client;

    public Controller(){
        client = new Client();
    }
    public void initialize(){
        client.setShowMessage(this::appendChatBox);
    }
    //method to tell the client how we'll write in the textbox
    public Void appendChatBox(String s){
        String nameOfTheOpenedTab = tabs.getSelectionModel().getSelectedItem().getText();
        //On append pour le tab ouverte mais aussi toutes les tabs ouvertes
        //      -> On peux avoir plusieurs tabs lié au même chan ouvertes en même temps
        FilteredList<Tab> similarTabs = tabs.getTabs().filtered((e) -> e.getText().equals(nameOfTheOpenedTab));
        similarTabs.forEach(tab ->
                ((TextArea)tab.getContent()).appendText(s + "\n")
        );
        return null;
    }
    @FXML
    private void sendText(ActionEvent e){
        Document message = new Document();
        message.put("Message", textInput.getText());
        message.put("Channel", tabs.getSelectionModel().getSelectedItem().getText());
        client.sendText(message.toJson());
        textInput.clear();
    }

    @FXML
    private void addTab(){
        try{
            System.out.println("Adding a tab...");
            Tab newTab = new Tab("Tab " + (tabs.getTabs().size() + 1));
            TextArea content = new TextArea();
            content.setOnMouseClicked(this::createStage);
            content.setEditable(false);
            newTab.setContent(content);
            tabs.getTabs().add(tabs.getTabs().size() - 1, newTab);
            tabs.getSelectionModel().select(newTab);
            System.out.println("Tab added");
        }
        catch (Exception e){
            e.printStackTrace();
        }
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
            tabs.getSelectionModel().getSelectedItem().getContent().setOnMouseClicked(null);//on veux pouvoir faire ca qu'une fois
            Document d = new Document();
            d.put("Join", invitation.get());
            client.sendText(d.toJson());
        }
    }

    @FXML
    public void shutdown(WindowEvent e){
        //cleanup what's needed
        Platform.exit();
        System.out.println("exiting...");
    }
}
