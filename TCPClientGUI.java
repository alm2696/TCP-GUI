package mod03_OYO;

import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * TCPClientGUI handles the client-side interface for sending
 * TCP requests to a server and displaying the responses. It allows
 * users to specify server address, port, and the request message.
 * 
 * @author angel
 */
public class TCPClientGUI {

    // GUI components
    private JFrame frame;
    private JTextArea textArea;
    private Socket socket;
    private PrintWriter out;

    /**
     * Constructor that sets up the GUI components, event
     * listeners, and default configurations. The client 
     * can send requests to the server and receive responses.
     */
    public TCPClientGUI() {
        // Initialize the JFrame and other GUI elements
        frame = new JFrame("TCP Client");
        textArea = new JTextArea(20, 30);
        textArea.setEditable(false);
        JButton sendButton = new JButton("Send Request");
        JTextField serverField = new JTextField(10); // For entering server address
        JTextField portField = new JTextField(5);    // For entering server port
        JTextField requestField = new JTextField(10); // For entering request string (e.g., TIME, VENDOR, VERSION)

        // Event listener for the 'Send Request' button
        sendButton.addActionListener(e -> {
            try {
                // Retrieve inputs for server address, port, and request message
                String serverAddress = serverField.getText();
                int port = Integer.parseInt(portField.getText());
                String request = requestField.getText();

                // Connect to the server and send the request
                connectToServer(serverAddress, port);
                sendRequest(request);
            } catch (IOException | NumberFormatException ex) {
                // Display error messages in case of connection failure or input errors
                textArea.append("Error: " + ex.getMessage() + "\n");
            }
        });

        // Setup layout and add components to the JFrame
        frame.setLayout(new FlowLayout());
        frame.add(new JLabel("Server Address:"));
        frame.add(serverField);
        frame.add(new JLabel("Port:"));
        frame.add(portField);
        frame.add(new JLabel("Request:"));
        frame.add(requestField);
        frame.add(sendButton);
        frame.add(new JScrollPane(textArea));
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Establishes a connection to the server at the specified address and port.
     * 
     * @param serverAddress The server's IP address or hostname.
     * @param port          The server's port number.
     * @throws IOException If there is an error connecting to the server.
     */
    public void connectToServer(String serverAddress, int port) throws IOException {
        if (socket != null) {
            socket.close(); // Close any existing connections
        }
        socket = new Socket(serverAddress, port); // Open a new socket connection
        out = new PrintWriter(socket.getOutputStream(), true); // Prepare to send data
    }

    /**
     * Sends a request to the server and handles the server's response.
     * 
     * @param request The request message to be sent to the server.
     * @throws IOException If there is an issue sending the request or receiving the response.
     */
    public void sendRequest(String request) throws IOException {
        out.println(request); // Send the request to the server
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Receive response
        String response = in.readLine();

        // Log request and response to the text area
        textArea.append("Sent request: " + request + "\n");
        textArea.append("Received response: " + response + "\n");

        // Close the connection after receiving the response
        socket.close();
    }

    /**
     * Main method to launch the TCPClientGUI.
     * 
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TCPClientGUI::new); // Start the GUI on the event dispatch thread
    }
}
