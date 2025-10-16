package org.example;

import org.example.board.Board;
import org.example.model.Move;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Board board = new Board(10, 10);

        System.out.println("Üdv az én amőba játékomban!");
        board.printBoard();
        System.out.println("Kérem a nevedet: ");
        String nev = sc.nextLine();
        System.out.println("Szia, "+nev+"!");
        while (true) {
            System.out.print("Add meg a sor és oszlop indexet (pl. 1 1): ");
            int r = sc.nextInt() - 1;
            int c = sc.nextInt() -1;

            if (!board.applyMove(new Move(r, c), 'X')) {
                System.out.println("Érvénytelen lépés!");
            }
            board.printBoard();
        }
    }
}
