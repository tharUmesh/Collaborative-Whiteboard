import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class WhiteboardClient extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private JPanel canvas;
    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private int eraserSize = 5;
    private boolean eraserMode = false;
    private boolean pencilMode = true;
    private Font font = new Font("Arial", Font.PLAIN, 14);

    public WhiteboardClient(String serverAddress) {
        setTitle("Collaborative Whiteboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            socket = new Socket(serverAddress, 4000);
            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(new ServerListener(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image pencilImage = toolkit.getImage("pencil_icon.png");
        Cursor pencilCursor = toolkit.createCustomCursor(pencilImage, new Point(0, 30), "Pencil");
        Image eraserImage = toolkit.getImage("eraser_icon.png");
        Cursor eraserCursor = toolkit.createCustomCursor(eraserImage, new Point(0, 25), "Eraser");


        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        canvas.setBackground(Color.WHITE);
        canvas.setCursor(pencilCursor);
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Graphics g = canvas.getGraphics();
                if (eraserMode) {
                    g.setColor(Color.WHITE);
                    g.fillOval(e.getX(), e.getY(), eraserSize, eraserSize);
                } else {
                    g.setColor(currentColor);
                    g.fillOval(e.getX(), e.getY(), brushSize, brushSize);
                }
                out.println(e.getX() + "," + e.getY() + "," + currentColor.getRGB() + "," + brushSize + "," + eraserMode + "," + eraserSize);
            }
        });


        JPanel controls = new JPanel();
        controls.setFont(font);
        String[] colors = {"Black", "Red", "Blue", "Green", "Yellow"};
        Color[] colorValues = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        JComboBox<String> colorSelector = new JComboBox<>(colors);
        colorSelector.setFont(font);
        colorSelector.addActionListener(e -> {
            currentColor = colorValues[colorSelector.getSelectedIndex()];
            canvas.setCursor(pencilCursor);
            eraserMode = false;
            pencilMode = true;
        });

        JComboBox<Integer> sizeSelector = new JComboBox<>(new Integer[]{3, 5, 10, 15, 20});
        sizeSelector.setFont(font);
        sizeSelector.addActionListener(e -> brushSize = (Integer) sizeSelector.getSelectedItem());


        JComboBox<Integer> eraserSizeSelector = new JComboBox<>(new Integer[]{3, 5, 10, 15, 20});
        eraserSizeSelector.setFont(font);
        eraserSizeSelector.addActionListener(e -> eraserSize = (Integer) eraserSizeSelector.getSelectedItem());


        ImageIcon pencilIcon = new ImageIcon("pencil_icon.png");
        ImageIcon eraserIcon = new ImageIcon("eraser_icon.png");


        Image pencilImageResized = pencilIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Image eraserImageResized = eraserIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);


        pencilIcon = new ImageIcon(pencilImageResized);
        eraserIcon = new ImageIcon(eraserImageResized);


        JButton pencilButton = new JButton(pencilIcon);
        pencilButton.setBorderPainted(false);
        pencilButton.setContentAreaFilled(false);
        pencilButton.setFocusPainted(false);
        pencilButton.setPreferredSize(new Dimension(40, 40));
        pencilButton.addActionListener(e -> {
            eraserMode = false;
            pencilMode = true;
            canvas.setCursor(pencilCursor);
        });


        JButton eraserButton = new JButton(eraserIcon);
        eraserButton.setBorderPainted(false);
        eraserButton.setContentAreaFilled(false);
        eraserButton.setFocusPainted(false);
        eraserButton.setPreferredSize(new Dimension(40, 40));
        eraserButton.addActionListener(e -> {
            eraserMode = !eraserMode;
            pencilMode = !eraserMode;
            canvas.setCursor(eraserMode ? eraserCursor : pencilCursor);
        });

        controls.add(new JLabel("Color: "));
        controls.add(colorSelector);
        controls.add(new JLabel("Brush Size: "));
        controls.add(sizeSelector);
        controls.add(new JLabel("Eraser Size: "));
        controls.add(eraserSizeSelector);
        controls.add(eraserButton);
        controls.add(pencilButton);

        add(controls, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        setVisible(true);
    }

    private class ServerListener implements Runnable {
        private BufferedReader in;

        public ServerListener(Socket socket) {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (!line.equals("EXISTING_STATE")) {
                        String[] parts = line.split(",");
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        Color color = new Color(Integer.parseInt(parts[2]));
                        int size = Integer.parseInt(parts[3]);
                        boolean isEraser = Boolean.parseBoolean(parts[4]);
                        int eraserSize = Integer.parseInt(parts[5]);

                        Graphics g = canvas.getGraphics();
                        g.setColor(isEraser ? Color.WHITE : color);
                        g.fillOval(x, y, isEraser ? eraserSize : size, isEraser ? eraserSize : size);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WhiteboardClient("localhost"));
    }
}
