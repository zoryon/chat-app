package com.client.chat.swing.protocol;

public class JsonUser {
    // attributes
    private String username;
    private String password;

    // constructors
    public JsonUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
