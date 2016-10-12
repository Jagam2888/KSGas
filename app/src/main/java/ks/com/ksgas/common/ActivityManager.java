package ks.com.ksgas.common;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import ks.com.ksgas.R;

/**
 * Created by jaga on 16/08/16.
 */
public class ActivityManager extends AppCompatActivity {

    protected int screenWidth, screenHeight;
    Calendar calendar;
    protected ProgressDialog progressDialog;
    protected ServiceHandler serviceHandler;
    protected SharedPreferences preferences;
    protected SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        calendar = java.util.Calendar.getInstance();
        serviceHandler = new ServiceHandler();
        preferences = this.getSharedPreferences("set",MODE_PRIVATE);
        editor = preferences.edit();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.loading));
    }

    public int getWidthByPercentage(double val) {
        int width = (int)(screenWidth * val) / 100;
        return width;
    }

    public int getHeightByPercentage(double val) {
        int height = (int)(screenHeight * val) / 100;
        return height;
    }

    public String getCurrentTime() {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("hh:mm a");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getDefault());

        String localTime = date.format(currentLocalTime);
        return localTime;
    }

    public void showAlertListView(ArrayList<HashMap<String,String>> lists, EditText text, String key) {

        final EditText editTxt = text;
        android.app.AlertDialog.Builder alertView = new android.app.AlertDialog.Builder(this);
        final ArrayAdapter<String> accountAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1);
        for (int i=0;i<lists.size();i++) {
            accountAdapter.add(lists.get(i).get(key));
        }

        alertView.setAdapter(accountAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = accountAdapter.getItem(which);
                editTxt.setText(strName);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertView.show();
    }

    public boolean hasStoragePermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void navigateWithBundle(Context context, Class<?> anotherClass,String value) {
        Intent intent = new Intent(context,anotherClass);
        intent.putExtra("key",value);
        startActivity(intent);
    }

    public void navigateTo(Context context, Class<?> anotherClass) {
        Intent intent = new Intent(context,anotherClass);
        startActivity(intent);
        finish();
    }

    public void showAlert(String value) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setIcon(R.mipmap.ic_launcher).setTitle(R.string.app_name).setCancelable(false).
                setMessage(value).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        alertBuilder.show();
    }
    public void showToast(String value) {
        Toast.makeText(getApplicationContext(),value,Toast.LENGTH_LONG).show();
    }

    public int getIndexOFValue(String value, ArrayList<HashMap<String, String>> listMap) {

        int i = 0;
        for (Map<String, String> map : listMap) {
            if (map.containsValue(value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    Log.w("INTERNET:",String.valueOf(i));
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.w("INTERNET:", "connected!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
