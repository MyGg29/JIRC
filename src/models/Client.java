package models;

import org.bson.Document;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class Client{
    private volatile boolean exitListenThread = false;
    private DataInputStream dIn;
    private DataOutputStream dOut;
    private int serverPort = 666;
    private int clientPort = 0;
    private Function<String,String,String, Void> showMessage;


    public Client(){
        try {
            InetAddress inetAdd = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(inetAdd, serverPort);//Ouvre un socket sur localhost
            setClientPort(socket.getLocalPort());

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            dIn = new DataInputStream(in);
            dOut = new DataOutputStream(out);

            Thread listen = new Thread(this::listen); //créer un thread qui va faire tourner listen()
            listen.start();
        }
        catch (Exception e) { }
    }

    public void joinChannel(String channelName, TypesChannel typeChannel){
        Document d = new Document();
        d.put("Type", "JOIN");
        d.put("Channel", channelName);
        d.put("TypeChannel",typeChannel.toString());
        send(d.toJson());
    }

    public void getChannelInfo(String channelName){ Document d = new Document();
        d.put("Type", "INFO");
        d.put("Channel", channelName);
        send(d.toJson());
    }
    // Used to show the message on the screen. The client is always listenning for a server response and show it when it sees something
    public void listen(){
        while(!exitListenThread){
            try{
                String line = dIn.readUTF();
                System.out.println("Line Sent back by the server---" + line);
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
                System.out.println("Closing listenning thread...");
                exitListenThread = true;
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void send(String text){
        try{
        dOut.writeUTF(text);
        dOut.flush();
        }catch(IOException e){}
    }

    public void sendMessage(String message, String channel) {
       Document d = new Document();
       d.put("Type", "MESSAGE");
       d.put("Channel", channel);
       d.put("Content", message);
       send(d.toJson());
    }

    public void addToWhitelist(String user, String channel){
        Document d = new Document();
        d.put("Type", "PARAMS");
        d.put("AddUser", user);
        d.put("Channel", channel);
        send(d.toJson());

    }
    //Permet de donner à la classe un comportement exterieur quand un message arrive sur le stream
    public void setShowMessage(Function<String,String,String,Void> showMessageFunction){
        this.showMessage = showMessageFunction;
    }

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
}

class ListenTheServer implements Runnable {

    @Override
    public void run() {
    }
}
