package com.serverchat.types;

import java.util.ArrayList;

import com.serverchat.protocol.Message;

public class Group implements ChatInterface {
    // basic var
    private int messageID;
    private int id;
    private String groupName;
    private ArrayList<User> members;
    private ArrayList<Message> messages;

    // constructor with generated ID
    public Group(String name, User firstUser) {
        this.messages = new ArrayList<>();
        this.id = ChatIDs.getNextChatID();
        this.members = new ArrayList<>();
        this.groupName = name;
        this.messageID = 0;
        this.members.add(firstUser);
    }

    public boolean ContainUser(User user){
        for (User member: members) {
            if ( member.getId() == user.getId())
            return true; 
        }
        return false;
    }

    public boolean addUser(User user){
        if(this.ContainUser(user)) return false;
        this.members.add(user);
        return true;
    }

    // return the chat id
    @Override
    public int getChatId() {
        return id;
    }

    // return the list of all users
    @Override
    public ArrayList<Integer> getUsersId() {
        ArrayList<Integer> ans = new ArrayList<>();
        for (User i : members) {
            ans.add(i.getId());
        }
        return ans;
    }

    @Override
    public String getChatName() {
        return this.groupName;
    }

    @Override
    public ArrayList<Message> getAllMessages() {
        return this.messages;
    }

    @Override
    public int addNewMsg(Message message) {
        message.setId(messageID++) ;
        for(Message m : messages){
            if(m.getId() == message.getId()) return -1;
        }
       messages.add(message);
       return message.getId();
    }

    @Override
    public boolean rmMessage(int messageId, int userID) {
        Message m = this.getMessageByID(messageId, userID);
        if(m == null) return false;

        
        messages.remove(m);
        return true;
    }

    private Message getMessageByID(int messageId, int userID){
        Message m = null;
        for(Message i : messages){
            if(i.getId() == messageId) m = i;
        }
        if(m == null) return null;
        if(m.getSenderId() != userID) return null;//double if to avoid errors
        return m ;
    } 

    @Override
    public boolean modMsg(Message message, int userID) {
        Message m = this.getMessageByID(message.getId(), userID);
        if(m == null) return false;
        
        m.setContent(message.getContent());
        return true;
    }

    @Override
    public void rmUser(User user, User deletedUserInfo) {
        for(User u : this.members){
            if(u.getId() == user.getId()){
                u = deletedUserInfo;
                return;
            }
        }
    }  

}
