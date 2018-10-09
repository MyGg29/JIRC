package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void main(String[] args) {
        int port = 666; //random port number
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Waiting for a client....");

            System.out.println("Got a client :) ... Finally, someone saw me through all the cover!");
            System.out.println();
            while(true) {
                Socket socket = ss.accept();

                SSocket sSocket = new SSocket(socket);
                Thread t = new Thread(sSocket);
                t.start();
                System.out.println("Socket Stack Size-----");//+socketMap.size());
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
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            DataInputStream dIn = new DataInputStream(in);
            DataOutputStream dOut = new DataOutputStream(out);

            String line = null;
            while (true) {
                line = dIn.readUTF();
                System.out.println("Recievd the line----" + line);
                dOut.writeUTF(line + " Comming back from the server");
                dOut.flush();
                System.out.println("waiting for the next line....");
            }
        }
        catch (Exception e) { }
    }
}