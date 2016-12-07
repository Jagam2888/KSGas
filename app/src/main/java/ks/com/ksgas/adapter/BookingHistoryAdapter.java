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
 * Created by jaga on 06/09/16.
 */
public class BookingHistoryAdapter extends BaseAdapter{

    ArrayList<HashMap<String,String>>data;
    LayoutInflater inflater;
    Context context;

    public BookingHistoryAdapter(Context context,ArrayList<HashMap<String,String>> list) {
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
            view = inflater.inflate(R.layout.history_row, null);
        }
            TextView cylinderView = (TextView)view.findViewById(R.id.cylinderTxt);
            TextView phoneTxt = (TextView)view.findViewById(R.id.phoneTxt);
            TextView phoneView = (TextView)view.findViewById(R.id.phoneView);
            TextView statusView = (TextView)view.findViewById(R.id.statusTxt);
            TextView dealerView = (TextView)view.findViewById(R.id.dealerView);
            TextView dealerTxt = (TextView)view.findViewById(R.id.dealerTxt);

            cylinderView.setText(data.get(i).get("cylinder_type"));


            if(!data.get(i).get("user_name").equalsIgnoreCase("")){
                dealerView.setVisibility(View.VISIBLE);
                dealerTxt.setVisibility(View.VISIBLE);
                phoneView.setVisibility(View.VISIBLE);
                phoneTxt.setVisibility(View.VISIBLE);
                dealerTxt.setText(data.get(i).get("user_name"));
                phoneTxt.setText(data.get(i).get("dealer_phone"));
            }else {
                dealerTxt.setText("");
                phoneTxt.setText("");
            }

            if(data.get(i).get("order_status").equalsIgnoreCase("P")) {
                statusView.setText("Pending");
            }else if(data.get(i).get("order_status").equalsIgnoreCase("A")){
                statusView.setText("Accepted");
            }else if(data.get(i).get("order_status").equalsIgnoreCase("D")){
                statusView.setText("Delivered");
            }else {
                statusView.setText("Cancelled");
            }
        return view;
    }
}
