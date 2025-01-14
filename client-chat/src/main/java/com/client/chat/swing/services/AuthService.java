package com.client.chat.swing.services;

import java.io.IOException;
import com.client.chat.swing.auth.AuthManager;
import com.client.chat.swing.protocol.CommandType;
import com.client.chat.swing.protocol.JsonUser;

public class AuthService extends Service {
    // attributes
    private static AuthService instance;
    private final AuthManager authManager;

    // constructors
    private AuthService() throws IOException {
        super();
        this.authManager = AuthManager.getInstance(); // equal to "new AuthManager()"

        // start thread
        super.eventListener.start();
    }

    // singleton --> only one instance can exist at a time
    public static AuthService getInstance() throws IOException {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean signUp(JsonUser user) throws IOException, InterruptedException {
        sendAuthReq(CommandType.NEW_USER, user);
        super.res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            authManager.authenticate(user.getUsername());
            return true;
        }

        System.out.println("Sign up error: " + res.getDescription());
        return false;
    }

    public boolean signIn(JsonUser user) throws IOException, InterruptedException {
        sendAuthReq(CommandType.OLD_USER, user);
        super.res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            authManager.authenticate(user.getUsername());
            return true;
        }

        System.out.println("Sign in error: " + res.getDescription());
        return false;
    }

    private void sendAuthReq(CommandType command, JsonUser user) throws IOException {
        // send the command to let the server know what to expect
        super.sendReq(command);

        // send the user in json format
        sendJsonReq(user);
    }

    // getters
    public CommandType getRes() {
        return res;
    }
}
