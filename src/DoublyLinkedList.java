public class DoublyLinkedList {
    Node head;
    Node tail;
    int size;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Node getFirst() {
        return head;
    }

    public Node getLast() {
        return tail;
    }

    public void addLast(Node n) {
        if (head == null) {
            head = n;
            tail = n;
            n.next = null;
            n.prev = null;
        } else {
            n.prev = tail;
            tail.next = n;
            tail = n;
            n.next = null;
        }
        size++;
    }

    public void addAfter(Node pred, Node n) {
        if (pred == null) {
            n.prev = null;
            n.next = head;
            if (head != null) {
                head.prev = n;
            } else {
                tail = n;          // list was empty
            }
            head = n;
        } else {
            Node after = pred.next;
            n.next = after;
            n.prev = pred;
            pred.next = n;
            if (after != null) {
                after.prev = n;
            } else {
                tail = n;          // pred was the tail
            }
        }
        size++;
    }

    public void remove(Node n) {
        if (n.prev == null) {
            head = n.next;        // n was the head
        } else {
            n.prev.next = n.next;
        }
        if (n.next == null) {
            tail = n.prev;        // n was the tail
        } else {
            n.next.prev = n.prev;
        }
        n.prev = null;
        n.next = null;
        size--;
    }

    public void removeFirst() {
        if (head != null) {
            remove(head);
        }
    }

    public void truncateAfter(Node n) {
        if (n == null) {
            clear();
            return;
        }
        Node cur = n.next;
        while (cur != null) {
            Node following = cur.next;
            cur.prev = null;
            cur.next = null;
            size--;
            cur = following;
        }
        n.next = null;
        tail = n;
    }

    public Node findByUrl(String url) {
        Node cur = head;
        while (cur != null) {
            if (cur.url.equals(url)) {
                return cur;
            }
            cur = cur.next;
        }
        return null;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}
