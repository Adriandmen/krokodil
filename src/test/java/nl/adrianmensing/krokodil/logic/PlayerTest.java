package nl.adrianmensing.krokodil.logic;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PlayerTest {

    @Test
    public void TestBasicUserInstantiation() {
        Player player = new Player(123, "Adnan");

        assertThat(player.getId()).isEqualTo(123);
        assertThat(player.getUsername()).isEqualTo("Adnan");
    }
}
