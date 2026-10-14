public class DeletedStack {

    private static class Item {
        Node node;
        Item below;

        Item(Node node, Item below) {
            this.node = node;
            this.below = below;
        }
    }

    private Item top;   
    private int count;

    public void push(Node n) {
        top = new Item(n, top);
        count++;
    }

    public Node pop() {
        if (top == null) {
            return null;
        }
        Node n = top.node;
        top = top.below;
        count--;
        return n;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return count;
    }

    public void clear() {
        top = null;
        count = 0;
    }
}
