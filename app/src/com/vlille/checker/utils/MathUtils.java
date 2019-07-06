package com.vlille.checker.utils;

import java.util.Random;

public class MathUtils {

    public static int range(int min, int max) {
        Random random = new Random();

        return random.nextInt((max - min) + 1) + min;
    }

}
