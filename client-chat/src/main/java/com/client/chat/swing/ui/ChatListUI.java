package com.client.chat.swing.ui;

import com.client.chat.swing.auth.AuthManager;
import com.client.chat.swing.controllers.CreateChatController;
import com.client.chat.swing.lib.Character;
import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.protocol.JsonChat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class ChatListUI extends JPanel {
    // attributes
    private static ChatListUI instance;
    private Consumer<JsonChat> chatClickHandler;

    // ui elements
    private ArrayList<ChatPreview> chatPreviews;
    private JTextField searchChatField;
    private static JPanel chatPreviewContainer;
    private JPanel userInfoPanel;

    // constructors
    private ChatListUI() {
        chatPreviews = new ArrayList<>();

        // Set up the panel layout
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 800));

        // Initialize components
        initializeComponents();

        // Style the panel
        customizeAppearance();
    }

    // singleton --> only one instance can exist at a time
    public static ChatListUI getInstance() {
        if (instance == null) {
            instance = new ChatListUI();
        }
        return instance;
    }

    // reset the singleton
    public static void resetInstance() {
        instance = null;
    }

    @SuppressWarnings("unused")
    private void initializeComponents() {
        // create panel for title and new chat button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.setOpaque(false);

        // create title
        JLabel titleLabel = new JLabel("Chat List", SwingConstants.CENTER); // Centered title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // create chat title
        JLabel title = new JLabel("Chat");
        title.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        title.setFont(new Font("Arial", Font.BOLD, 20));

        // create "New Chat" button
        JButton newChatButton = new JButton("+");
        newChatButton.setFont(new Font("Arial", Font.BOLD, 14));
        newChatButton.setBackground(CustomColors.PRIMARY.getColor());
        newChatButton.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        newChatButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        newChatButton.setFocusPainted(false);
        newChatButton.addActionListener(e -> {
            // get the parent window
            Window parentWindow = SwingUtilities.getWindowAncestor(this);

            // Create the UI without showing it
            CreateChatUI createChatUI = new CreateChatUI(parentWindow);

            try {
                // initialize the controller and attach listeners
                new CreateChatController(createChatUI, this);

                // show the dialog after the controller is ready
                createChatUI.setVisible(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Failed to open New Chat dialog.", 
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        // add title and new chat button to the top panel
        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(newChatButton, BorderLayout.EAST);

        // create search panel for search field
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 10, 0, CustomColors.BACKGROUND.getColor()));

        // create search bar
        searchChatField = new JTextField("Search an existing chat..");
        searchChatField.setForeground(Color.GRAY);
        searchChatField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchChatField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        searchChatField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchChatField.getText().equals("Search an existing chat..")) {
                    searchChatField.setText("");
                    searchChatField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchChatField.getText().isEmpty()) {
                    searchChatField.setText("Search an existing chat..");
                    searchChatField.setForeground(Color.GRAY);
                }
            }
        });

        // add search field to search panel
        searchPanel.add(searchChatField, BorderLayout.CENTER);

        // create chat preview container
        chatPreviewContainer = new JPanel();
        chatPreviewContainer.setLayout(new BoxLayout(chatPreviewContainer, BoxLayout.Y_AXIS));
        chatPreviewContainer.setBackground(CustomColors.BACKGROUND.getColor());

        // scroll pane to contain chat list
        JScrollPane chatListScrollPane = new JScrollPane(chatPreviewContainer);
        chatListScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatListScrollPane.setBackground(CustomColors.BACKGROUND.getColor());
        chatListScrollPane.getViewport().setBackground(CustomColors.BACKGROUND.getColor());

        // combine search and chat list in a central panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(chatListScrollPane, BorderLayout.CENTER);

        // panel for the user information
        userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BorderLayout());
        userInfoPanel.setBackground(CustomColors.BACKGROUND.getColor());
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            
        userInfoPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                userInfoPanel.setBackground(CustomColors.HOVER.getColor());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                userInfoPanel.setBackground(CustomColors.BACKGROUND.getColor());
            }
        });

        JLabel usernameLabel = new JLabel(AuthManager.getInstance().getUsername());
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        usernameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        usernameLabel.setFont(new Font("Arial", Font.TRUETYPE_FONT, 15));

        userInfoPanel.add(usernameLabel, BorderLayout.WEST);

        // add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(userInfoPanel, BorderLayout.SOUTH);
    }

    public void populateChatPreviews(ArrayList<JsonChat> chatList) {
        if (chatPreviews != null) {
            chatPreviewContainer.removeAll(); // clear existing previews
            chatPreviews.clear();
        }

        for (JsonChat chat : chatList) {
            ChatPreview preview = new ChatPreview(chat, this.chatClickHandler);
            chatPreviews.add(preview);
            chatPreviewContainer.add(preview);
        }

        /*
         * revalidate --> make the changes visible
         * repaint --> make the changes effective
         */
        chatPreviewContainer.revalidate();
        chatPreviewContainer.repaint();
    }

    private void customizeAppearance() {
        setBackground(CustomColors.BACKGROUND.getColor());

        searchChatField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchChatField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    }

    // listeners
    public void addProfileListener(MouseAdapter listener) {
        userInfoPanel.addMouseListener(listener);
    }

    // inner class for chat preview
    public class ChatPreview extends JPanel {
        @SuppressWarnings("unused")
        private final JsonChat chat;
        @SuppressWarnings("unused")
        private Consumer<JsonChat> clickHandler;

        public ChatPreview(JsonChat chat, Consumer<JsonChat> clickHandler) {
            this.chat = chat;
            this.clickHandler = clickHandler;

            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            setOpaque(false);
            setBackground(CustomColors.BACKGROUND.getColor());

            // contact name
            JLabel nameLabel = new JLabel(chat != null ? calculateContactName(chat.getChatName()) : "Chat");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
            nameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

            // last message preview - truncated to 10 chars
            JLabel messagePreview;

            if (chat.getMessages().isEmpty()) {
                messagePreview = new JLabel("...");
            } else {
                String message = chat.getMessages().get(chat.getMessages().size() - 1).getContent();
                // truncate message to 10 characters and add ellipsis (...)
                message = message.length() > 30 ? message.substring(0, 30) + "..." : message;
                messagePreview = new JLabel(message);
            }

            messagePreview.setFont(new Font("Arial", Font.PLAIN, 12));
            messagePreview.setForeground(Color.GRAY);

            // layout components
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.add(nameLabel, BorderLayout.NORTH);
            centerPanel.add(messagePreview, BorderLayout.CENTER);
            centerPanel.setOpaque(false);

            add(centerPanel, BorderLayout.CENTER);

            // add hover and click effects
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(CustomColors.HOVER.getColor());
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(CustomColors.BACKGROUND.getColor());
                }

                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (clickHandler != null) {
                        clickHandler.accept(chat);
                    }
                }
            });
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());

            // draw rounded rectangle
            int arc = 20; // arc radius for rounded corners
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.dispose();
        }

        public void updateClickHandler(Consumer<JsonChat> newHandler) {
            this.clickHandler = newHandler;
        }
    }

    public void setChatClickHandler(Consumer<JsonChat> handler) {
        this.chatClickHandler = handler;
        // update existing chat previews with new handler
        if (chatPreviews != null) {
            for (ChatPreview preview : chatPreviews) {
                preview.updateClickHandler(handler);
            }
        }
    }
}