
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DeleteRestoreTest {
    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + name);
        } else {
            failed++;
            System.out.println("FAIL: " + name);
        }
    }

    public static void main(String[] args) throws IOException {
        BrowserHistory history = new BrowserHistory("home", 3);
        DeleteRestoreManager manager = new DeleteRestoreManager(history);

        history.visit("a");
        history.visit("b");

        check("delete existing URL", manager.deleteUrl("a"));
        check("deleted URL is gone",
                history.getList().findByUrl("a") == null);
        check("delete missing URL", !manager.deleteUrl("missing"));

        check("restore deleted URL", "a".equals(manager.restore()));
        check("restored page is in history",
                history.getList().findByUrl("a") != null);

        check("delete current page", manager.deleteCurrent());
        check("restore current page", "a".equals(manager.restore()));
        check("restore empty stack", manager.restore() == null);

        BrowserHistory single = new BrowserHistory("only", 3);
        DeleteRestoreManager singleManager = new DeleteRestoreManager(single);

        check("delete only page", singleManager.deleteCurrent());
        check("empty history", single.getList().isEmpty());
        check("current is null", single.getCurrent() == null);
        check("restore only page", "only".equals(singleManager.restore()));
        check("current restored",
                single.getCurrent() != null &&
                "only".equals(single.getCurrent().url));

        BrowserHistory first = new BrowserHistory("home", 3);
        first.visit("a");
        first.visit("b");
        first.back(2);

        DeleteRestoreManager firstManager = new DeleteRestoreManager(first);
        check("delete first current page", firstManager.deleteCurrent());
        check("cursor moves to next",
                "a".equals(first.getCurrent().url));

        BrowserHistory full = new BrowserHistory("home", 3);
        full.visit("a");
        full.visit("b");

        DeleteRestoreManager fullManager = new DeleteRestoreManager(full);
        fullManager.deleteUrl("a");
        full.visit("c");

        check("restore with full capacity",
                "a".equals(fullManager.restore()));
        check("capacity remains 3", full.getList().size() == 3);
        check("oldest page pruned",
                full.getList().findByUrl("home") == null);

        BrowserHistory multiple = new BrowserHistory("home", 5);
        multiple.visit("a");
        multiple.visit("b");

        DeleteRestoreManager multipleManager =
                new DeleteRestoreManager(multiple);

        multipleManager.deleteUrl("a");
        multipleManager.deleteUrl("b");

        check("restore last deleted first",
                "b".equals(multipleManager.restore()));
        check("restore previous deleted second",
                "a".equals(multipleManager.restore()));

        HistoryFileManager files = new HistoryFileManager();
        Path file = Files.createTempFile("history-", ".txt");

        try {
            files.exportHistory(history, file.toString());

            BrowserHistory imported = new BrowserHistory("start", 3);
            int count = files.importHistory(imported, file.toString());

            check("import count", count == 3);
            check("import respects capacity",
                    imported.getList().size() == 3);
            check("import keeps last page",
                    "a".equals(imported.getCurrent().url));

            Files.write(file, List.of("one", "two", "three", "four"));

            BrowserHistory limited = new BrowserHistory("start", 2);
            int loaded = files.importHistory(limited, file.toString());

            check("four URLs processed", loaded == 4);
            check("import capacity limit", limited.getList().size() == 2);
            check("oldest remaining page",
                    "three".equals(limited.getList().getFirst().url));
            check("newest remaining page",
                    "four".equals(limited.getCurrent().url));

            files.exportHistory(limited, file.toString());

            check("export order",
                    Files.readAllLines(file).equals(
                            List.of("three", "four")));

            Path missing = file.resolveSibling("missing-history-test.txt");
            Files.deleteIfExists(missing);

            boolean caught = false;
            try {
                files.importHistory(limited, missing.toString());
            } catch (IOException e) {
                caught = true;
            }

            check("missing file throws IOException", caught);
            check("history kept after missing file",
                    "four".equals(limited.getCurrent().url));

        } finally {
            Files.deleteIfExists(file);
        }

        System.out.println("--------------------");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);

        if (failed > 0) {
            throw new AssertionError("Tests failed");
        }
    }
}
