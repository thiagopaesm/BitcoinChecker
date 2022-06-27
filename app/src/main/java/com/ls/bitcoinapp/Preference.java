package com.ls.bitcoinapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class Preference {
    private Context context1;
    private SharedPreferences sharedPreferences;
    private static final String KEY_URI = "KEY_URI";

    public Preference(Context context) {
        this.context1 = context;
        sharedPreferences = context1.getSharedPreferences("KEY_SHARE_APP", Context.MODE_PRIVATE);
    }

    public void saveUriSound(Uri uri) {
        sharedPreferences.edit().putString(KEY_URI, uri.toString()).apply();
    }

    public Uri getUriSound() {
        String uriString = sharedPreferences.getString(KEY_URI, "");
        if (uriString.isEmpty()) return null;
        return Uri.parse(uriString);
    }
}
