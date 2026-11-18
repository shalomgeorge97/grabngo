
package com.example.grabngo.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

public class SessionManager {

    private static final String PREF_NAME = "GrabNGoSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";

    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_MANAGER = "MANAGER";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save the logged-in user's ID and role.
    public boolean saveSession(int userId, String role) {

        String normalizedRole = normalizeRole(role);

        if (userId <= 0 || !isSupportedRole(normalizedRole)) {
            return false;
        }

        return preferences.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USER_ROLE, normalizedRole)
                .commit();
    }

    // Get saved user ID.
    public int getUserId() {
        return preferences.getInt(KEY_USER_ID, -1);
    }

    // Get saved user role.
    public String getUserRole() {
        return preferences.getString(KEY_USER_ROLE, "");
    }

    // Check whether a valid session exists.
    public boolean hasActiveSession() {
        return getUserId() > 0
                && isSupportedRole(getUserRole());
    }

    // Clear session when logging out.
    public boolean clearSession() {
        return preferences.edit()
                .remove(KEY_USER_ID)
                .remove(KEY_USER_ROLE)
                .commit();
    }

    public static String normalizeRole(String role) {
        return role == null
                ? ""
                : role.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isSupportedRole(String role) {
        String normalizedRole = normalizeRole(role);

        return ROLE_CUSTOMER.equals(normalizedRole)
                || ROLE_MANAGER.equals(normalizedRole);
    }
}
