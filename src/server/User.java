package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

public class User {
    private DataOutputStream outputStream;//Where we write
    private DataInputStream inputStream;//Where we receive
    private SocketAddress socketAddress;
    private String name;
    private SocketAddress ip;
    private List<Channel> channels = new ArrayList<>();

    public Boolean addChannel(Channel invit){
        return this.channels.add(invit);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }
    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void send(String data) throws IOException {
        outputStream.writeUTF(data);
        outputStream.flush();
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

}
