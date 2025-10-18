package org.example;

import org.example.board.Board;
import org.example.file.FileHandler;
import org.example.model.Move;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        FileHandler fileHandler = new FileHandler();
        Board board = new Board(10, 10);

        System.out.println("Üdv az amőba játékban!");


        System.out.print("Szeretnéd betölteni az előző játékot? (i/n): ");
        String answer = sc.nextLine().trim().toLowerCase();
        if (answer.equals("i")) {
            try {
                List<String> saved = fileHandler.readFile("save.txt");
                if (!saved.isEmpty()) {
                    board.deserialize(saved);
                    System.out.println("✅ Mentett játék betöltve!");
                } else {
                    System.out.println("⚠️ Nincs mentett játék, új játék indul.");
                }
            } catch (IOException e) {
                System.out.println("⚠️ Hiba a betöltés közben: " + e.getMessage());
            }
        }


        System.out.print("Első játékos neve (X): ");
        String player1 = sc.nextLine();
        System.out.print("Második játékos neve (O): ");
        String player2 = sc.nextLine();

        char currentSymbol = 'X';
        String currentPlayer = player1;

        if (!answer.equals("i")) {
            board.placeFirstMove(currentSymbol);
        }

        board.printBoard();

        while (true) {
            System.out.println(currentPlayer + " (" + currentSymbol + ") következik.");
            System.out.print("Add meg a sor és oszlop indexet (pl. 1 1), vagy írj 'mentés'-t: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("mentés")) {
                try {
                    fileHandler.writeFile("save.txt", board.serialize());
                    System.out.println("💾 Játékállás elmentve!");
                } catch (IOException e) {
                    System.out.println("⚠️ Mentési hiba: " + e.getMessage());
                }
                continue;
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                System.out.println("❌ Hibás formátum!");
                continue;
            }

            int r, c;
            try {
                r = Integer.parseInt(parts[0]) - 1;
                c = Integer.parseInt(parts[1]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("❌ Érvénytelen számformátum!");
                continue;
            }

            if (!board.applyMove(new Move(r, c), currentSymbol)) {
                System.out.println("❌ Érvénytelen lépés!");
                continue;
            }

            board.printBoard();

            if (board.checkWin(currentSymbol)) {
                System.out.println("🎉 Gratulálok, " + currentPlayer + " nyert!");
                break;
            }

            currentSymbol = (currentSymbol == 'X') ? 'O' : 'X';
            currentPlayer = (currentSymbol == 'X') ? player1 : player2;
        }
    }
}