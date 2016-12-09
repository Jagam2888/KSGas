package ks.com.ksgas.dealer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import ks.com.ksgas.common.ServiceHandler;

/**
 * Created by jaga on 14/09/16.
 */
public class OrderActivity extends ActivityManager implements View.OnClickListener{

    String booking_id, user_id, orderStatus;
    TextView typeView,qtyView, addressView,phoneView, nameView, timeView;
    Button accept, cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.order);

        initialize();
    }

    void initialize() {
        Bundle bun = getIntent().getExtras();
        booking_id = bun.getString("key");
        user_id = preferences.getString("user_id",null);

        nameView = (TextView)findViewById(R.id.customer_name);
        addressView = (TextView)findViewById(R.id.customer_address);
        phoneView = (TextView)findViewById(R.id.customer_phone);
        typeView = (TextView)findViewById(R.id.customer_type);
        qtyView = (TextView)findViewById(R.id.customer_qty);
        timeView = (TextView)findViewById(R.id.customer_time);
        accept = (Button)findViewById(R.id.accept);
        accept.setOnClickListener(this);
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        new get_order().execute();
    }

    private class get_order extends AsyncTask<String,String,String> {
            String id,name,address,phone,type,time,qty,city,state;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("booking_id",booking_id));
            String response = serviceHandler.makeServiceCall(Constants.get_order_request, ServiceHandler.POST,valuePairs);
            if(response != null) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getResponse = jsonObject.getJSONObject("response");
                    String success = getResponse.getString("Success");
                    if(success.equalsIgnoreCase("1")) {
                        JSONArray getArray = getResponse.getJSONArray("items");

                            JSONObject getItems = getArray.getJSONObject(0);
                            id = getItems.getString("booking_id");
                            type = getItems.getString("cylinder_type");
                            phone = getItems.getString("phone");
                            name = getItems.getString("user_name");
                            address = getItems.getString("address");
                            city = getItems.getString("city");
                            state = getItems.getString("state");
                            qty = getItems.getString("quantity");
                            time = getItems.getString("expected_time");
                            orderStatus = getItems.getString("order_status");

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
            nameView.setText(name);
            addressView.setText(address+"\n"+city+"\n"+state);
            phoneView.setText(phone);
            typeView.setText(type);
            qtyView.setText(qty);
            timeView.setText(time);
            if(orderStatus.equalsIgnoreCase("P")) {
                accept.setText("Accept");
                cancel.setVisibility(View.GONE);
            }else {
                accept.setText("Delivered");
                cancel.setVisibility(View.VISIBLE);
            }
        }
    }

    private class orderAcceptTask extends AsyncTask<String,String,String> {

        String msg, status;
        int flag = 0;
        public orderAcceptTask(String status) {
            this.status = status;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("booking_id",booking_id));
            valuePairs.add(new BasicNameValuePair("user_id",user_id));
            valuePairs.add(new BasicNameValuePair("status",status));
            String response = serviceHandler.makeServiceCall(Constants.accept_order_request,ServiceHandler.POST,valuePairs);
            if(response != null) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getResponse = jsonObject.getJSONObject("response");
                    String success = getResponse.getString("Success");
                    msg = getResponse.getString("msg");
                    if(success.equalsIgnoreCase("1")) {
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
            showAlert(msg);
            editor.putInt("return_back",1);
            editor.commit();

        }
    }

    private class orderDeliveredTask extends AsyncTask<String,String,String> {

        String msg,success;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("booking_id",booking_id));
            valuePairs.add(new BasicNameValuePair("user_id",user_id));

            String response = serviceHandler.makeServiceCall(Constants.orderDelivered,ServiceHandler.POST,valuePairs);
            if(response != null){
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    success = getJson.getString("Success");
                    msg = getJson.getString("msg");

                }catch (JSONException js) {
                    js.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            showAlert(msg);
            editor.putInt("return_back",1);
            editor.commit();
        }
    }

    private class orderCancelTask extends AsyncTask<String,String,String> {

        String msg,success;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("booking_id",booking_id));
            valuePairs.add(new BasicNameValuePair("user_id",user_id));

            String response = serviceHandler.makeServiceCall(Constants.orderCancel,ServiceHandler.POST,valuePairs);
            if(response != null){
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    success = getJson.getString("Success");
                    msg = getJson.getString("msg");

                }catch (JSONException js) {
                    js.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            showAlert(msg);
            editor.putInt("return_back",1);
            editor.commit();
        }
    }

    void showAlertView() {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(OrderActivity.this);
        alBuilder.setTitle(R.string.app_name).setIcon(R.mipmap.ic_launcher);
        alBuilder.setMessage("Are you sure want to Cancel?").setCancelable(false);
        alBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new orderCancelTask().execute();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept:
                if(isNetworkAvailable(getApplicationContext())) {
                    String status;
                    if (orderStatus.equalsIgnoreCase("P")) {
                        status = "A";
                        new orderAcceptTask(status).execute();
                    } else if(orderStatus.equalsIgnoreCase("A")){
                        status = "D";
                        new orderDeliveredTask().execute();
                    }else {
                        //this is for safety purpose
                        accept.setVisibility(View.GONE);
                        cancel.setVisibility(View.GONE);
                    }
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                break;
            case R.id.cancel:
                if(isNetworkAvailable(getApplicationContext())) {
                    showAlertView();
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
