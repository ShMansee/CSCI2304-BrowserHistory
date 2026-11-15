/**

 *
 * Methods that need to change the list or the stack
 * from outside (delete, restore, import, export) use the getters at the bottom.
 */
public class BrowserHistory {

    private final DoublyLinkedList list = new DoublyLinkedList();  // all pages, from oldest to newest
    private final DeletedStack deleted = new DeletedStack();       // pages that were deleted
    private Node current;            // the active page (null when the history is empty)
    private final int capacity;      // maximum number of pages kept

    /**
     * Starts a browser with one page, the homepage, and puts the cursor on it.
     * The capacity is the maximum number of pages the history may hold.
     */
    public BrowserHistory(String homepage, int capacity) {
        this.capacity = capacity;
        Node home = new Node(homepage);
        list.addLast(home);
        current = home;
    }

    /**
     * Opens a new page.
     * 1. Cut off every page after the current one (forward history is lost).
     * 2. Append the new page and move the cursor to it.
     * 3. If the history is now over capacity, remove the oldest page.
     * Works on an empty history too, because truncating after null clears the list.
     */
    public void visit(String url) {
        list.truncateAfter(current);
        Node page = new Node(url);
        list.addLast(page);
        current = page;
        if (list.size() > capacity) {
            list.removeFirst();
        }
    }

    /**
     * Moves the cursor back by up to the given number of pages and returns the
     * URL of the page it ends on. If there are fewer pages behind, it stops at
     * the oldest page. Returns null if the history is empty.
     */
    public String back(int steps) {
        if (current == null) return null;
        while (steps > 0 && current.prev != null) {
            current = current.prev;
            steps--;
        }
        return current.url;
    }

    /**
     * Moves the cursor forward by up to the given number of pages and returns
     * the URL of the page it ends on. If there are fewer pages ahead, it stops
     * at the newest page. Returns null if the history is empty.
     */
    public String forward(int steps) {
        if (current == null) return null;
        while (steps > 0 && current.next != null) {
            current = current.next;
            steps--;
        }
        return current.url;
    }

    /**
     * Returns the URL and timestamp of the active page without changing
     * anything. Returns null if the history is empty.
     */
    public String currentPage() {
        if (current == null) return null;
        return current.url + " (" + current.metadata + ")";
    }

    /**
     * Builds the whole history as text.The active page is marked with [<].
     */
    public String displayHistory() {
        if (list.isEmpty()) return "No pages.";
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Node n = list.getFirst(); n != null; n = n.next) {
            sb.append(index++).append(". ").append(n.url);
            if (n == current) sb.append(" [<]");
            if (n.next != null) sb.append("\n");   // no extra newline after the last page
        }
        return sb.toString();
    }

    /**
     * Deletes all history: every page and every deleted page waiting to be
     * restored. The cursor becomes null, so the history is empty.
     */
    public void deleteHistory() {
        list.clear();
        deleted.clear();
        current = null;
    }

    // Access for the other classes (DeleteRestoreManager and HistoryFileManager).
    public DoublyLinkedList getList()     { return list; }
    public DeletedStack getDeletedStack() { return deleted; }
    public Node getCurrent()              { return current; }
    public void setCurrent(Node n)        { current = n; }
}
