package ks.com.ksgas.customer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ks.com.ksgas.R;
import ks.com.ksgas.adapter.DealerListAdapter;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;

/**
 * Created by jaga on 07/09/16.
 */
public class DealerList extends ActivityManager{

    ListView dealerListview;
    ArrayList<HashMap<String,String>>dealerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Dealer List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.dealer_list);
        dealerListview = (ListView)findViewById(R.id.dealer_list_view);
        dealerList = new ArrayList<HashMap<String, String>>();
        new getUser().execute();
    }
    private class getUser extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            dealerList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_type","dealer"));
            String response = serviceHandler.makeServiceCall(Constants.getUsers, ServiceHandler.POST,valuePairs);

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
                            maps.put("user_name",getItems.getString("user_name"));
                            maps.put("address",getItems.getString("address"));
                            maps.put("city",getItems.getString("city"));
                            maps.put("state",getItems.getString("state"));
                            maps.put("phone",getItems.getString("phone"));
                            dealerList.add(maps);
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
            DealerListAdapter adapter = new DealerListAdapter(getApplicationContext(),dealerList);
            dealerListview.setAdapter(adapter);
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
