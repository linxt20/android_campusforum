package com.example.uidesign.utils;

import java.util.Random;

public class TitleGenerator {
    private static final String[] WORDS = {
            "Apple", "Banana", "Orange", "Grapes", "Mango", "Strawberry", "Pineapple", "Watermelon", "Cherry"
    };

    public static String generateTitle() {
        Random random = new Random();
        StringBuilder title = new StringBuilder();
        title.append(getRandomWord(random));
        title.append(" ");
        title.append(getRandomWord(random));
        title.append(" ");
        title.append(getRandomWord(random));
        return title.toString();
    }

    private static String getRandomWord(Random random) {
        int index = random.nextInt(WORDS.length);
        return WORDS[index];
    }
}

