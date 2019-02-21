package client;

import org.bson.Document;
import server.Channel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.function.Function;

public class Client{
    DataInputStream dIn;
    DataOutputStream dOut;
    int serverPort = 666;
    Function<String, Void> showMessage;
    public Client(){
        try {
            InetAddress inetAdd = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(inetAdd, serverPort);//Ouvre un socket sur localhost

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            dIn = new DataInputStream(in);
            dOut = new DataOutputStream(out);

            Thread listen = new Thread(this::listen); //créer un thread qui va faire tourner listen()
            listen.start();
        }
        catch (Exception e) { }
    }

    public void joinChannel(String channelName){
        Document d = new Document();
        d.put("Join", channelName);
        send(d.toJson());
    }

    private void send(String text){
        try{
        dOut.writeUTF(text);
        dOut.flush();
        }catch(IOException e){}
    }

    public void sendMessage(String message, String channel) {
       Document d = new Document();
       d.put("Channel", channel);
       d.put("Message", message);
       send(d.toJson());
    }

    // Used to show the message on the screen. The client is always listenning for a server response and show it when it sees something
    public void listen(){
        try{
            while(true){
                String line = dIn.readUTF();
                System.out.println("Line Sent back by the server---" + line);
                Document messageRecu = Document.parse(line);

                showMessage.apply(messageRecu.get("Sender", String.class) + ": " + messageRecu.get("Message", String.class));
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    //Permet de donner à la classe un comportement exterieur quand un message arrive sur le stream
    public void setShowMessage(Function<String, Void> showMessageFunction){
        this.showMessage = showMessageFunction;
    }

    public void shutdown(){

    }

}

