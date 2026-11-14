import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final int DEFAULT_CAPACITY = 50;
    public static void main(String[] args) {
        int capacity = DEFAULT_CAPACITY;
        if (args.length > 0) {
            try {
                int value = Integer.parseInt(args[0]);
                if (value > 0) {
                    capacity = value;
                }
            } catch (NumberFormatException e) {
                capacity = DEFAULT_CAPACITY;
            }
        }

        BrowserHistory history = new BrowserHistory("home", capacity);
        DeleteRestoreManager manager = new DeleteRestoreManager(history);
        HistoryFileManager files = new HistoryFileManager();
        Scanner input = new Scanner(System.in);
        printMenu(capacity);

        boolean running = true;
        while (running) {
            System.out.print("> ");
            if (!input.hasNextLine()) {
                running = false;
            } else {
                String line = input.nextLine().trim();
                if (line.length() > 0) {
                    String command = getCommand(line);
                    String argument = getArgument(line);

                    if (command.equals("visit")) {
                        if (argument.length() == 0) {
                            System.out.println("Usage: visit <url>");
                        } else {
                            history.visit(argument);
                            System.out.println("Visited: " + argument);
                        }

                    } else if (command.equals("prev")) {
                        int steps = parseSteps(argument);
                        if (steps < 1) {
                            System.out.println("Please enter a positive number.");
                        } else {
                            String url = history.back(steps);
                            printCurrent(url);
                        }

                    } else if (command.equals("next")) {
                        int steps = parseSteps(argument);
                        if (steps < 1) {
                            System.out.println("Please enter a positive number.");
                        } else {
                            String url = history.forward(steps);
                            printCurrent(url);
                        }

                    } else if (command.equals("current")) {
                        String page = history.currentPage();
                        if (page == null) {
                            System.out.println("No pages.");
                        } else {
                            System.out.println("Current page: " + page);
                        }

                    } else if (command.equals("history")) {
                        System.out.println(history.displayHistory());

                    } else if (command.equals("delete")) {
                        if (argument.length() == 0) {
                            boolean deleted = manager.deleteCurrent();
                            if (deleted) {
                                System.out.println("Deleted the current page.");
                            } else {
                                System.out.println("No pages.");
                            }
                        } else {
                            boolean deleted = manager.deleteUrl(argument);
                            if (deleted) {
                                System.out.println("Deleted: " + argument);
                            } else {
                                System.out.println("URL not found.");
                            }
                        }

                    } else if (command.equals("restore")) {
                        String url = manager.restore();
                        if (url == null) {
                            System.out.println("Nothing to restore.");
                        } else {
                            System.out.println("Restored: " + url);
                        }

                    } else if (command.equals("clear")) {
                        history.deleteHistory();
                        System.out.println("History cleared.");

                    } else if (command.equals("export")) {
                        if (argument.length() == 0) {
                            System.out.println("Usage: export <file>");
                        } else {
                            try {
                                files.exportHistory(history, argument);
                                System.out.println("History saved to " + argument);
                            } catch (IOException e) {
                                System.out.println("Could not export: " + e.getMessage());
                            }
                        }

                    } else if (command.equals("import")) {
                        if (argument.length() == 0) {
                            System.out.println("Usage: import <file>");
                        } else {
                            try {
                                int loaded = files.importHistory(history, argument);
                                System.out.println("Loaded " + loaded + " pages.");
                            } catch (IOException e) {
                                System.out.println("Could not import: " + e.getMessage());
                            }
                        }

                    } else if (command.equals("end")) {
                        running = false;
                        System.out.println("Goodbye.");

                    } else {
                        System.out.println("Unknown command.");
                        printMenu(capacity);
                    }
                }
            }
        }
        input.close();
    }

    private static void printMenu(int capacity) {
        System.out.println("Browser History (capacity " + capacity + ")");
        System.out.println("Commands:");
        System.out.println("  visit <url>     open a new page");
        System.out.println("  prev [n]        go back n pages (default 1)");
        System.out.println("  next [n]        go forward n pages (default 1)");
        System.out.println("  current         show the active page");
        System.out.println("  history         show all pages, [<] marks the active one");
        System.out.println("  delete [url]    delete a page (no url = current page)");
        System.out.println("  restore         restore the last deleted page");
        System.out.println("  clear           delete all history");
        System.out.println("  export <file>   save history to a txt file");
        System.out.println("  import <file>   load history from a txt file");
        System.out.println("  end             quit");
    }

    private static void printCurrent(String url) {
        if (url == null) {
            System.out.println("No pages.");
        } else {
            System.out.println("Current page: " + url);
        }
    }

    private static String getCommand(String line) {
        int space = line.indexOf(' ');
        if (space == -1) {
            return line.toLowerCase();
        }
        return line.substring(0, space).toLowerCase();
    }

    private static String getArgument(String line) {
        int space = line.indexOf(' ');
        if (space == -1) {
            return "";
        }
        return line.substring(space + 1).trim();
    }

    private static int parseSteps(String argument) {
        if (argument.length() == 0) {
            return 1;
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}