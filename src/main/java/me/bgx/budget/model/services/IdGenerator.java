package me.bgx.budget.model.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
public class IdGenerator {
    private static final int LENGTH = 22;
    private static final Random rng;
    private static final char CHARS[];
    private static final Range RANGES[];

    public String newId() {
        char newId[] = new char[LENGTH];
        for (int i = 0; i < LENGTH; i++) {
            int idx = rng.nextInt(CHARS.length);
            newId[i] = CHARS[idx];
        }
        return String.valueOf(newId);
    }

    public boolean isValidId(String id) {
        // "new" id is captured by the length
        if (id == null || id.length() != LENGTH) {
            return false;
        }

        // validate every char
        for (int i = 0; i < id.length(); i++) {
            char ch = id.charAt(i);
            if (!isValidChar(ch)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidChar(char ch) {
        for (Range range : RANGES) {
            if (range.from <= ch && ch <= range.to) {
                return true;
            }
        }
        return false;
    }

    static {
        RANGES = new Range[]{
                new Range('A', 'Z'),
                new Range('a', 'z'),
                new Range('0', '9'),
        };
        CHARS = generateChars(RANGES);
        Random r;
        try {
            r = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            r = new Random();
        }
        rng = r;
    }

    private static char[] generateChars(Range ranges[]) {
        int size = 0;
        for (Range range : ranges) {
            size += (range.to - range.from + 1);
        }
        char chars[] = new char[size];
        size = 0;
        for (Range range : ranges) {
            for (char ch = range.from; ch <= range.to; ch++) {
                chars[size++] = ch;
            }
        }
        return chars;
    }

    @AllArgsConstructor
    private static class Range {
        char from, to;
    }

}
