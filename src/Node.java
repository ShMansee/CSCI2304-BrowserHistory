import java.time.LocalDateTime;

public class Node {
    String url;
    String metadata;
    Node next = null;
    Node prev = null;

    public Node(String url) {
        this.url = url;
        this.metadata = LocalDateTime.now().toString();
    }
}
