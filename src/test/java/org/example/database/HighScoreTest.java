package org.example.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreTest {

    private HighScore highScore;
    // Ugyanazt az URL-t használjuk a teszthez, mint az éles kód
    private static final String DB_URL = "jdbc:h2:./highscore";

    @BeforeEach
    void setUp() throws Exception {
        // 1. Példányosítjuk az osztályt (ez létrehozza a táblát, ha nem létezik)
        highScore = new HighScore();

        // 2. Tiszta állapotot teremtünk: Töröljük az összes adatot a táblából
        // Így a tesztek nem zavarják egymást, és nem marad benne tesztadat a játékban.
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {
            st.execute("TRUNCATE TABLE scores");
        }
    }

    @Test
    @DisplayName("Új játékos hozzáadása sikeres")
    void testAddWinNewPlayer() {
        highScore.addWin("TesztElek");

        List<String> scores = highScore.getHighScores();

        assertFalse(scores.isEmpty(), "A lista nem lehet üres hozzáadás után.");
        assertEquals(1, scores.size());
        assertTrue(scores.get(0).contains("TesztElek"), "A névnek szerepelnie kell a listában.");
        assertTrue(scores.get(0).contains("1 win"), "Az első győzelemnek 1-nek kell lennie.");
    }

    @Test
    @DisplayName("Meglévő játékos győzelmeinek növelése")
    void testAddWinIncrementExistingPlayer() {
        // Első győzelem
        highScore.addWin("ProGamer");
        // Második győzelem
        highScore.addWin("ProGamer");

        List<String> scores = highScore.getHighScores();

        assertEquals(1, scores.size(), "Ugyanazon játékosnál nem új sort, hanem a meglévőt kell frissíteni.");
        assertTrue(scores.get(0).contains("ProGamer"));
        assertTrue(scores.get(0).contains("2 win"), "A győzelmek számának növekednie kell.");
    }

    @Test
    @DisplayName("Ranglista helyes sorrendje (csökkenő)")
    void testHighScoresOrdering() {
        // "Gyenge" játékos - 1 győzelem
        highScore.addWin("Gyenge");

        // "Bajnok" játékos - 3 győzelem
        highScore.addWin("Bajnok");
        highScore.addWin("Bajnok");
        highScore.addWin("Bajnok");

        // "Közepes" játékos - 2 győzelem
        highScore.addWin("Közepes");
        highScore.addWin("Közepes");

        List<String> scores = highScore.getHighScores();

        assertEquals(3, scores.size());

        // Ellenőrizzük a sorrendet: Bajnok (3) -> Közepes (2) -> Gyenge (1)
        assertTrue(scores.get(0).contains("Bajnok"), "A legtöbb győzelemmel rendelkezőnek kell elöl lennie.");
        assertTrue(scores.get(1).contains("Közepes"), "A második helyezett a közepes.");
        assertTrue(scores.get(2).contains("Gyenge"), "A legkevesebb győzelem a végén.");
    }

    @Test
    @DisplayName("Üres adatbázis esetén üres listát ad")
    void testGetHighScoresEmpty() {
        List<String> scores = highScore.getHighScores();
        assertNotNull(scores, "A visszatérési érték nem lehet null.");
        assertTrue(scores.isEmpty(), "Kezdetben üresnek kell lennie a listának.");
    }
}