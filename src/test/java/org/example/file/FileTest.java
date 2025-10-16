package org.example.file;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link File}.
 */
class FileTest {

    private static final String TEST_FILE = "testfile.txt";

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Path.of(TEST_FILE));
    }

    @Test
    @DisplayName("writeFile should create a file and write given content")
    void testWriteFileCreatesAndWritesContent() throws IOException {
        List<String> content = List.of("line1", "line2", "line3");
        File.writeFile(TEST_FILE, content);

        assertTrue(Files.exists(Path.of(TEST_FILE)), "File should be created");
        List<String> lines = Files.readAllLines(Path.of(TEST_FILE));
        assertEquals(content, lines, "File content should match the written content");
    }

    @Test
    @DisplayName("readFile should return empty list if file does not exist")
    void testReadFileWhenFileDoesNotExist() throws IOException {
        Files.deleteIfExists(Path.of(TEST_FILE)); // biztosan ne legyen ott
        List<String> result = File.readFile(TEST_FILE);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
    }

    @Test
    @DisplayName("readFile should correctly read existing file content")
    void testReadFileReadsExistingFile() throws IOException {
        List<String> content = List.of("A", "B", "C");
        Files.write(Path.of(TEST_FILE), content);

        List<String> result = File.readFile(TEST_FILE);

        assertEquals(content, result, "Read content should match written content");
    }

    @Test
    @DisplayName("writeFile should overwrite existing file content")
    void testWriteFileOverwritesContent() throws IOException {
        Files.write(Path.of(TEST_FILE), List.of("old"));
        File.writeFile(TEST_FILE, List.of("new"));

        List<String> lines = Files.readAllLines(Path.of(TEST_FILE));
        assertEquals(List.of("new"), lines, "File content should be overwritten");
    }
}
