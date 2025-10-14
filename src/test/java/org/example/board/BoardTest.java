package org.example.board;

import org.example.model.Move;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testApplyValidMove() {
        Board board = new Board(3, 3);
        assertTrue(board.applyMove(new Move(1, 1), 'X'));
    }

    @Test
    void testApplyInvalidMove() {
        Board board = new Board(3, 3);
        board.applyMove(new Move(1, 1), 'X');
        assertFalse(board.applyMove(new Move(1, 1), 'O'));
    }
}
