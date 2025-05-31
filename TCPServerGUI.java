package mod03_OYO;

import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * TCPServerGUI represents the server-side interface for handling
 * incoming TCP connections. The server listens for client requests
 * and responds with system properties or the current time.
 * 
 * @author angel
 */
public class TCPServerGUI {

    private JFrame frame;
    private JTextArea textArea;
    private ServerSocket serverSocket;
    private int port;
    private volatile boolean running;

    /**
     * Constructor that sets up the server GUI components and
     * initializes the server controls. The server can be started
     * and stopped, and will handle multiple client requests concurrently.
     */
    public TCPServerGUI() {
        // Setup the main JFrame and other GUI elements
        frame = new JFrame("TCP Server");
        textArea = new JTextArea(20, 30);
        textArea.setEditable(false);
        JButton startButton = new JButton("Start Server");
        JButton stopButton = new JButton("Stop Server");
        JTextField portField = new JTextField(10); // Input for specifying server port

        // Event listener for starting the server
        startButton.addActionListener(e -> {
            try {
                port = Integer.parseInt(portField.getText()); // Parse port number
                serverSocket = new ServerSocket(port); // Open server socket
                running = true;
                textArea.append("Server started on port: " + port + "\n");
                new Thread(this::listenForClients).start(); // Start thread to handle client connections
            } catch (IOException | NumberFormatException ex) {
                textArea.append("Starting server: " + ex.getMessage() + "\n");
            }
        });

        // Event listener for stopping the server
        stopButton.addActionListener(e -> {
            running = false; // Set server state to stopped
            try {
                if (serverSocket != null) {
                    serverSocket.close(); // Close the server socket
                    textArea.append("Server stopped.\n");
                }
            } catch (IOException ex) {
                textArea.append("Stopping server: " + ex.getMessage() + "\n");
            }
        });

        // Configure the frame layout and add components
        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Port:"));
        frame.add(portField);
        frame.add(startButton);
        frame.add(stopButton);
        frame.add(new JScrollPane(textArea));
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Listens for client connections and spawns a new thread to handle each client.
     */
    public void listenForClients() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept(); // Wait for a client to connect
                new Thread(() -> handleClient(clientSocket)).start(); // Handle client in a new thread
            } catch (IOException ex) {
                if (running) {
                    textArea.append("Accepting client: " + ex.getMessage() + "\n");
                }
            }
        }
    }

    /**
     * Handles communication with a connected client, processes requests, and sends responses.
     * 
     * @param clientSocket The socket connected to the client.
     */
    public void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = in.readLine(); // Read the client request
            String response = processRequest(request); // Process and generate the response
            textArea.append("Received request: " + request + "\n");
            textArea.append("Sent response: " + response + "\n");

            out.println(response); // Send response to the client
        } catch (IOException ex) {
            textArea.append("Handling client: " + ex.getMessage() + "\n");
        }
    }

    /**
     * Processes a client request and returns the appropriate response.
     * Supported requests: "TIME", "VENDOR", "VERSION".
     * 
     * @param request The client's request string.
     * @return The server's response based on the request.
     */
    private String processRequest(String request) {
        if (request == null || request.trim().isEmpty()) {
            return "Invalid request";
        }

        request = request.trim().toUpperCase(); // Normalize input

        switch (request) {
            case "TIME":
                return getCurrentTime();
            case "VENDOR":
                return System.getProperty("java.vendor");
            case "VERSION":
                return System.getProperty("java.version");
            default:
                return "Unknown request: " + request;
        }
    }

    /**
     * Returns the current system time as a formatted string.
     * 
     * @return The current time in the format "MM/dd/yyyy HH:mm:ss".
     */
    public String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    /**
     * Main method to launch the TCPServerGUI.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TCPServerGUI::new);
    }
}
