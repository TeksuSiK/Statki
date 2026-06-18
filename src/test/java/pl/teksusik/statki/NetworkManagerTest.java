package pl.teksusik.statki;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class NetworkManagerTest {

    private NetworkManager host;
    private NetworkManager client;

    @AfterEach
    void tearDown() {
        if (host != null) host.disconnect();
        if (client != null) client.disconnect();
    }

    private int freePort() throws IOException {
        try (ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        }
    }

    @Test
    void hostAndClientCanConnect() throws Exception {
        int port = freePort();
        host = new NetworkManager();
        client = new NetworkManager();

        host.startServer(port);
        client.connect("127.0.0.1", port);

        Thread.sleep(200);

        assertTrue(host.isConnected());
        assertTrue(client.isConnected());
    }

    @Test
    void hostCanSendShotToClient() throws Exception {
        int port = freePort();
        host = new NetworkManager();
        client = new NetworkManager();

        CountDownLatch latch = new CountDownLatch(1);
        int[] received = new int[2];

        client.setListener(new NetworkManager.MessageListener() {
            @Override public void onShotReceived(int x, int y) {
                received[0] = x;
                received[1] = y;
                latch.countDown();
            }
            @Override public void onResultReceived(ShotResult result) {}
            @Override public void onDisconnected() {}
        });

        host.startServer(port);
        client.connect("127.0.0.1", port);
        Thread.sleep(200);

        host.sendShot(3, 5);

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(3, received[0]);
        assertEquals(5, received[1]);
    }

    @Test
    void clientCanSendResultToHost() throws Exception {
        int port = freePort();
        host = new NetworkManager();
        client = new NetworkManager();

        CountDownLatch latch = new CountDownLatch(1);
        ShotResult[] received = new ShotResult[1];

        host.setListener(new NetworkManager.MessageListener() {
            @Override public void onShotReceived(int x, int y) {}
            @Override public void onResultReceived(ShotResult result) {
                received[0] = result;
                latch.countDown();
            }
            @Override public void onDisconnected() {}
        });

        host.startServer(port);
        client.connect("127.0.0.1", port);
        Thread.sleep(200);

        client.sendResult(ShotResult.SUNK);

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertEquals(ShotResult.SUNK, received[0]);
    }

    @Test
    void disconnectNotifiesOtherSide() throws Exception {
        int port = freePort();
        host = new NetworkManager();
        client = new NetworkManager();

        CountDownLatch latch = new CountDownLatch(1);

        host.setListener(new NetworkManager.MessageListener() {
            @Override public void onShotReceived(int x, int y) {}
            @Override public void onResultReceived(ShotResult result) {}
            @Override public void onDisconnected() { latch.countDown(); }
        });

        host.startServer(port);
        client.connect("127.0.0.1", port);
        Thread.sleep(200);

        client.disconnect();

        assertTrue(latch.await(2, TimeUnit.SECONDS));
    }

    @Test
    void parseShotMessage() {
        int[] result = NetworkManager.parseShot("SHOT A1");
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);

        int[] result2 = NetworkManager.parseShot("SHOT D7");
        assertEquals(3, result2[0]);
        assertEquals(6, result2[1]);
    }

    @Test
    void parseResultMessage() {
        assertEquals(ShotResult.HIT, NetworkManager.parseResult("RESULT HIT"));
        assertEquals(ShotResult.MISS, NetworkManager.parseResult("RESULT MISS"));
        assertEquals(ShotResult.SUNK, NetworkManager.parseResult("RESULT SUNK"));
    }
}
