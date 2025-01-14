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

public class RequestPasswordUI extends JDialog {
    // attributes
    private static final int DIALOG_WIDTH = 300;
    private static final int DIALOG_HEIGHT = 150;

    // ui elements
    private final JPasswordField passwordField;
    private final JButton confirmButton;
    private final JButton cancelButton;
    private final ProfileService profileService;
    private final CompletableFuture<Boolean> verificationResult;

    // constructors
    public RequestPasswordUI(Window owner, ProfileService profileService,
            CompletableFuture<Boolean> verificationResult) {
        super(owner, "Verify Password", ModalityType.APPLICATION_MODAL);
        this.profileService = profileService;
        this.verificationResult = verificationResult;

        initializeDialog(owner);

        // initialize components
        JPanel mainPanel = createMainPanel();
        JPanel passwordPanel = createPasswordPanel();
        passwordField = createPasswordField();
        JPanel buttonPanel = createButtonPanel();
        confirmButton = createConfirmButton();
        cancelButton = createCancelButton();

        /* 
         * create the UI.
         * add togheter
         * --> mainPanel, passwordPanel, buttonPanel
         */
        assembleUI(mainPanel, passwordPanel, buttonPanel);
        setupEventListeners();

        // on window closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                verificationResult.complete(false);
            }
        });

        setVisible(true);
    }

    // open dialog
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

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(CustomColors.BACKGROUND.getColor());

        JLabel passwordLabel = new JLabel("Enter your password:");
        passwordLabel.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        panel.add(passwordLabel, BorderLayout.NORTH);

        return panel;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();

        // if ENTER key is pressed then the fn is run
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handlePasswordConfirmation();
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

        /*  
         * if true and the button has a border, 
         * the border is painted with the background color.
         * otherwise the background color will
         * be set normally
         */
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

    private void assembleUI(JPanel mainPanel, JPanel passwordPanel, JPanel buttonPanel) {
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(passwordPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void handlePasswordConfirmation() {
        String password = new String(passwordField.getPassword());
        try {
            if (profileService.deleteUser(password)) {
                verificationResult.complete(true); // signal success
                dispose();
            } else {
                DialogUtils.showErrorDialog(this, "Error", "Incorrect password. Please try again.");
                passwordField.setText("");
                /* 
                 * not completing the future here,
                 * so that the user can try again
                 */
            }
        } catch (IOException | InterruptedException ex) {
            DialogUtils.showErrorDialog(this, "Error", "An error occurred: " + ex.getMessage());
            verificationResult.complete(false); // signaling failure
            dispose();
        } finally {
            // clean password field
            Arrays.fill(passwordField.getPassword(), '0');
        }
    }

    @SuppressWarnings("unused")
    private void setupEventListeners() {
        confirmButton.addActionListener(e -> handlePasswordConfirmation());
        cancelButton.addActionListener(e -> {
            // signaling failure, upon cancel request
            verificationResult.complete(false);
            dispose();
        });
    }
}
