package com.client.chat.swing.controllers;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import com.client.chat.swing.lib.DialogUtils;
import com.client.chat.swing.protocol.JsonUser;
import com.client.chat.swing.services.AuthService;
import com.client.chat.swing.ui.AppUI;
import com.client.chat.swing.ui.AuthUI;

public class AuthController {
    // attributes
    private final AuthService service;
    private final AuthUI ui;

    // constructors
    public AuthController(AuthUI ui) throws IOException {
        this.service = AuthService.getInstance();
        this.ui = ui;

        this.handleAddEventListeners();
    }

    // methods
    @SuppressWarnings("unused")
    private void handleAddEventListeners() {
        // add listener for signing up
        ui.addSignUpListener(e -> {
            try {
                handleSignUp(ui.getUsername(), ui.getPassword());
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        // add listener for signing in
        ui.addSignInListener(e -> {
            try {
                handleSignIn(ui.getUsername(), ui.getPassword());
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        });

        this.ui.addWindowExitListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    service.handleExit();
                    ui.dispose();
                } catch (IOException ex) {
                    DialogUtils.showErrorDialog(ui, "Error Closing The App", ex.getMessage());
                }
            }
        });
    }

    private void handleSignUp(String username, String password) throws Exception {
        if (!isValidUserData(username, password)) return;

        if (service.signUp(new JsonUser(username, password))) {
            ui.dispose();
            new AppController(new AppUI());
        } else {
            DialogUtils.showErrorDialog(ui, "Auth Error", service.getRes().getDescription());
        }
    }

    private void handleSignIn(String username, String password) throws Exception {
        if (!isValidUserData(username, password)) return;

        if (service.signIn(new JsonUser(username, password))) {
            ui.dispose();
            new AppController(new AppUI());
        } else {
            DialogUtils.showErrorDialog(ui, "Auth Error", service.getRes().getDescription());
        }
    }

    private boolean isValidUserData(String username, String password) {
        // validation
        return !(username.isBlank() 
            || password.isBlank() 
            || username.isEmpty() 
            || password.isEmpty() 
            || username.length() < 2 
            || password.length() < 5);
    }
}
