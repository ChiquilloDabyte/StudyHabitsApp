package com.utils;

import java.util.Random;

public class CodeGenerator {
    public static String generateCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); // 6 dígitos
        return String.valueOf(code);
    }
}
