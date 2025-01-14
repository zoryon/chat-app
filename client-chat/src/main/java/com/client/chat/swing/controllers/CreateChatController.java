package com.client.chat.swing.controllers;

import com.client.chat.swing.lib.DialogUtils;
import com.client.chat.swing.services.CreateChatService;
import com.client.chat.swing.ui.ChatListUI;
import com.client.chat.swing.ui.CreateChatUI;
import java.io.IOException;
import java.util.ArrayList;

public class CreateChatController {
    // attributes
    CreateChatService service;
    CreateChatUI ui;
    ChatListUI chatListPanelUI;

    // constructors
    public CreateChatController(CreateChatUI ui, ChatListUI chatListPanelUI) throws IOException {
        this.service = CreateChatService.getInstance();
        this.ui = ui;
        this.chatListPanelUI = chatListPanelUI;

        initListeners();
    }

    // methods
    @SuppressWarnings("unused")
    public void initListeners() {
        ui.addCreateChatButtonListener(e -> {
            try {
                ArrayList<String> users = ui.getUsernames();
                if (ui.isGroupMode()) {
                    String groupName = ui.getChatName();
                    handleCreateNewGroup(groupName, users);
                } else {
                    handleCreateNewChat(users.get(0));
                }
            } catch (IOException | InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }

    public void handleCreateNewChat(String username) throws IOException, InterruptedException {
        if (service.createPrivateDirectChat(username)) {
            ui.dispose();
            chatListPanelUI.populateChatPreviews(service.getEventListener().getChatList());
        } else {
            DialogUtils.showErrorDialog(ui, "Create Chat Error", service.getRes().getDescription());

        }
    }

    public void handleCreateNewGroup(String groupName, ArrayList<String> usernames) throws IOException, InterruptedException {
        if (service.createPrivateGroupChat(groupName, usernames)) {
            ui.dispose();
            chatListPanelUI.populateChatPreviews(service.getEventListener().getChatList());
        } else {
            DialogUtils.showErrorDialog(ui, "Create Group Error", service.getRes().getDescription());
        }
    }
}
