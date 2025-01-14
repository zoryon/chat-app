package com.serverchat;
import com.serverchat.types.*;
import com.serverchat.protocol.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import com.google.gson.*;




public class ClientHandler extends Thread {
    private User user;
    private BufferedReader in;
    private DataOutputStream out;
    private Socket socket;
    private Datas datas;
    private boolean connectionUP;

    public ClientHandler(Socket socket, Datas datas) {
        this.socket = socket;
        this.datas = datas;
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.connectionUP = true;
    }

    @Override
    public void run() {
        System.out.println("new client");
        // try to use the socket
        try {

            // read user info to establish connection
            boolean error = true;
            do {
                /*
                 * the authentication method
                 * return true whenever an error
                 * is found during the auth process
                 */
                error = Authentication();
            } while (error);

            String inCommand = null;
            CommandType command = null;
            boolean auth = false;
            String input = null; 


            do {

                if(!auth){
                    if(!connectionUP) return;
                    inCommand = null;
                    command = null;
                    auth = true;
                    input = null; 

                    // Once you know who the user is, the server get ready to send him
                    // all of his chats
                    WriteBytes(CommandType.INIT);
                    ArrayList<ChatInterface> chats = datas.getChatsByUserId(this.user.getId());
                    WriteBytes(this.getChatsToSend(chats));
        
                    // here the thread add himself to datas (connected)

                    datas.addConnectedClient(user.getId(), this);
                    
                }

                // read and cast the new command
                inCommand = in.readLine();
                command = CommandType.valueOf(inCommand); // cast like operation

                //execute every command 
                switch (command) {
                    case SEND_MSG:
                    //sending a new message
                    Message m = new Gson().fromJson(in.readLine(), Message.class);//try to cast to message
                    //send error or ok
                    if(m == null){
                        this.WriteBytes(CommandType.ERR_WRONG_DATA);//not able to cast
                    }
                    else{
                        m.setSenderId(this.user.getId());
                        if(datas.addNewMsg(m)){
                            WriteBytes(CommandType.OK);
                            WriteBytes(m);//send the whole message back with all datas updated
                        }
                        else
                        {
                            WriteBytes(CommandType.ERR_GEN);//something went wrong with the message
                        }
                    }
                    break;
                    case NAV_CHAT:
                        String chatIdentifier = new Gson().fromJson(in.readLine(), String.class);
                        try{
                            int chatId;
                            chatId = Integer.parseInt(chatIdentifier);
                            if(datas.isAllownToChat(chatId, this.getUserId())){
                                WriteBytes(CommandType.OK);
                                this.WriteByteNull();
                            }
                            else{
                                WriteBytes(CommandType.ERR_NOT_FOUND);
                            }
                        }catch(Exception e){
                            WriteBytes(CommandType.ERR_WRONG_DATA);
                        }
                        break;
                    case NEW_CHAT:
                        input = new Gson().fromJson(in.readLine(), String.class);//input will be username
                        //will be sent only the username of the other "component" and use this.user to create the chat
                        User t = datas.getUserByName(input);
                        Chat c = null;
                        if(t == null ){this.WriteBytes(CommandType.ERR_NOT_FOUND);}
                        else{
                            if(
                                datas.isExitingPrivateChat(this.user.getUsername(), input) // if this chat already exist
                            ){
                                WriteBytes(CommandType.ERR_CHAT_EXISTS); //return the error
                            }
                            else{
                                c = new Chat(this.user, t);
                                System.out.println("new chat: " + c.getChatName());
                                datas.addChatGroup(c);
                                WriteBytes(CommandType.OK);
                                this.WriteBytes(c.getChatName() + "#" + c.getChatId());//send chatName and ChatID
                            }
                        }
                        break;
                        case NEW_GROUP:
                        // get info to create new group 
                            JsonGroup newGroup = new Gson().fromJson(in.readLine(), JsonGroup.class); // try to cast check the group status
                            Group g = null;// create new group 
                            if(newGroup == null || newGroup.getGroupName().contains("-")){
                                WriteBytes(CommandType.ERR_WRONG_DATA);
                            }
                            else{
                                // create a new group and then add all users
                                for(int i = 0; i < newGroup.getUsernameList().size(); i++){
                                    if(i == 0){
                                        // the first time it create the group and add first admin
                                        g = new Group(newGroup.getGroupName(), datas.getUserByName(newGroup.getUsernameList().get(0)));
                                    }
                                    else{
                                        //add every user
                                        g.addUser(datas.getUserByName(newGroup.getUsernameList().get(i)));
                                    }
                                }
                                datas.addChatGroup(g); // add group to datas 
                                WriteBytes(CommandType.OK); // send datas to client via WriteBytes 
                                this.WriteBytes(g.getChatName() + "#" + g.getChatId());
                                System.out.println("new group: "+ g.getChatName());
                            }                            
                            break;
                        case REQ_CHATS:
                            in.readLine();//will recive null (no data required)
                            ArrayList<ChatInterface> allUserChats = datas.getChatsByUserId(this.getUserId());
                            ArrayList<JsonChat> chatsToSend = new ArrayList<>();
                            for(ChatInterface chat : allUserChats){
                                //create an array of JsonChat
                                chatsToSend.add(new JsonChat(chat.getChatId(), chat.getChatName(), chat.getAllMessages()));
                            }
                            WriteBytes(CommandType.OK);//send the OK
                            WriteBytes(allUserChats);// send an array of JsonChat
                        case UPD_NAME:
                            JsonUser newUsername = new Gson().fromJson(in.readLine(), JsonUser.class);//read the new username
                            if(newUsername == null || newUsername.getUsername().equals(this.user.getUsername())){
                                WriteBytes(CommandType.ERR_WRONG_DATA);
                            } 
                            else
                            {
                                if(!this.user.getPassword().equals(newUsername.getPassword())){
                                    WriteBytes(CommandType.ERR_WRONG_DATA);
                                }
                                else{
                                    //set the username
                                    datas.updateUserName(this.user, newUsername.getUsername());
                                    WriteBytes(CommandType.OK);
                                    this.WriteByteNull();
                                    System.out.println("username changed: " + newUsername.getUsername());
                                }
                            }
                        break;
                        case RM_MSG:
                            m = new Gson().fromJson(in.readLine(), Message.class);
                            if(m == null){
                                WriteBytes(CommandType.ERR_WRONG_DATA);
                            }
                            else{
                                m.setSenderId(datas.getUserByName(m.getSenderName()).getId());
                                if(datas.rmMessage(m, this.getUserId())){
                                    WriteBytes(CommandType.OK);
                                    WriteBytes(m);
                                }
                                else{
                                    WriteBytes(CommandType.ERR_NOT_FOUND);
                                }
                            }
                        break;
                        case UPD_MSG:
                            Message messageToMod = new Gson().fromJson(in.readLine(), Message.class);
                            if(messageToMod == null){
                                WriteBytes(CommandType.ERR_WRONG_DATA);
                            }
                            else{
                                //update message
                                messageToMod.setSenderId(datas.getUserByName(messageToMod.getSenderName()).getId());
                                if(datas.modMsg(messageToMod, this.getUserId())){
                                    WriteBytes(CommandType.OK);
                                    WriteBytes(messageToMod);
                                }
                                else{
                                    WriteBytes(CommandType.ERR_NOT_FOUND);
                                }
                            }
                            break;
                        case DEL_USER:
                            JsonUser userToDelete = new Gson().fromJson(in.readLine(), JsonUser.class);
                            if(datas.isExitingName(userToDelete.getUsername()) && userToDelete.getPassword().equals(this.user.getPassword())){
                                WriteBytes(CommandType.OK);
                                datas.deleteUser(this.user);
                                user = null;
                                auth = logout(auth);
                            }
                            else{
                                WriteBytes(CommandType.ERR_NOT_FOUND);
                            }
                        break;
                        case LOGOUT:
                            auth = logout(auth);
                        break;
                        case EXIT://close the socket
                            datas.rmConnectedUser(this);
                            System.out.println(this.user.getUsername() + " has closed his client");
                            socket.close();//close the socket on clientLogout
                            connectionUP = false;
                        break;
                    default:
                        WriteBytes(CommandType.ERR_WRONG_DATA);
                        break;
                }
            } while (connectionUP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean logout(boolean auth) throws IOException{
        datas.rmConnectedUser(this);
        WriteBytes(CommandType.OK);
        WriteByteNull();
        auth = true;
        do{
            auth = this.Authentication();
        }while(auth);
        return auth;
    }

    // the method do de authentication and return a boolean error
    private Boolean Authentication() throws IOException {

        String typeOfUser = in.readLine();// get the old/new user
        CommandType c = CommandType.valueOf(typeOfUser);// cast like operation c = command used
        String inputs = null;
        Boolean error = false;

        inputs = in.readLine();// get data

        switch (c) {
            case NEW_USER:
                // for new user wait to be sent all user info
                JsonUser newUserJ = new Gson().fromJson(inputs, JsonUser.class);// transform datas recived into a User
                if (newUserJ == null || newUserJ.getUsername().contains("-")) {
                    WriteBytes(CommandType.ERR_WRONG_DATA);
                    error = true;
                } else {
                    if (datas.isExitingName(newUserJ.getUsername())) {
                        WriteBytes(CommandType.ERR_USER_EXISTS);
                        error = true;
                    }else{
                        User newUser = new User(newUserJ.getUsername(), newUserJ.getPassword());
                        datas.newUser(newUser);// add the new user to the general array of datas
                        user = newUser; // set this.user
                        WriteBytes(CommandType.OK); // send the ok
                        System.out.println("new user has been created username: " + user.getUsername());
                    }
                }
                break;
            case OLD_USER:
                JsonUser searchFor = new Gson().fromJson(inputs, JsonUser.class);
                User tmp = datas.getUser(searchFor.getUsername(), searchFor.getPassword());
                // check if the user was successfully found
                if (tmp == null) {
                    WriteBytes(CommandType.ERR_WRONG_DATA);
                    error = true;
                } else {
                    if(datas.isExistingConnection(tmp) || tmp.getUsername().equalsIgnoreCase(datas.getDeletedUserInfo().getUsername())){
                        WriteBytes(CommandType.ERR_GEN);
                        error = true;
                    }
                    else{
                        user = tmp;
                        WriteBytes(CommandType.OK);
                        System.out.println(user.getUsername() + " has connected again :)");
                    }
                }
                break;
                case EXIT:
                    //what to do on exit
                    socket.close();
                    connectionUP = false;
                    error = false;
                break;
            default:
                WriteBytes(CommandType.ERR_WRONG_DATA);
                error = true;
                break;
        }
        if(connectionUP){
            this.WriteByteNull();
        }
        return error; // return error
    }

    //method that return the out of this client to be sent data
    public DataOutputStream getOutputStream(){
        return this.out;
    }

    //return this.user.getId
    public int getUserId(){
        return this.user.getId();
    } 


    // method for send a string , with before the implementation of "\n"
    private void WriteBytes(Object writeOut) throws IOException {
        out.writeBytes(new Gson().toJson(writeOut) + "\n");
    }

    private void WriteBytes(CommandType commandToSout) throws IOException {
        out.writeBytes(commandToSout.toString() + "\n");
        System.out.println(commandToSout.toString());
    }

    private void WriteByteNull() throws IOException{
        this.out.writeBytes(null + "\n");
    }

    // return an array the Gson of ChatToSend and require an array of ChatInterface
    private ArrayList<JsonChat> getChatsToSend(ArrayList<ChatInterface> chats) {
        ArrayList<JsonChat> arJsonChat = new ArrayList<>();
        for (ChatInterface i : chats) {
            arJsonChat.add(new JsonChat(i.getChatId(), i.getChatName(), i.getAllMessages()));
        }

        return arJsonChat;
    }

}
