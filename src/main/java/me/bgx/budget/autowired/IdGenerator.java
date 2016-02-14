package me.bgx.budget.autowired;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
public class IdGenerator {
    private static final int LENGTH = 22;
    private static final Random random = new SecureRandom();
    private static final char CHARS[];
    private static final Range RANGES[];

    @AllArgsConstructor
    private static class Range {
        char from, to;
    }

    static {
        RANGES = new Range[] {
                new Range('A', 'Z'),
                new Range('a', 'z'),
                new Range('0', '9'),
        };
        CHARS = generateChars(RANGES);
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

    public String newId() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int idx = random.nextInt(CHARS.length);
            sb.append(CHARS[idx]);
        }
        return sb.toString();
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
}
