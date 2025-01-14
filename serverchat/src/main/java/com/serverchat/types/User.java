package com.serverchat.types;

public class User {
    private static int nextId = 0;
    private int id;
    private String username;
    private String password;


    

    //constructor
    public User(String username, String password) {
        this.id = nextId++;
        this.username = username;
        this.password = password;
    }
    
    //getters
    public String getUsername() {
        return username;
    }



    public String getPassword() {
        return password;
    }



    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
