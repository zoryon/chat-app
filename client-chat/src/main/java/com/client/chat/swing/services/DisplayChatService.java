package com.client.chat.swing.services;

import com.client.chat.swing.lib.Character;
import com.client.chat.swing.protocol.CommandType;
import com.client.chat.swing.protocol.JsonChat;
import com.client.chat.swing.protocol.JsonMessage;
import java.io.IOException;
import java.util.ArrayList;

public class DisplayChatService extends Service {
    // attributes
    private static DisplayChatService instance;
    private CommandType res;

    // constructors
    private DisplayChatService() throws IOException {
        super();
    }

    // singleton --> only one instance can exist at a time
    public static DisplayChatService getInstance() throws IOException {
        if (instance == null) {
            instance = new DisplayChatService();
        }
        return instance;
    }

    // methods
    public JsonMessage sendMessage(String message, int chatId) throws IOException, InterruptedException {
        super.sendReq(CommandType.SEND_MSG);
        super.sendJsonReq(new JsonMessage(chatId, message));
        res = super.catchCommandRes();

        JsonMessage msg = super.catchJsonRes(JsonMessage.class);

        if (super.isSuccess(res)) {
            eventListener.addMessage(msg);
            return msg;
        }
            
        System.out.println(Character.NEW_LINE.getValue() + "Error " + res.getDescription());
        return null;
    }

    public void removeMessage(JsonMessage message) throws IOException, InterruptedException {
        super.sendReq(CommandType.RM_MSG);
        super.sendJsonReq(message);
        res = super.catchCommandRes();

        // catch the message, just to clear the buffer
        super.catchJsonRes(JsonMessage.class);

        if (super.isSuccess(res)) {
            JsonChat chat = super.eventListener.getChatByChatId(message.getChatId());
            if (chat != null) {
                chat.getMessages().removeIf(m -> m.getId() == message.getId());
            }
        }
    }

    public void updateMessage(JsonMessage message) throws IOException, InterruptedException {
        super.sendReq(CommandType.UPD_MSG);
        super.sendJsonReq(message);
        res = super.catchCommandRes();

        // catch the message, just to clear the buffer
        super.catchJsonRes(JsonMessage.class);

        if (super.isSuccess(res)) {
            ArrayList<JsonMessage> chatMessages = super.eventListener.getChatMessages(message.getChatId());
            for (int i = 0; i < chatMessages.size(); i++) {
                if (chatMessages.get(i).getId() == message.getId()) {
                    chatMessages.get(i).setContent(message.getContent());
                    break;
                }
            }
        }
    }
}
