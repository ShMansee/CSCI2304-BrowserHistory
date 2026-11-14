
public class DeleteRestoreManager {
    private final BrowserHistory history;

    public DeleteRestoreManager(BrowserHistory history) {
        this.history = history;
    }

    public boolean deleteUrl(String url) {
        Node page = history.getList().findByUrl(url);
        if (page == null) return false;
        return deletePage(page);
    }

    public boolean deleteCurrent() {
        Node page = history.getCurrent();
        if (page == null) return false;
        return deletePage(page);
    }

    private boolean deletePage(Node page) {
        DoublyLinkedList list = history.getList();
        if (page == history.getCurrent()) {
            if (page.prev != null) {
                history.setCurrent(page.prev);
            } else {
                history.setCurrent(page.next);
            }
        }
        list.remove(page);
        history.getDeletedStack().push(page);
        return true;
    }

    public String restore() {
        Node page = history.getDeletedStack().pop();
        if (page == null) return null;

        DoublyLinkedList list = history.getList();

        //move to the end so visit() doesnt remove forward pages
        history.setCurrent(list.getLast());
        history.visit(page.url);

        //keep the original node+its timestamp
        Node newPage = history.getCurrent();
        list.remove(newPage);
        list.addLast(page);
        history.setCurrent(page);
        return page.url;
    }
}
