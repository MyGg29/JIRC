package models;

import com.sun.javafx.scene.control.skin.DatePickerSkin;
import org.bson.Document;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public class Client{
    private volatile boolean exitListenThread = false;
    private DataInputStream dIn;
    private DataOutputStream dOut;
    private int serverPort = 666;
    private int clientPort = 0;
    private Function<String,String,String, Void> showMessage;


    public Client(){
        try {
            /* ---------- Ouverture d'un socket en localhost ---------- */

            InetAddress inetAdd = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(inetAdd, serverPort);
            this.clientPort = socket.getLocalPort();

            /* ---------- Lecture/écriture ---------- */

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            dIn = new DataInputStream(in);
            dOut = new DataOutputStream(out);

            /* ---------- Thread d'écoute ---------- */

            Thread listen = new Thread(this::listen);
            listen.start();
        }
        catch (Exception e) { }
    }

    public void connect(String userName,String password){
        //Connexion
    }

    /* ---------- Rejoindre un channel ---------- */

    public void joinChannel(String channelName, TypesChannel typeChannel){
        Document d = new Document();
        d.put("Type", "JOIN");
        d.put("Channel", channelName);
        d.put("TypeChannel",typeChannel.toString());
        send(d.toJson());
    }

    /* ---------- Récupérer infos d'un channel ---------- */

    public void getChannelInfo(String channelName){
        Document d = new Document();
        d.put("Type", "INFO");
        d.put("Channel", channelName);
        send(d.toJson());
    }

    /* ---------- Ecoute du serveur et affichage des messages à l'écran ---------- */

    public void listen(){
        while(!exitListenThread){
            try{
                String line = dIn.readUTF();
                System.out.println("Retour du serveur ---> " + line);
                Document messageRecu = Document.parse(line);

                if(messageRecu.get("Type").equals("MESSAGE")){
                    showMessage.apply(messageRecu.get("Channel",String.class),
                            messageRecu.get("Sender", String.class),
                            messageRecu.get("Content", String.class));
                }
                if(messageRecu.get("Type").equals("INFO")){

                }
            } catch (SocketException e){
                e.printStackTrace();
                System.out.println("Fermeture du thread en écoute...");
                exitListenThread = true;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /* ---------- Fonction d'envoi ---------- */

    private void send(String text){
        try{
            dOut.writeUTF(text);
            dOut.flush();
        }
        catch(IOException e){

        }
    }

    /* ---------- Envoi d'un message, sur un channel défini avec horodatage ---------- */

    public void sendMessage(String message, String channel) {
       Document d = new Document();
       d.put("Type", "MESSAGE");
       d.put("Time", new Date());
       d.put("Channel", channel);
       d.put("Content", message);
       send(d.toJson());
    }

    /* ----------  ---------- */

    public void addToWhitelist(String user, String channel){
        Document d = new Document();
        d.put("Type", "PARAMS");
        d.put("AddUser", user);
        d.put("Channel", channel);
        send(d.toJson());

    }

    public void sendToStats(String content, String stat) {
        Document d = new Document();
        d.put("Type", "STATS");
        d.put("Content", content);
        d.put("Stat", stat);
        send(d.toJson());
    }

    //Permet de donner à la classe un comportement exterieur quand un message arrive sur le stream
    public void setShowMessage(Function<String,String,String,Void> showMessageFunction){
        this.showMessage = showMessageFunction;
    }


    /* ---------- Arret ---------- */

    public void shutdown(){
        try{
            dIn.close();
            dOut.close();
        }
        catch (IOException e){
            System.out.println("Problème avec la fermeture du socket serveur...");
        }
        finally {

        }
    }

    public int getClientPort() {

        return clientPort;
    }

    public void setClientPort(int clientPort) {

        this.clientPort = clientPort;
    }
}

