package com.curtisnewbie.activities;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import javax.inject.Singleton;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that provides convenient methods to create msg using Toast.
 * </p>
 */
@Singleton
public class MsgToaster {

    public static final int SHORT = 1;
    public static final int LONG = 2;

    /**
     * Display a msg using Toast. A UI thread is always created for this method.
     *
     * @param msg message
     * @param len how long the message is shown (either {@code SHORT} or {@code LONG})
     */
    private static void msg(Activity activity, String msg, int len) {
        activity.runOnUiThread(() -> {
            Toast.makeText(activity, msg, len == LONG ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Display a msg using Toast and Toast.LENGTH_LONG. A UI thread is always created for this method.
     *
     * @param msg message
     */
    public static void msgLong(Activity activity, String msg) {
        msg(activity, msg, LONG);
    }

    /**
     * Display a msg using Toast and Toast.LENGTH_SHORT. A UI thread is always created for this method.
     *
     * @param msg message
     */
    public static void msgShort(Activity activity, String msg) {
        msg(activity, msg, SHORT);
    }
}
