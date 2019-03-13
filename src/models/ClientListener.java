package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;
import util.Function4Args;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.function.Function;

public class ClientListener implements Runnable{
    public boolean exitListenThread = false;
    public DataInputStream dIn;
    public Function4Args<String,String,String, Void> showMessage;
    public Function<Boolean,Void> connexionCallback;
    public Function<ObservableList<String>,Void> userlistCallback;

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
                        ObservableList<String> observableUsername = FXCollections.observableArrayList(userList);
                        userlistCallback.apply(observableUsername);
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
            } catch (SocketException e){
                System.out.println("Closing listenning thread...");
                this.exitListenThread = true;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
