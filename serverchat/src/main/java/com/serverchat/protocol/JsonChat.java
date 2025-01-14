package com.serverchat.protocol;

import java.util.ArrayList;

/* in the authentication phase this 
 * class give the client only some info about 
 * every chat
*/ 
public class JsonChat {
    private int id;
    private String chatName;
    private ArrayList<Message> messages;

    public JsonChat(int id, String chatName, ArrayList<Message> messages) {
        this.id = id;
        this.chatName = chatName;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    

    
}
