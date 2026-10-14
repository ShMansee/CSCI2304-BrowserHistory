public class BrowserHistoryTest {
    private static int passed = 0, failed = 0;
    private static void check(String name, Object expected, Object actual) {
        boolean ok = (expected == null) ? actual == null : expected.equals(actual);
        if (ok) { passed++; System.out.println("PASS  " + name); }
        else    { failed++; System.out.println("FAIL  " + name + "  expected=" + expected + " actual=" + actual); }
    }

    public static void main(String[] args) {

        BrowserHistory h = new BrowserHistory("home", 50);
        check("constructor: size is 1", 1, h.getList().size());
        check("constructor: current is homepage", "home", h.getCurrent().url);


        h.visit("a"); h.visit("b");
        check("visit: size 3", 3, h.getList().size());
        check("visit: current is newest", "b", h.getCurrent().url);


        BrowserHistory two = new BrowserHistory("p1", 50);
        two.visit("p2");
        for (int i = 0; i < 100; i++) two.back(1);
        check("back x100 on 2 pages stops at oldest", "p1", two.getCurrent().url);
        check("back(100) at oldest returns oldest", "p1", two.back(100));
        check("back x100 did not delete pages", 2, two.getList().size());


        check("forward(100) stops at newest", "p2", two.forward(100));
        check("forward at newest stays", "p2", two.forward(1));


        BrowserHistory m = new BrowserHistory("h", 50);
        m.visit("a"); m.visit("b"); m.visit("c");
        check("back(2)", "a", m.back(2));
        check("forward(1)", "b", m.forward(1));


        m.back(2);
        m.visit("x");
        check("branch: size is 2", 2, m.getList().size());
        check("branch: current is x", "x", m.getCurrent().url);
        check("branch: old pages unreachable", "x", m.forward(5));
        check("branch: history text", "1. h\n2. x [<]", m.displayHistory());


        BrowserHistory cap = new BrowserHistory("1", 3);
        cap.visit("2"); cap.visit("3"); cap.visit("4"); // 1 pruned
        check("capacity: size stays 3", 3, cap.getList().size());
        check("capacity: oldest is 2", "2", cap.getList().getFirst().url);
        cap.visit("5");
        check("capacity: oldest is 3", "3", cap.getList().getFirst().url);
        check("capacity: current is newest", "5", cap.getCurrent().url);
        check("capacity: back stops at oldest kept", "3", cap.back(99));


        BrowserHistory one = new BrowserHistory("a", 1);
        one.visit("b");
        check("capacity 1: only newest kept", 1, one.getList().size());
        check("capacity 1: current", "b", one.getCurrent().url);


        BrowserHistory cp = new BrowserHistory("site", 50);
        cp.visit("site2");
        String before = cp.getCurrent().url;
        String info = cp.currentPage();
        check("currentPage contains url", true, info.startsWith("site2"));
        check("currentPage contains metadata", true, info.contains(cp.getCurrent().metadata));
        check("currentPage did not move cursor", before, cp.getCurrent().url);


        BrowserHistory d = new BrowserHistory("a", 50);
        d.visit("b"); d.visit("c"); d.back(1);
        check("display marks current", "1. a\n2. b [<]\n3. c", d.displayHistory());


        d.getDeletedStack().push(new Node("junk"));
        d.deleteHistory();
        check("deleteHistory: list empty", true, d.getList().isEmpty());
        check("deleteHistory: current null", null, d.getCurrent());
        check("deleteHistory: stack cleared", true, d.getDeletedStack().isEmpty());
        check("empty: back returns null", null, d.back(1));
        check("empty: forward returns null", null, d.forward(1));
        check("empty: currentPage returns null", null, d.currentPage());
        check("empty: display says No pages.", "No pages.", d.displayHistory());


        d.visit("fresh");
        check("visit after delete: size 1", 1, d.getList().size());
        check("visit after delete: current", "fresh", d.getCurrent().url);

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}