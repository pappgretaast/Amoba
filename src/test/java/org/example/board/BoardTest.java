package org.example.board;

import org.example.model.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10, 10);
    }

    // A méretellenőrzés tesztjét (testInvalidSize) kivettük,
    // mert az eredeti Board osztályban nem volt ilyen validáció.

    @Test
    @DisplayName("Első lépés automatikus középre helyezése (10/2 = 5)")
    void testPlaceFirstMove() {
        board.placeFirstMove('X');
        // Az eredeti kódodban: centerRow = rows / 2; -> 10 / 2 = 5.
        // Tehát az (5, 5) mezőn kell lennie a jelnek.
        assertEquals('X', board.getCell(5, 5), "A középső mezőn (5,5) kell lennie a jelnek.");
    }

    @Test
    @DisplayName("Érvényes lépés alkalmazása (szomszédos)")
    void testApplyValidMove() {
        board.placeFirstMove('X'); // (5,5)-re kerül
        Move move = new Move(5, 6); // Közvetlen mellette
        boolean result = board.applyMove(move, 'O');
        assertTrue(result, "A lépésnek érvényesnek kell lennie.");
        assertEquals('O', board.getCell(5, 6));
    }

    @Test
    @DisplayName("Érvénytelen lépés: mező foglalt")
    void testApplyMoveToOccupiedCell() {
        board.placeFirstMove('X'); // (5,5)
        Move move = new Move(5, 5); // Ugyanoda próbálunk lépni
        boolean result = board.applyMove(move, 'O');
        assertFalse(result, "Nem szabad engedni lépést foglalt mezőre.");
    }

    @Test
    @DisplayName("Érvénytelen lépés: nincs szomszédos jel")
    void testApplyMoveWithoutAdjacent() {
        board.placeFirstMove('X'); // (5,5)
        Move move = new Move(0, 0); // Túl messze van
        boolean result = board.applyMove(move, 'O');
        assertFalse(result, "Nem szabad engedni szomszéd nélküli lépést.");
    }

    @Test
    @DisplayName("Vízszintes nyerés (5 db)")
    void testCheckWinHorizontal() {
        // Most 5 db X-et állítunk be, mert a kódodban winLength = 5 volt
        setupBoardWithMap(List.of(
                "..........",
                "XXXXX.....", // 5 db X
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                ".........."
        ));
        assertTrue(board.checkWin('X'), "Vízszintes 5-ösnél nyerni kell.");
    }

    @Test
    @DisplayName("Átlós nyerés (5 db)")
    void testCheckWinDiagonal() {
        Board b = new Board(10, 10);
        List<String> state = b.serialize();
        // 5 átlós lépés beállítása
        state.set(0, "X.........");
        state.set(1, ".X........");
        state.set(2, "..X.......");
        state.set(3, "...X......");
        state.set(4, "....X.....");
        b.deserialize(state);

        assertTrue(b.checkWin('X'), "Átlós 5-ösnél nyerni kell.");
    }

    @Test
    @DisplayName("Nincs nyerés ha csak 4 azonos jel van (mert 5 kell)")
    void testNoWinWithFour() {
        // Mivel a kódod winLength=5, a 4 még NEM nyerés
        setupBoardWithMap(List.of(
                "XXXX......", // Csak 4 db
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                "..........",
                ".........."
        ));
        assertFalse(board.checkWin('X'), "4 jelre még nem jár nyerés, ha 5 a szabály.");
    }

    @Test
    @DisplayName("Tábla kiírása (printBoard) ellenőrzése")
    void testPrintBoard() {
        // 1. Elmentjük az eredeti System.out-ot, hogy a teszt végén vissza tudjuk állítani
        PrintStream originalOut = System.out;

        // 2. Létrehozunk egy memóriába író folyamot (ByteArrayOutputStream)
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        try {
            // 3. Átirányítjuk a System.out-ot a mi folyamunkra
            System.setOut(new PrintStream(outContent));

            // 4. Meghívjuk a tesztelendő metódust
            board.printBoard();

            // 5. Ellenőrizzük a tartalmat
            String output = outContent.toString();

            assertFalse(output.isEmpty(), "A kimenet nem lehet üres.");
            assertTrue(output.contains("1 2 3"), "Az oszlopok számozásának látszódnia kell.");
            assertTrue(output.contains(" 1 "), "A sorok számozásának látszódnia kell (pl. ' 1 ').");
            assertTrue(output.contains(". . ."), "A tábla alapállapotának (pontok) látszódnia kell.");

        } finally {
            // 6. Nagyon fontos: visszaállítjuk az eredeti konzol kimenetet!
            // Ha ez elmarad, a többi teszt eredménye vagy hibaüzenete nem látszana.
            System.setOut(originalOut);
        }
    }

    private void setupBoardWithMap(List<String> map) {
        board.deserialize(map);
    }
}