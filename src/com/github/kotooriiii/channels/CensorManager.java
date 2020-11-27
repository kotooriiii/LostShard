package com.github.kotooriiii.channels;

import com.github.kotooriiii.util.HelperMethods;

public class CensorManager {

    private static CensorManager instance;
    private static char[] skipCharsArray = new char[]{' ', '_', '-'};
    private static String[] bannedWords = new String[]{"hypixel"};

    private CensorManager() {
    }

    public String apply(final String message) {
        String result = message;

        //basic word filter
        result = translateWord(result);

        //

        return result;
    }

    private String translateWord(String message) {
        String[] args = message.split(" ");
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "hypixel":
                case "bedwars":
                case "bedwarz":
                case "hypyxel":
                case "hipixel":
                case "highpixel":
                case "thehive":
                case "hivemc":
                case "mineplex":
                case "antiac":
                    args[i] = "lostshard";
            }
        }

        String built = HelperMethods.stringBuilder(args, 0, " ");

        return built;
    }

    private String translateWordOccurences(String message) {
        char[] chars = message.toCharArray();

        boolean exists = false;
        int index;
        int charIndex;

        for (char c : chars) {
            for (int i = 0; i < bannedWords.length; i++) {
                for(char bc : bannedWords[i].toCharArray())
                {

                }
            }
        }
        return "";
    }

    public static CensorManager getInstance() {
        if (instance == null) {
            synchronized (CensorManager.class) {
                if (instance == null)
                    instance = new CensorManager();
            }
        }
        return instance;

    }


}
