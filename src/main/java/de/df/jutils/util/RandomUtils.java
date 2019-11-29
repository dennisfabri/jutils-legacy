package de.df.jutils.util;

import java.util.Random;

import ec.util.MersenneTwister;

public class RandomUtils {

    public enum Generators {
        DonaldKnuth, MersenneTwister
    }

    public static Random getRandomNumberGenerator(Generators name) {
        return getRandomNumberGenerator(name, System.nanoTime());
    }

    public static Random getRandomNumberGenerator(Generators name, long seed) {
        switch (name) {
        default:
        case DonaldKnuth:
            return new Random(seed);
        case MersenneTwister:
            return new MersenneTwister(seed);
        }
    }

    private static final char[] validChars = "ABCDEFGHIJKLMNOPQRSTUVW23456789".toCharArray();

    public static char getChar(Random rng) {
        return validChars[rng.nextInt(validChars.length)];
    }

    public static String createString(Random rng, int length) {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < length; x++) {
            sb.append(getChar(rng));
        }
        return sb.toString();
    }
}