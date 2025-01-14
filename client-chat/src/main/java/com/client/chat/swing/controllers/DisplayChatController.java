package com.client.chat.swing.controllers;

import com.client.chat.swing.protocol.JsonChat;
import com.client.chat.swing.protocol.JsonMessage;
import com.client.chat.swing.services.DisplayChatService;
import com.client.chat.swing.ui.DisplayChatUI;
import com.client.chat.swing.ui.MessageContextMenu;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.MenuSelectionManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DisplayChatController {
    // attributes
    private static DisplayChatService service;
    private static DisplayChatUI ui;

    // flag to prevent multiple sends
    private static boolean isMessageSending = false;

    // constructors
    public DisplayChatController(DisplayChatUI ui) throws IOException {
        DisplayChatController.service = DisplayChatService.getInstance();
        DisplayChatController.ui = ui;

        DisplayChatController.handleAddEventListener();
    }

    @SuppressWarnings("unused")
    public static void handleAddEventListener() {
        // modify the send button listener to use the new method
        ui.addSendMessageListener(e -> {
            sendMessageIfNotSending();
        });

        // modify the key listener to use the new method
        ui.addKeySendMessageListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessageIfNotSending();
                }
            }
        });
    }

    // new method to prevent multiple sends
    private static void sendMessageIfNotSending() {
        JsonChat chat = ui.getChat();
        if (chat == null) return;

        // use synchronized block to ensure thread safety
        synchronized (DisplayChatController.class) {
            // Check if a message is already being sent
            if (isMessageSending) {
                return;
            }

            // set the flag to prevent concurrent sends
            isMessageSending = true;
        }

        try {
            String messageText = ui.getMessageText();

            // optionally, prevent sending empty messages
            if (messageText == null || messageText.trim().isEmpty()) {
                isMessageSending = false;
                return;
            }

            JsonMessage sentMessage = service.sendMessage(
                messageText,
                chat.getId()
            );

            if (sentMessage != null) {
                // add the last message to the UI
                DisplayChatUI.addMessage(sentMessage);
            }

            // clear the input field after sending
            ui.setMessageText("");
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException(ex);
        } finally {
            // always reset the flag, even if an exception occurs
            isMessageSending = false;
        }
    }

    public static void setupListeners(MessageContextMenu ctxMenu, JTextArea messageArea, JsonChat chat) {
        JsonMessage message = ctxMenu.getMessage();

        ctxMenu.setCopyMessageListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {                
                StringSelection stringSelection = new StringSelection(message.getContent());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);;
                System.out.println("Copied: " + stringSelection);

                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        });

        ctxMenu.setEditMessageListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                messageArea.setEditable(true);

                messageArea.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            try {
                                message.setContent(messageArea.getText());
                                messageArea.setEditable(false);

                                messageArea.removeKeyListener(this);

                                service.updateMessage(message);
                                System.out.println("Edited message with ID: " + message.getId() + ".\nWith content: " + message.getContent() + "\n");
                            } catch (IOException | InterruptedException ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    }
                });
            }
        });
        
        ctxMenu.setRemoveMessageListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    service.removeMessage(message);
                    DisplayChatUI.removeMessage(message);
                    
                    System.out.println("Removed message with ID: " + message.getId() + ".\nWith content: " + message.getContent() + "\n");
                    MenuSelectionManager.defaultManager().clearSelectedPath();
                } catch (IOException | InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
    }

    // getters and setters
    public DisplayChatService getService() {
        return DisplayChatController.service;
    }

    public DisplayChatUI getUI() {
        return DisplayChatController.ui;
    }

    public void setService(DisplayChatService service) {
        DisplayChatController.service = service;
    }

    public void setUI(DisplayChatUI ui) {
        DisplayChatController.ui = ui;
    }
}