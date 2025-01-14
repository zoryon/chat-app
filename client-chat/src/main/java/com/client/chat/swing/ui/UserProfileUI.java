package com.client.chat.swing.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import com.client.chat.swing.auth.AuthManager;
import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.ui.CustomFrame.CustomFrame;
import java.awt.*;
import java.awt.event.MouseAdapter;

public class UserProfileUI extends CustomFrame {
    // attributes
    private static UserProfileUI instance;
    private final String username;

    // ui elements
    private JButton logoutButton;
    private JButton deleteUserButton;
    private JButton changeUsernameButton;
    private JButton backButton;
    
    // constructors
    private UserProfileUI() {
        super(false);

        username = AuthManager.getInstance().getUsername();
        
        setBackground(CustomColors.BACKGROUND.getColor());
        setSize(380, 550);
        setLocationRelativeTo(null);

        initComponents();
    }

    // singleton --> only one instance can exist at a time
    public static UserProfileUI getInstance() {
        if (instance == null) {
            instance = new UserProfileUI();
        }
        return instance;
    }

    // reset the singleton
    public static void resetInstance() {
        instance = null;
    }

    private void initComponents() {
        // main panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(CustomColors.BACKGROUND.getColor());

        /* 
         * back button panel.
         * this will set the AppUI to visible
         * and dispose of this ui
         */ 
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBackground(CustomColors.BACKGROUND.getColor());
        backButton = createStyledButton("←", CustomColors.BORDER.getColor(), 50, 30);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backButtonPanel.add(backButton);

        // username panel
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        usernamePanel.setBackground(CustomColors.BACKGROUND.getColor());

        // username label
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        usernameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        usernamePanel.add(usernameLabel);

        /* 
         * change username button.
         * this will ask for the new username
         * and password.
         * then proceding to change username
         * in the appropriate controller
         */ 
        changeUsernameButton = createStyledButton("Edit Username", CustomColors.PRIMARY.getColor());
        changeUsernameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeUsernameButton.setMaximumSize(new Dimension(200, 40));

        // logout button
        logoutButton = createStyledButton("Logout", CustomColors.PRIMARY.getColor());
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 40));

        // delete button with danger color
        deleteUserButton = createStyledButton("Delete Account", CustomColors.DANGER.getColor());
        deleteUserButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteUserButton.setMaximumSize(new Dimension(200, 40));
        
        // adding components with spacing
        panel.add(backButtonPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(usernamePanel);
        panel.add(Box.createVerticalStrut(40));
        panel.add(changeUsernameButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(logoutButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(deleteUserButton);
        panel.add(Box.createVerticalStrut(20));

        add(panel);
    }

    private JButton createStyledButton(String text, Color mainColor) {
        // create a normal button
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        button.setBackground(mainColor);
        button.setFocusPainted(false);

        /*  
         * if true and the button has a border, 
         * the border is painted with the background color.
         * otherwise the background color will
         * be set normally
         */
        button.setBorderPainted(false);
        
        button.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(mainColor, 1, true),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        
        // make the button size more substantial
        button.setPreferredSize(new Dimension(150, 40));
        
        return button;
    }

    /**
     * 
     * @overload createStyledButton
     * with signature (String text, Color mainColor)
     */
    private JButton createStyledButton(String text, Color mainColor, int width, int height) {
        // create a normal button
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        button.setBackground(mainColor);
        button.setFocusPainted(false);

        /*  
         * if true and the button has a border, 
         * the border is painted with the background color.
         * otherwise the background color will
         * be set normally
         */
        button.setBorderPainted(false);
        
        button.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(mainColor, 1, true),
            BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        
        // make the button of the passed size
        button.setPreferredSize(new Dimension(width, height));
        
        return button;
    }

    // event listeners
    public void addLogoutListener(MouseAdapter listener) {
        logoutButton.addMouseListener(listener);
    }

    public void addDeleteUserListener(MouseAdapter listener) {
        deleteUserButton.addMouseListener(listener);
    }

    public void addChangeUsernameListener(MouseAdapter listener) {
        changeUsernameButton.addMouseListener(listener);
    }

    public void addBackButtonListener(MouseAdapter listener) {
        backButton.addMouseListener(listener);
    }
}