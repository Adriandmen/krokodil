package nl.adrianmensing.krokodil.logic.game.impl.crocodile;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

public final class CrocodileGameUtils {

    private CrocodileGameUtils() { }

    @NotNull
    public static String hashedTooth(@NotNull Tooth tooth, String salt) {
        String from = salt + tooth.number();
        return DigestUtils.md5Hex(from);
    }
}
