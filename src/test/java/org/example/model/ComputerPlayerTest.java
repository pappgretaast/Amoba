package org.example.model;

import org.example.board.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ComputerPlayerTest {

    private ComputerPlayer computerPlayer;
    private Board board;

    @BeforeEach
    void setUp() {
        computerPlayer = new ComputerPlayer();
        board = new Board(10, 10);
    }

    @Test
    @DisplayName("Ha van érvényes lépés, a gépnek találnia kell egyet")
    void testChooseMoveFindsValidMove() {
        // Előkészítés: Az ember lép egyet középre
        board.placeFirstMove('X');

        // A gép lépést választ
        Move move = computerPlayer.chooseMove(board, 'O');

        // Ellenőrzés
        assertNotNull(move, "A gépnek találnia kell lépést, ha van hely.");

        // Ellenőrizzük, hogy a választott lépés tényleg érvényes-e a táblán
        // (Ehhez megpróbáljuk alkalmazni)
        boolean applied = board.applyMove(move, 'O');
        assertTrue(applied, "A gép által választott lépésnek érvényesnek kell lennie.");
    }

    @Test
    @DisplayName("Ha a tábla tele van, null-t kell visszaadnia")
    @Timeout(value = 2, unit = TimeUnit.SECONDS) // Biztonsági időkorlát, nehogy végtelen ciklusba essen
    void testChooseMoveReturnsNullOnFullBoard() {
        // Feltöltjük a táblát teljesen
        fillBoardCompletely();

        // A gép próbál lépni
        Move move = computerPlayer.chooseMove(board, 'O');

        // Ellenőrzés
        assertNull(move, "Tele tábla esetén null-t kell visszaadni.");
    }

    @Test
    @DisplayName("A gép nem léphet foglalt mezőre")
    void testChooseMoveDoesNotOverwrite() {
        // Egy speciális helyzet: majdnem tele a tábla, csak 1 hely van.
        // Ezt nehéz tesztelni a Random miatt 5000 iterációval,
        // ezért inkább azt teszteljük, hogy amit visszaad, az üres helyre ment-e.

        board.placeFirstMove('X');
        Move move = computerPlayer.chooseMove(board, 'O');

        assertNotNull(move);
        // A lépés előtt a mezőnek üresnek kellett lennie (mivel a chooseMove nem módosítja a táblát, csak Move-ot ad vissza)
        // Azt a logikát használjuk, hogy a visszaadott Move koordinátáin a táblán '.' karakter van.
        assertEquals('.', board.getCell(move.row(), move.col()),
                "A gép csak üres mezőt választhat.");
    }

    /**
     * Segédmetódus a tábla feltöltéséhez.
     * Csalunk a serialize/deserialize-vel, hogy gyorsan tele legyen.
     */
    private void fillBoardCompletely() {
        java.util.List<String> fullBoard = new java.util.ArrayList<>();
        for (int i = 0; i < board.getRows(); i++) {
            // "XXXXXXXXXX"
            fullBoard.add("X".repeat(board.getColumns()));
        }
        board.deserialize(fullBoard);
    }
}