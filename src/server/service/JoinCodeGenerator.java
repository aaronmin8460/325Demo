package server.service;

import java.util.Random;

public class JoinCodeGenerator {

    private static final char[] ALLOWED_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private static final int CODE_LENGTH = 6;

    private final Random random;

    public JoinCodeGenerator() {

        this.random = new Random();

    }

    public String nextCode() {

        StringBuilder builder = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {

            builder.append(ALLOWED_CHARACTERS[random.nextInt(ALLOWED_CHARACTERS.length)]);

        }

        return builder.toString();

    }

}
