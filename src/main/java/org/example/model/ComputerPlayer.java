package org.example.model;

import java.util.Random;
import org.example.board.Board;
import org.example.model.Move;

public final class ComputerPlayer {
    private final Random random = new Random();

    /**
     * Kiválaszt egy érvényes lépést.
     * Ha nincs érvényes, visszaad null-t.
     */
    public Move chooseMove(Board board, char symbol) {
        int rows = board.getRows();
        int cols = board.getColumns();

        // max 5000 próbálkozás (10x10-nél bőven elég)
        for (int i = 0; i < 5000; i++) {
            int r = random.nextInt(rows);
            int c = random.nextInt(cols);

            if (isValidMove(board, r, c, symbol)) {
                return new Move(r, c);
            }
        }

        return null; // nincs több érvényes lépés
    }

    /**
     * Megnézzük, hogy egy lépés érvényes lenne-e anélkül, hogy a valódi táblát módosítanánk.
     */
    private boolean isValidMove(Board original, int r, int c, char symbol) {
        // ha nem üres: nem jó
        if (original.getCell(r, c) != '.') return false;

        // deep copy a tábláról
        Board copy = new Board(original.getRows(), original.getColumns());
        copy.deserialize(original.serialize());

        // ha rá lehet rakni → érvényes
        return copy.applyMove(new Move(r, c), symbol);
    }
}
