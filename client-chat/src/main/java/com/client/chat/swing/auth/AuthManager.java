package com.client.chat.swing.auth;

public class AuthManager {
    // attributes
    private static AuthManager instance;
    private boolean isAuthenticated;
    private String username;
    
    // constructors
    private AuthManager() {
        this.isAuthenticated = false;
        this.username = null;
    }
    
    // singleton --> only one instance can exist at a time
    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }
    
    // methods
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    
    public void authenticate(String username) {
        this.username = username;
        this.isAuthenticated = true;
    }
    
    public void logout() {
        this.username = null;
        this.isAuthenticated = false;
    }
    
    // getters and setters
    public String getUsername() {
        return username;
    }
}