package com.client.chat.swing.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

import com.client.chat.swing.auth.AuthManager;
import com.client.chat.swing.lib.SimpleDocumentListener;
import com.client.chat.swing.protocol.CustomColors;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CreateChatUI extends JDialog {
    // ui elements
    private JButton createChatButton;
    private JButton toggleModeButton;
    private JButton addUserFieldButton;
    private JPanel userFieldsPanel;
    private JScrollPane scrollPane;
    private JTextField chatNameField;
    private JPanel chatNamePanel;
    private JLabel titleLabel;
    private boolean isGroupMode = false;

    // constructors
    public CreateChatUI(Window owner) {
        super(owner, "Create Chats", Dialog.ModalityType.APPLICATION_MODAL);

        setMinimumSize(new Dimension(500, 400));
        setSize(500, 400);
        setLocationRelativeTo(owner);
        
        setBackground(CustomColors.BACKGROUND.getColor());
        setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        initializeComponents();
        validateButtonStates();
    }

    @SuppressWarnings("unused")
    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CustomColors.BACKGROUND.getColor());
        mainPanel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // title and mode toggle panel
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.setBackground(CustomColors.BACKGROUND.getColor());
        titlePanel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        
        titleLabel = new JLabel("New Direct Chat");
        titleLabel.setBackground(CustomColors.BACKGROUND.getColor());
        titleLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        toggleModeButton = new JButton("New Group");
        toggleModeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        toggleModeButton.addActionListener(e -> toggleMode());
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(toggleModeButton, BorderLayout.EAST);

        // chat name panel (initially invisible)
        chatNamePanel = new JPanel(new BorderLayout(5, 5));
        chatNamePanel.setBackground(CustomColors.BACKGROUND.getColor());
        chatNamePanel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        
        JLabel chatNameLabel = new JLabel("Group Name:");
        chatNameLabel.setBackground(CustomColors.BACKGROUND.getColor());
        chatNameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        chatNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        chatNameField = new JTextField();
        chatNameField.setBackground(CustomColors.BACKGROUND.getColor());
        chatNameField.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        chatNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        chatNameField.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        chatNameField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update(DocumentEvent e) {
                validateButtonStates();
            }
        });
        
        chatNamePanel.add(chatNameLabel, BorderLayout.WEST);
        chatNamePanel.add(chatNameField, BorderLayout.CENTER);
        chatNamePanel.setVisible(false);

        // username section
        JPanel usernameSection = new JPanel(new BorderLayout(10, 10));
        usernameSection.setBackground(CustomColors.BACKGROUND.getColor());
        usernameSection.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        
        JLabel instructionLabel = new JLabel("Enter username:");
        instructionLabel.setBackground(CustomColors.BACKGROUND.getColor());
        instructionLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        userFieldsPanel = new JPanel();
        userFieldsPanel.setBackground(CustomColors.BACKGROUND.getColor());
        userFieldsPanel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        userFieldsPanel.setLayout(new BoxLayout(userFieldsPanel, BoxLayout.Y_AXIS));
        addUsernameField();

        scrollPane = new JScrollPane(userFieldsPanel);
        scrollPane.setBackground(CustomColors.BACKGROUND.getColor());
        scrollPane.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        scrollPane.getViewport().setBackground(CustomColors.BACKGROUND.getColor());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        addUserFieldButton = new JButton("+");
        addUserFieldButton.setFont(new Font("Arial", Font.BOLD, 14));
        addUserFieldButton.setPreferredSize(new Dimension(40, 40));
        addUserFieldButton.addActionListener(e -> addUsernameField());
        addUserFieldButton.setVisible(false);

        usernameSection.add(instructionLabel, BorderLayout.NORTH);
        usernameSection.add(scrollPane, BorderLayout.CENTER);
        usernameSection.add(addUserFieldButton, BorderLayout.EAST);

        // create chat button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(CustomColors.BACKGROUND.getColor());
        buttonPanel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        
        createChatButton = new JButton("Create Direct Chat");
        createChatButton.setFont(new Font("Arial", Font.PLAIN, 14));
        buttonPanel.add(createChatButton);

        // layout creation
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(CustomColors.BACKGROUND.getColor());
        topPanel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(chatNamePanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(usernameSection, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void toggleMode() {
        isGroupMode = !isGroupMode;
        
        // update UI elements
        titleLabel.setText(isGroupMode ? "New Group Chat" : "New Direct Chat");
        toggleModeButton.setText(isGroupMode ? "New Direct Chat" : "New Group Chat");
        createChatButton.setText(isGroupMode ? "Create Group" : "Create Chat");
        chatNamePanel.setVisible(isGroupMode);
        addUserFieldButton.setVisible(isGroupMode);

        // clear existing fields
        chatNameField.setText("");
        userFieldsPanel.removeAll();
        addUsernameField();

        /*
         * revalidate --> make the changes visible
         * repaint --> make the changes effective
         */
        revalidate();
        repaint();
        validateButtonStates();
    }

    private void addUsernameField() {
        JTextField usernameField = new JTextField();
        usernameField.setBackground(CustomColors.BACKGROUND.getColor());
        usernameField.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JPanel fieldWrapper = new JPanel(new BorderLayout());
        fieldWrapper.setBackground(CustomColors.BACKGROUND.getColor());
        fieldWrapper.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        fieldWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        fieldWrapper.add(usernameField);
        fieldWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        usernameField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update(DocumentEvent e) {
                validateButtonStates();
            }
        });

        userFieldsPanel.add(fieldWrapper);
        userFieldsPanel.revalidate();
        userFieldsPanel.repaint();
    }

    public ArrayList<String> getUsernames() {
        ArrayList<String> usernames = new ArrayList<>();

        if (isGroupMode) {
            usernames.add(AuthManager.getInstance().getUsername());
        }

        for (Component comp : userFieldsPanel.getComponents()) {
            if (comp instanceof JPanel) {
                Component[] components = ((JPanel) comp).getComponents();
                for (Component c : components) {
                    if (c instanceof JTextField) {
                        String text = ((JTextField) c).getText().trim();
                        if (!text.isEmpty()) {
                            usernames.add(text);
                        }
                    }
                }
            }
        }
        return usernames;
    }

    public String getChatName() {
        return chatNameField.getText().trim();
    }

    private void validateButtonStates() {
        int filledFieldsCount = 0;
        for (Component comp : userFieldsPanel.getComponents()) {
            if (comp instanceof JPanel) {
                Component[] components = ((JPanel) comp).getComponents();
                for (Component c : components) {
                    if (c instanceof JTextField) {
                        String text = ((JTextField) c).getText().trim();
                        if (!text.isEmpty()) {
                            filledFieldsCount++;
                        }
                    }
                }
            }
        }
    
        if (isGroupMode) {
            String chatName = chatNameField.getText().trim();
            boolean chatNameFilled = !chatName.isEmpty();
            boolean anyFieldFilled = filledFieldsCount > 0;
            createChatButton.setEnabled(chatNameFilled && anyFieldFilled);
            createChatButton.setBackground(chatNameFilled && anyFieldFilled ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        } else {
            boolean directChatEnabled = filledFieldsCount == 1;
            createChatButton.setEnabled(directChatEnabled);
            createChatButton.setBackground(directChatEnabled ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        }
    }

    public void addCreateChatButtonListener(ActionListener listener) {
        createChatButton.addActionListener(listener);
    }

    // getters
    public boolean isGroupMode() {
        return isGroupMode;
    }
}