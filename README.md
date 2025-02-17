# Collaborative Whiteboard Application

## Overview
This project is a real-time collaborative whiteboard application, where multiple users can draw, erase, and interact on a shared canvas. It uses Java Socket Programming for the client-server communication and Swing for the graphical user interface (GUI).

### Features:
- **Real-time Collaboration**: All actions are broadcast to every connected client in real-time.
- **Drawing Tools**: Pencil and eraser tools with customizable sizes and colors.
- **User Interface**: Simple and intuitive interface with a color picker, brush size, and eraser size selectors.
- **Cross-Platform**: Java-based application works across platforms with Java Runtime Environment (JRE).

## Technologies Used:
- **Java** (for client-server communication and UI)
- **Swing** (for GUI)
- **Socket Programming** (for real-time communication)

## How It Works:
1. **Server**:
   - The server listens on a specific port (default: 4000) and accepts incoming client connections.
   - It broadcasts drawing actions (mouse movements) to all connected clients.
   - Sends the existing whiteboard state to new clients when they connect.

2. **Client**:
   - Each client has a graphical whiteboard (Swing JPanel) where users can draw or erase using the mouse.
   - The client sends drawing actions (coordinates, color, size, and tool type) to the server, which then broadcasts it to all other clients.
   - Clients receive and render the drawing actions from other users in real-time.

## Installation and Setup

1. Clone the repository:
    ```bash
    git clone https://github.com/tharUmesh/collaborative-whiteboard.git
    cd collaborative-whiteboard
    ```

2. Compile the Java files:
    ```bash
    javac WhiteboardServer.java WhiteboardClient.java
    ```

3. Run the server:
    ```bash
    java WhiteboardServer
    ```

4. Run the client:
    ```bash
    java WhiteboardClient localhost
    ```

## Usage:
- Launch the server and then launch multiple clients to see the real-time collaboration.
- Clients can draw using the pencil, change colors, and erase using the eraser.

## Contributing:
1. Fork this repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes and commit (`git commit -am 'Add new feature'`).
4. Push to your branch (`git push origin feature-branch`).
5. Create a new Pull Request.


