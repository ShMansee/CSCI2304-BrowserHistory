// keeps the list of visited pages and which one we are on right now
public class BrowserHistory {

    private final DoublyLinkedList list = new DoublyLinkedList();
    private final DeletedStack deleted = new DeletedStack();
    private Node current;            // the page we are on (null if history is empty)
    private final int capacity;      // max number of pages we keep

    public BrowserHistory(String homepage, int capacity) {
        this.capacity = capacity;
        Node home = new Node(homepage);
        list.addLast(home);
        current = home;
    }

    // cuts off everything after current, adds the new page at the end,
    // and removes the oldest page if we go over capacity
    public void visit(String url) {
        list.truncateAfter(current);   // if current is null this clears the list, so it still works
        Node page = new Node(url);
        list.addLast(page);
        current = page;
        if (list.size() > capacity) {
            list.removeFirst();
        }
    }

    // goes back up to 'steps' pages, stops at the oldest one
    // returns the url we end up on, or null if the history is empty
    public String back(int steps) {
        if (current == null) return null;
        while (steps > 0 && current.prev != null) {
            current = current.prev;
            steps--;
        }
        return current.url;
    }

    // same as back but forward, stops at the newest page
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

    // all pages as text, [<] marks the current one
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

    // deletes everything, including the deleted stack
    public void deleteHistory() {
        list.clear();
        deleted.clear();
        current = null;
    }

    // used by DeleteRestoreManager and HistoryFileManager
    public DoublyLinkedList getList()     { return list; }
    public DeletedStack getDeletedStack() { return deleted; }
    public Node getCurrent()              { return current; }
    public void setCurrent(Node n)        { current = n; }
}
