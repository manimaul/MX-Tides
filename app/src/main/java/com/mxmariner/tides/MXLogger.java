package com.mxmariner.tides;

import android.util.Log;

public class MXLogger {
    
    private static boolean DEBUGON = false;

    public static void setDebugOn(boolean on) {
        MXLogger.DEBUGON = on;
    }

    public static void i(String tag, Object... msg) {
        if (DEBUGON) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : msg) {
                sb.append(obj);
                sb.append(' ');
            }
            Log.i(tag, sb.toString());
        }
    }

    public static void w(String tag, Object... msg) {
        if (DEBUGON) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : msg) {
                sb.append(obj);
                sb.append(' ');
            }
            Log.w(tag, sb.toString());
        }
    }

    public static void d(String tag, Object... msg) {
        if (DEBUGON) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : msg) {
                sb.append(obj);
                sb.append(' ');
            }
            Log.d(tag, sb.toString());
        }
    }

    public static void e(String tag, Object... msg) {
        if (DEBUGON) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : msg) {
                sb.append(obj);
                sb.append(' ');
            }
            Log.e(tag, sb.toString());
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        e(tag, e, msg);
    }

    public static void e(String tag, Throwable e, String... msg) {
        if (DEBUGON) {
            StringBuilder sb = new StringBuilder();
            for (Object obj : msg) {
                sb.append(obj);
                sb.append(' ');
            }
            Log.e(tag, sb.toString(), e);
            e.printStackTrace();
        }
    }
}

