package models;

import org.bson.Document;
import org.bson.conversions.Bson;

public class Message extends Document {
    private String type;
    private String content;
    private String sender;
    private String channel;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
