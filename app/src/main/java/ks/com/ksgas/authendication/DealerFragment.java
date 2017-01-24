package ks.com.ksgas.authendication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ks.com.ksgas.MainActivity;
import ks.com.ksgas.R;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;
import ks.com.ksgas.fcm.MySharedPreference;

/**
 * Created by jaga on 12/08/16.
 */
public class DealerFragment extends Fragment {

    TextView phoneTxt,codeTxt,passwordTxt;
    private ProgressDialog progressDialog;
    private ServiceHandler serviceHandler;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    MySharedPreference mySharedPreference;
    private MySharedPreference mySharedPreferences;
    String fcmToken;
    public DealerFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceHandler = new ServiceHandler();
        preferences = getActivity().getSharedPreferences("set",getActivity().MODE_PRIVATE);
        mySharedPreferences = new MySharedPreference(getActivity());
        editor = preferences.edit();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        mySharedPreference = new MySharedPreference(getActivity());
        fcmToken = mySharedPreference.getRefreshToken();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dealer,container,false);
        Button submit = (Button)view.findViewById(R.id.submit);
        phoneTxt = (TextView)view.findViewById(R.id.edit_phone);
        codeTxt = (TextView)view.findViewById(R.id.code);
        passwordTxt = (TextView)view.findViewById(R.id.edit_password);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable(getActivity())) {
                    if (!phoneTxt.getText().toString().trim().isEmpty() && !passwordTxt.getText().toString().trim().isEmpty()) {
                        new loginTask(codeTxt.getText().toString().trim() + phoneTxt.getText().toString().trim(), passwordTxt.getText().toString().trim()).execute();
                    } else {
                        Toast.makeText(getActivity(), "Field(s) Missing", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"Please Check Your Internet Connection!",Toast.LENGTH_LONG).show();
                }
            }
        });
        TextView signup = (TextView)view.findViewById(R.id.request);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),DealerRequest.class));
            }
        });
        return view;
    }

    private class loginTask extends AsyncTask<String,String,String> {
        String phone,password,msg;
        int flag = 0;
        public loginTask(String phone,String password) {
            this.phone = phone;
            this.password = password;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("phone",phone));
            valuePairs.add(new BasicNameValuePair("password",password));
            valuePairs.add(new BasicNameValuePair("user_type","dealer"));
            valuePairs.add(new BasicNameValuePair("fcm_token",fcmToken));
            String response = serviceHandler.makeServiceCall(Constants.login, ServiceHandler.POST,valuePairs);
            if(response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    String success = getJson.getString("Success");
                    if(success.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = getJson.getJSONArray("items");
                        JSONObject getArray = jsonArray.getJSONObject(0);
                        mySharedPreferences.setUserId(getArray.getString("user_id"));
                        editor.putString("user_id",getArray.getString("user_id"));
                        editor.putString("user_name",getArray.getString("user_name"));
                        editor.putString("phone",getArray.getString("phone"));
                        editor.putString("user_type",getArray.getString("user_type"));
                        editor.putString("address",getArray.getString("address"));
                        editor.putString("city",getArray.getString("city"));
                        editor.putString("state",getArray.getString("state"));
                        editor.commit();
                    }else {
                        msg = getJson.getString("msg");
                        flag = 1;
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(flag == 0) {
                getActivity().finish();
            }else {
                Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    Log.w("INTERNET:",String.valueOf(i));
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.w("INTERNET:", "connected!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
