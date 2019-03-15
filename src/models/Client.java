package models;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.bson.Document;
import util.Function4Args;
import util.MessagesFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.function.Function;

public class Client{
    private DataInputStream dIn;
    private DataOutputStream dOut;
    private ClientListener clientListener;
    private int serverPort = 666;
    private int clientPort = 0;
    private String name = "Anonyme";

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
            clientListener = new ClientListener(dIn);
            Thread listen = new Thread(clientListener);
            listen.start();
        }
        catch (Exception e) { }
    }

    public void connect(String userName,String password){
        Document d = MessagesFactory.loginMessage(userName,password);
        send(d);
    }

    /**
     * Permet au client de se connecter à un autre channel
     * @param channelName le nom du channel, peut être n'importe quoi
     * @param typeChannel le type du channel, faisant partie de l'enum TypeChannel
     */
    public void joinChannel(String channelName, TypesChannel typeChannel){
        Document d = MessagesFactory.joinMessage(channelName,typeChannel);
        send(d);
    }

    /**
     * Envoi un message sur un channel spécifique
     * @param message le message a partager
     * @param channel le channel ou partager le message
     */
    public void sendNormalMessage(String message, String channel) {
       Document d = MessagesFactory.normalMessage(channel,message);
       send(d);
    }

    /* ---------- Add a user to the whitelist of a private/group channel ---------- */

    /**
     * Ajoute un utilisateur à la whitelist d'un channel privée/groupé pour qu'il puisse le rejoindre
     * @param user le nom de l'utilisateur à ajouter
     * @param channel le channel ou ajouter cet utilisateur
     */
    public void addToWhitelist(String user, String channel){
        Document d = MessagesFactory.addToWhitelistMessage(user,channel);
        send(d);

    }

    /**
     *
     * @param content
     * @param stat
     */
    public void sendToStats(String content, String stat) {
        Document d = new Document();
        d.put("Type", "STATS");
        d.put("Content", content);
        d.put("Stat", stat);
        send(d);
    }

    /**
     * Envoie n'importe quoi au serveur. Methode privé qui permet de rajouter un niveau d'abstraction
     * @param d le Document a envoyer
     */
    private void send(Document d){
        try{
            dOut.writeUTF(d.toJson());
            dOut.flush();
        }catch(IOException e){
            System.out.println("FATAL ERROR SENDING A MESSAGE");
            e.printStackTrace();
        }
    }

    /**
     * Permet de donner le comportement du ClientListener (ce qui écoute les messages du server)
     * quand le ClientListener recoit un message
     * @param showMessageFunction la fonction qui explique comment afficher le message
     */
    public void setShowMessage(Function4Args<String,String,String,Void> showMessageFunction){
        this.clientListener.showMessage = showMessageFunction;
    }

    /**
     * set the behavior of after the client is connected
     * @param connexionCallback
     */
    public void setConnexionCallback(Function<Boolean,Void> connexionCallback) {
        this.clientListener.connexionCallback = connexionCallback;
    }

    /**
     * set the behavior when the client is going to ask for the list of users of a channel
     * @param usernameList
     */
    public void setUserlistCallback(Function<Pair<String,ObservableList<String>>, Void> usernameList) {
        this.clientListener.userlistCallback = usernameList;
    }

    /**
     * permet de fermer proprement les connexions entre le client et le serveur.
     */
    public void shutdown(){
        try{
            dIn.close();
            dOut.close();
        }
        catch (IOException e){
            System.out.println("Problem closing the server socket");
            dIn = null; //we let the garbage collector handle this
            dOut = null;
        }
    }

    /**
     * Demande au serveur de m'envoyer la liste des utilsateurs connecté actuellement sur le channel
     * @param channel le channel dont on souhaite l'info
     */
    public void updateUserList(String channel) {
        Document d = MessagesFactory.getUserList(channel);
        send(d);
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

