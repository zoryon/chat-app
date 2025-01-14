package com.client.chat.swing;

import java.io.IOException;
import java.net.Socket;
import com.client.chat.swing.controllers.AuthController;
import com.client.chat.swing.ui.AuthUI;

/**
 *
 * @author gioele
 */
public class Main {
    private static Socket socket;

    public static void main(String[] args) {
        try {
            // LAB PC's IP: 10.22.9.10
            Main.socket = new Socket("localhost", 3000);

            /* 
             * create auth controller 
             * which will then provide the ui
             */
            new AuthController(new AuthUI());
        } catch (IOException e) {
            System.err.println("Error during connection with the server: " + e.getMessage());
        }
    }

    public static Socket getSocket() {
        return socket;
    }
}
