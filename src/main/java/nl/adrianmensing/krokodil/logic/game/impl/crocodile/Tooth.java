package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

public record Tooth(int number, boolean available) {
    public Tooth(int number) {
        this(number, true);
    }
}
