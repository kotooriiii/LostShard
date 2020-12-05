package com.github.kotooriiii.channels;

import com.github.kotooriiii.util.HelperMethods;

public class CensorManager {

    private static CensorManager instance;
    private static String[] bannedWords = new String[]{"nigger", "nigga"};

    private CensorManager() {
    }

    public String apply(final String message) {
        String result = message;

        //basic word filter
        result = translateWord(result);
        result = translateIncludes(result);

        //

        return result;
    }

    private String translateWord(String message) {
        String[] args = message.split(" ");
        for (int i = 0; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "nigger":
                case "nigga":
                    args[i] = stars(args[i].toLowerCase());
            }
        }

        String built = HelperMethods.stringBuilder(args, 0, " ");

        return built;
    }

    private String translateIncludes(String message) {
        String[] args = message.split(" ");
        for (int i = 0; i < args.length; i++)
            for (String bannedWord : bannedWords)
                if (args[i].toLowerCase().contains(bannedWord))
                    args[i] = stars(args[i]);


        String built = HelperMethods.stringBuilder(args, 0, " ");

        return built;
    }

    private String stars(String message) {
        String stars = "";
        for (char c : message.toCharArray()) {
            stars += "*";
        }
        return stars;
    }

    private String translateWordOccurences(String message) {
        char[] chars = message.toCharArray();

        boolean exists = false;
        int index;
        int charIndex;

        for (char c : chars) {
            for (int i = 0; i < bannedWords.length; i++) {
                for (char bc : bannedWords[i].toCharArray()) {

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
