
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HistoryFileManager {

    public void exportHistory(BrowserHistory history, String file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Path.of(file), StandardCharsets.UTF_8)) {
            Node page = history.getList().getFirst();
            while (page != null) {
                writer.write(page.url);
                writer.newLine();
                page = page.next;
            }
        }
    }

    public int importHistory(BrowserHistory history, String file) throws IOException {
        //check if the file opens before history clean
        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(file), StandardCharsets.UTF_8)) {
            history.deleteHistory();
            int loaded = 0;
            String url;
            while ((url = reader.readLine()) != null) {
                if (url.isEmpty()) continue;
                history.visit(url);
                loaded++;
            }
            return loaded;
        }
    }
}
