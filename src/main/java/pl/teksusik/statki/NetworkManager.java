package pl.teksusik.statki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {

    public interface MessageListener {
        void onShotReceived(int x, int y);
        void onResultReceived(ShotResult result);
        void onDisconnected();
    }

    private boolean isHost;
    private int port;

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener listener;
    private volatile boolean connected;

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public void startServer(int port) throws IOException {
        this.isHost = true;
        this.port = port;
        serverSocket = new ServerSocket(port);
        Thread serverThread = new Thread(() -> {
            try {
                socket = serverSocket.accept();
                setupStreams();
                startReading();
            } catch (IOException e) {
                if (connected) notifyDisconnected();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void connect(String ip, int port) throws IOException {
        this.isHost = false;
        this.port = port;
        socket = new Socket(ip, port);
        setupStreams();
        startReading();
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;
    }

    private void startReading() {
        Thread readerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    handleMessage(line);
                }
            } catch (IOException ignored) {
            } finally {
                if (connected) notifyDisconnected();
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void handleMessage(String message) {
        if (listener == null) return;
        if (message.startsWith("SHOT ")) {
            int[] coords = parseShot(message);
            listener.onShotReceived(coords[0], coords[1]);
        } else if (message.startsWith("RESULT ")) {
            listener.onResultReceived(parseResult(message));
        }
    }

    private void notifyDisconnected() {
        connected = false;
        if (listener != null) listener.onDisconnected();
    }

    public void sendShot(int x, int y) {
        if (out != null) {
            out.println("SHOT " + (char) ('A' + x) + (y + 1));
        }
    }

    public void sendResult(ShotResult result) {
        if (out != null) {
            out.println("RESULT " + result.name());
        }
    }

    public void disconnect() {
        connected = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isHost() {
        return isHost;
    }

    public int getPort() {
        return port;
    }

    public static int[] parseShot(String message) {
        // "SHOT A1" → x=0, y=0
        String coords = message.substring(5);
        int x = coords.charAt(0) - 'A';
        int y = Integer.parseInt(coords.substring(1)) - 1;
        return new int[]{x, y};
    }

    public static ShotResult parseResult(String message) {
        String value = message.substring(7);
        return ShotResult.valueOf(value);
    }
}
