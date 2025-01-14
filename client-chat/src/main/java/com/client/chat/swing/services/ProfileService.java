package com.client.chat.swing.services;

import java.io.IOException;
import com.client.chat.swing.auth.AuthManager;
import com.client.chat.swing.protocol.CommandType;
import com.client.chat.swing.protocol.JsonUser;

public class ProfileService extends Service {
    // constructors
    public ProfileService() throws IOException {
        super();
    }

    // methods
    public boolean logout() throws IOException, InterruptedException {
        super.sendReq(CommandType.LOGOUT);
        super.res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            AuthManager.getInstance().logout();
            return true;
        } 
            
        System.out.println(res.getDescription());
        return false;
    }

    public boolean deleteUser(String password) throws IOException, InterruptedException {
        super.sendReq(CommandType.DEL_USER);
        super.sendJsonReq(new JsonUser(AuthManager.getInstance().getUsername(), password));
        super.res = super.catchCommandRes();

        if (super.isSuccess(res)) {
            AuthManager.getInstance().logout();
            return true;
        }

        System.out.println("Error: " + res.getDescription());
        return false;
    }


    public boolean changeUsername(String newUsername, String password) throws IOException, InterruptedException {
        super.sendReq(CommandType.UPD_NAME);
        super.sendJsonReq(new JsonUser(newUsername, password));
        super.res = super.catchCommandRes();
    
        if (super.isSuccess(res)) {
            AuthManager.getInstance().authenticate(newUsername);
            return true;
        } 
    
        System.out.println(res.getDescription());
        return false;
    }
}
