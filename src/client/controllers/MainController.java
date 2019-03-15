package client.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.sun.deploy.util.FXLoader;
import com.sun.org.glassfish.external.statistics.Stats;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Duration;
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
import server.User;
import util.ISODate;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


public class MainController {
    @FXML
    private TextField textInput;
    @FXML
    private Button envoyerBtn;
    @FXML
    private TabPane tabs;
    @FXML
    private Label time;
    @FXML
    private Label date;
    @FXML
    private Text userNameLabel;

    private int hour;
    private int minute;
    private int second;
    private int year;
    private int month;
    private int day;

    private Client client;
    private Stage statsStage;//Used to keep track of the user data live
    StatsController statsController;

    public MainController(){
        try{
            FXMLLoader loaderStats = new FXMLLoader(getClass().getResource("../views/Stats.fxml"));//
            final Parent rootStats= loaderStats.load();//
            statsController = loaderStats.getController();
            statsStage = new Stage();
            statsStage.setTitle("Statistiques");
            statsStage.setScene(new Scene(rootStats));
            statsController.setClient(client);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    public void initialize(){
        Platform.runLater(() -> {
            client.setShowMessage(this::showText);
            client.joinChannel(tabs.getSelectionModel().getSelectedItem().getText(), TypesChannel.PUBLIC);
        });

        //Horloge
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            second = LocalDateTime.now().getSecond();
            minute = LocalDateTime.now().getMinute();
            hour = LocalDateTime.now().getHour();
            time.setText(hour + ":" + (minute) + ":" + second);
            year = LocalDateTime.now().getYear();
            month = LocalDateTime.now().getMonthValue();
            day = LocalDateTime.now().getDayOfMonth();
            date.setText(year + "-" + (month) + "-" + day);

        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }


    /**
     * shows some text on the textarea a tab
     * @param channel the channel where the text should be added
     * @param sender sender of the message
     * @param content the message
     * @return
     */
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

    /**
     * sends a message to the server
     * @param e
     */
    @FXML
    private void sendMessage(ActionEvent e){
        if(!textInput.getText().isEmpty()) {
            client.sendNormalMessage(textInput.getText(), tabs.getSelectionModel().getSelectedItem().getText());
            textInput.clear();
            if (statsController != null) {
                statsController.incrementNbMessagesEnvoyes();
            }
        }
    }

    /**
     * adds a tab to the window. Fired when clicking the + tab
     */
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


    /**
     * Opens the popup used for the user to add a channel. Fired when clicking the text area for the first time
     * @param event
     */
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


    /**
     * shows the channel parameters upon clicking the params button
     * @param event
     */
    @FXML
    private void showChannelParameters(ActionEvent event){
        try{
            FXMLLoader loaderParameters = new FXMLLoader(getClass().getResource("../views/ChannelParameters.fxml"));//
            final Parent rootParameters = loaderParameters.load();//
            ParametersController parametresController = loaderParameters.getController();
            Stage stage = new Stage();
            parametresController.setClient(client);
            ObservableList<String> opennedTabName = tabs.getTabs().stream()
                                                                .filter(tab -> !tab.getText().equals("+"))
                                                                .map(Tab::getText)
                                                                .collect(Collectors.collectingAndThen(toList(), l -> FXCollections.observableArrayList(l)));
            parametresController.setChannelList(opennedTabName);
            stage.setTitle("Channel Parameters");
            stage.setScene(new Scene(rootParameters));
            stage.show();
            client.updateUserList(tabs.getSelectionModel().getSelectedItem().getText());//Shows the users logged in of the active tab
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Opens the stats window.Fired upon clicking the stats button
     * @param event
     */
    @FXML
    private void showStats(ActionEvent event){
        statsStage.show();
    }


    /**
     * When the user closes the main window
     * @param e
     */
    @FXML
    public void shutdown(WindowEvent e){
        //cleanup what's needed
        client.shutdown();
        Platform.exit();
        System.out.println("exiting...");
    }

    /**
     * sets the client context
     * @param client
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * set the user label (top left)
     * @param username
     */
    public void setUserNameLabel(String username){
        this.userNameLabel.setText("Utilisateur : " + username);
    }
}
