package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.*;

public class Channel {
    public static Map<String, Channel> everyChannels = new HashMap<>();
    public List<User> userList = new ArrayList<>();
    public List<User> adminUserList;
    public TypeChannel type;
    public String name;

    public Channel(){
    }

    public void sendToChannel(String s){
        try{
            for(User user : userList){
                user.getOutputStream().writeUTF(s);
                user.getOutputStream().flush();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
