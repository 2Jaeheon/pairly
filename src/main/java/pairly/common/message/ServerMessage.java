package pairly.common.message;

public class ServerMessage {
    private MessageType messageType;
    private String message;

    public ServerMessage(MessageType messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public MessageType getType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }
}
