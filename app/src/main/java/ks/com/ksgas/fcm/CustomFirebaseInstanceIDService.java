package ks.com.ksgas.fcm;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;

public class CustomFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = CustomFirebaseInstanceIDService.class.getSimpleName();

    private RequestQueue queue;


    private MySharedPreference mySharedPreference;
    ServiceHandler serviceHandler;
    String userid, refreshedToken;

    @Override
    public void onTokenRefresh() {

        serviceHandler = new ServiceHandler();
        mySharedPreference = new MySharedPreference(getApplicationContext());
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token Value: " + refreshedToken);
        if(refreshedToken == null) {
            mySharedPreference.saveNotificationSubscription(false);
        }else {
            mySharedPreference.saveNotificationSubscription(true);
            mySharedPreference.refreshToken(refreshedToken);
        }
        userid = mySharedPreference.getUserId();

        sendTheRegisteredTokenToWebServer();
    }

    private class refreshToken extends AsyncTask<String,String,String> {

        String msg;
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_id",userid));
            valuePairs.add(new BasicNameValuePair("token",refreshedToken));
            String response = serviceHandler.makeServiceCall(Constants.refreshFCMToken,ServiceHandler.POST,valuePairs);
            if(response != null) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    String success = getJson.getString("success");
                     msg = getJson.getString("msg");

                }catch (JSONException js) {
                    js.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
//            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            super.onPostExecute(s);
        }
    }

    private void sendTheRegisteredTokenToWebServer(){
        new refreshToken().execute();
    }

    /*private void sendTheRegisteredTokenToWebServer(final String token){
        queue = Volley.newRequestQueue(getApplicationContext());
        mySharedPreference = new MySharedPreference(getApplicationContext());

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, Constants.fcm_token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                tokenObject = gson.fromJson(response, TokenObject.class);

                if (null == tokenObject) {
                    Toast.makeText(getApplicationContext(), "Can't send a token to the server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(false);

                } else {
                    Toast.makeText(getApplicationContext(), "Token successfully send to server", Toast.LENGTH_LONG).show();
                    mySharedPreference.saveNotificationSubscription(true);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constants.tokenKey, token);
                return params;
            }
        };
        queue.add(stringPostRequest);
    }*/
}
