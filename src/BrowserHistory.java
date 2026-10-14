public class BrowserHistory {

    private final DoublyLinkedList list = new DoublyLinkedList();
    private final DeletedStack deleted = new DeletedStack();
    private Node current;
    private final int capacity;

    public BrowserHistory(String homepage, int capacity) {
        this.capacity = capacity;
        Node home = new Node(homepage);
        list.addLast(home);
        current = home;
    }

    public void visit(String url) {
        list.truncateAfter(current);
        Node page = new Node(url);
        list.addLast(page);
        current = page;
        if (list.size() > capacity) {
            list.removeFirst();
        }
    }

    public String back(int steps) {
        if (current == null) return null;
        while (steps > 0 && current.prev != null) {
            current = current.prev;
            steps--;
        }
        return current.url;
    }

    public String forward(int steps) {
        if (current == null) return null;
        while (steps > 0 && current.next != null) {
            current = current.next;
            steps--;
        }
        return current.url;
    }

    public String currentPage() {
        if (current == null) return null;
        return current.url + " (" + current.metadata + ")";
    }

    public String displayHistory() {
        if (list.isEmpty()) return "No pages.";
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Node n = list.getFirst(); n != null; n = n.next) {
            sb.append(index++).append(". ").append(n.url);
            if (n == current) sb.append(" [<]");
            if (n.next != null) sb.append("\n");
        }
        return sb.toString();
    }

    public void deleteHistory() {
        list.clear();
        deleted.clear();
        current = null;
    }

    public DoublyLinkedList getList()     { return list; }
    public DeletedStack getDeletedStack() { return deleted; }
    public Node getCurrent()              { return current; }
    public void setCurrent(Node n)        { current = n; }
}