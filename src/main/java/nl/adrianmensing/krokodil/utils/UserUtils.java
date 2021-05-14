package nl.adrianmensing.krokodil.utils;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserUtils {

    /**
     * The username pattern, used to match usernames for their validity.
     * For now, we simply check whether the username is an alphanumeric string with spaces.
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9 ]*$");

    /**
     * Check if the given username is a valid username. A username is considered to be valid when it is
     * an alphanumeric string that also may contain space characters, and is between 2 and 36 characters long.
     *
     * @param username The username that needs to be checked for validity.
     * @return A boolean indicating whether this is a valid username.
     */
    public static boolean isValidUsername(@NotNull String username) {
        if (username.length() < 2 || username.length() > 32)
            return false;

        Matcher matcher = USERNAME_PATTERN.matcher(username);
        return matcher.matches();
    }
}
