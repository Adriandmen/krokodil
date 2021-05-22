package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

import java.util.Objects;

public class Tooth {
    private int number;
    private boolean available;

    public Tooth(int number, boolean available) {
        this.number = number;
        this.available = available;
    }

    public Tooth(int number) {
        this(number, true);
    }

    public int number() {
        return number;
    }

    public boolean available() {
        return available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tooth tooth = (Tooth) o;
        return number == tooth.number && available == tooth.available;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, available);
    }
}
