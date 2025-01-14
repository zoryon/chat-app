package com.client.chat.swing.controllers;

import com.client.chat.swing.lib.DialogUtils;
import com.client.chat.swing.services.AppService;
import com.client.chat.swing.services.ProfileService;
import com.client.chat.swing.ui.AppUI;
import com.client.chat.swing.ui.AuthUI;
import com.client.chat.swing.ui.ChangeUsernameUI;
import com.client.chat.swing.ui.ChatListUI;
import com.client.chat.swing.ui.RequestPasswordUI;
import com.client.chat.swing.ui.UserProfileUI;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javax.swing.SwingUtilities;

public class AppController {
    // attributes
    private final AppService service;
    private final AppUI ui;

    // constructors
    public AppController(AppUI ui) throws IOException {
        this.service = AppService.getInstance();
        this.ui = ui;

        this.handleAddEventListeners();
        this.handlePopulateChatPreviews();
    }

    // methods
    private void handleAddEventListeners() {
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

        // set up the click handler
        ChatListUI chatList = ChatListUI.getInstance();
        chatList.setChatClickHandler(chat -> {
            Window parentWindow = SwingUtilities.getWindowAncestor(chatList);
            if (parentWindow instanceof AppUI appUI) {
                try {
                    if (service.handleConnectToChat(chat)) {
                        appUI.getDisplayChatUI().updateChat(chat);
                        DisplayChatController.handleAddEventListener();
                    }
                } catch (IOException | InterruptedException ex) {
                    DialogUtils.showErrorDialog(ui, "Error", ex.getMessage());
                }
            }
        });

        chatList.addProfileListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ui.setVisible(false);

                UserProfileUI panel = UserProfileUI.getInstance();
                panel.setVisible(true);

                try {
                    handleAddProfileListener(panel);
                } catch (IOException ex) {
                    DialogUtils.showErrorDialog(ui, "Error", ex.getMessage());
                }
            }
        });
    }

    private void handleAddProfileListener(UserProfileUI profileUI) throws IOException {
        ProfileService profileService = new ProfileService();

        profileUI.addWindowExitListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    service.handleExit();
                    ui.dispose();
                    profileUI.dispose();
                } catch (IOException ex) {
                    DialogUtils.showErrorDialog(ui, "Error Closing The App", ex.getMessage());
                }
            }
        });

        profileUI.addBackButtonListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                profileUI.dispose();
                ui.setVisible(true);
            }
        });

        profileUI.addLogoutListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleLogout(profileUI, profileService);
            }
        });

        profileUI.addDeleteUserListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleDeleteUser(profileUI, profileService);
            }
        });

        profileUI.addChangeUsernameListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleChangeUsername(profileUI, profileService);
            }
        });
    }

    private void handleLogout(UserProfileUI profileUI, ProfileService profileService) {
        try {
            profileService.logout();
            restartAllServices(profileUI);
        } catch (IOException | InterruptedException ex) {
            DialogUtils.showErrorDialog(ui, "Error", ex.getMessage());
        }
    }

    private void handleDeleteUser(UserProfileUI profileUI, ProfileService profileService) {
        try {
            // create a CompletableFuture to manage password verification result
            CompletableFuture<Boolean> passwordVerification = new CompletableFuture<>();
            
            // create dialog pasing the CompletableFuture
            new RequestPasswordUI(ui, profileService, passwordVerification);
            
            // wait for result
            passwordVerification.thenAccept(verified -> {
                if (verified) {
                    try {
                        restartAllServices(profileUI);
                    } catch (IOException ex) {
                        DialogUtils.showErrorDialog(profileUI, "Restart Error", ex.getMessage());
                    }
                }
            });
        } catch (Exception ex) {
            DialogUtils.showErrorDialog(profileUI, "Delete User Error", ex.getMessage());
        }
    }

    private void handleChangeUsername(UserProfileUI profileUI, ProfileService profileService) {
        try {
            CompletableFuture<Boolean> changeResult = new CompletableFuture<>();
            
            new ChangeUsernameUI(ui, profileService, changeResult);
            
            changeResult.thenAccept(changed -> {
                if (changed) {
                    refreshApp(profileUI);
                }
            });
        } catch (Exception ex) {
            DialogUtils.showErrorDialog(profileUI, "Change Username Error", ex.getMessage());
        }
    }

    private void restartAllServices(UserProfileUI profileUI) throws IOException {
        UserProfileUI.resetInstance();
        profileUI.dispose();

        ChatListUI.resetInstance();
        ui.dispose();

        new AuthController(new AuthUI());
    }

    private void refreshApp(UserProfileUI profileUI) {
        UserProfileUI.resetInstance();
        profileUI.dispose();

        ChatListUI.resetInstance();
        
        ui.rebuildChatList();
        
        ChatListUI chatList = ChatListUI.getInstance();
        chatList.addProfileListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ui.setVisible(false);

                UserProfileUI panel = UserProfileUI.getInstance();
                panel.setVisible(true);

                try {
                    handleAddProfileListener(panel);
                } catch (IOException ex) {
                    DialogUtils.showErrorDialog(ui, "Error", ex.getMessage());
                }
            }
        });

        ui.revalidate();
        ui.repaint();

        ui.setVisible(true);
    }

    private void handlePopulateChatPreviews() {
        this.ui.populateChatPreviews(service.getEventListener().getChatList());
    }
}
