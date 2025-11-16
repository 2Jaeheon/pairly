package pairly.common.message;

public class MarkerUpdateDto {
    private int lineNumber;
    private String comment;

    private MarkerUpdateDto() {
    }

    public MarkerUpdateDto(int lineNumber, String comment) {
        this.lineNumber = lineNumber;
        this.comment = comment;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getComment() {
        return comment;
    }
}
