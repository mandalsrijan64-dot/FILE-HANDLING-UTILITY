import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ============================================================
 *  FileHandlingUtility.java
 *  ------------------------------------------------------------
 *  A comprehensive Java utility that demonstrates common file
 *  operations using both the legacy java.io API and the modern
 *  java.nio.file (NIO.2) API introduced in Java 7+.
 *
 *  Operations covered:
 *    1. Create a file / directory
 *    2. Write text to a file (overwrite & append)
 *    3. Read a file (line-by-line & all-at-once)
 *    4. Copy a file
 *    5. Move / rename a file
 *    6. Delete a file / directory
 *    7. List directory contents
 *    8. Query file metadata (size, timestamps, permissions)
 *    9. Search files by extension
 *   10. Count words / lines in a file
 *
 *  Usage:
 *    javac FileHandlingUtility.java
 *    java  FileHandlingUtility
 * ============================================================
 */
public class FileHandlingUtility {

    // ── demo working directory ────────────────────────────────
    private static final String DEMO_DIR = "file_demo";

    // ── tiny helper: print a section banner ──────────────────
    private static void banner(String title) {
        System.out.println("\n" + "=".repeat(55));
        System.out.println("  " + title);
        System.out.println("=".repeat(55));
    }

    // ─────────────────────────────────────────────────────────
    //  MAIN – runs every demo in sequence
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        FileHandlingUtility util = new FileHandlingUtility();

        // 1. Setup sandbox directory
        util.createDirectory(DEMO_DIR);

        // 2. Write operations
        String filePath = DEMO_DIR + "/sample.txt";
        util.writeFile(filePath,
                "Hello, File Handling!\nLine 2: Java NIO rocks.\nLine 3: End of demo.\n",
                false);
        util.appendToFile(filePath, "Line 4: Appended line.\n");

        // 3. Read operations
        util.readFileLineByLine(filePath);
        util.readFileAllAtOnce(filePath);

        // 4. Copy
        String copyPath = DEMO_DIR + "/sample_copy.txt";
        util.copyFile(filePath, copyPath);

        // 5. Move / rename
        String movedPath = DEMO_DIR + "/sample_renamed.txt";
        util.moveFile(copyPath, movedPath);

        // 6. Metadata
        util.printFileMetadata(filePath);

        // 7. List directory
        util.listDirectory(DEMO_DIR);

        // 8. Word / line count
        util.countWordsAndLines(filePath);

        // 9. Search by extension
        util.findFilesByExtension(DEMO_DIR, ".txt");

        // 10. Delete individual files then the directory
        util.deleteFile(movedPath);
        util.deleteDirectory(DEMO_DIR);

        System.out.println("\n✔  All file operations completed successfully.\n");
    }

    // ─────────────────────────────────────────────────────────
    //  1. CREATE DIRECTORY
    // ─────────────────────────────────────────────────────────
    /**
     * Creates a directory (and any missing parent directories).
     *
     * @param dirPath path to the new directory
     */
    public void createDirectory(String dirPath) {
        banner("1. CREATE DIRECTORY");
        try {
            Path path = Paths.get(dirPath);
            Files.createDirectories(path);          // NIO.2 – idempotent
            System.out.println("Directory created : " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("ERROR creating directory: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  2a. WRITE FILE (overwrite or create)
    // ─────────────────────────────────────────────────────────
    /**
     * Writes text content to a file, creating it if it doesn't exist.
     *
     * @param filePath path to the target file
     * @param content  text to write
     * @param append   if true, appends; if false, overwrites
     */
    public void writeFile(String filePath, String content, boolean append) {
        banner("2a. WRITE FILE");
        // OpenOption determines overwrite vs append behaviour
        OpenOption mode = append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING;

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(filePath),
                StandardOpenOption.CREATE, mode)) {

            writer.write(content);
            System.out.println("Written to        : " + filePath);
            System.out.println("Content preview   : " + content.split("\n")[0] + " …");
        } catch (IOException e) {
            System.err.println("ERROR writing file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  2b. APPEND TO FILE
    // ─────────────────────────────────────────────────────────
    /**
     * Appends a line of text to an existing file.
     *
     * @param filePath path to the file
     * @param text     text to append
     */
    public void appendToFile(String filePath, String text) {
        banner("2b. APPEND TO FILE");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath, true))) {   // java.io append mode

            writer.write(text);
            System.out.println("Appended to       : " + filePath);
            System.out.println("Appended text     : " + text.trim());
        } catch (IOException e) {
            System.err.println("ERROR appending to file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  3a. READ FILE – line by line
    // ─────────────────────────────────────────────────────────
    /**
     * Reads a file line by line using BufferedReader (memory-efficient
     * for large files).
     *
     * @param filePath path to the file
     */
    public void readFileLineByLine(String filePath) {
        banner("3a. READ FILE (line by line)");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                System.out.printf("  [%2d] %s%n", lineNo++, line);
            }
        } catch (IOException e) {
            System.err.println("ERROR reading file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  3b. READ FILE – all at once
    // ─────────────────────────────────────────────────────────
    /**
     * Reads the entire file into a String in one call (convenient for
     * small files).
     *
     * @param filePath path to the file
     */
    public void readFileAllAtOnce(String filePath) {
        banner("3b. READ FILE (all at once)");
        try {
            String content = Files.readString(Paths.get(filePath));  // Java 11+
            System.out.println("Full content:\n" + content);
        } catch (IOException e) {
            System.err.println("ERROR reading file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  4. COPY FILE
    // ─────────────────────────────────────────────────────────
    /**
     * Copies a file to a new location, replacing it if it already exists.
     *
     * @param source      source file path
     * @param destination destination file path
     */
    public void copyFile(String source, String destination) {
        banner("4. COPY FILE");
        try {
            Files.copy(Paths.get(source),
                       Paths.get(destination),
                       StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied            : " + source);
            System.out.println("           ──►    : " + destination);
        } catch (IOException e) {
            System.err.println("ERROR copying file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  5. MOVE / RENAME FILE
    // ─────────────────────────────────────────────────────────
    /**
     * Moves (or renames) a file atomically when possible.
     *
     * @param source      current path
     * @param destination new path
     */
    public void moveFile(String source, String destination) {
        banner("5. MOVE / RENAME FILE");
        try {
            Files.move(Paths.get(source),
                       Paths.get(destination),
                       StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved             : " + source);
            System.out.println("           ──►    : " + destination);
        } catch (IOException e) {
            System.err.println("ERROR moving file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  6a. DELETE FILE
    // ─────────────────────────────────────────────────────────
    /**
     * Deletes a single file.  Prints a warning if the file doesn't exist
     * instead of throwing.
     *
     * @param filePath path to the file
     */
    public void deleteFile(String filePath) {
        banner("6a. DELETE FILE");
        try {
            boolean deleted = Files.deleteIfExists(Paths.get(filePath));
            if (deleted) {
                System.out.println("Deleted           : " + filePath);
            } else {
                System.out.println("File not found (skip): " + filePath);
            }
        } catch (IOException e) {
            System.err.println("ERROR deleting file: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  6b. DELETE DIRECTORY (recursively)
    // ─────────────────────────────────────────────────────────
    /**
     * Deletes a directory and ALL its contents recursively.
     * Uses Files.walkFileTree so it handles nested structures.
     *
     * @param dirPath path to the directory
     */
    public void deleteDirectory(String dirPath) {
        banner("6b. DELETE DIRECTORY (recursive)");
        Path root = Paths.get(dirPath);
        if (!Files.exists(root)) {
            System.out.println("Directory not found (skip): " + dirPath);
            return;
        }
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    System.out.println("  Removed file      : " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                        throws IOException {
                    Files.delete(dir);
                    System.out.println("  Removed directory : " + dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("ERROR deleting directory: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  7. LIST DIRECTORY CONTENTS
    // ─────────────────────────────────────────────────────────
    /**
     * Lists all entries (files and sub-directories) in the given directory,
     * distinguishing files from directories.
     *
     * @param dirPath path to the directory
     */
    public void listDirectory(String dirPath) {
        banner("7. LIST DIRECTORY CONTENTS");
        try (var stream = Files.list(Paths.get(dirPath))) {
            stream.sorted().forEach(p -> {
                String type = Files.isDirectory(p) ? "[DIR ] " : "[FILE] ";
                System.out.println("  " + type + p.getFileName());
            });
        } catch (IOException e) {
            System.err.println("ERROR listing directory: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  8. FILE METADATA
    // ─────────────────────────────────────────────────────────
    /**
     * Prints key metadata for a file: size, timestamps, and
     * read/write/execute permissions.
     *
     * @param filePath path to the file
     */
    public void printFileMetadata(String filePath) {
        banner("8. FILE METADATA");
        try {
            Path path = Paths.get(filePath);
            BasicFileAttributes attrs =
                    Files.readAttributes(path, BasicFileAttributes.class);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            System.out.printf("  File        : %s%n",  path.toAbsolutePath());
            System.out.printf("  Size        : %d bytes%n", attrs.size());
            System.out.printf("  Created     : %s%n",  sdf.format(new Date(attrs.creationTime().toMillis())));
            System.out.printf("  Modified    : %s%n",  sdf.format(new Date(attrs.lastModifiedTime().toMillis())));
            System.out.printf("  Readable    : %b%n",  Files.isReadable(path));
            System.out.printf("  Writable    : %b%n",  Files.isWritable(path));
            System.out.printf("  Executable  : %b%n",  Files.isExecutable(path));
            System.out.printf("  Regular file: %b%n",  attrs.isRegularFile());
            System.out.printf("  Directory   : %b%n",  attrs.isDirectory());
        } catch (IOException e) {
            System.err.println("ERROR reading metadata: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  9. FIND FILES BY EXTENSION
    // ─────────────────────────────────────────────────────────
    /**
     * Recursively searches a directory tree and returns all files
     * matching a given extension (e.g., ".txt", ".java").
     *
     * @param dirPath   root directory to search
     * @param extension file extension to match (include the dot)
     */
    public void findFilesByExtension(String dirPath, String extension) {
        banner("9. FIND FILES BY EXTENSION (" + extension + ")");
        try (var stream = Files.walk(Paths.get(dirPath))) {
            List<Path> matches = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(extension))
                    .collect(Collectors.toList());

            if (matches.isEmpty()) {
                System.out.println("  No files found with extension: " + extension);
            } else {
                matches.forEach(p -> System.out.println("  Found: " + p));
            }
        } catch (IOException e) {
            System.err.println("ERROR searching files: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    //  10. WORD & LINE COUNT
    // ─────────────────────────────────────────────────────────
    /**
     * Counts the number of lines, words, and characters in a text file,
     * similar to the Unix {@code wc} command.
     *
     * @param filePath path to the file
     */
    public void countWordsAndLines(String filePath) {
        banner("10. WORD & LINE COUNT");
        int lines = 0, words = 0, chars = 0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines++;
                chars += line.length() + 1;                    // +1 for newline
                String[] tokens = line.trim().split("\\s+");
                if (!line.trim().isEmpty()) words += tokens.length;
            }
            System.out.printf("  Lines : %d%n", lines);
            System.out.printf("  Words : %d%n", words);
            System.out.printf("  Chars : %d%n", chars);
        } catch (IOException e) {
            System.err.println("ERROR counting words: " + e.getMessage());
        }
    }
}
