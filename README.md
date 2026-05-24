# Java File Handling Utility

A self-contained Java project demonstrating **10 essential file operations** using both the classic `java.io` API and the modern `java.nio.file` (NIO.2) API.

---

## Project Structure

```
FileHandlingUtility/
├── src/
│   └── FileHandlingUtility.java   ← single source file, all operations
└── README.md
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK    | 11 +    |

> `Files.readString()` (operation 3b) requires Java 11.  
> All other operations work with Java 8+.

---

## Compile & Run

```bash
# 1 – enter the source directory
cd FileHandlingUtility/src

# 2 – compile
javac FileHandlingUtility.java

# 3 – run
java FileHandlingUtility
```

A temporary directory called `file_demo/` is created in the working directory during the run and fully deleted at the end.

---

## Operations Covered

| # | Method | API Used | Description |
|---|--------|----------|-------------|
| 1 | `createDirectory` | NIO.2 | Creates a directory tree with `Files.createDirectories` |
| 2a | `writeFile` | NIO.2 | Writes/overwrites a file with `BufferedWriter` + `StandardOpenOption` |
| 2b | `appendToFile` | java.io | Appends text using `FileWriter(path, true)` |
| 3a | `readFileLineByLine` | NIO.2 | Reads large files efficiently with `BufferedReader` |
| 3b | `readFileAllAtOnce` | NIO.2 | Reads small files in one call with `Files.readString` (Java 11+) |
| 4 | `copyFile` | NIO.2 | Copies with `Files.copy` + `REPLACE_EXISTING` |
| 5 | `moveFile` | NIO.2 | Moves/renames with `Files.move` (atomic on same filesystem) |
| 6a | `deleteFile` | NIO.2 | Safe delete with `Files.deleteIfExists` |
| 6b | `deleteDirectory` | NIO.2 | Recursive delete via `Files.walkFileTree` + `SimpleFileVisitor` |
| 7 | `listDirectory` | NIO.2 | Lists directory entries with `Files.list` |
| 8 | `printFileMetadata` | NIO.2 | Reads size, timestamps, permissions via `BasicFileAttributes` |
| 9 | `findFilesByExtension` | NIO.2 | Recursive search with `Files.walk` + stream filter |
| 10 | `countWordsAndLines` | NIO.2 | Word/line/char count (like Unix `wc`) |

---

## Sample Output

```
=======================================================
  1. CREATE DIRECTORY
=======================================================
Directory created : /path/to/file_demo

=======================================================
  2a. WRITE FILE
=======================================================
Written to        : file_demo/sample.txt
Content preview   : Hello, File Handling! …

=======================================================
  3a. READ FILE (line by line)
=======================================================
  [ 1] Hello, File Handling!
  [ 2] Line 2: Java NIO rocks.
  [ 3] Line 3: End of demo.
  [ 4] Line 4: Appended line.

=======================================================
  8. FILE METADATA
=======================================================
  File        : /path/to/file_demo/sample.txt
  Size        : 72 bytes
  Created     : 2025-08-01 10:23:45
  Modified    : 2025-08-01 10:23:45
  Readable    : true
  Writable    : true
  Executable  : false
  Regular file: true
  Directory   : false

✔  All file operations completed successfully.
```

---

## Key Concepts

### java.io vs java.nio.file

| Aspect | `java.io` (`File`) | `java.nio.file` (NIO.2) |
|--------|-------------------|------------------------|
| API style | Legacy, boolean returns | Modern, exceptions on failure |
| Atomic move | ✗ | ✓ (`ATOMIC_MOVE` option) |
| Symbolic links | Limited | Full support |
| File attributes | Basic | Rich (`BasicFileAttributes`, POSIX, etc.) |
| Stream/walk | Manual recursion | `Files.walk`, `Files.list` |

### Error Handling
Every method wraps its logic in a `try-catch (IOException e)` block and prints a descriptive error rather than crashing the whole program — a good practice for utilities.

### OpenOption Flags (write mode)
```java
StandardOpenOption.CREATE           // create if not exists
StandardOpenOption.TRUNCATE_EXISTING // overwrite if exists
StandardOpenOption.APPEND            // add to end
```

---

## Extending the Utility

- **CSV / JSON read-write** — wrap `readFileAllAtOnce` + a parser (e.g., Jackson, Gson).
- **File watcher** — use `WatchService` (NIO.2) to monitor a directory for changes.
- **Zip/unzip** — use `java.util.zip.ZipOutputStream` and `ZipInputStream`.
- **Async I/O** — use `AsynchronousFileChannel` for non-blocking reads on large files.
