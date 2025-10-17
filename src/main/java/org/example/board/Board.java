package org.example.board;

import org.example.model.Move;
import java.util.Arrays;

/** Represents the game board. */
public class Board {
    private final int rows;
    private final int columns;
    private final char[][] board;
    private boolean firstMovePlaced = false;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new char[rows][columns];
        for (char[] row : board) {
            Arrays.fill(row, '.');
        }
    }

    /** Automatikusan lerakja az első jelet a középső mezőre */
    public void placeFirstMove(char symbol) {
        int centerRow = rows / 2;
        int centerCol = columns / 2;
        board[centerRow][centerCol] = symbol;
        firstMovePlaced = true;
    }

    public boolean applyMove(Move move, char symbol) {
        // Tartományellenőrzés
        if (move.row() < 0 || move.row() >= rows || move.col() < 0 || move.col() >= columns)
            return false;
        // Foglalt mező
        if (board[move.row()][move.col()] != '.') return false;

        // Ha nem az első lépés, akkor ellenőrizzük, hogy van-e szomszéd
        if (firstMovePlaced && !hasAdjacentSymbol(move.row(), move.col())) {
            return false;
        }

        board[move.row()][move.col()] = symbol;
        firstMovePlaced = true;
        return true;
    }

    /** Ellenőrzi, hogy van-e szomszédos mező, amin már van jel */
    private boolean hasAdjacentSymbol(int row, int col) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr;
                int nc = col + dc;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < columns) {
                    if (board[nr][nc] != '.') {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Ellenőrzi, hogy a megadott szimbólum nyert-e (5 egymás mellett) */
    public boolean checkWin(char symbol) {
        int winLength = 5;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (board[r][c] != symbol) continue;

                if (checkDirection(r, c, 0, 1, symbol, winLength)) return true; // vízszintes
                if (checkDirection(r, c, 1, 0, symbol, winLength)) return true; // függőleges
                if (checkDirection(r, c, 1, 1, symbol, winLength)) return true; // átló ↘
                if (checkDirection(r, c, 1, -1, symbol, winLength)) return true; // átló ↙
            }
        }
        return false;
    }

    /** Segédmetódus: ellenőrzi, hogy adott irányban van-e 5 azonos */
    private boolean checkDirection(int r, int c, int dr, int dc, char symbol, int length) {
        for (int i = 0; i < length; i++) {
            int nr = r + dr * i;
            int nc = c + dc * i;

            if (nr < 0 || nr >= rows || nc < 0 || nc >= columns) return false;
            if (board[nr][nc] != symbol) return false;
        }
        return true;
    }

    public void printBoard() {
        System.out.print("   ");
        for (int i = 1; i <= columns; i++) {
            System.out.print(i % 10 + " ");
        }
        System.out.println();
        for (int r = 0; r < rows; r++) {
            System.out.printf("%2d ", r + 1);
            for (int c = 0; c < columns; c++) {
                System.out.print(board[r][c] + " ");
            }
            System.out.println();
        }
    }
}
