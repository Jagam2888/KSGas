package ks.com.ksgas.authendication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ks.com.ksgas.MainActivity;
import ks.com.ksgas.R;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.GPSTracker;
import ks.com.ksgas.common.ServiceHandler;
import ks.com.ksgas.customer.BookCylinder;
import ks.com.ksgas.fcm.MySharedPreference;

/**
 * Created by jaga on 16/08/16.
 */
public class Signup extends ActivityManager implements View.OnClickListener{

    EditText nameTxt, emailTxt, phoneTxt, passwordTxt, confirmTxt,codeTxt,addressTxt,cityTxt,stateTxt;
    Button submit;
    MySharedPreference mySharedPreference;
    String fcmToken;
    ArrayList<HashMap<String,String>>stateList,cityList;
    GPSTracker gps;
    boolean isStateClick = false, isCheckLocation = false;
    String stateIdz;
    ArrayList<String>formatAddressList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        mySharedPreference = new MySharedPreference(this);
        fcmToken = mySharedPreference.getRefreshToken();
        gps = new GPSTracker(this);
        stateList = new ArrayList<HashMap<String,String>>();
        cityList = new ArrayList<HashMap<String,String>>();
        formatAddressList = new ArrayList<String>();

        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
    }

    void initialize() {
        nameTxt = (EditText)findViewById(R.id.edit_name);
        emailTxt = (EditText)findViewById(R.id.edit_email);
        phoneTxt = (EditText)findViewById(R.id.edit_phone);
        passwordTxt = (EditText)findViewById(R.id.edit_password);
        confirmTxt = (EditText)findViewById(R.id.edit_confirm_password);
        codeTxt = (EditText)findViewById(R.id.edit_code);
        addressTxt = (EditText)findViewById(R.id.edit_address);
        cityTxt = (EditText)findViewById(R.id.edit_city);
        cityTxt.setOnClickListener(this);
        stateTxt = (EditText)findViewById(R.id.edit_state);
        stateTxt.setOnClickListener(this);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        gps.getAddress(addressTxt,cityTxt,stateTxt);
        new get_state().execute();
    }

    private class get_state extends AsyncTask<String,String,String> {
        String addressValue;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            stateList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = serviceHandler.makeServiceCall(Constants.stateList, ServiceHandler.GET);

            if(response != null) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getResponse = jsonObject.getJSONObject("response");
                    String success = getResponse.getString("Success");
                    if(success.equalsIgnoreCase("1")) {
                        JSONArray getArray = getResponse.getJSONArray("items");
                        for(int i=0;i<getArray.length();i++) {
                            JSONObject getItems = getArray.getJSONObject(i);
                            HashMap<String,String> maps = new HashMap<String, String>();
                            maps.put("state_id",getItems.getString("state_id"));
                            maps.put("state_name",getItems.getString("state_name"));
                            stateList.add(maps);
                        }
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
            if(isStateClick) {
                android.app.AlertDialog.Builder alertView = new android.app.AlertDialog.Builder(Signup.this);
                final ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(Signup.this, android.R.layout.simple_list_item_1, android.R.id.text1);
                for (int i = 0; i < stateList.size(); i++) {
                    accountAdapter.add(stateList.get(i).get("state_name"));
                }

                alertView.setAdapter(accountAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = accountAdapter.getItem(which);
                        stateTxt.setText(strName);
                        stateIdz = stateList.get(which).get("state_id");
                        cityTxt.setText("");
                        addressTxt.setText("");

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertView.show();
            }else {
                if(!stateTxt.getText().toString().trim().equalsIgnoreCase("")) {
                    if(stateList.contains(stateTxt.getText().toString())) {
                        stateIdz = stateList.get(getIndexOFValue(stateTxt.getText().toString(), stateList)).get("state_id");
                    }
                }else {
                    gps.showSettingsAlert();
                    isCheckLocation = true;
                }
            }
        }
    }

    private class get_city extends AsyncTask<String,String,String> {

        String state_id;
        public get_city(String state_id) {
            this.state_id = state_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            cityList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("state_id",state_id));
            String response = serviceHandler.makeServiceCall(Constants.cityList, ServiceHandler.POST,valuePairs);

            if(response != null) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getResponse = jsonObject.getJSONObject("response");
                    String success = getResponse.getString("Success");
                    if(success.equalsIgnoreCase("1")) {
                        JSONArray getArray = getResponse.getJSONArray("items");
                        for(int i=0;i<getArray.length();i++) {
                            JSONObject getItems = getArray.getJSONObject(i);
                            HashMap<String,String> maps = new HashMap<String, String>();
                            maps.put("city_id",getItems.getString("city_id"));
                            maps.put("city_name",getItems.getString("city_name"));
                            cityList.add(maps);
                        }
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
            android.app.AlertDialog.Builder alertView = new android.app.AlertDialog.Builder(Signup.this);
            final ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(Signup.this,android.R.layout.simple_list_item_1, android.R.id.text1);
            for (int i=0;i<cityList.size();i++) {
                accountAdapter.add(cityList.get(i).get("city_name"));
            }

            alertView.setAdapter(accountAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String strName = accountAdapter.getItem(which);
                    cityTxt.setText(strName);

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertView.show();
        }
    }

    private class signUpTask extends AsyncTask<String,String,String> {
        String name,email,phone,password,msg,address,city,state;
        int flag = 0;
        public signUpTask(String name, String email,String phone,String password,String address,String city,String state) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.address = address;
            this.city = city;
            this.state = state;

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_name",name));
            valuePairs.add(new BasicNameValuePair("email",email));
            valuePairs.add(new BasicNameValuePair("phone",phone));
            valuePairs.add(new BasicNameValuePair("password",password));
            valuePairs.add(new BasicNameValuePair("status","1"));
            valuePairs.add(new BasicNameValuePair("user_type","customer"));
            valuePairs.add(new BasicNameValuePair("address",address));
            valuePairs.add(new BasicNameValuePair("city",city));
            valuePairs.add(new BasicNameValuePair("state",state));
            valuePairs.add(new BasicNameValuePair("fcm_token",fcmToken));
            String response = serviceHandler.makeServiceCall(Constants.signUp, ServiceHandler.POST,valuePairs);
            if(response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    String success = getJson.getString("Success");
                    if(success.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = getJson.getJSONArray("items");
                        JSONObject getArray = jsonArray.getJSONObject(0);
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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if(isNetworkAvailable(getApplicationContext())) {
                    if (mySharedPreference.hasUserSubscribeToNotification()) {
                        if (!nameTxt.getText().toString().trim().isEmpty() && !phoneTxt.getText().toString().trim().isEmpty() &&
                                !passwordTxt.getText().toString().trim().isEmpty()) {
                            if (passwordTxt.getText().toString().trim().equals(confirmTxt.getText().toString().trim())) {

                                new signUpTask(nameTxt.getText().toString().trim(), emailTxt.getText().toString().trim(),
                                        codeTxt.getText().toString().trim() + phoneTxt.getText().toString().trim(), passwordTxt.getText().toString().trim(),
                                        addressTxt.getText().toString().trim(),cityTxt.getText().toString().trim(),stateTxt.getText().toString().trim()).execute();
                            } else {
                                Toast.makeText(getApplicationContext(), "Password Mismatch", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Username,Phone,Password is Mandatory", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please wait few minutes", Toast.LENGTH_LONG).show();
                    }
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                break;
            case R.id.edit_state:
                if(isNetworkAvailable(getApplicationContext())) {
                    new get_state().execute();
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                isStateClick = true;
                break;
            case R.id.edit_city:
                if(isNetworkAvailable(getApplicationContext())) {
                    if(stateIdz != null) {
                        new get_city(stateIdz).execute();
                    }
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
