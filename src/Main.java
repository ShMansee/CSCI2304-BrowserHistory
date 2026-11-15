import java.io.IOException;
import java.util.Scanner;

// console menu: reads what the user types and calls the right method
// (Main only does input/output, the real work is in BrowserHistory)
// you can give a capacity when running, like: java -cp out Main 3

public class Main {
    // used if nobody gives a capacity
    private static final int DEFAULT_CAPACITY = 50;

    public static void main(String[] args) {
        // use the number from the command line if there is one, otherwise 50
        int capacity = DEFAULT_CAPACITY;
        if (args.length > 0) {
            try {
                int value = Integer.parseInt(args[0]);
                if (value > 0) {
                    capacity = value;
                }
            } catch (NumberFormatException e) {
                // wasn't a number so just keep 50
                capacity = DEFAULT_CAPACITY;
            }
        }

        // start with "home" as the first page
        BrowserHistory history = new BrowserHistory("home", capacity);
        DeleteRestoreManager manager = new DeleteRestoreManager(history);
        HistoryFileManager files = new HistoryFileManager();
        Scanner input = new Scanner(System.in);
        printMenu(capacity);

        // keeps asking for commands until the user types end
        boolean running = true;
        while (running) {
            System.out.print("> ");
            // no more input, so stop
            if (!input.hasNextLine()) {
                running = false;
            } else {
                String line = input.nextLine().trim();
                // skip empty lines
                if (line.length() > 0) {
                    // first word is the command, the rest is the argument
                    String command = getCommand(line);
                    String argument = getArgument(line);

                    // visit <url>
                    if (command.equals("visit")) {
                        if (argument.length() == 0) {
                            System.out.println("Usage: visit <url>");
                        } else {
                            history.visit(argument);
                            System.out.println("Visited: " + argument);
                        }

                        // prev [n] - go back, n has to be positive
                    } else if (command.equals("prev")) {
                        int steps = parseSteps(argument);
                        if (steps < 1) {
                            System.out.println("Please enter a positive number.");
                        } else {
                            String url = history.back(steps);
                            printCurrent(url);
                        }

                        // next [n] - go forward, n has to be positive
                    } else if (command.equals("next")) {
                        int steps = parseSteps(argument);
                        if (steps < 1) {
                            System.out.println("Please enter a positive number.");
                        } else {
                            String url = history.forward(steps);
                            printCurrent(url);
                        }

                        // current - show the page we are on
                    } else if (command.equals("current")) {
                        String page = history.currentPage();
                        // null = history is empty
                        if (page == null) {
                            System.out.println("No pages.");
                        } else {
                            System.out.println("Current page: " + page);
                        }

                        // history - show all pages
                    } else if (command.equals("history")) {
                        System.out.println(history.displayHistory());

                        // delete [url] - no url means delete the current page
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

                        // restore - bring back the last deleted page
                    } else if (command.equals("restore")) {
                        String url = manager.restore();
                        // null = nothing was deleted
                        if (url == null) {
                            System.out.println("Nothing to restore.");
                        } else {
                            System.out.println("Restored: " + url);
                        }

                        // clear - delete everything
                    } else if (command.equals("clear")) {
                        history.deleteHistory();
                        System.out.println("History cleared.");

                        // export <file> - save history to a file
                    } else if (command.equals("export")) {
                        if (argument.length() == 0) {
                            System.out.println("Usage: export <file>");
                        } else {
                            // can fail (wrong file name etc) so we catch the error
                            try {
                                files.exportHistory(history, argument);
                                System.out.println("History saved to " + argument);
                            } catch (IOException e) {
                                System.out.println("Could not export: " + e.getMessage());
                            }
                        }

                        // import <file> - load history from a file
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

                        // end - quit
                    } else if (command.equals("end")) {
                        running = false;
                        System.out.println("Goodbye.");

                    } else {
                        // didn't recognise it, show the commands again
                        System.out.println("Unknown command.");
                        printMenu(capacity);
                    }
                }
            }
        }
        input.close();
    }

    // prints all the commands
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

    // prints the page after prev/next, or "No pages." if empty
    private static void printCurrent(String url) {
        if (url == null) {
            System.out.println("No pages.");
        } else {
            System.out.println("Current page: " + url);
        }
    }

    // gets the first word of the line (the command)
    private static String getCommand(String line) {
        int space = line.indexOf(' ');
        if (space == -1) {
            return line.toLowerCase();
        }
        return line.substring(0, space).toLowerCase();
    }

    // gets everything after the first word, or "" if there is nothing
    private static String getArgument(String line) {
        int space = line.indexOf(' ');
        if (space == -1) {
            return "";
        }
        return line.substring(space + 1).trim();
    }

    // turns the text into a number of steps
    // no number given = 1, not a number = -1
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
