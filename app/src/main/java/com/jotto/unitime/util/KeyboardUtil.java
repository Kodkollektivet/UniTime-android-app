package com.jotto.unitime.util;

/**
 * Created by otto on 2015-08-30.
 */

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Utility methods for manipulating the onscreen keyboard
 */
public class KeyboardUtil {
    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Class is not instantiable
    private KeyboardUtil() {}
}