package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    DataInputStream dIn;
    DataOutputStream dOut;
    int serverPort = 666;

    public Client() {
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
            DInListenner listenner = new DInListenner(dIn);
            Thread listen = new Thread(listenner);
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
}

class DInListenner implements Runnable {
    private DataInputStream dIn;
    public DInListenner(DataInputStream dIn){
       this.dIn = dIn;
    }
    @Override
    public void run(){
        try{
            while(true){
                String line = dIn.readUTF();
                System.out.println("Line Sent back by the server---" + line);
            }
        }
        catch (IOException e){

        }
    }
}