package com.client.chat.swing.services;

import com.client.chat.swing.Main;
import com.client.chat.swing.lib.Character;
import com.client.chat.swing.protocol.CommandType;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.Scanner;

public class Service {
    // attributes
    protected final Socket socket;
    protected final BufferedReader in;
    protected final DataOutputStream out;
    protected final Scanner keyboard;
    protected CommandType res;
    protected final Gson gson;
    protected static boolean isRunning = true;
    protected EventListenerService eventListener;

    // constructors
    public Service() throws IOException {
        this.socket = Main.getSocket();
        this.keyboard = new Scanner(System.in);
        this.out = new DataOutputStream(socket.getOutputStream());
        this.gson = new Gson();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        this.eventListener = EventListenerService.getInstance(in);
    }
    
    // protected methods --> can only be seen inside services package
    public synchronized String catchRes() throws InterruptedException {
        return eventListener.getDataQueue().take(); // wait for a socket stream input
    }

    public synchronized CommandType catchCommandRes() throws InterruptedException {
        // return the CommandType which value correspond to the caught string
        return eventListener.getCommandQueue().take(); // wait for a socket stream input
    }

    public <T> T catchJsonRes(Type type) {
        try {
            String json = catchRes();
            return gson.fromJson(json, type);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public <T> T catchJsonReq(Type type) {
        return catchJsonRes(type);
    }

    public void sendReq(CommandType req) throws IOException {
        // send the String request in the socket output stream
        out.writeBytes(req.toString() + Character.NEW_LINE.getValue());
    }

    public void sendJsonReq(Object req) throws IOException {
        // send the Object to socket output stream in JSON FORMAT
        out.writeBytes(gson.toJson(req) + Character.NEW_LINE.getValue());
    }

    public void sendRes(CommandType res) throws IOException {
        // same concept as sendReq, but different naming to be more precise
        // send the String response in the socket output stream
        sendReq(res);
    }

    public void sendJsonRes(Object res) throws IOException {
        // same concept as sendJsonReq, but different naming to be more precise
        // send the Object to socket output stream in JSON FORMAT
        sendJsonReq(res);
    }

    public boolean isSuccess(CommandType res) {
        return res == CommandType.OK;
    }

    public void handleExit() throws IOException {
        // stop the listening service, as it would still be listening while the socket get closed
        Service.isRunning = false;
        EventListenerService.getInstance(in).interrupt();

        sendReq(CommandType.EXIT);
        
        System.out.println("Thank you for having trusted us!");
    }

    public void handleUnknownOption() {
        System.out.println("Unknown option. Please try again.");
    }

    public EventListenerService getEventListener() {
        return eventListener;
    }

    public CommandType getRes() {
        return res;
    }
}
