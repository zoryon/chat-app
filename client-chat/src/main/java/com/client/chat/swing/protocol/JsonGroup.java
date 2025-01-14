package com.client.chat.swing.protocol;

import java.util.ArrayList;

public class JsonGroup {
    // attributes
    private String groupName;
    private ArrayList<String> usernameList;

    // constructors
    public JsonGroup(String groupName, ArrayList<String> usernameList) {
        this.groupName = groupName;
        this.usernameList = usernameList;
    }

    // getters and setters
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getUsernameList() {
        return usernameList;
    }

    public void setUsernameList(ArrayList<String> usernameList) {
        this.usernameList = usernameList;
    }
}
