package com.curtisnewbie.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Util class for Intent
 */
public class IntentUtil {

    /**
     * Check whether there is existing package/activity that can resolve this intent action.
     *
     * @param intent an intent action
     * @return whether there is an activity that can resolve this intent action
     */
    public static boolean hasIntentActivity(Activity activity, Intent intent) {
        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() < 1)
            return false;
        else
            return true;
    }
}
