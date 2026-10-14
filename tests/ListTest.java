// Author: <your name>, ID <your ID> (Member 1)
// Tests for Node, DoublyLinkedList and DeletedStack.
// Compile and run from the project folder:
//   javac -d out src/Node.java src/DoublyLinkedList.java src/DeletedStack.java tests/ListTest.java
//   java -cp out ListTest
public class ListTest {

    static int passed = 0;
    static int failed = 0;

    // Prints one PASS/FAIL line and counts the result
    static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    // URLs from head to tail, e.g. "a,b,c" ("" for an empty list)
    static String forward(DoublyLinkedList list) {
        StringBuilder sb = new StringBuilder();
        for (Node cur = list.head; cur != null; cur = cur.next) {
            if (sb.length() > 0) sb.append(",");
            sb.append(cur.url);
        }
        return sb.toString();
    }

    // URLs from tail to head, e.g. "c,b,a"
    static String backward(DoublyLinkedList list) {
        StringBuilder sb = new StringBuilder();
        for (Node cur = list.tail; cur != null; cur = cur.prev) {
            if (sb.length() > 0) sb.append(",");
            sb.append(cur.url);
        }
        return sb.toString();
    }

    // True when walking backward gives exactly the reverse of walking forward
    static boolean consistent(DoublyLinkedList list) {
        String f = forward(list);
        String b = backward(list);
        if (f.isEmpty() || b.isEmpty()) {
            return f.isEmpty() && b.isEmpty() && list.head == null && list.tail == null;
        }
        String[] fa = f.split(",");
        String[] ba = b.split(",");
        if (fa.length != ba.length || fa.length != list.size) return false;
        for (int i = 0; i < fa.length; i++) {
            if (!fa[i].equals(ba[fa.length - 1 - i])) return false;
        }
        return list.head.prev == null && list.tail.next == null;
    }

    // Builds a list from the given URLs using addLast
    static DoublyLinkedList build(String... urls) {
        DoublyLinkedList list = new DoublyLinkedList();
        for (String u : urls) list.addLast(new Node(u));
        return list;
    }

    public static void main(String[] args) {

        System.out.println("--- Node ---");
        Node n0 = new Node("x.com");
        check("Node stores url", n0.url.equals("x.com"));
        check("Node metadata is set", n0.metadata != null && !n0.metadata.isEmpty());
        check("Node starts with prev == null", n0.prev == null);
        check("Node starts with next == null", n0.next == null);

        System.out.println("\n--- empty list ---");
        DoublyLinkedList empty = new DoublyLinkedList();
        check("empty: size is 0", empty.size() == 0);
        check("empty: isEmpty is true", empty.isEmpty());
        check("empty: getFirst is null", empty.getFirst() == null);
        check("empty: getLast is null", empty.getLast() == null);
        check("empty: findByUrl is null", empty.findByUrl("a") == null);
        empty.removeFirst();
        check("empty: removeFirst does not crash", empty.size() == 0);

        System.out.println("\n--- addLast ---");
        DoublyLinkedList list = build("a", "b", "c");
        check("addLast: forward order", forward(list).equals("a,b,c"));
        check("addLast: backward order", backward(list).equals("c,b,a"));
        check("addLast: size is 3", list.size() == 3);
        check("addLast: getFirst is a", list.getFirst().url.equals("a"));
        check("addLast: getLast is c", list.getLast().url.equals("c"));
        check("addLast: consistent", consistent(list));

        Node stale = new Node("stale");
        stale.next = new Node("junk1");
        stale.prev = new Node("junk2");
        DoublyLinkedList l1 = new DoublyLinkedList();
        l1.addLast(stale);
        check("addLast: clears stale links on empty list", stale.next == null && stale.prev == null);
        Node stale2 = new Node("stale2");
        stale2.next = new Node("junk3");
        l1.addLast(stale2);
        check("addLast: clears stale next on non-empty list", stale2.next == null && stale2.prev == stale);
        check("addLast: stale test list consistent", consistent(l1));

        System.out.println("\n--- remove ---");
        list = build("a", "b", "c");
        Node removed = list.head;
        list.remove(removed);
        check("remove head: forward", forward(list).equals("b,c"));
        check("remove head: backward", backward(list).equals("c,b"));
        check("remove head: new head has prev null", list.head.prev == null);
        check("remove head: size", list.size() == 2);
        check("remove head: removed node links cleared", removed.prev == null && removed.next == null);
        check("remove head: consistent", consistent(list));

        list = build("a", "b", "c");
        removed = list.tail;
        list.remove(removed);
        check("remove tail: forward", forward(list).equals("a,b"));
        check("remove tail: backward", backward(list).equals("b,a"));
        check("remove tail: new tail has next null", list.tail.next == null);
        check("remove tail: size", list.size() == 2);
        check("remove tail: removed node links cleared", removed.prev == null && removed.next == null);
        check("remove tail: consistent", consistent(list));

        list = build("a", "b", "c");
        removed = list.head.next;
        list.remove(removed);
        check("remove middle: forward", forward(list).equals("a,c"));
        check("remove middle: backward", backward(list).equals("c,a"));
        check("remove middle: size", list.size() == 2);
        check("remove middle: removed node links cleared", removed.prev == null && removed.next == null);
        check("remove middle: consistent", consistent(list));

        list = build("a");
        list.remove(list.head);
        check("remove only node: head null", list.head == null);
        check("remove only node: tail null", list.tail == null);
        check("remove only node: size 0", list.size() == 0);
        check("remove only node: isEmpty", list.isEmpty());

        list = build("a", "b");
        list.remove(list.head);
        list.remove(list.head);
        check("remove all one by one: empty and consistent", list.isEmpty() && consistent(list));
        list.addLast(new Node("z"));
        check("add after emptying works", forward(list).equals("z") && consistent(list));

        System.out.println("\n--- removeFirst ---");
        list = build("a", "b", "c");
        list.removeFirst();
        check("removeFirst: forward", forward(list).equals("b,c"));
        check("removeFirst: size", list.size() == 2);
        check("removeFirst: consistent", consistent(list));

        System.out.println("\n--- addAfter ---");
        list = new DoublyLinkedList();
        list.addAfter(null, new Node("x"));
        check("addAfter(null) on empty list", forward(list).equals("x") && list.head == list.tail);
        check("addAfter(null) on empty: size and consistent", list.size() == 1 && consistent(list));

        list = build("a", "b");
        list.addAfter(null, new Node("x"));
        check("addAfter(null) on non-empty: forward", forward(list).equals("x,a,b"));
        check("addAfter(null) on non-empty: backward", backward(list).equals("b,a,x"));
        check("addAfter(null) on non-empty: size", list.size() == 3);
        check("addAfter(null) on non-empty: consistent", consistent(list));

        list = build("a", "b", "c");
        list.addAfter(list.head, new Node("x"));
        check("addAfter middle: forward", forward(list).equals("a,x,b,c"));
        check("addAfter middle: backward", backward(list).equals("c,b,x,a"));
        check("addAfter middle: size", list.size() == 4);
        check("addAfter middle: consistent", consistent(list));

        list = build("a", "b", "c");
        list.addAfter(list.tail, new Node("x"));
        check("addAfter tail: forward", forward(list).equals("a,b,c,x"));
        check("addAfter tail: backward", backward(list).equals("x,c,b,a"));
        check("addAfter tail: tail moved", list.tail.url.equals("x") && list.tail.next == null);
        check("addAfter tail: size", list.size() == 4);
        check("addAfter tail: consistent", consistent(list));

        System.out.println("\n--- truncateAfter ---");
        list = build("a", "b", "c", "d", "e");
        Node c = list.findByUrl("c");
        list.truncateAfter(c);
        check("truncateAfter middle: forward", forward(list).equals("a,b,c"));
        check("truncateAfter middle: backward", backward(list).equals("c,b,a"));
        check("truncateAfter middle: size", list.size() == 3);
        check("truncateAfter middle: tail is c", list.tail == c && c.next == null);
        check("truncateAfter middle: consistent", consistent(list));

        list = build("a", "b", "c");
        list.truncateAfter(list.tail);
        check("truncateAfter tail: nothing changes", forward(list).equals("a,b,c") && list.size() == 3);

        list = build("a", "b", "c");
        list.truncateAfter(list.head);
        check("truncateAfter head: only head left", forward(list).equals("a") && list.size() == 1);
        check("truncateAfter head: consistent", consistent(list));

        list = build("a", "b", "c");
        list.truncateAfter(null);
        check("truncateAfter(null): list emptied", list.isEmpty() && list.head == null && list.tail == null);

        list = build("a", "b", "c");
        list.truncateAfter(list.findByUrl("a"));
        list.addLast(new Node("n"));
        check("add after truncate (visit-like): forward", forward(list).equals("a,n"));
        check("add after truncate (visit-like): consistent", consistent(list));

        System.out.println("\n--- findByUrl ---");
        list = build("a", "b", "c");
        check("findByUrl existing returns same node", list.findByUrl("b") == list.head.next);
        check("findByUrl missing returns null", list.findByUrl("zzz") == null);
        list = build("a", "dup", "b", "dup");
        check("findByUrl duplicate returns first", list.findByUrl("dup") == list.head.next);

        System.out.println("\n--- clear ---");
        list = build("a", "b", "c");
        list.clear();
        check("clear: empty list", list.isEmpty() && list.head == null && list.tail == null && list.size() == 0);
        list.addLast(new Node("again"));
        check("clear: list usable afterwards", forward(list).equals("again") && consistent(list));

        System.out.println("\n--- DeletedStack ---");
        DeletedStack st = new DeletedStack();
        check("stack: starts empty", st.isEmpty() && st.size() == 0);
        check("stack: pop on empty returns null", st.pop() == null);
        Node sa = new Node("a");
        Node sb = new Node("b");
        Node sc = new Node("c");
        st.push(sa);
        st.push(sb);
        st.push(sc);
        check("stack: not empty after pushes", !st.isEmpty());
        check("stack: size is 3", st.size() == 3);
        check("stack: first pop is last pushed (c)", st.pop() == sc);
        check("stack: second pop is b", st.pop() == sb);
        check("stack: third pop is a", st.pop() == sa);
        check("stack: fourth pop is null", st.pop() == null);
        check("stack: empty again, size 0", st.isEmpty() && st.size() == 0);
        st.push(sa);
        st.push(sb);
        st.clear();
        check("stack: clear empties it", st.isEmpty() && st.size() == 0 && st.pop() == null);
        st.push(sc);
        check("stack: usable after clear", st.pop() == sc);

        System.out.println("\n--- delete and restore (list + stack together) ---");
        list = build("a", "b", "c", "d");
        st = new DeletedStack();
        Node pred = list.findByUrl("b").prev;
        Node gone = list.findByUrl("b");
        list.remove(gone);
        st.push(gone);
        check("delete b: forward", forward(list).equals("a,c,d") && consistent(list));
        Node back = st.pop();
        list.addAfter(pred, back);
        check("restore b to old position: forward", forward(list).equals("a,b,c,d"));
        check("restore b: backward", backward(list).equals("d,c,b,a"));
        check("restore b: size and consistent", list.size() == 4 && consistent(list));

        list = build("a", "b", "c");
        st = new DeletedStack();
        Node first = list.head;
        list.remove(first);
        st.push(first);
        Node r = st.pop();
        list.addAfter(null, r);
        check("restore head to the front", forward(list).equals("a,b,c") && consistent(list));

        System.out.println("\n--- larger stress test ---");
        list = new DoublyLinkedList();
        for (int i = 0; i < 200; i++) list.addLast(new Node("p" + i));
        check("stress: 200 nodes added", list.size() == 200 && consistent(list));
        for (int i = 0; i < 200; i += 2) list.remove(list.findByUrl("p" + i));
        check("stress: removed every even node", list.size() == 100 && consistent(list));
        check("stress: first remaining is p1", list.head.url.equals("p1"));
        check("stress: last remaining is p199", list.tail.url.equals("p199"));
        while (!list.isEmpty()) list.removeFirst();
        check("stress: removeFirst until empty", list.size() == 0 && consistent(list));

        System.out.println("\n==============================");
        System.out.println("Passed: " + passed + "   Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
    }
}
