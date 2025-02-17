import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class WhiteboardServer {
    private static final int PORT = 4000;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Whiteboard Server started...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected.");
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                new Thread(client).start();

                // Send the existing whiteboard state to the new client
                sendExistingStateToClient(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    broadcast(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(this);
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }

    private static void sendExistingStateToClient(ClientHandler client) {
        for (ClientHandler otherClient : clients) {
            if (otherClient != client) {
                // Send all the data broadcasted so far to the new client
                // Fix this
                otherClient.out.println("EXISTING_STATE");
            }
        }
    }
}
