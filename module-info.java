/**
 * The module involves building and managing GUI applications for a TCP client
 * and server, which allow communication over a network. The GUIs enable users to
 * interact with the system by specifying server addresses, ports, and messages, which
 * also allowing the server to handle requests for time, vendor, and version information.
 * 
 * This module requires the java.desktop package, which provides the necessary classes
 * for creating GUIs such as frames, buttons, text fields, and areas for the project.
 * 
 * @author angel
 */
module edu.commonwealthu.alm2696.CMSC230 {
    
    // The java.desktop module is required to create and manage GUI components like JFrame, JTextArea, etc.
    requires java.desktop;
    
}
