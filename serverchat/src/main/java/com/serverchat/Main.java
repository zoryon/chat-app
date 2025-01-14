package com.serverchat;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.serverchat.types.User;

public class Main {
    public static void main(String[] args) throws IOException {
        //Server socket creation
        ServerSocket socket = new ServerSocket(3000);
        //must exist only one instance of type Datas, since it simulate a DB
        Datas datas = new Datas();
        User deletedUser = new User("DeletedUser", "0000");
        datas.newUser(deletedUser);//user deleted shown as owner for message sent by deleted users
        /* need to add an ArrayList of all the chats and groups 
         * with their id and a list of all the user for that chat
         * so once the server know who is the client can easly 
         * know in which chat he is 
         */
        
        do{
            Socket socketC = socket.accept();//accepting new connections
            new ClientHandler(socketC, datas).start();//passing required datas the the handler
        }while(true);
    }
}