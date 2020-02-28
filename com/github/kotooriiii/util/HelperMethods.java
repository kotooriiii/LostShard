package com.github.kotooriiii.util;

public final class HelperMethods {
    private HelperMethods() {}

    public static String stringBuilder(String[] args, int n) {

        String string = "";
        for (int i = n; i < args.length; i++) {
            if (i == n)
                string += args[i];
            else
                string += " " + args[i];
        }
        return string;
    }
}
