package com.ronda.audiodemo.utils;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.ronda.audiodemo.BuildConfig;

/**
 * Created by Ronda on 2017/12/28.
 */

public class LogHelper {
    private static final String LOG_PREFIX = "liu_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;


    /**
     * 对传入的tag进行过长处理, 然后返回结果.
     */
    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH);
        }

        return LOG_PREFIX + str;
    }

    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }


    public static void log(String tag, int level, Throwable t, Object... messages) {
        if (Log.isLoggable(tag, level)) {

            String message;
            if (t == null && messages != null && messages.length == 1) { // 普通的log
                message = messages[0].toString();
            } else { // 其他情况的log. 输出所有message 和 异常信息

                StringBuilder sb = new StringBuilder();
                if (messages != null) {
                    for (Object m : messages) {
                        sb.append(m.toString());
                    }
                }

                if (t != null) {
                    sb.append("\n" + Log.getStackTraceString(t));
                }

                message = sb.toString();
            }
            Log.println(level, tag, message);
        }
    }


    /**
     * 对于 release 版 过滤掉 v 和 d
     *
     * @param tag
     * @param messages
     */
    public static void v(String tag, Object... messages) {
        if (BuildConfig.DEBUG) {
            log(tag, Log.VERBOSE, null, messages);
        }
    }

    public static void d(String tag, Object... messages) {
        if (BuildConfig.DEBUG) {
            log(tag, Log.DEBUG, null, messages);
        }
    }


    public static void i(String tag, Object... messages) {
        log(tag, Log.INFO, null, messages);
    }

    public static void w(String tag, Object... messages) {
        log(tag, Log.WARN, null, messages);
    }

    public static void w(String tag, Throwable t, Object... messages) {
        log(tag, Log.WARN, t, messages);
    }


    public static void e(String tag, Object... messages) {
        log(tag, Log.ERROR, null, messages);
    }

    public static void e(String tag, Throwable t, Object... messages) {
        log(tag, Log.ERROR, t, messages);
    }

}
