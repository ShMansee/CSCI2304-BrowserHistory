# CSCI 2304 - Browser History Management

**Group:** <number>
**Course:** CSCI 2304 - Data Structures & Algorithms

## Team

| Name | Student ID | Responsibility |
|------|------------|----------------|
| Abilmansur    | <21490> | Node, DoublyLinkedList, DeletedStack, delete_history, console menu |
| Natavan | <22881> | BrowserHistory: visit, back, forward, current_page, display_history, capacity 
| Bakhshali | <17151> | Delete/restore, export/import |

## Description

A browser history manager built on a doubly linked list implemented from
scratch in Java (no built-in linked list or deque classes). The program runs
in the console and supports navigation, branching history, a capacity limit,
delete/restore, and export/import to txt files.

## File ownership

| File | Owner |
|------|-------|
| `src/Node.java` | Member 1 |
| `src/DoublyLinkedList.java` | Member 1 |
| `src/DeletedStack.java` | Member 1 |
| `src/Main.java` | Member 1 |
| `src/BrowserHistory.java` | Member 2 |
| `src/DeleteRestoreManager.java` | Member 3 |
| `src/HistoryFileManager.java` | Member 3 |
| `tests/` | Each member tests their own part |

## How to compile and run

    mkdir out
    javac -d out src/*.java
    java -cp out Main

## Console commands

| Command | Action |
|---------|--------|
| visit <url> | Open a new page (clears forward history) |
| prev [n] | Go back n pages |
| next [n] | Go forward n pages |
| current | Show the active page |
| history | Show all pages, `[<]` marks the active one |
| delete | Delete a page (can be restored) |
| restore | Restore the last deleted page |
| clear | Delete all history |
| export <file> | Save history to a txt file |
| import <file> | Load history from a txt file |
| end | Quit |

## Agreed design (method contract)

### Member 1: base classes

**Node**: fields `String url`, `String metadata` (timestamp), `Node prev`, `Node next`. Constructor `Node(String url)`.

**DoublyLinkedList** (fields `head`, `tail`, `size`)

| Method | What it does |
|--------|--------------|
| `int size()` | number of nodes |
| `boolean isEmpty()` | true if no nodes |
| `Node getFirst()` / `Node getLast()` | first or last node, `null` if empty |
| `void addLast(Node n)` | appends at the end |
| `void addAfter(Node pred, Node n)` | inserts `n` after `pred` (`pred == null` means at the front) |
| `void remove(Node n)` | unlinks `n` and reconnects its neighbours |
| `void removeFirst()` | removes the oldest node (capacity pruning) |
| `void truncateAfter(Node n)` | removes everything after `n` (`null` removes all) |
| `Node findByUrl(String url)` | first node with that URL, or `null` |
| `void clear()` | removes all nodes |

**DeletedStack**: `push(Node n)`, `Node pop()` (`null` if empty), `boolean isEmpty()`, `int size()`, `void clear()`.

### Member 2: BrowserHistory

| Method | What it does |
|--------|--------------|
| `BrowserHistory(String homepage, int capacity)` | creates the history with one page, current on it |
| `void visit(String url)` | cuts everything after current, appends the new node, moves current to it, then removes the oldest if size exceeds capacity |
| `String back(int steps)` | moves back up to `steps` pages, stops at the oldest, returns the current URL |
| `String forward(int steps)` | same, but forward |
| `String currentPage()` | URL plus metadata of the current page, changes nothing |
| `String displayHistory()` | whole chain, head to tail, `[<]` marks the current page |
| `void deleteHistory()` | clears the list and the deleted stack, current becomes `null` |
| `DoublyLinkedList getList()` | gives access to the list |
| `DeletedStack getDeletedStack()` | gives access to the stack |
| `Node getCurrent()` / `void setCurrent(Node n)` | read or move the cursor |

### Member 3: features

**DeleteRestoreManager** (constructor takes a `BrowserHistory`)

| Method | What it does |
|--------|--------------|
| `boolean deleteUrl(String url)` | finds and removes the page, pushes it on the stack, returns false if not found |
| `boolean deleteCurrent()` | same, for the active page |
| `String restore()` | pops the stack and puts the page back, returns its URL, or `null` if the stack is empty |

**HistoryFileManager**

| Method | What it does |
|--------|--------------|
| `void exportHistory(BrowserHistory h, String file)` | writes one URL per line, oldest first (throws `IOException`) |
| `int importHistory(BrowserHistory h, String file)` | calls `deleteHistory()`, then `visit(url)` for each line, returns the number loaded |

### Agreed rules
1. `back` and `forward` need a positive number; `Main` rejects anything else.
2. If the history is empty (`current == null`), `back`, `forward` and `currentPage` return `null`, and `Main` prints "No pages."
3. `visit` must also work when `current` is `null`.
4. A restored page goes to the end of the list. If the history is already full, the oldest page is pruned.
5. When the current page is deleted, the cursor moves to the previous page (or the next one if there is none).
6. Default capacity is 50; a small value (like 3) is used for testing.
7. Metadata is a timestamp string.
8. Export format is one URL per line. Import goes through `visit()`, so capacity pruning applies automatically.

## Git workflow

1. `git pull` before starting work
2. Edit only your own files
3. `git add .` then `git commit -m "message"` then `git push`
