package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


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
        catch (Exception e) { }
    }
}

class SSocket implements Runnable {
    private Socket socket;
    private static List<DataOutputStream> listeClients = new ArrayList<DataOutputStream>(); //liste des clients connectés

    public SSocket(Socket socket) throws IOException {
        this.socket = socket;
        try{
            listeClients.add(new DataOutputStream(socket.getOutputStream()));
        }
        catch(IOException e){
            throw new IOException(e.getMessage());
        }
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
                //dOut.writeUTF(line + " Comming back from the server");
                //dOut.flush();
                System.out.println("waiting for the next line....");
                for(DataOutputStream client : listeClients){
                    client.writeUTF(line + " Comming back from the server");
                    client.flush();
                }
            }
        }
        catch (Exception e) { }
    }
}