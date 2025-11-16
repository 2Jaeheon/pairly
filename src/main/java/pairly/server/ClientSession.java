package pairly.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import pairly.common.message.ClientMessage;
import pairly.common.message.ServerMessage;
import pairly.common.util.JsonConverter;

public class ClientSession implements Runnable {

    private final Socket socket;
    private final SessionManager sessionManager;
    private final JsonConverter jsonConverter;

    private PrintWriter out;

    public ClientSession(Socket socket, SessionManager sessionManager, JsonConverter jsonConverter) {
        this.socket = socket;
        this.sessionManager = sessionManager;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            this.out = writer;
            String line;

            while ((line = in.readLine()) != null) {
                try {
                    ClientMessage message = jsonConverter.fromJson(line, ClientMessage.class);
                    sessionManager.processMessage(this, message);
                } catch (Exception e) {
                    System.err.println("메시지 처리 오류");
                }
            }

        } catch (IOException e) {
            System.out.println("I/O 오류: " + e.getMessage());
        } finally {
            sessionManager.clientDisconnected(this);
            try {
                socket.close(); // 소켓 자원 정리
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(ServerMessage message) {
        if (out != null) {
            String json = jsonConverter.toJson(message);
            out.println(json);
        }
    }
}
