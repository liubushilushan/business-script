package com.liuapi.aliyun.code.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PwdGenerator {
    public static final Random random = ThreadLocalRandom.current();

    public static String generate(int length){
        if(length<0){
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length;i++) {
            sb.append(generateChar( random.nextInt(3)));
        }
        return sb.toString();
    }

    private static char generateChar(int box){
        if(box>2){
            throw new IllegalArgumentException();
        }
        switch (box){
            case 0:
               return (char) (random.nextInt(10) + 48);
            case 1:
               return (char) (random.nextInt(26) + 97);
            default:
               return (char) (random.nextInt(26) + 65);
        }
    }
}
