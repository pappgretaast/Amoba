package org.example.board;

import org.example.model.Move;

import java.util.Arrays;

/** Represents the game board. */
public class Board {
    private final int rows;
    private final int columns;
    private final char[][] board;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.board = new char[rows][columns];
        for (char[] row : board) {
            Arrays.fill(row, '.');
        }
    }

    public boolean applyMove(Move move, char symbol) {
        if (move.row() < 0 || move.row() >= rows || move.col() < 0 || move.col() >= columns)
            return false;
        if (board[move.row()][move.col()] != '.') return false;

        board[move.row()][move.col()] = symbol;
        return true;
    }

    public void printBoard() {
        for (char[] row : board) {
            System.out.println(new String(row));
        }
    }
}
