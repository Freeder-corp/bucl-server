package com.freeder.buclserver.global.util;

public class DateUtils {

    public static Long nowDate(){
        return System.currentTimeMillis() / 1000;
    }

    public static boolean isOneWeekPassed(long parameterTime) {
        return nowDate() - parameterTime <= (7 * 24 * 60 * 60);
    }
}
