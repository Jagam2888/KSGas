package ks.com.ksgas.authendication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ks.com.ksgas.R;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;

/**
 * Created by jaga on 11/3/16.
 */

public class ChangePassword extends ActivityManager implements View.OnClickListener{

    EditText old_password,new_password,confirm_password;
    Button submit;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.change_password);
        Bundle bun = getIntent().getExtras();
        userId = bun.getString("key");
        initialize();
    }

    void initialize() {
        old_password = (EditText)findViewById(R.id.old_edit_password);
        new_password = (EditText)findViewById(R.id.new_edit_password);
        confirm_password = (EditText)findViewById(R.id.edit_confirm_password);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    private class changePassword extends AsyncTask<String,String,String> {

        String user_id,old_password,new_password,msg;
        public changePassword(String user_id,String old_password,String new_password) {
            this.user_id = user_id;
            this.old_password = old_password;
            this.new_password = new_password;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_id",user_id));
            valuePairs.add(new BasicNameValuePair("old_password",old_password));
            valuePairs.add(new BasicNameValuePair("new_password",new_password));
            String response = serviceHandler.makeServiceCall(Constants.change_password, ServiceHandler.POST,valuePairs);
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
        if(view.getId() == R.id.submit)
        {
            if(isNetworkAvailable(getApplicationContext())) {
                if(new_password.getText().toString().trim().equals(confirm_password.getText().toString().trim())) {
                    new changePassword(userId, old_password.getText().toString().trim(), new_password.getText().toString().trim()).execute();
                }else {
                    showToast("Password Mismatch");
                }

            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
