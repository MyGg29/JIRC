package util;

import models.TypesChannel;
import org.bson.Document;

import java.util.Date;

public class MessagesProtocol {
    public static Document loginMessage(String name, String password){
        Document d = new Document();
        d.put("Type", "LOGIN");
        d.put("Name", name);
        d.put("Password", password);
        return d;
    }
    public static Document joinMessage(String channelName, TypesChannel typeChannel){
        Document d = new Document();
        d.put("Type", "JOIN");
        d.put("Channel", channelName);
        d.put("TypeChannel",typeChannel.toString());
        return d;
    }
    public static Document normalMessage(String channel,String message){
        Document d = new Document();
        d.put("Type", "MESSAGE");
        d.put("Time", new Date());
        d.put("Channel", channel);
        d.put("Content", message);
        return d;
    }
    public static Document normalMessage(String channel,String message, String senderIp){
        Document d = normalMessage(channel,message);
        d.put("Sender", senderIp);
        return d;
    }
    public static Document normalMessage(String channel,String message, String senderIp,String senderName){
        Document d = normalMessage(channel,message,senderIp);
        d.put("SenderName", senderName);
        return d;
    }
    //Used when the server foward and already existing message sent by the client
    public static Document normalMessage(Document forwardedMessage, String senderIp, String senderName){
        Document d = new Document(forwardedMessage);
        d.put("Sender", senderIp);
        d.put("SenderName", senderName);
        return d;
    }
    public static Document addToWhitelistMessage(String user, String channel){
        Document d = new Document();
        d.put("Type", "PARAMS");
        d.put("UserName", user);
        d.put("Channel", channel);
        return d;
    }

}
