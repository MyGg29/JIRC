package Server;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BSON;
import org.bson.Document;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server {
    public static void main(String[] args) {
        int port = 666; //random port number
        List clients = new ArrayList<DataOutputStream>(); //liste des clients connectés

        MongoClient mongo = new MongoClient("localhost",27017);
        MongoDatabase JIRC = mongo.getDatabase("JIRC");
        MongoCollection logsCollection =JIRC.getCollection("logs");
        Document message = new Document();
        message.put("Sec", "second message");
        logsCollection.insertOne(message);



        try {
            ServerSocket ss = new ServerSocket(port); //Le serveur ecoute quel port
            System.out.println("Ecoute sur le port " + port);

            while(true) {
                Socket socket = ss.accept();//La connexion est faite entre le client (port random) et le serveur (port 666)

                //On ajoute le client dans la liste des clients connectés.
                clients.add(new DataOutputStream(socket.getOutputStream()));
                //On crée un thread par client
                SSocket sSocket = new SSocket(socket);
                Thread t = new Thread(sSocket);
                t.start();
            }
        }
        catch (Exception e) { }
    }
}

class SSocket implements Runnable {
    private Socket socket;

    public SSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();//Where we receive
            OutputStream out = socket.getOutputStream();//Where we write

            //Des abstractions en plus afin que l'on lise des lettres et pas des bytes[]
            //Sinon in.read() rends les lettres une par une avec des entiers de 0 à 255
            DataInputStream dIn = new DataInputStream(in);
            DataOutputStream dOut = new DataOutputStream(out);

            String line = null;
            while (true) {
                //Les données sont reçu en utf-16
                line = dIn.readUTF();
                System.out.println("Received from " + socket.getRemoteSocketAddress() + " : " + line);
                dOut.writeUTF(line + " Comming back from the server");
                dOut.flush();
                System.out.println("waiting for the next line....");
            }
        }
        catch (Exception e) { }
    }
}