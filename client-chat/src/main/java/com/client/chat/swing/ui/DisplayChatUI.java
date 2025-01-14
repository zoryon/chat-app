package com.client.chat.swing.ui;

import com.client.chat.swing.auth.AuthManager;
import com.client.chat.swing.controllers.DisplayChatController;
import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.protocol.JsonChat;
import com.client.chat.swing.protocol.JsonMessage;
import com.client.chat.swing.lib.Character;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;

public class DisplayChatUI extends JPanel {
    // attributes
    private static JsonChat chat;

    // ui elements
    private JPanel chatHeaderPanel;
    private static JScrollPane chatMessageScrollPane;
    private static JPanel chatMessageContainer;
    private JPanel messageInputPanel;
    private JTextField messageInputField;
    private JButton sendButton;

    // constructors
    public DisplayChatUI(JsonChat chat) {
        DisplayChatUI.chat = chat;

        // Set up panel layout
        setLayout(new BorderLayout());
        setBackground(CustomColors.BACKGROUND.getColor());
        setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        // Initialize components
        initializeComponents();

        addResizeListener();

        // Load initial chat or show blank display
        if (chat == null) {
            showBlankDisplay();
        } else {
            updateChat(chat);
            scrollToBottom();
        }
    }

    private void initializeComponents() {
        createChatHeader();
        createChatMessageArea();
        createMessageInputPanel();
    }

    // show blank display in case no chat is selected
    private void showBlankDisplay() {
        removeAll();
        JLabel blankLabel = new JLabel("No chat selected", SwingConstants.CENTER);
        blankLabel.setBackground(CustomColors.BACKGROUND.getColor());
        blankLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        blankLabel.setFont(new Font("Arial", Font.ITALIC, 16));

        add(blankLabel, BorderLayout.CENTER);

        /*
         * revalidate --> make the changes visible
         * repaint --> make the changes effective
         */
        revalidate();
        repaint();
    }

    private void createChatHeader() {
        chatHeaderPanel = new JPanel(new BorderLayout());
        chatHeaderPanel.setPreferredSize(new Dimension(0, 70));
        chatHeaderPanel.setBackground(CustomColors.BACKGROUND.getColor());
        chatHeaderPanel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        // borders
        Border matteBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, CustomColors.BORDER.getColor());
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 15, 5, 15);
        chatHeaderPanel.setBorder(BorderFactory.createCompoundBorder(matteBorder, emptyBorder));

        // contact name
        JLabel chatNameLabel = new JLabel(chat != null ? calculateContactName(chat.getChatName()) : "Chat");
        chatNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        chatNameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        JPanel contactInfoPanel = new JPanel(new BorderLayout());
        contactInfoPanel.add(chatNameLabel, BorderLayout.CENTER);
        contactInfoPanel.setOpaque(false);

        chatHeaderPanel.add(contactInfoPanel, BorderLayout.CENTER);
        add(chatHeaderPanel, BorderLayout.NORTH);
    }

    private void createChatMessageArea() {
        chatMessageContainer = new JPanel();
        chatMessageContainer.setLayout(new BoxLayout(chatMessageContainer, BoxLayout.Y_AXIS));
        chatMessageContainer.setBackground(CustomColors.SECONDARY.getColor());
        chatMessageContainer.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        chatMessageScrollPane = new JScrollPane(chatMessageContainer);
        chatMessageScrollPane.setBackground(CustomColors.SECONDARY.getColor());
        chatMessageScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatMessageScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatMessageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(chatMessageScrollPane, BorderLayout.CENTER);
    }

    private static JPanel createMessageBubble(JsonChat chat, JsonMessage message, boolean isSentByCurrentUser) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);

        // add sender's name for each received message
        if (!isSentByCurrentUser) {
            JLabel senderNameLabel = new JLabel(message.getSenderName());
            senderNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
            senderNameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

            // create a panel for the name to control its alignment
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
            namePanel.setOpaque(false);
            namePanel.add(senderNameLabel);
            messagePanel.add(namePanel);
        }

        // create message panel --> "bubble"
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new FlowLayout(isSentByCurrentUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        bubblePanel.setOpaque(false);

        // use JTextArea for wrapping text
        JTextArea messageArea = new JTextArea(message.getContent());

        // add a property id to delete when needed
        messageArea.putClientProperty("messageId", message.getId());

        messageArea.setLineWrap(true); // adding wrapping on lines
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false); // text is not editablee
        messageArea.setBackground(isSentByCurrentUser
                ? CustomColors.TEXT_BUBBLE_BACKGROUND_OWNER.getColor()
                : CustomColors.TEXT_BUBBLE_BACKGROUND_OTHERS.getColor());
        messageArea.setFont(new Font("Arial", Font.PLAIN, 16));
        messageArea.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageArea.setMaximumSize(new Dimension(
                chatMessageScrollPane.getViewport().getWidth() / 2,
                Integer.MAX_VALUE));

        if (isSentByCurrentUser) {
            MessageContextMenu ctxMenu = new MessageContextMenu(message);
            messageArea.setComponentPopupMenu(ctxMenu);
            DisplayChatController.setupListeners(ctxMenu, messageArea, chat);
        }

        bubblePanel.add(messageArea);
        messagePanel.add(bubblePanel);

        return messagePanel;
    }

    public static void updateMessages(JsonChat chat, ArrayList<JsonMessage> newMessages) {
        if (chatMessageContainer == null || newMessages == null) {
            return;
        }

        chatMessageContainer.removeAll(); // remove all existing messages

        for (JsonMessage message : newMessages) {
            boolean isSentByCurrentUser = message.getSenderName().equals(AuthManager.getInstance().getUsername());
            JPanel messageRow = createMessageBubble(chat, message, isSentByCurrentUser);
            chatMessageContainer.add(messageRow);
        }

        // add distance between every message
        for (int i = 0; i < chatMessageContainer.getComponentCount(); i++) {
            Component row = chatMessageContainer.getComponent(i);
            if (row instanceof JPanel) {
                ((JPanel) row).setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            }
        }

        /*
         * revalidate --> make the changes visible
         * repaint --> make the changes effective
         */
        chatMessageContainer.revalidate();
        chatMessageContainer.repaint();

        // automatic scroll
        scrollToBottom();
    }

    public static void removeMessage(JsonMessage message) {
        if (chat.getId() != message.getChatId())
            return;
        if (chatMessageContainer == null || message == null)
            return;

        // iterate over the components in chatMessageContainer
        Component[] components = chatMessageContainer.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel messagePanel = (JPanel) component;

                // look for the JTextArea containing the message content
                for (Component innerComponent : messagePanel.getComponents()) {
                    if (innerComponent instanceof JPanel) {
                        JPanel bubblePanel = (JPanel) innerComponent;

                        for (Component bubbleComponent : bubblePanel.getComponents()) {
                            if (bubbleComponent instanceof JTextArea) {
                                JTextArea messageArea = (JTextArea) bubbleComponent;

                                // check if this message matches the one to be removed
                                Integer messageId = (Integer) messageArea.getClientProperty("messageId");
                                if (messageId == message.getId()) {
                                    chatMessageContainer.remove(messagePanel); // remove the entire message panel

                                    /*
                                     * revalidate --> make the changes visible
                                     * repaint --> make the changes effective
                                     */
                                    chatMessageContainer.revalidate();
                                    chatMessageContainer.repaint();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void addMessage(JsonMessage newMessage) {
        if (DisplayChatUI.chat == null)
            return;

        /* 
         * if the new message's chat does not match the 
         * chat which is being displayed currently,
         * in the display chat panel, then this message
         * does not need to be displayed right away
         */
        if (DisplayChatUI.chat.getId() != newMessage.getChatId())
            return;

        if (chatMessageContainer == null || newMessage == null)
            return;

        boolean isSentByCurrentUser = newMessage.getSenderName().equals(AuthManager.getInstance().getUsername());
        JPanel messageBubble = createMessageBubble(chat, newMessage, isSentByCurrentUser);

        chatMessageContainer.add(messageBubble);
        chatMessageContainer.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * revalidate --> make the changes visible
         * repaint --> make the changes effective
         */
        chatMessageContainer.revalidate();
        chatMessageContainer.repaint();

        scrollToBottom();
    }

    private void createMessageInputPanel() {
        messageInputPanel = new JPanel(new BorderLayout());
        messageInputPanel.setPreferredSize(new Dimension(0, 50));
        messageInputPanel.setBackground(CustomColors.BACKGROUND.getColor());
        messageInputPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, CustomColors.BORDER.getColor()));

        messageInputField = new JTextField();
        messageInputField.setFont(new Font("Arial", Font.PLAIN, 16));
        messageInputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageInputField.setBackground(CustomColors.BACKGROUND.getColor());
        messageInputField.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        messageInputField.setText("...");

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0, 168, 132));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        messageInputPanel.add(messageInputField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);

        add(messageInputPanel, BorderLayout.SOUTH);
    }

    public static void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatMessageScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());

            int maximum = vertical.getMaximum() - vertical.getVisibleAmount();
            vertical.setValue(maximum);
        });
    }

    public void updateChat(JsonChat newChat) {
        if (DisplayChatUI.chat == newChat)
            return;

        DisplayChatUI.chat = newChat;

        if (newChat == null) {
            showBlankDisplay();
            return;
        }

        removeAll();

        // update header
        createChatHeader();

        // update messages
        updateMessages(chat, chat.getMessages());

        // add scroll pane and input panel
        add(chatMessageScrollPane, BorderLayout.CENTER);
        add(messageInputPanel, BorderLayout.SOUTH);

        /*
         * revalidate --> make the changes visible
         * repaint --> make the changes effective
         */
        revalidate();
        repaint();
    }

    private void updateMessageBubbles() {
        /*
         * recalculate the message width based on the current width of the container
         * recalculate each message to fit the new width
         */
        for (Component component : chatMessageContainer.getComponents()) {
            if (component instanceof JPanel) {
                JPanel rowPanel = (JPanel) component;
                for (Component bubbleComponent : rowPanel.getComponents()) {
                    if (bubbleComponent instanceof JPanel) {
                        for (Component textComponent : ((JPanel) bubbleComponent).getComponents()) {
                            if (textComponent instanceof JTextArea) {
                                JTextArea messageArea = (JTextArea) textComponent;

                                // adjust size based on the current scroll pane width
                                messageArea
                                        .setPreferredSize(new Dimension((int) messageArea.getPreferredSize().getWidth(),
                                                (int) messageArea.getPreferredSize().getHeight()));
                                messageArea.setMaximumSize(new Dimension(
                                        chatMessageScrollPane.getViewport().getWidth() / 2, Integer.MAX_VALUE));
                                messageArea.revalidate();
                            }
                        }
                    }
                }
            }
        }

        /*
         * revalidate --> make the changes visible
         * repaint --> make the changes effective
         */
        chatMessageContainer.revalidate();
        chatMessageContainer.repaint();
    }

    public String calculateContactName(String chatName) {
        if (chatName == null || chatName.isEmpty()) {
            return "Chat";
        }

        if (!chatName.contains(Character.HYPHEN.getValue())) {
            return chatName;
        }

        try {
            String[] parts = chatName.split(Character.HYPHEN.getValue());

            // ensure the split produces at least two parts
            if (parts.length < 2) {
                return chatName;
            }

            return parts[1].equals(AuthManager.getInstance().getUsername()) ? parts[0] : parts[1];
        } catch (Exception ex) {
            System.err.println("Error while parsing chat name: " + ex.getMessage());
            return chatName;
        }
    }

    // listeners
    private void addResizeListener() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // recalculate messages sizes everytime the windows is resized
                updateMessageBubbles();
            }
        });
    }

    public void addSendMessageListener(ActionListener listener) {
        for (var al : sendButton.getActionListeners()) {
            sendButton.removeActionListener(al);
        }
        sendButton.addActionListener(listener);
    }

    public void addKeySendMessageListener(KeyAdapter listener) {
        messageInputField.addKeyListener(listener);
    }

    // getters and setters
    public String getMessageText() {
        return messageInputField.getText();
    }

    public void setMessageText(String newText) {
        messageInputField.setText(newText);
    }

    public JsonChat getChat() {
        return chat;
    }
}
