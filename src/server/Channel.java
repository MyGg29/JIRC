package server;

import models.TypesChannel;
import util.MessagesProtocol;

import java.io.IOException;
import java.util.*;

public class Channel {
    public static Map<String, Channel> everyChannels = new HashMap<>();
    public TypesChannel type;
    public String name;
    private List<User> userList = new ArrayList<>();
    private List<User> adminUserList;
    private List<User> whiteList = new ArrayList<>(); // liste des utilisateurs autorisé à utiliser le channel

    public Channel(){
    }

    public void sendToChannel(String s){
        try{
            for(User user : this.userList){
                if(isAllowedToJoin(user)){
                    user.send(s);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public boolean isAllowedToJoin(User user) {
        if(this.type == TypesChannel.PUBLIC){
            return true;
        }
        //Check if the user is in the whitelist so we know if he can join the channel
        if(this.type == TypesChannel.GROUPE || this.type == TypesChannel.PRIVATE){
            //filter the whitelist to see if the user is inside
            Optional<User> filteredWhitelist = this.whiteList.stream().filter(whiteListedUser -> user.getName().equals(whiteListedUser.getName()))
                                                             .findFirst();
            //if the user is in the whitelist
            if(filteredWhitelist.isPresent()){
                return true;
            }
        }
        return false;
    }

    public boolean addUserToWhiteList(User user){
        return this.whiteList.add(user);
    }
    public List<User> getUserList() {
        return userList;
    }
}
