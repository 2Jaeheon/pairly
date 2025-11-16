package pairly.server;

import java.net.Socket;
import pairly.common.message.ClientMessage;

public class SessionManager {
    public synchronized void handleNewConnection(Socket socket) {
        // 임시: ClientSession 생성 및 스레드 시작
        System.out.println("새 연결 처리");
        // new Thread().start() 해야함. 아직 미구현
    }

    public synchronized void processMessage(ClientSession sender, ClientMessage msg) {
        // 임시: 미구현 상태
        System.out.println("[SessionManager] 메시지 수신" + msg.getType());
    }

    public synchronized void clientDisconnected(ClientSession session) {
        // 임시: 미구현 상태
        System.out.println("[SessionManager] 연결 종료");
    }
}
