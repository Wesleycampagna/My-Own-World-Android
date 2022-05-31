package study.android.android_study.utils;

import android.util.Log;

public class Logger {

    private static final String CODE = "debug_wesley";

    public static void debug(String text) {
        Log.d(CODE, text);
    }

    public static void error(String text) {
        Log.e(CODE, text);
    }

    public static void error(String text, Exception e) {
        Log.e(CODE, text, e);
    }
}
