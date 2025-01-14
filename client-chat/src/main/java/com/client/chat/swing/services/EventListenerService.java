package com.client.chat.swing.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.client.chat.swing.protocol.CommandType;
import com.client.chat.swing.protocol.JsonChat;
import com.client.chat.swing.protocol.JsonMessage;
import com.client.chat.swing.ui.ChatListUI;
import com.client.chat.swing.ui.DisplayChatUI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EventListenerService extends Thread {
    // attributes
    private static EventListenerService instance;
    private final BufferedReader in;
    private final BlockingQueue<CommandType> commandQueue;
    private final BlockingQueue<String> dataQueue;
    private ArrayList<JsonChat> chatList;

    // constructors
    private EventListenerService(BufferedReader in) {
        this.in = in;
        this.commandQueue = new LinkedBlockingQueue<>();
        this.dataQueue = new LinkedBlockingQueue<>();
        this.chatList = new ArrayList<>();
    }

    // singleton --> only one instance can exist at a time
    public static EventListenerService getInstance(BufferedReader in) {
        if (instance == null) {
            instance = new EventListenerService(in);
        }
        return instance;
    }

    // main body --> run fn when thread starts
    @Override
    public void run() {
        while (Service.isRunning) {
            try {
                // get the command type
                String commandStr = in.readLine();
                if (commandStr == null || commandStr.equals("null")) continue;
                
                CommandType command = CommandType.valueOf(commandStr);
                commandQueue.put(command);

                switch (command) {
                    case OK:
                        // get the general data and push it into the blocking queue
                        String data = in.readLine();
                        if (!data.equals("null")) dataQueue.put(data);
                        break;
                    case INIT:
                        // catch the initial array when INIT CommandType is sent
                        chatList = new Gson().fromJson(in.readLine(), new TypeToken<ArrayList<JsonChat>>() {}.getType());
                        commandQueue.take();
                        break;
                    case SEND_MSG:
                        // add new msg to the array
                        JsonMessage msg = new Gson().fromJson(in.readLine(), JsonMessage.class);
                        chatList.forEach(chat -> {
                            if (chat.getId() == msg.getChatId()) {
                                chat.addMessage(msg);
                                DisplayChatUI.addMessage(msg);
                            }
                        });
                        
                        commandQueue.take();
                        break;
                    case RM_MSG:
                        JsonMessage rmvdMsg = new Gson().fromJson(in.readLine(), JsonMessage.class);
                        
                        for (JsonChat chat : chatList) {
                            if (chat.getId() == rmvdMsg.getChatId()) {
                                ArrayList<JsonMessage> chatMessages = chat.getMessages();
                                for (int i = 0; i < chatMessages.size(); i++) {
                                    if (chatMessages.get(i).getId() == rmvdMsg.getId()) {
                                        chat.getMessages().remove(i);
                                        DisplayChatUI.removeMessage(rmvdMsg);
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                        commandQueue.take();
                        break;
                    case UPD_MSG:
                        JsonMessage updtdMsg = new Gson().fromJson(in.readLine(), JsonMessage.class);
                            
                        for (JsonChat chat : chatList) {
                            if (chat.getId() == updtdMsg.getChatId()) {
                                ArrayList<JsonMessage> chatMessages = chat.getMessages();
                                for (int i = 0; i < chatMessages.size(); i++) {
                                    if (chatMessages.get(i).getId() == updtdMsg.getId()) {
                                        chat.getMessages().get(i).setContent(updtdMsg.getContent());
                                        DisplayChatUI.updateMessages(chat, chat.getMessages());
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                        commandQueue.take();
                        break;
                    case NEW_CHAT:
                        chatList.add(new Gson().fromJson(in.readLine(), JsonChat.class));
                        commandQueue.take();

                        ChatListUI.getInstance().populateChatPreviews(chatList);
                        break;
                    case NEW_GROUP:
                        chatList.add(new Gson().fromJson(in.readLine(), JsonChat.class));
                        commandQueue.take();

                        ChatListUI.getInstance().populateChatPreviews(chatList);
                        break;
                    default:
                }
            } catch (IOException | InterruptedException ex) { }
        }
    }

    // public methods
    public void printUserChatList() {
        for (JsonChat chat : chatList) {
            System.out.println("> " + chat.getChatName() + "#" + chat.getId());
        }

        if (chatList.isEmpty()) {
            System.out.println("> Nessuna chat presente..");
        }
    }

    public BlockingQueue<CommandType> getCommandQueue() {
        return commandQueue;
    }

    public BlockingQueue<String> getDataQueue() {
        return dataQueue;
    }

    public ArrayList<JsonChat> getChatList() { return new ArrayList<>(chatList); }

    // public chat-related methods
    public ArrayList<JsonMessage> getChatMessages(int chatId) {
        for (JsonChat chat : chatList) {
            if (chat.getId() == chatId) return chat.getMessages();
        }

        return new ArrayList<JsonMessage>();
    }

    public JsonChat getChatByChatId(int chatId) {
        for (JsonChat chat : chatList) {
            if (chat.getId() == chatId) {
                return chat;
            }
        }

        return null;
    }

    public boolean addChat(JsonChat newChat) {
        return chatList.add(newChat);
    }

    public boolean addMessage(JsonMessage msg) {
        return getChatMessages(msg.getChatId()).add(msg);
    }
}
