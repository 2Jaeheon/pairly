package pairly.common.message;

public class ClientMessage {
    private MessageType messageType;
    private String message;

    public ClientMessage(MessageType messageType, String message) {
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
