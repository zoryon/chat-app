package com.client.chat.swing.protocol;

import java.util.ArrayList;

public class JsonChat {
    // attributes
    private int id;
    private String chatName;
    private ArrayList<JsonMessage> messages;

    // constructors
    public JsonChat(int id, String chatName) {
        this.id = id;
        this.chatName = chatName;
        this.messages = new ArrayList<>();
    }

    // getters and setters
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

    public ArrayList<JsonMessage> getMessages() {
        return messages;
    }

    public boolean addMessage(JsonMessage msg) {
        return messages.add(msg);
    }
}
