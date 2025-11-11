package pairly.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorState {

    private List<String> codeLines = new ArrayList<>();
    private Map<Integer, Marker> marks = new HashMap<>();

    // 코드를 동기화
    public void syncCode(List<String> newCodeLines) {
        this.codeLines = new java.util.ArrayList<>(newCodeLines);
        this.marks.clear();
    }

    // 특정 라인에 특정 마커를 추가
    public void addMarker(int lineNumber, Marker marker) {
        this.marks.put(lineNumber, marker);
    }

    public List<String> getCodeLines() {
        return List.copyOf(this.codeLines);
    }

    public Map<Integer, Marker> getMarks() {
        return Map.copyOf(this.marks);
    }
}
