package org.example.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighScoreTest {

    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Test
    @DisplayName("Konstruktor: Tábla létrehozása meghívódik")
    void testConstructorCreatesTable() throws SQLException {
        // Statikus mockolás a DriverManager-hez
        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            // BEÁLLÍTÁS: Amikor a DriverManager.getConnection-t hívják, adja vissza a mockolt Connection-t
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            // Amikor a kapcsolatból Statement-et kérünk, adja vissza a mockStatement-et
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            // MŰVELET: Példányosítjuk az osztályt (ez hívja a createTableIfNotExists-et)
            new HighScore();

            // ELLENŐRZÉS: Megnézzük, lefutott-e a CREATE TABLE parancs
            verify(mockStatement).execute(contains("CREATE TABLE IF NOT EXISTS"));
        }
    }

    @Test
    @DisplayName("addWin: Új játékos esetén INSERT fut le")
    void testAddWinNewPlayer() throws SQLException {
        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);

            // Konstruktorhoz kell a statement mock
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            // A metódus logikájához kell a PreparedStatement mock
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Szimuláció: Az UPDATE 0 sort módosított (még nincs ilyen játékos)
            // Az első hívás az UPDATE lesz, a második az INSERT.
            // Beállítjuk, hogy az első executeUpdate() 0-t adjon vissza.
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            HighScore highScore = new HighScore();
            highScore.addWin("UjJatekos");

            // ELLENŐRZÉS:
            // 1. Először megpróbálta az UPDATE-et
            verify(mockConnection).prepareStatement(contains("UPDATE scores"));
            // 2. Mivel 0 jött vissza, meg kellett hívnia az INSERT-et is
            verify(mockConnection).prepareStatement(contains("INSERT INTO scores"));
            // 3. Ellenőrizzük, hogy beállította-e a nevet
            verify(mockPreparedStatement, times(2)).setString(eq(1), eq("UjJatekos"));
        }
    }

    @Test
    @DisplayName("addWin: Meglévő játékos esetén csak UPDATE fut le")
    void testAddWinExistingPlayer() throws SQLException {
        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // Szimuláció: Az UPDATE 1 sort módosított (van ilyen játékos)
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            HighScore highScore = new HighScore();
            highScore.addWin("RegiJatekos");

            // ELLENŐRZÉS:
            // Csak az UPDATE-et kellett meghívnia, az INSERT-et NEM
            verify(mockConnection).prepareStatement(contains("UPDATE scores"));
            verify(mockConnection, never()).prepareStatement(contains("INSERT INTO scores"));
        }
    }

    @Test
    @DisplayName("getHighScores: Helyesen olvassa be és mapparolja a ResultSet-et")
    void testGetHighScores() throws SQLException {
        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString())).thenReturn(mockConnection);
            when(mockConnection.createStatement()).thenReturn(mockStatement);

            // Amikor lekérdezést futtatunk, adja vissza a mockResultSet-et
            when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

            // Szimuláljuk a ResultSet viselkedését (egy sor van benne)
            // rs.next() -> true (első hívás), false (második hívás)
            when(mockResultSet.next()).thenReturn(true).thenReturn(false);

            // Adatok visszaadása
            when(mockResultSet.getString("name")).thenReturn("TesztElek");
            when(mockResultSet.getInt("wins")).thenReturn(5);

            HighScore highScore = new HighScore();
            List<String> scores = highScore.getHighScores();

            // ELLENŐRZÉS
            assertNotNull(scores);
            assertEquals(1, scores.size());
            assertEquals("TesztElek - 5 win", scores.get(0)); // Java 21: getFirst() is használható List-nél

            // Biztosítjuk, hogy jó query futott le
            verify(mockStatement).executeQuery(contains("ORDER BY wins DESC"));
        }
    }

    @Test
    @DisplayName("Kivételkezelés: SQL hiba esetén nem omlik össze")
    void testSqlExceptionHandling() { // Itt kivehetjük a throws SQLException-t, mert elkapjuk
        // 1. LÉPÉS: Hozzuk létre a kivételt a mockolás ELŐTT!
        // Így a konstruktor lefutása nem zavarja meg a Mockitót.
        SQLException tesztHiba = new SQLException("Kapcsolódási hiba");

        try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
            // 2. LÉPÉS: Használjuk az előre létrehozott objektumot
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString()))
                    .thenThrow(tesztHiba);

            // Nem szabad kivételt dobnia a programnak, mert a try-catch elkapja és logolja
            assertDoesNotThrow(() -> new HighScore());
        }
    }
}