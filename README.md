# CSCI 2304 - Browser History Management

**Group:** <number>
**Course:** CSCI 2304 - Data Structures & Algorithms

## Team

| Name | Student ID | Responsibility |
|------|------------|----------------|
| <Abilmansur> | <21490> | Node, DoublyLinkedList, DeletedStack, delete_history, console menu |
| <Natavan> | <22881> | BrowserHistory: visit, back, forward, current_page, display_history, capacity |
| <Bakhshali> | <17151> | Delete/restore, export/import |

## Description

A browser history manager built on a doubly linked list implemented from
scratch in Java (no built-in linked list or deque classes). The program runs
in the console and supports navigation, branching history, a capacity limit,
delete/restore, and export/import to txt files.

## Project structure

- `src/` - source code
  - `Node.java` - one page (URL, metadata, prev, next)
  - `DoublyLinkedList.java` - our own doubly linked list
  - `DeletedStack.java` - stack of deleted pages for restore
  - `BrowserHistory.java` - history manager with the cursor
  - `Main.java` - console menu
- `tests/` - test script covering edge cases

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

## Design decisions

- **Capacity:** <e.g. 50 pages; the oldest page is removed when exceeded>
- **Metadata:** <timestamp / title / visit number>
- **Restore:** <where a restored page goes>
- **Import/export format:** <one URL per line>

## Git workflow

1. `git pull` before starting work
2. Edit only your own files
3. `git add .` then `git commit -m "message"` then `git push`
