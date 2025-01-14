package com.client.chat.swing.ui;

import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.services.ProfileService;
import com.client.chat.swing.lib.DialogUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ChangeUsernameUI extends JDialog {
    // attributes
    private static final int DIALOG_WIDTH = 350;
    private static final int DIALOG_HEIGHT = 200;

    // ui elements
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton confirmButton;
    private final JButton cancelButton;
    private final ProfileService profileService;
    private final CompletableFuture<Boolean> changeResult;

    public ChangeUsernameUI(Window owner, ProfileService profileService, CompletableFuture<Boolean> changeResult) {
        super(owner, "Change Username", ModalityType.APPLICATION_MODAL);
        this.profileService = profileService;
        this.changeResult = changeResult;

        // initialize fields first
        usernameField = createUsernameField();
        passwordField = createPasswordField();
        confirmButton = createConfirmButton();
        cancelButton = createCancelButton();

        initializeDialog(owner);

        // initialize panels
        JPanel mainPanel = createMainPanel();
        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        // create UI
        assembleUI(mainPanel, inputPanel, buttonPanel);

        setupEventListeners();

        // on close the fn is run
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                changeResult.complete(false);
            }
        });

        setVisible(true);
    }

    private void initializeDialog(Window owner) {
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(CustomColors.BACKGROUND.getColor());
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBackground(CustomColors.BACKGROUND.getColor());

        JLabel usernameLabel = new JLabel("New Username:");
        usernameLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        JLabel passwordLabel = new JLabel("Enter your password:");
        passwordLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);

        return panel;
    }

    private JTextField createUsernameField() {
        JTextField field = new JTextField();
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
        });
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleUsernameChange();
                }
            }
        });
        return field;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(CustomColors.BACKGROUND.getColor());
        return panel;
    }

    private JButton createConfirmButton() {
        JButton button = new JButton("Confirm");
        button.setBorderPainted(false);
        button.setBackground(CustomColors.DANGER.getColor());
        button.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        return button;
    }

    private JButton createCancelButton() {
        JButton button = new JButton("Cancel");
        button.setBorderPainted(false);
        button.setBackground(CustomColors.PRIMARY.getColor());
        button.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        return button;
    }

    private void assembleUI(JPanel mainPanel, JPanel inputPanel, JPanel buttonPanel) {
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handleUsernameChange() {
        String newUsername = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (newUsername.isEmpty() || newUsername.length() < 2) {
            DialogUtils.showErrorDialog(this, "Error", "Username must be at least 2 character long.");
            return;
        }

        try {
            if (profileService.changeUsername(newUsername, password)) {
                changeResult.complete(true);
                dispose();
            } else {
                DialogUtils.showErrorDialog(this, "Error", "Failed to change username. Please try again.");
                usernameField.setText("");
                passwordField.setText("");
                usernameField.requestFocus();
            }
        } catch (IOException | InterruptedException ex) {
            DialogUtils.showErrorDialog(this, "Error", "An error occurred: " + ex.getMessage());
            changeResult.complete(false);
            dispose();
        } finally {
            Arrays.fill(passwordField.getPassword(), '0');
        }
    }

    @SuppressWarnings("unused")
    private void setupEventListeners() {
        confirmButton.addActionListener(e -> handleUsernameChange());
        cancelButton.addActionListener(e -> {
            changeResult.complete(false);
            dispose();
        });
    }
}
