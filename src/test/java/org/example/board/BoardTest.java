package org.example.board;

import org.example.model.Move;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Board}.
 */
class BoardTest {

    @Test
    @DisplayName("Valid move should be applied successfully")
    void testApplyValidMove() {
        Board board = new Board(3, 3);
        boolean result = board.applyMove(new Move(1, 1), 'X');
        assertTrue(result, "Valid move should return true");
    }

    @Test
    @DisplayName("Applying a move on an already occupied cell should fail")
    void testApplyInvalidMoveOccupied() {
        Board board = new Board(3, 3);
        board.applyMove(new Move(1, 1), 'X');
        boolean result = board.applyMove(new Move(1, 1), 'O');
        assertFalse(result, "Applying move to occupied cell should fail");
    }

    @Test
    @DisplayName("Move outside the board boundaries should be rejected")
    void testApplyInvalidMoveOutOfBounds() {
        Board board = new Board(3, 3);
        assertFalse(board.applyMove(new Move(-1, 0), 'X'), "Negative row should be invalid");
        assertFalse(board.applyMove(new Move(0, -1), 'X'), "Negative column should be invalid");
        assertFalse(board.applyMove(new Move(3, 0), 'X'), "Row beyond limit should be invalid");
        assertFalse(board.applyMove(new Move(0, 3), 'X'), "Column beyond limit should be invalid");
    }

    @Test
    @DisplayName("Board should initialize with '.' characters")
    void testBoardInitialization() {
        Board board = new Board(2, 2);

        // Capture the printed output of printBoard()
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        board.printBoard();
        String output = out.toString().trim();

        assertEquals("..\n..", output.replace("\r", ""), "Board should initialize with dots");
    }

    @Test
    @DisplayName("printBoard should print the current state correctly")
    void testPrintBoardAfterMove() {
        Board board = new Board(2, 2);
        board.applyMove(new Move(0, 0), 'X');
        board.applyMove(new Move(1, 1), 'O');

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        board.printBoard();
        String output = out.toString().trim().replace("\r", "");

        assertEquals("X.\n.O", output, "Board should reflect current game state");
    }
}
