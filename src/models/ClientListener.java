package models;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.Pair;
import org.bson.Document;
import util.Function4Args;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ClientListener implements Runnable{
    public boolean exitListenThread = false;
    public DataInputStream dIn;

    //set behaviors of the client when the server is sending some message to the client
    public Function4Args<String,String,String, Void> showMessage;
    public Function<String,Void> showError;
    public Function<Boolean,Void> connexionCallback;
    public Function<Pair<String,ObservableList<String>>,Void> userlistCallback;

    public ClientListener(DataInputStream dIn){
        this.dIn = dIn;
    }

    @Override
    public void run(){
        while(!exitListenThread){
            try{
                String line = dIn.readUTF();
                System.out.println("Line Sent back by the server---" + line);
                Document messageRecu = Document.parse(line);

                if(messageRecu.get("Type").equals("MESSAGE")){
                    showMessage.apply(messageRecu.get("Channel",String.class),
                            messageRecu.get("SenderName", String.class),
                            messageRecu.get("Content", String.class));
                }
                if(messageRecu.get("Type").equals("INFO")){
                    if(messageRecu.get("TypeInfo").equals("userList")){
                        List<String> userList = messageRecu.get("userNameList", List.class);
                        ObservableList<String> userListObservable = FXCollections.observableArrayList(userList);
                        String userListChannel = messageRecu.get("userNameListChannel",String.class);
                        Pair<String,ObservableList<String>> userListPair = new Pair<>(userListChannel,userListObservable);
                        userlistCallback.apply(userListPair);
                    }
                    if(messageRecu.get("TypeInfo").equals("confirmAddUser")){
                        Platform.runLater(() ->{
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Utilisateur bien ajouté");
                            alert.setContentText("Utilisateur bien ajouté");
                            alert.showAndWait();
                        });
                    }
                }
                if(messageRecu.get("Type").equals("LOGIN")){
                    if(messageRecu.get("Response").equals("Ok")){
                        connexionCallback.apply(true);
                    }
                    else{
                        connexionCallback.apply(false);
                    }
                }
                if(messageRecu.get("Type").equals("ERROR")){
                    if(messageRecu.get("ErrorType").equals("NotAllowed")){
                        //showError.apply("Vous n'avez pas le droit de faire cela");
                        Platform.runLater(() ->{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Vous n'avez pas le droit de faire cela");
                            alert.setContentText("Vous n'avez pas le droit de faire cela");
                            alert.showAndWait();
                        });
                    }
                }
            } catch (SocketException e){
                System.out.println("Closing listenning thread...");
                this.exitListenThread = true;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
