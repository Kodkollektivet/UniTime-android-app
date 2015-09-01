package com.jotto.unitime.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by otto on 2015-08-22.
 */

/**
 * Network util class for checking the network.
 */
public class Network {


    /**
     * Checks if the internet connection is availible and not turned off or is in airplane mode.
     * @param context Context
     * @return boolean
     */
    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
