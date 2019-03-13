package client.controllers;

import com.sun.deploy.util.FXLoader;
import com.sun.org.glassfish.external.statistics.Stats;
import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;
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
import util.ISODate;

import javax.swing.JTextField;

import java.io.IOException;

import static javafx.geometry.Pos.CENTER;


public class MainController {
    @FXML
    private TextField textInput;
    @FXML
    private Button send;
    @FXML
    private TabPane tabs;
    private Client client;

    private Stage statsStage;

    StatsController statsController;



    public MainController(){
        try{
            FXMLLoader loaderStats = new FXMLLoader(getClass().getResource("../views/Stats.fxml"));//
            final Parent rootStats= loaderStats.load();//
            statsController = loaderStats.getController();
            statsStage = new Stage();
            statsStage.setTitle("Statistiques");
            statsStage.setScene(new Scene(rootStats));
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public void initialize(){
        Platform.runLater(() -> {
            client.setShowMessage(this::showText);
            client.joinChannel(tabs.getSelectionModel().getSelectedItem().getText(), TypesChannel.PUBLIC);
        });

    }

    /* ---------- Méthode d'écriture du texte du message envoyé dans le channel ---------- */
    //Utilisé par le client qui écoute

    public Void showText(String channel, String sender, String content){
        //On append pour le tab ouvert mais aussi toutes les tabs ouverts
        //On peut avoir plusieurs tabs liés au même channel ouverts en même temps
        FilteredList<Tab> similarTabs = tabs.getTabs().filtered(e -> e.getText().equals(channel));
        ISODate now = new ISODate();
        //show the message in every tabs. Asking the platform runLater helps when there are many calls (when writting the history for exemple)
        Platform.runLater(()->
                similarTabs.forEach(tab ->
                        ((TextArea)tab.getContent()).appendText(now + " / " + sender + " : " + content + "\n")
                )
        );
        return null;
    }

    @FXML
    private void sendMessage(ActionEvent e){
        client.sendMessage(textInput.getText(), tabs.getSelectionModel().getSelectedItem().getText());
        textInput.clear();
        if(statsController!=null){
            statsController.incrementNbMessagesEnvoyes();
        }

    }



    /* ---------- Création d'un nouvel onglet ---------- */
    @FXML
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



    /* ---------- Ouverture de la fenêtre d'accès à un channel ---------- */
    @FXML
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
            e.printStackTrace();
        }
    }


    /* ---------- Ouverture de la fenêtre de paramètres ---------- */
    @FXML
    private void showChannelParameters(ActionEvent event){
        try{
            FXMLLoader loaderParameters = new FXMLLoader(getClass().getResource("../views/ChannelParameters.fxml"));//
            final Parent rootParameters = loaderParameters.load();//
            ParametersController parametresController = loaderParameters.getController();
            Stage stage = new Stage();
            parametresController.setClient(client);
            stage.setTitle("Channel Parameters");
            stage.setScene(new Scene(rootParameters));
            stage.show();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /* ---------- Ouverture de la fenêtre de stats ---------- */
    @FXML
    private void showStats(ActionEvent event){
        statsStage.show();
    }


    /* ---------- Fermeture de la fenêtre ---------- */
    @FXML
    public void shutdown(WindowEvent e){
        //cleanup what's needed
        client.shutdown();
        Platform.exit();
        System.out.println("exiting...");
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
