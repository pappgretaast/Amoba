package org.example.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Move}.
 */
class MoveTest {

    @Test
    @DisplayName("Constructor should correctly set row and col values")
    void testConstructorSetsValues() {
        Move move = new Move(2, 5);
        assertEquals(2, move.row());
        assertEquals(5, move.col());
    }

    @Test
    @DisplayName("Move object should be immutable")
    void testImmutability() {
        Move move1 = new Move(1, 1);
        Move move2 = new Move(1, 1);
        assertNotSame(move1, move2, "Each Move instance should be a separate object");
        assertEquals(move1.row(), move2.row());
        assertEquals(move1.col(), move2.col());
    }

    @Test
    @DisplayName("Negative coordinates should be allowed and stored as-is")
    void testNegativeCoordinates() {
        Move move = new Move(-3, -2);
        assertEquals(-3, move.row());
        assertEquals(-2, move.col());
    }

    @Test
    @DisplayName("Zero coordinates should be handled correctly")
    void testZeroCoordinates() {
        Move move = new Move(0, 0);
        assertEquals(0, move.row());
        assertEquals(0, move.col());
    }
}
