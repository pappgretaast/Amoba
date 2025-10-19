package org.example.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.List;


/** A kiírást és a beolvasást kezeli. */
public class FileHandler {

    public List<String> readFile(String path) throws IOException {
        return Files.exists(Path.of(path))
                ? Files.readAllLines(Path.of(path))
                : List.of();
    }

    public void writeFile(String path, List<String> content) throws IOException {
        try {
            Files.write(Path.of(path), content);
        } catch (InvalidPathException e) {
            throw new IOException("Invalid path: " + path, e);
        }
    }

}