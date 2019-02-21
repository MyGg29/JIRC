package client;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import org.bson.Document;

import java.util.Date;
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
        client.setShowMessage(this::showText);
        client.joinChannel("Main");
    }

    //method to tell the client how we'll write in the textbox, used by the client who's listenning.
    public Void showText(String channel, String sender, String content){
        //On append pour le tab ouverte mais aussi toutes les tabs ouvertes
        //      -> On peux avoir plusieurs tabs lié au même chan ouvertes en même temps
        FilteredList<Tab> similarTabs = tabs.getTabs().filtered(e -> e.getText().equals(channel));
        similarTabs.forEach(tab ->
                ((TextArea)tab.getContent()).appendText(new Date().toLocaleString() + " / " + sender + " : " + content + "\n")
        );
        return null;
    }
    @FXML
    private void sendText(ActionEvent e){
        client.sendMessage(textInput.getText(), tabs.getSelectionModel().getSelectedItem().getText());
        textInput.clear();
    }

    @FXML
    private void addTab(){
        try{
            System.out.println("Adding a tab...");
            Tab newTab = new Tab("Tab " + (tabs.getTabs().size() + 1));
            TextArea content = new TextArea();
            content.setOnMouseClicked(this::showJoinChannel);
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
    private void showJoinChannel(MouseEvent event){
        final TextInputDialog textDialog = new TextInputDialog();
        textDialog.setContentText("Entrer l'invitation");
        textDialog.setTitle("Rejoindre un channel");
        textDialog.setHeaderText(null);//on ne veux pas afficher le header n'y d'image pour pas surcharger le truc
        textDialog.setGraphic(null);
        Optional<String> invitation =  textDialog.showAndWait();
        if(invitation.isPresent()){
            tabs.getSelectionModel().getSelectedItem().setText(invitation.get());//on change le nom du tab actuel
            tabs.getSelectionModel().getSelectedItem().getContent().setOnMouseClicked(null);//on veux pouvoir faire ca qu'une fois
            client.joinChannel(invitation.get());
        }
    }

    @FXML
    public void shutdown(WindowEvent e){
        //cleanup what's needed
        Platform.exit();
        System.out.println("exiting...");
    }
}
