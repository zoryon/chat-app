package com.serverchat.types;

import java.util.ArrayList;

import com.serverchat.protocol.Message;

/*this interface allow the data obj to have on the same 
 * ArrayList every chat, to then provide all needed info
*/
public interface ChatInterface {
    public ArrayList<Integer> getUsersId();
    public int getChatId();
    public String getChatName();
    public ArrayList<Message> getAllMessages();
    public int addNewMsg(Message message);
    public boolean rmMessage(int messageId, int userID);
    public boolean modMsg(Message message, int userID);
    public void rmUser(User user, User deletedUserInfo);
}



