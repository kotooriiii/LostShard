package com.github.kotooriiii.register_system.bracket;

import java.util.UUID;

public class Bracket<T> {

    enum Type {
        A, B, UNDEFINED;
    }

    private final T a;
    private final T b;

    private Type winner;

    /**
     * Constructor to create bracket system.
     *
     * @param a Represents one of the individuals in the bracket
     * @param b Represents the other individual in the bracket
     */
    public Bracket(T a, T b) {
        this.a = a;
        this.b = b;
        winner = Type.UNDEFINED;
    }

    public Bracket(T a) {
        this.a = a;
        this.b = null;
        winner = Type.UNDEFINED;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }

    public boolean hasWinner() {
        return this.winner != Type.UNDEFINED;
    }

    public void setWinner(Type type) {
        this.winner = type;
    }

    public T getWinner() {
        switch (winner)
        {
            case A:
                return a;
            case B:
                return b;
            case UNDEFINED:
            default:
                return null;
        }
    }
}
