package org.example.file;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    private FileHandler fileHandler;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        fileHandler = new FileHandler();
        tempFile = Files.createTempFile("testfile", ".txt");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    @DisplayName("Létező fájl beolvasása működik")
    void testReadExistingFile() throws IOException {
        List<String> expectedContent = List.of("alma", "körte", "banán");
        Files.write(tempFile, expectedContent);

        List<String> result = fileHandler.readFile(tempFile.toString());

        assertEquals(expectedContent, result,
                "A beolvasott tartalomnak egyeznie kell a fájlban lévő sorokkal.");
    }

    @Test
    @DisplayName("Nem létező fájl beolvasása üres listát ad vissza")
    void testReadNonExistingFile() throws IOException {
        Path nonExistent = Path.of("does_not_exist_12345.txt");

        List<String> result = fileHandler.readFile(nonExistent.toString());

        assertNotNull(result, "Az eredmény nem lehet null.");
        assertTrue(result.isEmpty(), "A nem létező fájl esetén üres listát kell visszaadni.");
    }

    @Test
    @DisplayName("Fájl írása és visszaolvasása helyesen működik")
    void testWriteAndReadFile() throws IOException {
        List<String> content = List.of("Első sor", "Második sor", "Harmadik sor");

        fileHandler.writeFile(tempFile.toString(), content);
        List<String> result = Files.readAllLines(tempFile);

        assertEquals(content, result, "A fájlba írt és visszaolvasott tartalomnak egyeznie kell.");
    }

    @Test
    @DisplayName("writeFile IOException dobása, ha az elérési út érvénytelen")
    void testWriteFileThrowsIOException() {
        String invalidPath = "\0invalid:path";

        assertThrows(IOException.class, () ->
                        fileHandler.writeFile(invalidPath, List.of("adat")),
                "Érvénytelen elérési út esetén IOException-t kell dobnia.");
    }



}