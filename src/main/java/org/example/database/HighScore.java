package org.example.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HighScore {

    // MÓDOSÍTVA: Fájl alapú adatbázis "./highscore"
    // Ez a projekt gyökérmappájába menti az adatokat (highscore.mv.db néven)
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
        } catch (Exception e) {
            System.out.println("Adatbázis hiba (tábla létrehozás): " + e.getMessage());
        }
    }

    public void addWin(String playerName) {
        String insert = """
                MERGE INTO scores (name, wins)
                KEY(name)
                VALUES (?, COALESCE((SELECT wins FROM scores WHERE name = ?), 0) + 1);
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(insert)) {

            ps.setString(1, playerName);
            ps.setString(2, playerName);
            ps.executeUpdate();

            System.out.println("Eredmény mentve: " + playerName);

        } catch (Exception e) {
            System.out.println("Mentési hiba: " + e.getMessage());
        }
    }

    public List<String> getHighScores() {
        List<String> result = new ArrayList<>();
        String query = "SELECT name, wins FROM scores ORDER BY wins DESC;";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                result.add(rs.getString("name") + " - " + rs.getInt("wins") + " win");
            }

        } catch (Exception e) {
            System.out.println("Lekérdezési hiba: " + e.getMessage());
        }

        return result;
    }

    public void printHighScores() {
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