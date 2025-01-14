# Messaging App Protocol

## Overview
This document defines the protocol for a multi-threaded messaging application with a server-client architecture in Java. It outlines the data format, communication flow, key components, and general commands for the messaging app.

## Data Format
All data exchanged between the server and client must be structured in **JSON format**. This ensures that data types, such as objects and arrays, remain consistent and reliable on both sides of the connection.

## Components of the App
The chat application consists of two main components:
- **Client**: This is the software that users interact with. It runs on the user's device and allows them to send messages, sign up, log in, and log out.
- **Server**: This is the backend system that processes requests from clients. It manages user accounts, stores messages, and ensures secure and reliable communication.

## How Communication Works
The communication process between the client and server can be broken down into several stages:

### 1. Connection Establishment
Before any interaction between the client and server can occur, a connection must be established:
- The **server** initializes a socket connection, binding to a specific port to make itself available for incoming client requests.
- When the **client** wishes to interact with the server, it opens a socket connection by specifying the server’s IP address and port number.
- Upon connection, the server creates a dedicated thread for each client, allowing for concurrent interactions and ensuring that each client’s requests are managed independently.

### 2. Authentication
Upon connection, the client requests a session with the server, which could either be a new user registration (`NEW_USER`) or an existing user login (`OLD_USER`):
- The client provides a unique **username** and **password**.
- For registration, the server verifies that the username is unique. If so, it adds the user's details to an array, grants permissions, and sends an acknowledgment message back to the client.
- In case of an error (e.g., username duplication), the server returns an error message. The client then informs the user of the outcome, either allowing access to the main app interface or redirecting back to the authentication menu.

### 3. Client Requests and Server Handling
Post-authentication, clients interact with the app interface that offers some features and actions. Each action is assigned a specific command code. For example:
- Initiating a private message
- Joining a group chat
- Retrieving message history

#### Phases of Every Request:
| Step | Action                                      |
|------|---------------------------------------------|
| 1    | Client sends command code                   |
| 2    | Server receives command code                |
| 3    | Client sends data (if no data, sends NULL)  |
| 4    | Server receives JSON data                   |
| 5    | Server sends command code (OK or ERROR TYPE)|
| 6    | Server sends data (if no data, sends NULL)  |

### 4. Exit Process
When the user decides to exit the chat application:
- The **client** sends the `EXIT` command to the server, signaling the user's intent to disconnect.
- After sending the disconnection message, the client performs necessary tasks, such as closing used resources.
  
On the **server** side:
- The server listens for incoming messages from clients. Upon receiving the `EXIT` command, it identifies which client's session is being terminated and processes the disconnection by terminating the socket.

## General Key Components
- **User**: Represents a participant in the app.
- **Chat**: Represents a private conversation between two users.
- **Group**: Represents a collection of users participating in shared private chats.

## General Commands
| Command Code   | Description                                                |
|----------------|------------------------------------------------------------|
| OK             | The previous command was executed                          |
| INIT           | Send initial user's chat array                             |
| EXIT           | Exit from the app and close the connection                 |

## User Management Commands
| Command Code   | Description                                                |
|----------------|------------------------------------------------------------|
| NEW_USER       | Create new user account and initialize user session        |
| OLD_USER       | Initialize user session                                    |
| DEL_USER       | Request to delete user information                         |
| UPD_NAME       | Change username                                            |
| LOGOUT         | Logout from the current account                            |

## Chat Management Commands
| Command Code   | Description                                                |
|----------------|------------------------------------------------------------|
| NEW_CHAT       | Initiate a new private chat                                |
| NEW_GROUP      | Create a new group                                         |
| NAV_CHAT       | Navigate to specified chat or group                        |
| REQ_CHATS      | Request information about user chats (userId)              |

##  Messages Management Commands
| Command Code   | Description                                                |
|----------------|------------------------------------------------------------|
| SEND_MSG       | Send a new message                                         |
| RM_MSG         | Remove a message                                           |
| UPD_MSG        | Update the content of a message                            |

## Error Management Messages
| Error Code     | Description                                                |
|----------------|------------------------------------------------------------|
| ERR_GEN        | General server error                                       |
| ERR_NOT_FOUND  | User not found                                             |
| ERR_CHAT_EXISTS| Private chat already exists                                |
| ERR_USER_EXISTS| Username already in use                                    |
| ERR_DISCONNECT | Client has disconnected                                    |
| ERR_WRONG_DATA | The data entered is incorrect                              |

## GitHub Reference
- **Server**: [GitHub - Server](https://github.com/VettoriDante/serverchat.git)
- **Client**: [GitHub - Client](https://github.com/zoryon/client-chat.git)
