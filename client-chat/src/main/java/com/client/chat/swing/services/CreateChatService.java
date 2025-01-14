package com.client.chat.swing.services;

import com.client.chat.swing.protocol.CommandType;
import com.client.chat.swing.protocol.JsonChat;
import com.client.chat.swing.protocol.JsonGroup;
import com.client.chat.swing.lib.Character;
import java.io.IOException;
import java.util.ArrayList;

public class CreateChatService extends Service {
    // attributes
    private static CreateChatService instance;

    // constructors
    private CreateChatService() throws IOException {
        super();
    }

    // singleton --> only one instance can exist at a time
    public static CreateChatService getInstance() throws IOException {
        if (instance == null) {
            instance = new CreateChatService();
        }
        return instance;
    }

    public boolean createPrivateDirectChat(String username) throws IOException, InterruptedException {
        // processing request
        sendReq(CommandType.NEW_CHAT);
        sendJsonReq(username);
        super.res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            String chatTmp = catchJsonRes(String.class);

            /*
             * [0] is the chat name
             * [1] is the actual chatId
             */
            String[] chatId = splitAndValidate(chatTmp, Character.HASHTAG.getValue(), 2);
            if (chatId == null) return false;

            JsonChat newChat = new JsonChat(Integer.parseInt(chatId[1]), chatId[0]);
            super.eventListener.addChat(newChat);

            super.eventListener.printUserChatList();
            return true;
        } 
            
        System.out.println("Error: " + res.getDescription());
        return false;
    }

    public boolean createPrivateGroupChat(String groupName, ArrayList<String> usernames) throws IOException, InterruptedException {
        // processing request
        sendReq(CommandType.NEW_GROUP);
        sendJsonReq(new JsonGroup(groupName, usernames));
        super.res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            String chatTmp = catchJsonRes(String.class);

            /*
             * [0] is the chat name
             * [1] is the actual chatId
             */
            String[] chatId = splitAndValidate(chatTmp, Character.HASHTAG.getValue(), 2);
            if (chatId == null) return false;

            JsonChat newChat = new JsonChat(Integer.parseInt(chatId[1]), chatId[0]);

            super.eventListener.addChat(newChat);

            super.eventListener.printUserChatList();
            return true;
        } 

        System.out.println("Error: " + res.getDescription());
        return false;
    }

    private String[] splitAndValidate(String toSplit, String pattern, int partsNum) {
        String[] ans = toSplit.split(pattern);

        if (ans.length != partsNum) {
            System.out.println("Error: " + CommandType.ERR_WRONG_DATA);
            return null;
        }

        return ans;
    }
}
