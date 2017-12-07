package org.qrcodedemo.utils;

public class NoDoubleClickUtils {
    private static long lastClickTime;
    private final static int SPACE_TIME = 200;

    public static void initLastClickTime() {
        lastClickTime = 0;
    }

    public synchronized static boolean isDoubleClick() {
        return isDoubleClick(SPACE_TIME);
    }

    public synchronized static boolean isDoubleClick(long minDuration) {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        isClick2 = currentTime - lastClickTime <= minDuration;
        lastClickTime = currentTime;
        return isClick2;
    }
}
