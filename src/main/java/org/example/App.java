package org.example;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.example.board.Board;
import org.example.database.HighScore;
import org.example.file.FileHandler;
import org.example.model.Move;
import org.example.model.ComputerPlayer;

public class App {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FileHandler fileHandler = new FileHandler();
        Board board = new Board(10, 10);
        ComputerPlayer computerPlayer = new ComputerPlayer();
        HighScore highScore = new HighScore();

        System.out.println("Üdv az amőba játékban!");

        // --- HIGH SCORE MEGJELENÍTÉSE ---
        highScore.printHighScores();

        // --- BETÖLTÉS ---
        System.out.print("Szeretnéd betölteni az előző játékot? (i/n): ");
        String answer = sc.nextLine().trim().toLowerCase();

        if (answer.equals("i")) {
            try {
                List<String> saved = fileHandler.readFile("save.txt");
                if (!saved.isEmpty()) {
                    board.deserialize(saved);
                    System.out.println("Mentett játék betöltve!");
                } else {
                    System.out.println("Nincs mentett játék, új játék indul.");
                }
            } catch (IOException e) {
                System.out.println("Hiba a betöltés közben: " + e.getMessage());
            }
        }

        // Első játékos: Ember (X)
        System.out.print("Add meg a neved (X): ");
        String player = sc.nextLine();

        // Második játékos: Gép (O)
        String computer = "Computer";

        char currentSymbol = 'X';
        String currentPlayer = player;

        // Ha nem betöltésből indult → ember első lépése középen
        if (!answer.equals("i")) {
            board.placeFirstMove(currentSymbol);
        }

        board.printBoard();

        while (true) {
            System.out.println(currentPlayer + " (" + currentSymbol + ") következik.");

            // --- GÉP KÖRE ---
            if (currentPlayer.equals(computer)) {

                Move aiMove = computerPlayer.chooseMove(board, currentSymbol);

                if (aiMove == null) {
                    System.out.println("A gép nem talált érvényes lépést. Döntetlen!");
                    break;
                }

                board.applyMove(aiMove, currentSymbol);
                System.out.println("Gép lépett: " + (aiMove.row() + 1) + " " + (aiMove.col() + 1));
                board.printBoard();

                if (board.checkWin(currentSymbol)) {
                    System.out.println("A gép nyert!");

                    // --- HIGH SCORE DB FRISSÍTÉS ---
                    highScore.addWin(computer);
                    System.out.println("\nGyőzelmek frissítve!");

                    break;
                }

                // váltás vissza emberre
                currentSymbol = 'X';
                currentPlayer = player;
                continue;
            }

            // --- EMBER KÖRE ---
            System.out.print("Add meg a sor és oszlop indexet (pl. 1 1), vagy írj 'mentés'-t: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("mentés")) {
                try {
                    fileHandler.writeFile("save.txt", board.serialize());
                    System.out.println("Játékállás elmentve!");
                } catch (IOException e) {
                    System.out.println("Mentési hiba: " + e.getMessage());
                }
                continue;
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Hibás formátum!");
                continue;
            }

            int r;
            int c;
            try {
                r = Integer.parseInt(parts[0]) - 1;
                c = Integer.parseInt(parts[1]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen számformátum!");
                continue;
            }

            // ember lépése
            if (!board.applyMove(new Move(r, c), currentSymbol)) {
                System.out.println("Érvénytelen lépés!");
                continue;
            }

            board.printBoard();

            if (board.checkWin(currentSymbol)) {
                System.out.println("Gratulálok, " + currentPlayer + " nyert!");

                // --- HIGH SCORE DB FRISSÍTÉS ---
                highScore.addWin(player);
                System.out.println("\nGyőzelmek frissítve!");

                break;
            }

            // váltás gépre
            currentSymbol = 'O';
            currentPlayer = computer;
        }

        // --- VÉGÉN HIGH SCORE KIÍRÁSA ---
        System.out.println("\n=== Jelenlegi ranglista ===");
        highScore.printHighScores();
    }
}
