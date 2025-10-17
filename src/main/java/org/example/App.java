package org.example;

import org.example.board.Board;
import org.example.model.Move;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Board board = new Board(10, 10);

        System.out.println("Üdv az én amőba játékomban!");
        System.out.print("Kérem az első játékos nevét (X): ");
        String player1 = sc.nextLine();
        System.out.print("Kérem a második játékos nevét (O): ");
        String player2 = sc.nextLine();

        System.out.println("Szia " + player1 + " és " + player2 + "! Kezdődjön a játék!");

        char currentSymbol = 'X';
        String currentPlayer = player1;

        // első lépés automatikusan a közepére
        board.placeFirstMove(currentSymbol);
        board.printBoard();
        System.out.println(currentPlayer + " (" + currentSymbol + ") kezdett a tábla közepén.");

        // játékosváltás
        currentSymbol = 'O';
        currentPlayer = player2;

        while (true) {
            System.out.println(currentPlayer + " (" + currentSymbol + ") következik.");
            System.out.print("Add meg a sor és oszlop indexet (pl. 1 1): ");
            int r = sc.nextInt() -1;
            int c = sc.nextInt() -1;

            if (!board.applyMove(new Move(r, c), currentSymbol)) {
                System.out.println("❌ Érvénytelen lépés! Csak szomszédos mezőre tehetsz, ami nincs elfoglalva.");
                continue;
            }

            board.printBoard();

            if (board.checkWin(currentSymbol)) {
                System.out.println("🎉 Gratulálok, " + currentPlayer + " nyert!");
                break;
            }

            // játékosváltás
            if (currentSymbol == 'X') {
                currentSymbol = 'O';
                currentPlayer = player2;
            } else {
                currentSymbol = 'X';
                currentPlayer = player1;
            }
        }
    }
}
