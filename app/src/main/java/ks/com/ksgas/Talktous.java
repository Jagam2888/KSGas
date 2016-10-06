package ks.com.ksgas;

import android.os.Bundle;
import android.view.MenuItem;

import ks.com.ksgas.common.ActivityManager;

/**
 * Created by jaga on 10/6/16.
 */

public class Talktous extends ActivityManager{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Talk To Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.talktous);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
