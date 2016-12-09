package ks.com.ksgas.authendication;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ks.com.ksgas.R;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.GPSTracker;
import ks.com.ksgas.common.ServiceHandler;
import ks.com.ksgas.customer.BookCylinder;

/**
 * Created by jaga on 11/3/16.
 */

public class UpdateProfile extends ActivityManager implements View.OnClickListener {

    EditText nameTxt, emailTxt, phoneTxt, passwordTxt, confirmTxt,codeTxt,addressTxt,cityTxt,stateTxt;
    Button submit;
    GPSTracker gps;
    String userId, stateIdz;
    ArrayList<HashMap<String,String>>stateList,cityList;
    boolean isStateClick = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.signup);
        stateList = new ArrayList<HashMap<String,String>>();
        cityList = new ArrayList<HashMap<String,String>>();
        gps = new GPSTracker(this);
        Bundle bun = getIntent().getExtras();
        userId = bun.getString("key");
        initialize();

    }

    void initialize() {
        nameTxt = (EditText)findViewById(R.id.edit_name);
        emailTxt = (EditText)findViewById(R.id.edit_email);
        passwordTxt = (EditText)findViewById(R.id.edit_password);
        passwordTxt.setVisibility(View.GONE);
        confirmTxt = (EditText)findViewById(R.id.edit_confirm_password);
        confirmTxt.setVisibility(View.GONE);
        addressTxt = (EditText)findViewById(R.id.edit_address);
        cityTxt = (EditText)findViewById(R.id.edit_city);
        cityTxt.setOnClickListener(this);
        stateTxt = (EditText)findViewById(R.id.edit_state);
        stateTxt.setOnClickListener(this);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        submit.setText("Update");
//        gps.getAddress(addressTxt,cityTxt,stateTxt);
        codeTxt = (EditText)findViewById(R.id.edit_code);
        codeTxt.setVisibility(View.GONE);
        phoneTxt = (EditText)findViewById(R.id.edit_phone);
        phoneTxt.setVisibility(View.GONE);
        new get_user().execute();
    }

    private class get_user extends AsyncTask<String,String,String> {

        String user_name,email,address,state,city;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_id",userId));
            String response = serviceHandler.makeServiceCall(Constants.getUserDetail, ServiceHandler.POST,valuePairs);
            if(response != null){
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    String success = getJson.getString("Success");
                    if(success.equalsIgnoreCase("1")){
                        JSONArray jsonArray = getJson.getJSONArray("items");
                        JSONObject getArray = jsonArray.getJSONObject(0);
                        user_name = getArray.getString("user_name");
                        email = getArray.getString("email");
                        address = getArray.getString("address");
                        state = getArray.getString("state");
                        city = getArray.getString("city");

                    }
                }catch (JSONException js){
                    js.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            nameTxt.setText(user_name);
            emailTxt.setText(email);
            addressTxt.setText(address);
            cityTxt.setText(city);
            stateTxt.setText(state);
            new get_state().execute();
        }
    }

    private class get_state extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            stateList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = serviceHandler.makeServiceCall(Constants.stateList, ServiceHandler.GET);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getResponse = jsonObject.getJSONObject("response");
                    String success = getResponse.getString("Success");
                    if (success.equalsIgnoreCase("1")) {
                        JSONArray getArray = getResponse.getJSONArray("items");
                        for (int i = 0; i < getArray.length(); i++) {
                            JSONObject getItems = getArray.getJSONObject(i);
                            HashMap<String, String> maps = new HashMap<String, String>();
                            maps.put("state_id", getItems.getString("state_id"));
                            maps.put("state_name", getItems.getString("state_name"));
                            stateList.add(maps);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!isStateClick) {
                if (isContains(stateTxt.getText().toString().trim(),stateList)) {
                    stateIdz = stateList.get(getIndexOFValue(stateTxt.getText().toString(), stateList)).get("state_id");
                }
            }else {

                android.app.AlertDialog.Builder alertView = new android.app.AlertDialog.Builder(UpdateProfile.this);
                final ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(UpdateProfile.this, android.R.layout.simple_list_item_1, android.R.id.text1);
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
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("state_id", state_id));
            String response = serviceHandler.makeServiceCall(Constants.cityList, ServiceHandler.POST, valuePairs);

            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getResponse = jsonObject.getJSONObject("response");
                    String success = getResponse.getString("Success");
                    if (success.equalsIgnoreCase("1")) {
                        JSONArray getArray = getResponse.getJSONArray("items");
                        for (int i = 0; i < getArray.length(); i++) {
                            JSONObject getItems = getArray.getJSONObject(i);
                            HashMap<String, String> maps = new HashMap<String, String>();
                            maps.put("city_id", getItems.getString("city_id"));
                            maps.put("city_name", getItems.getString("city_name"));
                            cityList.add(maps);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            android.app.AlertDialog.Builder alertView = new android.app.AlertDialog.Builder(UpdateProfile.this);
            final ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(UpdateProfile.this,android.R.layout.simple_list_item_1, android.R.id.text1);
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

    private class editProfile extends AsyncTask<String,String,String> {

        String user_id,name,email,address,city,state,msg,success;
        public editProfile(String user_id,String name,String email,String address,String city,String state) {
            this.user_id = user_id;
            this.name = name;
            this.email = email;
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
            valuePairs.add(new BasicNameValuePair("user_id",userId));
            valuePairs.add(new BasicNameValuePair("user_name",name));
            valuePairs.add(new BasicNameValuePair("email",email));
            valuePairs.add(new BasicNameValuePair("address",address));
            valuePairs.add(new BasicNameValuePair("city",city));
            valuePairs.add(new BasicNameValuePair("state",state));

            String response = serviceHandler.makeServiceCall(Constants.editProfile,ServiceHandler.POST,valuePairs);
            if(response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    success = getJson.getString("Success");
                    msg = getJson.getString("msg");

                }catch (JSONException js){
                    js.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            showToast(msg);
            if(success.equalsIgnoreCase("1")){
                finish();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if(isNetworkAvailable(getApplicationContext())) {
                    if(!nameTxt.getText().toString().trim().isEmpty() && !emailTxt.getText().toString().trim().isEmpty() && !addressTxt.getText().toString().trim().isEmpty()) {
                        new editProfile(userId, nameTxt.getText().toString().trim(), emailTxt.getText().toString().trim(), addressTxt.getText().toString().trim(),
                                cityTxt.getText().toString().trim(), stateTxt.getText().toString().trim()).execute();
                    }else {
                        showToast("Please Fill All Fields!");
                    }
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                break;
            case R.id.edit_state:
                if(isNetworkAvailable(getApplicationContext())){
                    isStateClick = true;
                    new get_state().execute();
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                break;
            case R.id.edit_city:
                if(isNetworkAvailable(getApplicationContext())){
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
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
