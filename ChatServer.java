import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Error in server: " + e.getMessage());
        }
    }

    static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your name: ");
            clientName = in.readLine();
            System.out.println(clientName + " has joined the chat.");

            ChatServer.broadcastMessage(clientName + " has joined the chat.", this);

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(clientName + ": " + message);
                ChatServer.broadcastMessage(clientName + ": " + message, this);
            }
        } catch (IOException e) {
            System.out.println(clientName + " has left the chat.");
        } finally {
            try {
                socket.close();
                ChatServer.removeClient(this);
                ChatServer.broadcastMessage(clientName + " has left the chat.", this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}
