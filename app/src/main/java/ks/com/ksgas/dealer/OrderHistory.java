package ks.com.ksgas.dealer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import ks.com.ksgas.adapter.DealerListAdapter;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;

/**
 * Created by jaga on 14/09/16.
 */
public class OrderHistory extends ActivityManager{

    ListView orderHistoryListview;
    TextView noHistoryView;
    ArrayList<HashMap<String,String>>orderHistoryList;
    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Order History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.order_history);
        orderHistoryList = new ArrayList<HashMap<String, String>>();
        orderHistoryListview = (ListView)findViewById(R.id.order_history_list);
        noHistoryView = (TextView)findViewById(R.id.no_history);
        user_id = preferences.getString("user_id",null);
        new getOrderHistory().execute();

        /*orderHistoryListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                navigateWithBundle(getApplicationContext(),OrderActivity.class,OrderHistoryList.get(i).get("booking_id"));
            }
        });*/
    }

    private class getOrderHistory extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            orderHistoryList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("dealer_id",user_id));
            String response = serviceHandler.makeServiceCall(Constants.order_request_history, ServiceHandler.POST,valuePairs);

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
                            maps.put("status",getItems.getString("order_status"));
                            maps.put("user_id",getItems.getString("user_id"));
                            maps.put("user_name",getItems.getString("user_name"));
                            maps.put("address",getItems.getString("address"));
                            maps.put("city",getItems.getString("city"));
                            maps.put("state",getItems.getString("state"));
                            orderHistoryList.add(maps);
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
            if(orderHistoryList.size() > 0) {
                DealerListAdapter adapter = new DealerListAdapter(getApplicationContext(), orderHistoryList);
                orderHistoryListview.setAdapter(adapter);
            }else {
                orderHistoryListview.setVisibility(View.GONE);
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
