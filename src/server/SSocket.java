package server;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import models.TypesChannel;
import org.bson.Document;
import org.json.XML;
import util.MessagesFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;


//This is the main socket of the server, each user has one instance of this class running on the server
public class SSocket implements Runnable {
    private Socket socket;
    private User userData;
    MongoDatabase database;

    public SSocket(Socket socket, MongoDatabase jIrcDatabase) throws IOException {
        this.socket = socket;
        userData =  new User();
        userData.setOutputStream(new DataOutputStream(this.socket.getOutputStream()));
        userData.setInputStream(new DataInputStream(this.socket.getInputStream()));
        userData.setSocketAddress(this.socket.getRemoteSocketAddress());
        userData.setName("Anonyme");
        this.database = jIrcDatabase;
    }

    @Override
    public void run() {
        try{
            String line;
            while (true) {

                /* ---------- Réception des données en UTF-16 ---------- */
                line = userData.getInputStream().readUTF();
                System.out.println("Received from " + userData.getSocketAddress() + " : " + line);
                //parsing of the JSON
                Document messageRecu = Document.parse(line);


                /* ---------- Rejoindre un channel ---------- */
                if(messageRecu.get("Type").equals("JOIN")){
                    this.handleJoinChannel(messageRecu);
                }

                /* ---------- Affichage du message reçu dans le channel concerné ---------- */
                if(messageRecu.get("Type").equals("MESSAGE")){
                    this.handleNormalMessage(messageRecu);
                }

                /* ---------- Information sur le channel ---------- */
                if(messageRecu.get("Type").equals("INFO")){
                    this.handleInfo(messageRecu);
                }

                /* ---------- Paramètres ---------- */
                if(messageRecu.get("Type").equals("PARAMS")){
                    this.handleParams(messageRecu);
                }

                /* ---------- Connexion ---------- */
                if(messageRecu.get("Type").equals("LOGIN")){
                    this.handleLogin(messageRecu);
                }
                System.out.println("waiting for the next line....");

            }
        }
        catch (IOException e) {
            userData.getChannels().forEach(channel -> channel.getUserList().remove(userData));
            e.printStackTrace();
            System.out.println("Deleting connection...");
        }
    }

    private void handleLogin(Document messageRecu) {
        try{
            String name = messageRecu.get("Name",String.class);
            String password = messageRecu.get("Password", String.class);

            Document storedUser = database.getCollection("users").find(eq("Name",name)).first();
            if(storedUser == null){
                //user dont exist yet, we insert it in the database
                Document d = new Document();
                d.put("Name", name);
                d.put("Password", password);
                database.getCollection("users").insertOne(d);
                userData.setName(name);
                userData.anwserConnectionAttempt(true);
            }
            else{
                //user exists, we check if the password sent by the client correspond to what's in the database
                if(storedUser.get("Password",String.class).equals(password)){
                    userData.setName(name);
                    userData.anwserConnectionAttempt(true);
                }
                else{
                    userData.anwserConnectionAttempt(false);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleParams(Document messageRecu) throws IOException {
        if(messageRecu.get("TypeParams").equals("WhitelistUser")){
            String userName = messageRecu.get("UserName", String.class);
            String channel = messageRecu.get("Channel", String.class);
            User userToAdd = new User();
            userToAdd.setName(userName);
            if(Channel.everyChannels.get(channel).isAllowed(userData)){
                Channel.everyChannels.get(channel).addUserToWhiteList(userToAdd);
                userData.send(MessagesFactory.confirmAddUser().toJson());
            }
            else{
                userData.send(MessagesFactory.errorMessage().toJson());
            }
            database.getCollection("params").insertOne(messageRecu);
        }
        if(messageRecu.get("TypeParams").equals("ExtractJson")){
            String channel = messageRecu.get("Channel",String.class);
            FindIterable<Document> e = database.getCollection("messages").find(eq("Channel",channel));
            final StringBuilder json = new StringBuilder();
            e.forEach((Consumer<? super Document>) x-> json.append(x.toJson()));
            Document d = MessagesFactory.extractJsonReponse(json.toString());
            userData.send(d.toJson());
        }

    }

    private void handleInfo(Document messageRecu) {
        try{
            if(messageRecu.get("TypeInfo").equals("getUserList")){
                String channel = messageRecu.get("Channel",String.class);
                List<String> userNameList = Channel.everyChannels.get(channel).getUserList().stream().map(User::getName).collect(Collectors.toList());
                Document d = MessagesFactory.userList(userNameList,channel);
                userData.send(d.toJson());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleJoinChannel(Document messageRecu){
        try{
            String nomChannel = messageRecu.get("Channel",String.class);
            if(Channel.everyChannels.containsKey(nomChannel)){
                if(Channel.everyChannels.get(nomChannel).isAllowed(userData)){
                    Channel.everyChannels.get(nomChannel).getUserList().add(userData);
                    userData.getChannels().add(Channel.everyChannels.get(nomChannel));
                    sendHistory(userData,nomChannel);
                }
                else{
                    Document d = MessagesFactory.normalMessage(nomChannel,"Tu n'as pas le droit d'entrer dans ce channel","", "System");
                    userData.send(d.toJson());//Not allowed to join
                }
            }
            else{
                Channel mainChannel = new Channel();
                mainChannel.name = nomChannel;
                mainChannel.type = TypesChannel.valueOf(messageRecu.get("TypeChannel", String.class));
                if(mainChannel.type == TypesChannel.PRIVATE){
                    mainChannel.addUserToWhiteList(userData);
                }
                Channel.everyChannels.put(nomChannel, mainChannel);
                Channel.everyChannels.get(nomChannel).getUserList().add(userData);
                userData.getChannels().add(Channel.everyChannels.get(nomChannel));
                sendHistory(userData,nomChannel);
            }
            database.getCollection("join").insertOne(messageRecu);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void handleNormalMessage(Document messageRecu){
        Channel channel = Channel.everyChannels.get(messageRecu.get("Channel",String.class));
        if(channel != null) {//the channel exits
            Document messageAPartager = MessagesFactory.normalMessage(messageRecu,userData.getSocketAddress().toString(),userData.getName());
            if(channel.isAllowed(userData)){
                channel.sendToChannel(messageAPartager.toJson());
            }
            database.getCollection("messages").insertOne(messageAPartager);
        }
    }

    //Sends the channel history from the database to the user
    private void sendHistory(User user, String nomChannel) throws IOException{
        //send the message history to this user
        FindIterable<Document> messageHistory = database.getCollection("messages").find(eq("Channel", nomChannel));
        for (Document message : messageHistory) {
            userData.send(message.toJson());
        }
    }
}
