package ks.com.ksgas.customer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import ks.com.ksgas.adapter.BookingHistoryAdapter;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;

/**
 * Created by jaga on 06/09/16.
 */
public class BookingHistory extends ActivityManager{

    ListView bookingListview;
    TextView noHistoryView;
    ArrayList<HashMap<String,String>>bookingHistoryList;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Booking History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.bookinghistory);
        bookingListview = (ListView)findViewById(R.id.booking_list);
        noHistoryView = (TextView)findViewById(R.id.no_history);
        bookingHistoryList = new ArrayList<HashMap<String, String>>();
        user_id = preferences.getString("user_id",null);
        new getBookingHistory().execute();
    }

    private class getBookingHistory extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            bookingHistoryList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_id",user_id));
            String response = serviceHandler.makeServiceCall(Constants.bookingHistory, ServiceHandler.POST,valuePairs);

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
                            maps.put("booking_id",getItems.getString("booking_id"));
                            maps.put("cylinder_type",getItems.getString("cylinder_type"));
                            maps.put("phone",getItems.getString("phone"));
                            maps.put("order_status",getItems.getString("order_status"));
                            maps.put("user_name",getItems.getString("user_name"));
                            maps.put("dealer_phone",getItems.getString("dealer_phone"));
                            bookingHistoryList.add(maps);
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
            if(bookingHistoryList.size() > 0) {
                BookingHistoryAdapter adapter = new BookingHistoryAdapter(getApplicationContext(), bookingHistoryList);
                bookingListview.setAdapter(adapter);
            }else {
                bookingListview.setVisibility(View.GONE);
                noHistoryView.setVisibility(View.VISIBLE);
            }
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
