package com.adfer.util;

/**
 * Created by adrianferenc on 02.05.2017.
 */
public class GameUtil {

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
