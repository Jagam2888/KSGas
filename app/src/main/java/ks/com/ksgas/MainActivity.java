package ks.com.ksgas;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ks.com.ksgas.authendication.Login;
import ks.com.ksgas.common.ActivityManager;
import ks.com.ksgas.common.Constants;
import ks.com.ksgas.common.ServiceHandler;
import ks.com.ksgas.customer.BookCylinder;
import ks.com.ksgas.customer.BookingHistory;
import ks.com.ksgas.customer.DealerList;
import ks.com.ksgas.dealer.OrderHistory;
import ks.com.ksgas.dealer.OrderRequest;
import ks.com.ksgas.floatingactionmenu.FloatingActionButton;
import ks.com.ksgas.floatingactionmenu.FloatingActionsMenu;

public class MainActivity extends ActivityManager
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    View shadowView;
    FloatingActionsMenu menus;
    RelativeLayout mainLayout;
    String userName, phone, user_id, user_type;
    NavigationView navigationView;
    FloatingActionButton actionLogin, actionBookingHistory, actionBookCylinder, orderHistory, orderReq;
    TextView loginUser;
    ImageView imageOne,imageTwo,imageThree,imageFour,imageFive;
    MenuItem menuLogout,menuBookHistory, menuBook, menuOrderHistory, menuOrderRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        menus = (FloatingActionsMenu)findViewById(R.id.multiple_actions);
        shadowView = (View)findViewById(R.id.shadowView);
        shadowView.setOnClickListener(this);

        actionLogin = (FloatingActionButton)findViewById(R.id.action_login);
        actionLogin.setOnClickListener(this);

        actionBookCylinder = (FloatingActionButton)findViewById(R.id.action_book_cylinder);
        actionBookCylinder.setOnClickListener(this);

        actionBookingHistory = (FloatingActionButton)findViewById(R.id.action_book_history);
        actionBookingHistory.setOnClickListener(this);

        orderHistory = (FloatingActionButton)findViewById(R.id.action_order_history);
        orderHistory.setOnClickListener(this);

        orderReq = (FloatingActionButton)findViewById(R.id.action_order_req);
        orderReq.setOnClickListener(this);

        menus.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                shadowView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                shadowView.setVisibility(View.GONE);
            }
        });

        mainLayout = (RelativeLayout)findViewById(R.id.mainlayout);
        loginUser = (TextView)findViewById(R.id.login_user);
        userName = preferences.getString("user_name",null);
        phone = preferences.getString("phone",null);
        user_id = preferences.getString("user_id",null);
        user_type = preferences.getString("user_type",null);
        if(user_id != null) {
            actionLogin.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            loginUser.setText("Dear "+userName);
            if(user_type.equalsIgnoreCase("dealer")) {
                new check_balance().execute();
                visibleItem();
            }
        }else {
            actionBookingHistory.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.nav_book_history).setVisible(false);
        }
        displayImages();
        hasStoragePermission(1);


    }

    void displayImages() {


        TextView textView = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textView.setLayoutParams(textViewLayoutParams);
        textView.setText("We have best dealers in Malaysia");
        textView.setTextColor(Color.RED);
        textView.setTextSize(16);
        textView.setSingleLine();
        textView.setId(View.generateViewId());
        mainLayout.addView(textView);

        imageOne = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams imageOneLayoutParams = new RelativeLayout.LayoutParams(getWidthByPercentage(50),getHeightByPercentage(20));
        imageOneLayoutParams.addRule(RelativeLayout.BELOW,textView.getId());
        imageOne.setLayoutParams(imageOneLayoutParams);
        imageOne.setImageResource(R.drawable.gas_one);
        imageOne.setId(View.generateViewId());
        mainLayout.addView(imageOne);
        imageOne.setOnClickListener(this);

        imageTwo = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams imageTwoLayoutParams = new RelativeLayout.LayoutParams(getWidthByPercentage(50),getHeightByPercentage(20));
        imageTwoLayoutParams.addRule(RelativeLayout.RIGHT_OF,imageOne.getId());
        imageTwoLayoutParams.addRule(RelativeLayout.BELOW,textView.getId());
        imageTwo.setLayoutParams(imageTwoLayoutParams);
        imageTwo.setImageResource(R.drawable.gas_two);
        imageTwo.setId(View.generateViewId());
        mainLayout.addView(imageTwo);
        imageTwo.setOnClickListener(this);

        imageThree = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams imageThreeLayoutParams = new RelativeLayout.LayoutParams(getWidthByPercentage(50),getHeightByPercentage(20));
        imageThreeLayoutParams.addRule(RelativeLayout.BELOW,imageOne.getId());
        imageThreeLayoutParams.setMargins(0,getHeightByPercentage(2),0,0);
        imageThree.setLayoutParams(imageThreeLayoutParams);
        imageThree.setImageResource(R.drawable.gas_three);
        imageThree.setId(View.generateViewId());
        mainLayout.addView(imageThree);
        imageThree.setOnClickListener(this);

        imageFour = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams imageFourLayoutParams = new RelativeLayout.LayoutParams(getWidthByPercentage(50),getHeightByPercentage(20));
        imageFourLayoutParams.addRule(RelativeLayout.RIGHT_OF,imageThree.getId());
        imageFourLayoutParams.addRule(RelativeLayout.BELOW,imageOne.getId());
        imageFourLayoutParams.setMargins(0,getHeightByPercentage(2),0,0);
        imageFour.setLayoutParams(imageFourLayoutParams);
        imageFour.setImageResource(R.drawable.gas_four);
        imageFour.setId(View.generateViewId());
        mainLayout.addView(imageFour);
        imageFour.setOnClickListener(this);

        imageFive = new ImageView(getApplicationContext());
        RelativeLayout.LayoutParams imageFiveLayoutParams = new RelativeLayout.LayoutParams(getWidthByPercentage(50),getHeightByPercentage(20));
        imageFiveLayoutParams.addRule(RelativeLayout.BELOW,imageThree.getId());
        imageFiveLayoutParams.setMargins(0,getHeightByPercentage(2),0,0);
        imageFive.setLayoutParams(imageFiveLayoutParams);
        imageFive.setImageResource(R.drawable.gas_five);
        imageFive.setId(View.generateViewId());
        mainLayout.addView(imageFive);
        imageFive.setOnClickListener(this);

    }

    private class check_balance extends AsyncTask<String,String,String> {

        String balanceAmount;
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("dealer_id",user_id));
            String response = serviceHandler.makeServiceCall(Constants.check_balance, ServiceHandler.POST,valuePairs);
            if(response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject getJson = jsonObject.getJSONObject("response");
                    String success = getJson.getString("Success");
                    if(success.equalsIgnoreCase("1")) {
                        JSONArray itemsArray = getJson.getJSONArray("items");
                        JSONObject getItems = itemsArray.getJSONObject(0);
                        balanceAmount = getItems.getString("amount");
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
            navigationView.getMenu().findItem(R.id.nav_order_balance).setTitle("Balance : "+balanceAmount);
        }
    }

    private class logoutTask extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            List<NameValuePair>valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("user_id",user_id));
            serviceHandler.makeServiceCall(Constants.logout,ServiceHandler.POST,valuePairs);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            editor.clear();
            editor.commit();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    void menusClosed() {
        if(menus.isExpanded())
            menus.collapse();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menuLogout = menu.findItem(R.id.action_logout);

        if(user_id != null) {
            menuLogout.setTitle("Logout");
        }else {
            menuLogout.setTitle("Login");
        }

        return true;
    }

    void visibleItem() {
        navigationView.getMenu().findItem(R.id.nav_book_history).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_book_cylinder).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_dealer_list).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_order_history).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_order_req).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_order_balance).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_feedback).setTitle("Feedback");
        orderHistory.setVisibility(View.VISIBLE);
        orderReq.setVisibility(View.VISIBLE);
        actionBookCylinder.setVisibility(View.GONE);
        actionBookingHistory.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        editor.putInt("return_back",0);
        editor.commit();
        if(id == R.id.shadowView) {
            menusClosed();
        }else if(id == R.id.action_login) {
            startActivity(new Intent(getApplicationContext(),Login.class));
            menusClosed();
        }else if(id == R.id.action_book_cylinder){
            if(isNetworkAvailable(getApplicationContext())) {
                navigateWithBundle(getApplicationContext(), BookCylinder.class, "YELLOW");
                menusClosed();
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == R.id.action_book_history){
            if(isNetworkAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), BookingHistory.class));
            }else {
                showAlert("Please Check Your Internet Connection!");
            }

        }else if(id == R.id.action_order_req) {
            if(isNetworkAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), OrderRequest.class));
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == R.id.action_order_history) {
            if(isNetworkAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), OrderHistory.class));
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == imageOne.getId()) {
            if(isNetworkAvailable(getApplicationContext())) {
                navigateWithBundle(getApplicationContext(), BookCylinder.class, "YELLOW");
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == imageTwo.getId()) {
            if(isNetworkAvailable(getApplicationContext())) {
                navigateWithBundle(getApplicationContext(), BookCylinder.class, "ORANGE");
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == imageThree.getId()) {
            if(isNetworkAvailable(getApplicationContext())) {
                navigateWithBundle(getApplicationContext(), BookCylinder.class, "BLUE");
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == imageFour.getId()) {
            if(isNetworkAvailable(getApplicationContext())) {
                navigateWithBundle(getApplicationContext(), BookCylinder.class, "RED");
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == imageFive.getId()) {
            if(isNetworkAvailable(getApplicationContext())) {
                navigateWithBundle(getApplicationContext(), BookCylinder.class, "GREEN");
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }
       /* switch (view.getId()) {
            case R.id.shadowView:
                menusClosed();
                break;
            case R.id.action_login:
                startActivity(new Intent(getApplicationContext(),Login.class));
                menusClosed();
                break;
            case R.id.action_book_cylinder:
                startActivity(new Intent(getApplicationContext(), BookCylinder.class));
                menusClosed();
                break;
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            if(isNetworkAvailable(getApplicationContext())) {
                if (user_id != null) {
                    new logoutTask().execute();
                } else {
                    startActivity(new Intent(getApplicationContext(), Login.class));
                }
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            menusClosed();
            // Handle the camera action
        } else if (id == R.id.nav_book_cylinder) {
            if(isNetworkAvailable(getApplicationContext())) {
                navigateWithBundle(getApplicationContext(), BookCylinder.class, "YELLOW");
                menusClosed();
            }else {
                showAlert("Please Check Your Internet Connection!");
            }

        } else if(id == R.id.nav_book_history){
            if(isNetworkAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), BookingHistory.class));
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == R.id.nav_order_req) {
            if(isNetworkAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), OrderRequest.class));
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if(id == R.id.nav_order_history) {
            if(isNetworkAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), OrderHistory.class));
            }else {
                showAlert("Please Check Your Internet Connection!");
            }
        }else if (id == R.id.nav_dealer_list) {
            /*if(isNetworkAvailable(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), DealerList.class));
            }else {
                showAlert("Please Check Your Internet Connection!");
            }*/

        } else if (id == R.id.nav_service_center) {

        } else if (id == R.id.nav_talk) {
            startActivity(new Intent(getApplicationContext(),Talktous.class));
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(getApplicationContext(),Feedback.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        userName = preferences.getString("user_name",null);
        phone = preferences.getString("phone",null);
        user_id = preferences.getString("user_id",null);
        user_type = preferences.getString("user_type",null);
        if(user_id != null) {
            actionLogin.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            loginUser.setText("Dear "+userName);
            actionBookingHistory.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.nav_book_history).setVisible(true);
            if(user_type.equalsIgnoreCase("dealer")) {
                new check_balance().execute();
                visibleItem();
            }
        }else {
            actionBookingHistory.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.nav_book_history).setVisible(false);
        }
    }
}
