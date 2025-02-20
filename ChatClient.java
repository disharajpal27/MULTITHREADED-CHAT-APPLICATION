import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println(in.readLine());  // Request for name
            String name = userInput.readLine();
            out.println(name);

            // Thread to read messages from the server
            Thread readThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            readThread.start();

            // Sending messages to server
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);
            }
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}
