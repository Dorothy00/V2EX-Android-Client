package com.dorothy.v2ex.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dorothy.v2ex.models.UserProfile;
import com.google.gson.Gson;

/**
 * Created by dorothy on 16/9/5.
 */
public class UserCache {
    private static final String USER_KEY = "user";
    private static final String PROFILE_KEY = "profile";

    public static void userCatch(Context context, UserProfile userProfile) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_KEY, Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_KEY, new Gson().toJson(userProfile));
    }

    public static UserProfile getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(USER_KEY, Context
                .MODE_PRIVATE);
        String profileStr = sharedPreferences.getString(PROFILE_KEY, "");
        UserProfile userProfile = new Gson().fromJson(profileStr, UserProfile.class);
        return userProfile;
    }
}
