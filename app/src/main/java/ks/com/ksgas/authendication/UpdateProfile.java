package ks.com.ksgas.authendication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ks.com.ksgas.R;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.GPSTracker;
import ks.com.ksgas.common.ServiceHandler;

/**
 * Created by jaga on 11/3/16.
 */

public class UpdateProfile extends ActivityManager implements View.OnClickListener{

    EditText nameTxt, emailTxt, phoneTxt, passwordTxt, confirmTxt,codeTxt,addressTxt,cityTxt,stateTxt;
    Button submit;
    GPSTracker gps;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.signup);
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
        }
    }

    private class editProfile extends AsyncTask<String,String,String> {

        String user_id,name,email,address,city,state,msg;
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
                    String success = getJson.getString("Success");
                    if(success.equalsIgnoreCase("1")){
                        msg = getJson.getString("msg");
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
            showToast(msg);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.submit){
            if(isNetworkAvailable(getApplicationContext())) {
                new editProfile(userId,nameTxt.getText().toString().trim(),emailTxt.getText().toString().trim(),addressTxt.getText().toString().trim(),
                        cityTxt.getText().toString().trim(),stateTxt.getText().toString().trim()).execute();
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
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
