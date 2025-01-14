package com.serverchat.types;

public class ChatIDs {
    private static int chatsGroupsID = 0;

    static int getNextChatID(){
        int tmp = chatsGroupsID;
        chatsGroupsID++;
        return tmp;
    }
}
