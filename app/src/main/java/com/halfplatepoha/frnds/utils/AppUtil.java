package com.halfplatepoha.frnds.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public class AppUtil {

    public static String getTimestampFromUTC(long timestamp) {
        return "10:00 am";
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (null != activity.getCurrentFocus()) {
            ((InputMethodManager)(activity.getSystemService(Activity.INPUT_METHOD_SERVICE)))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}
