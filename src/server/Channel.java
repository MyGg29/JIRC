package server;

import models.TypesChannel;

import java.io.IOException;
import java.util.*;

public class Channel {
    public static Map<String, Channel> everyChannels = new HashMap<>();
    public TypesChannel type;
    public String name;
    private List<User> userList = new ArrayList<>();
    private List<User> adminUserList;
    private List<User> whiteList = new ArrayList<>();

    public Channel(){
    }

    public void sendToChannel(String s){
        try{
            for(User user : this.userList){
                if(type == TypesChannel.PUBLIC){
                    user.send(s);
                }
                if((type == TypesChannel.GROUPE || type == TypesChannel.PRIVATE)
                        && whiteList.contains(user)){
                    user.send(s);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public boolean isAllowedToJoin(User user) {
        if(type == TypesChannel.PUBLIC){
            return true;
        }
        if((type == TypesChannel.GROUPE || type == TypesChannel.PRIVATE)
                && this.whiteList.contains(user)){
            return true;
        }
        return false;
    }

    private boolean addUserToWhiteList(User user){
        return this.whiteList.add(user);
    }
    public List<User> getUserList() {
        return userList;
    }
}
