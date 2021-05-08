package nl.adrianmensing.krokodil.utils;

import java.security.SecureRandom;

public class SessionIDGenerator {
    private static final String NUMERIC_CHARS = "0123456789";
    private static final String L_ALPHA_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String U_ALPHA_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String CHARS = NUMERIC_CHARS + L_ALPHA_CHARS + U_ALPHA_CHARS;
    private static final SecureRandom random = new SecureRandom();

    private SessionIDGenerator() { }

    /**
     * Generates a new random session ID with the specified length, which consists of
     * alphanumeric characters only. For security purposes, we use the {@link SecureRandom} class
     * for the random number generation, rather than the classic Random class.
     *
     * @param length    The length of the random session ID, which needs to be a positive number.
     * @return          A new session ID string.
     */
    public static String randomSessionID(int length) {
        if (length <= 0)
            throw new RuntimeException("Could not generate a random session ID with length " + length);

        StringBuilder sb = new StringBuilder(length);

        for (int index = 0; index < length; index++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return sb.toString();
    }
}
