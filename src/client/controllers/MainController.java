package client.controllers;

import com.sun.deploy.util.FXLoader;
import com.sun.org.glassfish.external.statistics.Stats;
import javafx.scene.Node;
import javafx.stage.Modality;
import models.Client;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.TypesChannel;

import javax.swing.JTextField;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static javafx.geometry.Pos.CENTER;


public class MainController {
    @FXML
    private TextField textInput;
    @FXML
    private Button send;
    @FXML
    private TabPane tabs;
    @FXML
    private Client client;
    @FXML
    public TextField nbMessagesEnvoyes;


    int nbMessageSend = 0;


    public MainController(){
        client = new Client();
    }
    public void initialize(){
        client.setShowMessage(this::showText);
        client.joinChannel(tabs.getSelectionModel().getSelectedItem().getText(), TypesChannel.PUBLIC);
    }


    /* ---------- Méthode d'écriture du texte du message envoyé dans le channel ---------- */
    //Utilisé par le client qui écoute

    public Void showText(String channel, String sender, String content){
        //On append pour le tab ouvert mais aussi toutes les tabs ouverts
        //On peut avoir plusieurs tabs liés au même channel ouverts en même temps
        FilteredList<Tab> similarTabs = tabs.getTabs().filtered(e -> e.getText().equals(channel));
        similarTabs.forEach(tab ->
                ((TextArea)tab.getContent()).appendText(new Date().toLocaleString() + " / " + sender + " : " + content + "\n")
        );
        return null;
    }

    @FXML

    /* ---------- Envoi du contenu écrit dans le champ message  ---------- */

    private void sendText(ActionEvent e){
        client.sendMessage(textInput.getText(), tabs.getSelectionModel().getSelectedItem().getText());
        textInput.clear();

}

    @FXML

    /* ---------- Création d'un nouvel onglet ---------- */

    private void addTab(){
        try{
            System.out.println("Adding a tab...");
            Tab newTab = new Tab("Tab " + (tabs.getTabs().size() + 1));
            TextArea content = new TextArea();
            content.setOnMouseClicked(this::showJoinChannel);
            content.setEditable(false);
            content.setPromptText("Click to add a channel");
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

    /* ---------- Ouverture de la fenêtre d'accès à un channel ---------- */

    private void showJoinChannel (MouseEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../views/JoinChannel.fxml"));
            Parent root = loader.load();
            JoinChannelController joinChannelModalController = loader.getController();
            Stage joinChannelStage = new Stage();
            joinChannelStage.setTitle("Rejoindre un channel");
            joinChannelStage.initModality(Modality.APPLICATION_MODAL);
            joinChannelStage.setScene(new Scene(root));
            joinChannelStage.initOwner(
                    ((Node)event.getSource()).getScene().getWindow()
            );
            joinChannelStage.showAndWait();

            if(joinChannelModalController.isPresent()){
                String channelNameEntree = joinChannelModalController.getChannelName();
                TypesChannel channelTypeEntree = joinChannelModalController.getChannelType();
                tabs.getSelectionModel().getSelectedItem().setText(channelNameEntree);//on change le nom du tab actuel
                tabs.getSelectionModel().getSelectedItem().getContent().setOnMouseClicked(null);//on veut pouvoir faire ca qu'une fois
                client.joinChannel(channelNameEntree, channelTypeEntree);
            }
        }
        catch(IOException e){

        }
        return;
        /*
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
        }*/
    }

    @FXML

    /* ---------- Ouverture de la fenêtre de paramètres ---------- */

    private void showChannelParameters(ActionEvent event){
        try{
            client.addToWhitelist("/127.0.0.1:" + 334090, "Accueil");
            Parent root = FXMLLoader.load(getClass().getResource("../views/ChannelParameters.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Channel Parameters");
            stage.setScene(new Scene(root));
            stage.show();
            client.getChannelInfo("Main");
        }
        catch(Exception e){}
    }

    @FXML

    /* ---------- Ouverture de la fenêtre de stats ---------- */

    private void showStats(ActionEvent event){
        try{
            Parent rootStats = FXMLLoader.load(getClass().getResource("../views/Stats.fxml"));
            Stage statsStage = new Stage();
            statsStage.setTitle("Statistiques");
            statsStage.setScene(new Scene(rootStats));
            statsStage.show();
        }
        catch(Exception e){}
    }

    @FXML

    /* ---------- Fermeture de la fenêtre ---------- */

    public void shutdown(WindowEvent e){
        //cleanup what's needed
        client.shutdown();
        Platform.exit();
        System.out.println("exiting...");
    }

    
}
