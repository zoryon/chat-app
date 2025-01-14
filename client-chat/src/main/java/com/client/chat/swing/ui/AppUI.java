package com.client.chat.swing.ui;

import com.client.chat.swing.controllers.DisplayChatController;
import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.protocol.JsonChat;
import com.client.chat.swing.ui.CustomFrame.CustomFrame;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class AppUI extends CustomFrame {
    // ui elements
    private JPanel mainContainer;
    private ChatListUI chatListPanel;
    private final DisplayChatController displayChatController;

    // constructors
    public AppUI() throws IOException {
        displayChatController = new DisplayChatController(new DisplayChatUI(null));

        // set the window to be 100% the size of the screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 500));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("Project ONE PIECE");
        setLocationRelativeTo(null);
        setResizable(true);

        setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(CustomColors.BACKGROUND.getColor());

        // create sidebar with chat list and profile
        chatListPanel = ChatListUI.getInstance();
        mainContainer.add(chatListPanel, BorderLayout.WEST);

        // create chat display area
        mainContainer.add(displayChatController.getUI(), BorderLayout.CENTER);

        // apply a compound border - combines the right separator with a rounded border
        chatListPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, CustomColors.BORDER.getColor()), // right border only
            BorderFactory.createEmptyBorder(10, 20, 10, 20) // inner padding
        ));

        setupResponsiveDesign();

        add(mainContainer);

        customizeAppearance();
    }

    private void setupResponsiveDesign() {
        // implement responsive layout adjustments
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // adjust component sizes based on window size
                int width = getWidth();
                int chatListWidth = Math.min(400, width / 3);
                chatListPanel.setPreferredSize(new Dimension(chatListWidth, getHeight()));

                mainContainer.revalidate();
            }
        });
    }

    private void customizeAppearance() {
        mainContainer.setBackground(new Color(240, 242, 245));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    // methods
    public void populateChatPreviews(ArrayList<JsonChat> chatList) {
        ChatListUI.getInstance().populateChatPreviews(chatList);
    }

    public void rebuildChatList() {
        // remove old chat list from main container
        mainContainer.remove(chatListPanel);
        
        // create new chat list instance
        chatListPanel = ChatListUI.getInstance();
        
        // reapply the border settings
        chatListPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, CustomColors.BORDER.getColor()),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // add the new chat list to the main container
        mainContainer.add(chatListPanel, BorderLayout.WEST);
        
        // ensure the responsive design is maintained
        int width = getWidth();
        int chatListWidth = Math.min(400, width / 3);
        chatListPanel.setPreferredSize(new Dimension(chatListWidth, getHeight()));
        
        // repopulate chat previews if needed
        ChatListUI.getInstance().populateChatPreviews(
            displayChatController.getUI().getChat() != null ? 
            new ArrayList<>(java.util.List.of(displayChatController.getUI().getChat())) : 
            new ArrayList<>()
        );
    }

    // getters
    public DisplayChatUI getDisplayChatUI() {
        return displayChatController.getUI();
    }

    public ChatListUI getChatListUI() {
        return chatListPanel;
    }
}
