package ks.com.ksgas.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import org.json.JSONException;

import java.util.HashMap;

import ks.com.ksgas.MainActivity;
import ks.com.ksgas.R;
import ks.com.ksgas.customer.BookingHistory;
import ks.com.ksgas.dealer.OrderActivity;

public class CustomFirebaseMessagingService extends FirebaseMessagingService{

    private static final String TAG = CustomFirebaseMessagingService.class.getSimpleName();
    String value, data, message,flag;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        if (remoteMessage.getData().size() > 0) {
            data = remoteMessage.getData().get("id");
            Log.d(TAG, "Notification Message Data: " + data);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            message = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        try{
            JSONObject jsonObject = new JSONObject(data);
            value = jsonObject.getString("id");
            flag = jsonObject.getString("flag");
            sendNotification(message,value,flag);

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendNotification(String msg,String id,String flag) {

        Intent intent = null;
        if(flag.equalsIgnoreCase("0")) {
            intent = new Intent(this, OrderActivity.class);
            intent.putExtra("key", id);
        }else {
            intent = new Intent(this, BookingHistory.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(msg)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
