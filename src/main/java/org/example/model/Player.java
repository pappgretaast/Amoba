package org.example.model;

import java.util.Objects;

/** Represents a player in the game (immutable). */
public final class Player {
    private final String name;
    private final char symbol;

    public Player(String name, char symbol) {
        this.name = Objects.requireNonNull(name);
        this.symbol = symbol;
    }

    public String name() {
        return name;
    }

    public char symbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return symbol == player.symbol && name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, symbol);
    }
}
