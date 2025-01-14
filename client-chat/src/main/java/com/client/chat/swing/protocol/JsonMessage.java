package com.client.chat.swing.protocol;

import com.client.chat.swing.auth.AuthManager;

public class JsonMessage {
    // attributes
    private int id;
    private int chatId;
    private int senderId;
    private String senderName;
    private String content;
    
    // constructors
    public JsonMessage(int chatId, String content) {
        this.chatId = chatId;
        this.content = content;
        this.senderName = AuthManager.getInstance().getUsername();
        
        // the server will modify these
        this.id = 0;
        this.senderId = 0;
    }

    public JsonMessage(int chatId, int id) {
        this.chatId = chatId;
        this.senderName = AuthManager.getInstance().getUsername();
        
        // the server will modify these
        this.content = null;
        this.senderId = 0;
        this.id = id;
    }

    public JsonMessage(int chatId, int id, String content) {
        this.chatId = chatId;
        this.senderName = AuthManager.getInstance().getUsername();
        this.content = content;

        // the server will modify these
        this.senderId = 0;
        this.id = id;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean equals(JsonMessage msg) {
        // compare the relevant fields for equality
        return 
            (this.getId() == msg.getId()) &&
            (this.getChatId() == msg.getChatId()) &&
            (this.getSenderId() == msg.getSenderId()) &&
            (this.getSenderName().equals(msg.getSenderName())) &&
            (this.getContent().equals(msg.getContent()));
    }
}
