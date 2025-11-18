package pairly;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import pairly.server.SessionManager;

public class ServerApplication {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        System.out.println("서버를 시작합니다... ");
        SessionManager sessionManager = new SessionManager();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("새 클라이언트 접속: " + clientSocket.getInetAddress());
                    sessionManager.handleNewConnection(clientSocket);
                } catch (IOException e) {
                    System.err.println("클라이언트 연결 수락 실패: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("서버 소켓을 열 수 없습니다: " + e.getMessage());
            e.printStackTrace(); // 심각한 오류이므로 종료
        }
    }
}

