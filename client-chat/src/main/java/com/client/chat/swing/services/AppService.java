package com.client.chat.swing.services;

import com.client.chat.swing.protocol.CommandType;
import com.client.chat.swing.protocol.JsonChat;

import java.io.IOException;

public class AppService extends Service {
    // attributes
    private static AppService instance;

    // constructors
    private AppService() throws IOException {
        super();
    }

    // singleton --> only one instance can exist at a time
    public static AppService getInstance() throws IOException {
        if (instance == null) {
            instance = new AppService();
        }
        return instance;
    }

    public boolean handleConnectToChat() throws IOException, InterruptedException {
        // can call this version of the fn, if there's no chat identifier to send
        return handleConnectToChat(null);
    }

    public boolean handleConnectToChat(JsonChat chat) throws IOException, InterruptedException {
        // making sure the user has rights to access the chat
        super.sendReq(CommandType.NAV_CHAT);
        super.sendJsonReq(chat.getId());

        return super.isSuccess(catchCommandRes());
    }
}
