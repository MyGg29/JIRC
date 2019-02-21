package client;

import com.mongodb.client.model.Filters;
import com.mongodb.util.ObjectSerializer;
import netscape.javascript.JSObject;
import org.bson.BSON;
import org.bson.Document;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
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

            //BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            //System.out.println("Type in something and press enter. Will send it to the server and tell ya what it thinks.");
            //System.out.println();

            //String line = null;
            //while (true) {
            //line = keyboard.readLine();
            //    System.out.println("Wrinting Something on the server");
            //    dOut.writeUTF(line);
            //    dOut.flush();

            //    line = dIn.readUTF();
            //    System.out.println("Line Sent back by the server---" + line);
            //}
            Thread listen = new Thread(this::listen); //créer un thread qui va faire tourner listen()
            listen.start();


        }
        catch (Exception e) { }
    }
    public void sendText(String s) {
        try{
            dOut.writeUTF(s);
            dOut.flush();
        }catch(IOException e){}
    }

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

