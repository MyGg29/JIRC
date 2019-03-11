package models;

import org.bson.Document;
import util.Function4Args;
import util.MessagesProtocol;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Function;

public class Client{
    private volatile boolean exitListenThread = false;
    private DataInputStream dIn;
    private DataOutputStream dOut;
    private int serverPort = 666;
    private int clientPort = 0;
    private Function4Args<String,String,String, Void> showMessage;
    private Function<Boolean,Void> connexionCallback;


    public Client(){
        try {
            InetAddress inetAdd = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(inetAdd, serverPort);//Ouvre un socket sur localhost
            this.clientPort = socket.getLocalPort();

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
        Document d = MessagesProtocol.loginMessage(userName,password);
        send(d);
    }

    /* ---------- Rejoindre un channel ---------- */
    public void joinChannel(String channelName, TypesChannel typeChannel){
        Document d = MessagesProtocol.joinMessage(channelName,typeChannel);
        send(d);
    }

    /* ---------- Récupérer infos d'un channel ---------- */
    public void getChannelInfo(String channelName){
        Document d = new Document();
        d.put("Type", "INFO");
        d.put("Channel", channelName);
        send(d);
    }

    /* ---------- Ecoute du serveur et affichage des messages à l'écran ---------- */
    public void listen(){
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
                e.printStackTrace();
                System.out.println("Closing listenning thread...");
                this.exitListenThread = true;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /* ---------- Fonction d'envoi ---------- */
    private void send(Document d){
        try{
        dOut.writeUTF(d.toJson());
        dOut.flush();
        }catch(IOException e){
            System.out.println("FATAL ERROR SENDING A MESSAGE");
            e.printStackTrace();
        }
    }

    /* ---------- Envoi d'un message, sur un channel défini avec horodatage ---------- */
    public void sendMessage(String message, String channel) {
       Document d = MessagesProtocol.normalMessage(channel,message);
       send(d);
    }

    /* ----------  ---------- */
    public void addToWhitelist(String user, String channel){
        Document d = MessagesProtocol.addToWhitelistMessage(user,channel);
        send(d);

    }
    //Permet de donner à la classe un comportement exterieur quand un message arrive sur le stream
    public void setShowMessage(Function4Args<String,String,String,Void> showMessageFunction){
        this.showMessage = showMessageFunction;
    }


    /* ---------- Arret ---------- */

    public void shutdown(){
        try{
            dIn.close();
            dOut.close();
        }
        catch (IOException e){
            System.out.println("Problem closing the server socket");
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

    public void connectionCallback() {

    }

    public Function<Boolean, Void> getConnexionCallback() {
        return connexionCallback;
    }

    public void setConnexionCallback(Function<Boolean,Void> connexionCallback) {
        this.connexionCallback = connexionCallback;
    }
}

