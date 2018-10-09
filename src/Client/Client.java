package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        int serverPort = 666;

        try {
            InetAddress inetAdd = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(inetAdd, serverPort);//Ouvre un socket sur localhost

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            DataInputStream dIn = new DataInputStream(in);
            DataOutputStream dOut = new DataOutputStream(out);

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Type in something and press enter. Will send it to the server and tell ya what it thinks.");
            System.out.println();

            String line = null;
            while (true) {
                line = keyboard.readLine();
                System.out.println("Wrinting Something on the server");
                dOut.writeUTF(line);
                dOut.flush();

                line = dIn.readUTF();
                System.out.println("Line Sent back by the server---" + line);
            }
        }
        catch (Exception e) { }
    }
}
