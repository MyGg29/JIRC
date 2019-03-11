package server;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.Message;
import models.TypesChannel;
import models.TypesMessage;
import org.bson.Document;
import util.MessagesProtocol;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


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

