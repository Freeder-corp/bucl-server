package com.freeder.buclserver.global.util;

public class DateUtils {

    public static Long nowDate() {
        return System.currentTimeMillis() / 1000;
    }

    public static boolean isOneWeekPassed(long parameterTime, Long expireTime) {
        return nowDate() - parameterTime <= (expireTime == -1 ? (60 * 60 * 24 * 7) : expireTime);
    }
}
