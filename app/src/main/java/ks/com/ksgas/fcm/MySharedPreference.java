package ks.com.ksgas.fcm;


import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {

    private SharedPreferences prefs;

    private Context context;

    public static final String FIREBASE_CLOUD_MESSAGING = "fcm";

    public static final String SET_NOTIFY = "set_notify";
    public static final String SET_TOKEN = "set_token";
    public static final String SET_USERID = "set_user_id";

    public MySharedPreference(Context context){
        this.context = context;
        prefs = context.getSharedPreferences(FIREBASE_CLOUD_MESSAGING, Context.MODE_PRIVATE);
    }

    public void saveNotificationSubscription(boolean value){
        SharedPreferences.Editor edits = prefs.edit();
        edits.putBoolean(SET_NOTIFY, value);
        edits.apply();
    }
    public void refreshToken(String token) {
        SharedPreferences.Editor edits = prefs.edit();
        edits.putString(SET_TOKEN, token);
        edits.apply();
    }

    public void setUserId(String userId) {
        SharedPreferences.Editor edits = prefs.edit();
        edits.putString(SET_USERID, userId);
        edits.apply();
    }

    public String getUserId() {
        return prefs.getString(SET_USERID,null);
    }
    public String getRefreshToken() {
        return prefs.getString(SET_TOKEN,null);
    }

    public boolean hasUserSubscribeToNotification(){
        return prefs.getBoolean(SET_NOTIFY, false);
    }

    public void clearAllSubscriptions(){
        prefs.edit().clear().apply();
    }
}
