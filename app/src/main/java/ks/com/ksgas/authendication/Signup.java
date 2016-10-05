package ks.com.ksgas.authendication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;
import ks.com.ksgas.fcm.MySharedPreference;

/**
 * Created by jaga on 16/08/16.
 */
public class Signup extends ActivityManager implements View.OnClickListener{

    EditText nameTxt, emailTxt, phoneTxt, passwordTxt, confirmTxt,codeTxt;
    Button submit;
    MySharedPreference mySharedPreference;
    String fcmToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        mySharedPreference = new MySharedPreference(this);
        fcmToken = mySharedPreference.getRefreshToken();

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
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    private class signUpTask extends AsyncTask<String,String,String> {
        String name,email,phone,password,msg;
        int flag = 0;
        public signUpTask(String name, String email,String phone,String password) {
            this.name = name;
            this.email = email;
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
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_name",name));
            valuePairs.add(new BasicNameValuePair("email",email));
            valuePairs.add(new BasicNameValuePair("phone",phone));
            valuePairs.add(new BasicNameValuePair("password",password));
            valuePairs.add(new BasicNameValuePair("status","1"));
            valuePairs.add(new BasicNameValuePair("user_type","customer"));
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
                                        codeTxt.getText().toString().trim() + phoneTxt.getText().toString().trim(), passwordTxt.getText().toString().trim()).execute();
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
