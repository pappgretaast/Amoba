package org.example.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HighScore {

    // Logger inicializálása az osztályhoz
    private static final Logger logger = LoggerFactory.getLogger(HighScore.class);

    private static final String DB_URL = "jdbc:h2:./highscore";

    public HighScore() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = """
                CREATE TABLE IF NOT EXISTS scores (
                    name VARCHAR(255) PRIMARY KEY,
                    wins INT NOT NULL
                );
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {
            st.execute(sql);
            logger.debug("Adatbázis tábla ellenőrizve/létrehozva.");
        } catch (SQLException e) {
            // Hiba esetén ERROR szintű logolás, stack trace-szel
            logger.error("Nem sikerült létrehozni az adatbázis táblát!", e);
        }
    }

    public void addWin(String playerName) {
        String updateSql = "UPDATE scores SET wins = wins + 1 WHERE name = ?";
        String insertSql = "INSERT INTO scores (name, wins) VALUES (?, 1)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            // 1. lépés: Próbáljunk Update-elni
            try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                updatePs.setString(1, playerName);
                int rowsAffected = updatePs.executeUpdate();

                // Ha 0 sort módosított, az azt jelenti, nincs még ilyen játékos
                if (rowsAffected == 0) {
                    // 2. lépés: Insert
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setString(1, playerName);
                        insertPs.executeUpdate();
                        logger.info("Új játékos létrehozva és pont felírva: {}", playerName);
                    }
                } else {
                    logger.info("Meglévő játékos pontszáma növelve: {}", playerName);
                }
            }

        } catch (SQLException e) {
            logger.error("Hiba az eredmény mentése közben a '{}' játékosnál.", playerName, e);
        }
    }

    public List<String> getHighScores() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name, wins FROM scores ORDER BY wins DESC";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int wins = rs.getInt("wins");
                result.add(name + " - " + wins + " win");
            }

        } catch (SQLException e) {
            logger.error("Hiba a ranglista lekérdezése közben.", e);
        }

        return result;
    }

    public void printHighScores() {
        // Ez maradhat System.out, mert ez a játék felhasználói felülete (UI output), nem log.
        System.out.println("\n===== RANGLISTA =====");

        List<String> scores = getHighScores();

        if (scores.isEmpty()) {
            System.out.println("(Még nincs mentett eredmény)");
        } else {
            for (String score : scores) {
                System.out.println(score);
            }
        }

        System.out.println("=====================\n");
    }
}