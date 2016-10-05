package ks.com.ksgas.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ks.com.ksgas.R;

/**
 * Created by jaga on 07/09/16.
 */
public class DealerListAdapter extends BaseAdapter{

    ArrayList<HashMap<String,String>> data;
    LayoutInflater inflater;
    Context context;

    public DealerListAdapter(Context context,ArrayList<HashMap<String,String>> list) {
        this.context = context;
        this.data = list;
        inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.dealer_list_row, null);
        }
            TextView dealerView = (TextView)view.findViewById(R.id.dealerTxt);
            TextView phoneView = (TextView)view.findViewById(R.id.phoneTxt);
            TextView addressView = (TextView)view.findViewById(R.id.addressTxt);
            TextView statusView = (TextView)view.findViewById(R.id.status_view);

            dealerView.setText(data.get(i).get("user_name"));
            phoneView.setText(data.get(i).get("phone"));
            addressView.setText(data.get(i).get("address")+"\n"+data.get(i).get("city")+"\n"+data.get(i).get("state"));
            if(data.get(i).get("status").equalsIgnoreCase("P")) {
                statusView.setText("Pending");
            }else if(data.get(i).get("status").equalsIgnoreCase("A")){
                statusView.setText("Accepted");
            }else if(data.get(i).get("status").equalsIgnoreCase("D")){
                statusView.setText("Delivered");
            }else {
                statusView.setText("Cancelled");
            }
        return view;
    }
}
