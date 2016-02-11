package me.bgx.budget.service;


import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class IdGenerator {
    private static final int LENGTH = 22;
    private static final Random random = new Random();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String newId() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i=0; i < LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
