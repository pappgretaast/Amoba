package org.example.board;

import org.example.model.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10, 10);
    }

    @Test
    @DisplayName("Első lépés automatikus középre helyezése")
    void testPlaceFirstMove() {
        board.placeFirstMove('X');
        assertTrue(getCell(5, 5) == 'X' || getCell(4, 4) == 'X' || getCell(5, 4) == 'X' || getCell(4, 5) == 'X',
                "A középső mezőn kell lennie az első lépésnek.");
    }

    @Test
    @DisplayName("Érvényes lépés alkalmazása")
    void testApplyValidMove() {
        board.placeFirstMove('X');
        Move move = new Move(5, 6);
        boolean result = board.applyMove(move, 'O');
        assertTrue(result, "A lépésnek érvényesnek kell lennie.");
    }

    @Test
    @DisplayName("Érvénytelen lépés: mező foglalt")
    void testApplyMoveToOccupiedCell() {
        board.placeFirstMove('X');
        Move move = new Move(5, 5);
        boolean result = board.applyMove(move, 'O');
        assertFalse(result, "Nem szabad engedni lépést foglalt mezőre.");
    }

    @Test
    @DisplayName("Érvénytelen lépés: tartományon kívül")
    void testApplyMoveOutOfBounds() {
        Move move = new Move(-1, 10);
        boolean result = board.applyMove(move, 'X');
        assertFalse(result, "A lépés tartományon kívül van, nem szabad engedni.");
    }

    @Test
    @DisplayName("Érvénytelen lépés: nincs szomszédos jel az első után")
    void testApplyMoveWithoutAdjacent() {
        board.placeFirstMove('X');
        Move move = new Move(0, 0);
        boolean result = board.applyMove(move, 'O');
        assertFalse(result, "Nem szabad engedni szomszéd nélküli lépést.");
    }

    @Test
    @DisplayName("Vízszintes nyerés ellenőrzése")
    void testCheckWinHorizontal() {
        for (int i = 0; i < 5; i++) {
            board.applyMove(new Move(4, i), 'X');
        }
        assertTrue(board.checkWin('X'), "Az 'X' játékosnak vízszintesen nyernie kellene.");
    }

    @Test
    @DisplayName("Függőleges nyerés ellenőrzése")
    void testCheckWinVertical() {
        for (int i = 0; i < 5; i++) {
            board.applyMove(new Move(i, 4), 'X');
        }
        assertTrue(board.checkWin('X'), "Az 'X' játékosnak függőlegesen nyernie kellene.");
    }

    @Test
    @DisplayName("Átlós ↘ nyerés ellenőrzése")
    void testCheckWinDiagonalDownRight() {
        for (int i = 0; i < 5; i++) {
            board.applyMove(new Move(i, i), 'X');
        }
        assertTrue(board.checkWin('X'), "Az 'X' játékosnak átlósan (↘) nyernie kellene.");
    }

    @Test
    @DisplayName("Átlós ↙ nyerés ellenőrzése")
    void testCheckWinDiagonalDownLeft() {
        for (int i = 0; i < 5; i++) {
            board.applyMove(new Move(i, 9 - i), 'X');
        }
        assertTrue(board.checkWin('X'), "Az 'X' játékosnak átlósan (↙) nyernie kellene.");
    }

    @Test
    @DisplayName("Nincs nyerés ha kevesebb mint 5 azonos jel van")
    void testNoWinWithFourInRow() {
        for (int i = 0; i < 4; i++) {
            board.applyMove(new Move(3, i), 'X');
        }
        assertFalse(board.checkWin('X'), "4 egymás melletti jel még nem nyerés.");
    }

    // Segédmetódus a mező lekérésére (tesztekhez)
    private char getCell(int row, int col) {
        try {
            var field = Board.class.getDeclaredField("board");
            field.setAccessible(true);
            char[][] b = (char[][]) field.get(board);
            return b[row][col];
        } catch (Exception e) {
            fail("Nem sikerült hozzáférni a board mezőhöz: " + e.getMessage());
            return '?';
        }
    }

    @Test
    @DisplayName("serialize() és deserialize() működése")
    void testSerializeAndDeserialize() {
        // Lépések lerakása
        board.applyMove(new Move(4, 4), 'X');
        board.applyMove(new Move(4, 5), 'O');

        // Mentés
        var saved = board.serialize();

        // Új tábla betöltése
        Board loaded = new Board(10, 10);
        loaded.deserialize(saved);

        // Ellenőrzés, hogy az állapot egyezik
        var loadedSaved = loaded.serialize();
        assertEquals(saved, loadedSaved, "A deszerializált tábla állapota egyezzen az eredetivel.");
    }

    @Test
    @DisplayName("deserialize() beállítja a firstMovePlaced értékét")
    void testDeserializeSetsFirstMovePlaced() throws Exception {
        var lines = java.util.List.of(
                "..........",
                "....X.....",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                ".........."
        );
        Board b = new Board(10, 10);
        b.deserialize(lines);

        var field = Board.class.getDeclaredField("firstMovePlaced");
        field.setAccessible(true);
        boolean placed = (boolean) field.get(b);
        assertTrue(placed, "Ha volt már jel a táblán, a firstMovePlaced-nek igaznak kell lennie.");
    }

}
