package org.example.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Handles saving and loading game state. */
public class File {

    public static List<String> readFile(String path) throws IOException {
        return Files.exists(Path.of(path))
                ? Files.readAllLines(Path.of(path))
                : List.of();
    }

    public static void writeFile(String path, List<String> content) throws IOException {
        Files.write(Path.of(path), content);
    }
}
