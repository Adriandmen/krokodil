package nl.adrianmensing.krokodil.logic;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    @Test
    public void TestBasicPlayerInstantiation() {
        Player player = new Player("ABC", "Adnan");

        assertThat(player.id()).isEqualTo("ABC");
        assertThat(player.username()).isEqualTo("Adnan");
    }
}
