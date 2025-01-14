package com.serverchat;

import java.io.DataOutputStream;
import java.util.ArrayList;

import com.serverchat.protocol.CommandType;
import com.serverchat.protocol.JsonChat;
import com.serverchat.protocol.JsonUser;
import com.serverchat.protocol.Message;
import com.serverchat.types.Chat;
import com.serverchat.types.ChatInterface;
import com.serverchat.types.User;
import com.google.gson.*;

public class Datas {
    public ArrayList<User> allUsers;
    public ArrayList<ChatInterface> chatsData;
    public ArrayList<ClientHandler> connectedUsers;
    
    public Datas() {
        allUsers = new ArrayList<>();
        chatsData = new ArrayList<>();
        connectedUsers = new ArrayList<>();//here we find userID/clientHandler, to know who must be sent new messages
        //theorically this should also allow the server to have more clients for the same user
    }
    
    //add a new user got Created in ClientHandler
    public synchronized void newUser(User newUser){
        allUsers.add(newUser);
    }

    public synchronized void rmConnectedUser(ClientHandler connection){
        this.connectedUsers.remove(connection);
    }

    //return the whole user by his username
    public User getUserByName(String username){
        for(User u : allUsers){
            if(u.getUsername().equals(username)) return u;
        }
        return null;
    }

    //return a user (if it exists) by Username && Password
    public User getUser(String userName, String password){
        for(User i : allUsers){
            if(userName.equals(i.getUsername()) && password.equals(i.getPassword())){
                return i;
            }
        }
        return null;
    }

    //return a user by his id
    public User getUserById(int id){
        for(User u : allUsers){
            if(u.getId() == id) return u;
        }
        return null;
    }

    //return an ArrayList of ChatInterface by UserID
    public ArrayList<ChatInterface> getChatsByUserId(int id){
        ArrayList<ChatInterface> ans = new ArrayList<>();
        for(ChatInterface i : chatsData){
            if(i.getUsersId().contains(id)){
                ans.add(i);
            }
        }
        return ans;
    }

    public ChatInterface getChatByChatId(int chatId){
        for(ChatInterface i : chatsData){
            if(i.getChatId() == chatId)return i;
        }
        return null;
    }  

    //add of chat and groups
    public synchronized void addChatGroup(ChatInterface toAdd){
        chatsData.add(toAdd);
        for(ClientHandler i : this.connectedUsers){
            if(toAdd.getUsersId().contains(i.getUserId()) && i.getUserId() != toAdd.getUsersId().get(0)){
                //if the user is into the chat && is ConnectedNow
                sendNotificationsCreatedChat(i, (toAdd instanceof Chat), toAdd);
            }
        }
    }

    //send notification every time a chat is created
    private synchronized void sendNotificationsCreatedChat(ClientHandler client , boolean isChat , ChatInterface chat){
            //advise the user of the new incoming chat
            if(isChat){
                new OutThread (client.getOutputStream(), CommandType.NEW_CHAT.toString() ).start();
            }
            else
            {
                new OutThread (client.getOutputStream(), CommandType.NEW_GROUP.toString() ).start();
            }
            JsonChat toSend = new JsonChat(chat.getChatId(), chat.getChatName(), chat.getAllMessages());
            new OutThread (client.getOutputStream(), new Gson().toJson(toSend), 10).start();//send data as a JsonChat OBJ
            //which is the "standard" to send and recive chats
    }

    //when a user try to create a username if it's already user return true
    public synchronized boolean isExitingName(String name){
        for(User i : allUsers){
            if(i.getUsername().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    // add an obj to connectedUser
    public synchronized void addConnectedClient(int userID, ClientHandler client){
        connectedUsers.add(client);
    }

    // get all user as JsonUser
    public synchronized ArrayList<JsonUser> getAllJsonUsers(){
        if(allUsers.size() == 0) return null;
        ArrayList<JsonUser> ans = new ArrayList<>();
        for(User i : allUsers){
            ans.add(new JsonUser(i.getUsername(), i.getPassword()));
        }
        return ans;
    }

    //send new message
    public boolean addNewMsg(Message message){
        //get the chat of this message
        ChatInterface c = this.getChatByChatId(message.getChatId());
        //add the message into the chat
        int val = c.addNewMsg(message);
        if(val < 0) return false;// if the user was not in the chat
        //check 
        sendMessageToOthers(message, c);
        return true;
    }

    //this search for each user in the client
    private void sendMessageToOthers(Message m, ChatInterface c){
        for(ClientHandler client : this.connectedUsers){//searching into connected user for every user of this chat
            if(c.getUsersId().contains(client.getUserId()) && client.getUserId() != m.getSenderId()){//check if the client is in the chat
                //and its different from the sender

                //found connected user now sent him data
                DataOutputStream out = client.getOutputStream();//get the outputStream of the client which will be sent data
                //passing the output OBJ
                new OutThread(out, CommandType.SEND_MSG.toString()).start();
                new OutThread(out, new Gson().toJson(m), 10).start();
            }
        }
    }

    //get user allown to chat
    public boolean isAllownToChat(int chatId, int userID){
        if(this.getChatByChatId(chatId).getUsersId().contains(userID))return true;
        return false;
    }

    //check a private chat between 2 users
    public boolean isExitingPrivateChat(String username1, String username2){
        User user1 = this.getUserByName(username1);
        User user2 = this.getUserByName(username2);

        for(ChatInterface c : this.chatsData){
            if(c instanceof Chat && c.getUsersId().contains(user1.getId()) && c.getUsersId().contains(user2.getId())){
                return true;
            }
        }
        return false;
    }

    public synchronized boolean rmMessage(Message message,int userID ){
        ChatInterface c = null;
        for(ChatInterface chat : this.chatsData){
            if(chat.getChatId() == message.getChatId()){
                c = chat;
            }
        }
        if(c == null) return false;
        if(c.rmMessage(message.getId(), userID)){
            //for true removed
            for(ClientHandler client : this.connectedUsers){
                if(c.getUsersId().contains(client.getUserId()) && client.getUserId() != message.getSenderId()){
                    new OutThread(client.getOutputStream(), CommandType.RM_MSG.toString()).start();
                    new OutThread(client.getOutputStream(), new Gson().toJson(message), 10).start();
                }
            }
            return true;
        }else{
            return false;
        }
    }

    public synchronized boolean modMsg(Message message, int userID){
        ChatInterface c = null;
        for(ChatInterface chat : this.chatsData){
            if(chat.getChatId() == message.getChatId()){
                c = chat;
            }
        }
        if(c == null) return false;
        if(c.modMsg(message, userID)){
            //for true modified
            for(ClientHandler client : this.connectedUsers){
                if(c.getUsersId().contains(client.getUserId()) && client.getUserId() != message.getSenderId()){
                    new OutThread(client.getOutputStream(), CommandType.UPD_MSG.toString()).start();
                    new OutThread(client.getOutputStream(), new Gson().toJson(message), 10).start();
                }
            }
            return true;
        }else{
            return false;
        }
    }

    //check if there is an instance of ClientHandler for that user
    public synchronized boolean isExistingConnection(User user){
        for(ClientHandler client : this.connectedUsers){
            if(client.getUserId() == user.getId())
                return true;
        }
        return false;
    }

    public synchronized boolean deleteUser(User userToDelete){
        boolean ans = false;
        for(ChatInterface chat : chatsData){
            if(chat.getUsersId().contains(userToDelete.getId())){
                chat.rmUser(userToDelete, getDeletedUserInfo());
                ans = true;
            }
        }
        this.allUsers.remove(userToDelete);
        System.out.println("deleted user");
        return ans;
    }

    public User getDeletedUserInfo(){
        return this.allUsers.get(0);
    }

    public synchronized void updateUserName(User user, String newUsername){
        for(ChatInterface chat : chatsData){
            if(chat.getUsersId().contains(user.getId())){
                for(Message mod : chat.getAllMessages()){
                    if(mod.getSenderId() == user.getId()){
                        mod.setSenderName(newUsername);
                    }
                }
            }
        }
        user.setUsername(newUsername);
    }

}
