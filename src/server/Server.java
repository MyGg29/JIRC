package server;

import org.bson.Document;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;


public class Server {
    public static void main(String[] args) {
        int port = 666; //random port number
/*
        MongoClient mongo = new MongoClient("localhost",27017);
        MongoDatabase JIRC = mongo.getDatabase("JIRC");
        MongoCollection logsCollection =JIRC.getCollection("logs");
        Document message = new Document();
        message.put("Sec", "second message");
        logsCollection.insertOne(message);
*/

        try {
            ServerSocket ss = new ServerSocket(port); //Le serveur ecoute quel port
            System.out.println("Ecoute sur le port " + port);

            while(true) {
                Socket socket = ss.accept();//La connexion est faite entre le client (port random) et le serveur (port 666)

                //On ajoute le client dans la liste des clients connectés.
                //On crée un thread par client
                SSocket sSocket = new SSocket(socket);
                Thread t = new Thread(sSocket);
                t.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SSocket implements Runnable {
    private Socket socket;
    private User userData = new User();

    public SSocket(Socket socket) throws IOException {
        this.socket = socket;
        userData.setOutputStream(new DataOutputStream(this.socket.getOutputStream()));
        userData.setInputStream(new DataInputStream(this.socket.getInputStream()));
        userData.setSocketAddress(this.socket.getRemoteSocketAddress());
        userData.setName(this.socket.getRemoteSocketAddress().toString());
    }

    @Override
    public void run() {
        try {
            String line;
            while (true) {
                //Les données sont reçu en utf-16
                line = userData.getInputStream().readUTF();
                System.out.println("Received from " + userData.getSocketAddress() + " : " + line);
                //parsing of the JSON
                Document messageRecu = Document.parse(line);
                messageRecu.put("Sender", userData.getName());

                //Si on veux join un channel
                if(messageRecu.containsKey("Join")){
                    String nomChannel = messageRecu.get("Join",String.class);
                    if(Channel.everyChannels.containsKey(nomChannel)){
                        Channel.everyChannels.get(nomChannel).userList.add(userData);
                    }
                    else{
                        Channel mainChannel = new Channel();
                        mainChannel.name = nomChannel;
                        mainChannel.type = TypeChannel.PUBLIC;
                        Channel.everyChannels.put(nomChannel, mainChannel);
                        Channel.everyChannels.get(nomChannel).userList.add(userData);
                    }
                }
                if(messageRecu.containsKey("Message")){
                    //On trouve le channel ou envoyer le message
                    for(Map.Entry<String, Channel> entry : Channel.everyChannels.entrySet()){
                        String key = entry.getKey();
                        Channel value = entry.getValue();
                        if(key.equals(messageRecu.get("Channel", String.class))){
                            value.sendToChannel(messageRecu.toJson());
                        }
                    }
                }
                System.out.println("waiting for the next line....");

            }
        }
        catch (IOException e) {
            //when a connection is closed
            //mainChannel.remove(userData.getSocketAddress());
            userData.getChannels().forEach(channel ->
                    channel.userList.remove(userData));
            e.printStackTrace();
            System.out.println("Deleting connection...");
        }
    }
}

