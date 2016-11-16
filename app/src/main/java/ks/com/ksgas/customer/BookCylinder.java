package ks.com.ksgas.customer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ks.com.ksgas.R;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.GPSTracker;
import ks.com.ksgas.common.ServiceHandler;

/**
 * Created by jaga on 22/08/16.
 */
public class BookCylinder extends ActivityManager implements View.OnClickListener{

    EditText cylinderColor, quantity, addressTxt, stateTxt, cityTxt, phoneTxt,codeTxt;
    static EditText expectedTime;
    ArrayList<HashMap<String,String>>cylinderList, stateList,cityList;
    GPSTracker gps;
    String stateIdz, cylinderType, user_id,address,city,state,phone;
    boolean isStateClick = false, isCheckLocation = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookcylinder);
        getSupportActionBar().setTitle("Book Cylinder");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cylinderList = new ArrayList<HashMap<String,String>>();
        stateList = new ArrayList<HashMap<String,String>>();
        cityList = new ArrayList<HashMap<String,String>>();
        gps = new GPSTracker(this);

        Bundle bun = getIntent().getExtras();
        cylinderType = bun.getString("key");
        user_id = preferences.getString("user_id",null);
        address = preferences.getString("address",null);
        city = preferences.getString("city",null);
        state = preferences.getString("state",null);
        phone = preferences.getString("phone",null);
        initialize();

    }


    public void onCheckboxClicked(View view) {
        boolean isChecked = ((CheckBox)view).isChecked();
        switch (view.getId()) {
            case R.id.checkbox:
                if(isChecked) {
                    if(user_id != null) {
                        addressTxt.setText(address);
                        cityTxt.setText(city);
                        stateTxt.setText(state);
                    }else {
                        showToast("Please Login!");
                    }
                }else {
                    gps.getAddress(addressTxt,cityTxt,stateTxt);
                }
                break;
        }
    }

    void initialize() {
        cylinderColor = (EditText)findViewById(R.id.cylinder_color);
        cylinderColor.setText(cylinderType);
        cylinderColor.setOnClickListener(this);
        quantity = (EditText)findViewById(R.id.quantity);
        addressTxt = (EditText)findViewById(R.id.address);
        stateTxt = (EditText)findViewById(R.id.state);
        stateTxt.setOnClickListener(this);
        cityTxt = (EditText)findViewById(R.id.city);
        cityTxt.setOnClickListener(this);
        phoneTxt = (EditText)findViewById(R.id.phone);
        codeTxt = (EditText)findViewById(R.id.edit_code);
        expectedTime = (EditText)findViewById(R.id.expected_time);
        expectedTime.setText(getCurrentTime());
        expectedTime.setOnClickListener(this);
        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        gps.getAddress(addressTxt,cityTxt,stateTxt);
        new get_state().execute();
        if(user_id != null)
            phoneTxt.setText(phone.substring(3));
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
//            expectedTime.setText(hourOfDay+":"+minute);
            boolean isPM = (hourOfDay >= 12);
            expectedTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
        }
    }

    private class get_gasType extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            cylinderList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response = serviceHandler.makeServiceCall(Constants.get_gasType, ServiceHandler.GET);

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
                            maps.put("gas_id",getItems.getString("gas_id"));
                            maps.put("gas_name",getItems.getString("gas_name"));
                            maps.put("gas_image",getItems.getString("gas_image"));
                            cylinderList.add(maps);
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
            showAlertListView(cylinderList,cylinderColor,"gas_name");
        }
    }


    private class get_state extends AsyncTask<String,String,String> {
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
                android.app.AlertDialog.Builder alertView = new android.app.AlertDialog.Builder(BookCylinder.this);
                final ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(BookCylinder.this, android.R.layout.simple_list_item_1, android.R.id.text1);
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
            android.app.AlertDialog.Builder alertView = new android.app.AlertDialog.Builder(BookCylinder.this);
            final ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(BookCylinder.this,android.R.layout.simple_list_item_1, android.R.id.text1);
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

    private class bookingGas extends AsyncTask<String,String,String> {

        String id, type,quantity,address,city,state,phone,time;
        int flag = 0;
        public bookingGas(String id, String type,String quantity,String address,String city,String state,String phone,String time) {
            this.id = id;
            this.type = type;
            this.quantity = quantity;
            this.address = address;
            this.city = city;
            this.state = state;
            this.phone = phone;
            this.time = time;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_id",id));
            valuePairs.add(new BasicNameValuePair("cylinder_type",type));
            valuePairs.add(new BasicNameValuePair("quantity",quantity));
            valuePairs.add(new BasicNameValuePair("address",address));
            valuePairs.add(new BasicNameValuePair("city",city));
            valuePairs.add(new BasicNameValuePair("state",state));
            valuePairs.add(new BasicNameValuePair("phone",phone));
            valuePairs.add(new BasicNameValuePair("expected_time",time));

            String response = serviceHandler.makeServiceCall(Constants.bookingGas,ServiceHandler.POST,valuePairs);
            if(response != null) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getResponse = jsonObject.getJSONObject("response");
                    String success = getResponse.getString("Success");
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
            if(flag == 1) {
                showAlert("Your Booking has been sent successfully.Please wait for response");
            }else {
                showAlert("Your Booking Failed");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.expected_time:
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.cylinder_color:
                if(isNetworkAvailable(getApplicationContext())) {
                    new get_gasType().execute();
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                break;
            case R.id.state:
                if(isNetworkAvailable(getApplicationContext())) {
                    new get_state().execute();
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                isStateClick = true;
                break;
            case R.id.city:
                if(isNetworkAvailable(getApplicationContext())) {
                    if(stateIdz != null) {
                        new get_city(stateIdz).execute();
                    }
                }else {
                    showAlert("Please Check Your Internet Connection!");
                }
                break;
            case R.id.submit:
                if(user_id !=null) {
                    if(isNetworkAvailable(getApplicationContext())) {
                        if (!phoneTxt.getText().toString().trim().isEmpty() && !addressTxt.getText().toString().trim().isEmpty()) {
                            if(phoneTxt.getText().toString().trim().length() == 9 || phoneTxt.getText().toString().trim().length() == 10) {
                                new bookingGas(user_id, cylinderColor.getText().toString().trim(), quantity.getText().toString().trim(), addressTxt.getText().toString().trim()
                                        , cityTxt.getText().toString().trim(), stateTxt.getText().toString().trim(), codeTxt.getText().toString().trim() + phoneTxt.getText().toString().trim(),
                                        expectedTime.getText().toString().trim()).execute();
                            }else {
                                showToast("Invalid Phone Number");
                            }
                        } else {
                            showToast("Filed(s) Missing");
                        }
                    }else {
                        showAlert("Please Check Your Internet Connection!");
                    }
                }else {
                    showToast("Please Login");
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

    @Override
    protected void onResume() {
        super.onResume();
        if(isCheckLocation)
            gps.getAddress(addressTxt,cityTxt,stateTxt);
    }
}
