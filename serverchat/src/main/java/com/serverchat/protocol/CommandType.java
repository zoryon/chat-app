package com.serverchat.protocol;

public enum CommandType {
    // general commmands
    OK("The previous command was executed successfully"),
    INIT("Send initial user's chat array"),
    EXIT("Exit from the app and close the connection"),
    
    // user management commmands
    NEW_USER("Initialize new user's session"),
    OLD_USER("Initialize old     user's session"),
    DEL_USER("Request to delete user's account"),
    UPD_NAME("Change username"),
    LOGOUT("Logout from the current account"),
    
    // chat management commmands
    NEW_CHAT("Initiate a new private chat"),
    NEW_GROUP("Create a new group"),
    NAV_CHAT("Navigate to specified chat or group"),
    REQ_CHATS("Request information about user chats (userId)"),

    // messages management commmands
    SEND_MSG("Send a new message"),
    RM_MSG("Remove a message"),
    UPD_MSG("Update the content of a message"),

    // error management commmands
    ERR_GEN("General server error"),
    ERR_NOT_FOUND("User not found"),
    ERR_CHAT_EXISTS("Private chat already exists"),
    ERR_USER_EXISTS("Username already in use"),
    ERR_DISCONNECT("Client has disconnected"),
    ERR_WRONG_DATA("The data entered is incorrect");

    // attributes
    private final String description;

    // constructors
    CommandType(String description) {
        this.description = description;
    }

    // getters
    public String getDescription() {
        return description;
    }
}