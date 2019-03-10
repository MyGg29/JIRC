package server;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.TypesChannel;
import models.TypesMessage;
import org.bson.Document;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server {
    public static void main(String[] args) {
        int port = 666; //random port number
        MongoClient mongo = new MongoClient("localhost",27017);
        MongoDatabase jIrcDatabase = mongo.getDatabase("JIRC");

        try {
            ServerSocket ss = new ServerSocket(port); //Le serveur ecoute quel port
            System.out.println("Ecoute sur le port " + port);

            while(true) {
                Socket socket = ss.accept();//La connexion est faite entre le client (port random) et le serveur (port 666)

                //On ajoute le client dans la liste des clients connectés.
                //On crée un thread par client
                SSocket sSocket = new SSocket(socket, jIrcDatabase);
                Thread t = new Thread(sSocket);
                t.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/* ---------------------------------------- Class Socket ---------------------------------------- */


class SSocket implements Runnable {
    private Socket socket;
    private User userData = new User();
    MongoDatabase database;

    public SSocket(Socket socket, MongoDatabase jIrcDatabase) throws IOException {
        this.socket = socket;
        userData.setOutputStream(new DataOutputStream(this.socket.getOutputStream()));
        userData.setInputStream(new DataInputStream(this.socket.getInputStream()));
        userData.setSocketAddress(this.socket.getRemoteSocketAddress());
        userData.setName("DefaultName (temporary)");
        this.database = jIrcDatabase;
    }

    @Override
    public void run() {
        try {
            String line;
            while (true) {

                /* ---------- Réception des données en UTF-16 ---------- */
                line = userData.getInputStream().readUTF();
                System.out.println("Emetteur : " + userData.getSocketAddress() + " : " + line);
                //parsing of the JSON
                Document messageRecu = Document.parse(line);
                messageRecu.put("Sender", userData.getSocketAddress().toString());
                messageRecu.put("SenderName", userData.getName());


                /* ---------- Rejoindre un channel ---------- */
                if(messageRecu.get("Type").equals("JOIN")){

                    String nomChannel = messageRecu.get("Channel",String.class);
                    if(Channel.everyChannels.containsKey(nomChannel)){
                        if(Channel.everyChannels.get(nomChannel).isAllowedToJoin(userData)){
                            Channel.everyChannels.get(nomChannel).getUserList().add(userData);
                            userData.getChannels().add(Channel.everyChannels.get(nomChannel));
                        }
                    }
                    else{
                        Channel mainChannel = new Channel();
                        mainChannel.name = nomChannel;
                        mainChannel.type = TypesChannel.valueOf(messageRecu.get("TypeChannel", String.class));
                        Channel.everyChannels.put(nomChannel, mainChannel);
                        Channel.everyChannels.get(nomChannel).getUserList().add(userData);
                        userData.getChannels().add(Channel.everyChannels.get(nomChannel));
                    }
                    database.getCollection("join").insertOne(messageRecu);
                }


                /* ---------- Affichage du message reçu dans le channel concerné ---------- */
                if(messageRecu.get("Type").equals("MESSAGE")){

                    Channel channel = Channel.everyChannels.get(messageRecu.get("Channel",String.class));
                    channel.sendToChannel(messageRecu.toJson());
                    database.getCollection("messages").insertOne(messageRecu);
                }

                /* ---------- Information sur le channel ---------- */
                if(messageRecu.get("Type").equals("INFO")){
                    String channelRequested = messageRecu.get("Channel",String.class);
                    Channel channel = Channel.everyChannels.get(channelRequested);
                    TypesChannel e = channel.type;
                    Document d = new Document();
                    d.put("Type", "INFO");
                    d.put("TypeChannel", e.toString());
                    userData.send(d.toJson());
                }

                /* ---------- Paramètres ---------- */
                if(messageRecu.get("Type").equals("PARAMS")){
                    String userIP = messageRecu.get("AddUser", String.class);
                    String channel = messageRecu.get("Channel", String.class);
                    User user = new User();
                    user.setName(userIP);
                    Channel.everyChannels.get(channel).addUserToWhiteList(user);
                    database.getCollection("params").insertOne(messageRecu);
                }

                /* ---------- Connexion ---------- */
                if(messageRecu.get("Type").equals("LOGIN")){

                }
                System.out.println("En attente de la prochaine ligne...");

            }
        }
        catch (IOException e) {
            //when a connection is closed
            //mainChannel.remove(userData.getSocketAddress());
            userData.getChannels().forEach(channel -> channel.getUserList().remove(userData));
            e.printStackTrace();
            System.out.println("Fermeture de l'instance...");
        }
        catch (IllegalArgumentException e){

        }
    }
}

