package com.serverchat.protocol;

/* The struct of a single message
 * by using this the server can insert the
 * message in the right chat and give all needed 
 * permission to right users 
 */
public class Message {
    private int id; //the server must set the message id (relative to the chat) => send it to the client
    private int chatId;
    private int senderId;
    private String senderName;
    private String content;

    public Message(int id, int chatId, int senderId, String senderName, String content) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
    }

    public String getContent() {
        return content;
    }


    public int getId() {
        return id;
    }


    public int getChatId() {
        return chatId;
    }


    public int getSenderId() {
        return senderId;
    }


    public String getSenderName() {
        return senderName;
    }


    public void setId(int id) {
        this.id = id;
    }


    public void setChatId(int chatId) {
        this.chatId = chatId;
    }


    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }


    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


    public void setContent(String content) {
        this.content = content;
    }

    
    
    
}
