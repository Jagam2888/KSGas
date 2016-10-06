package ks.com.ksgas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ks.com.ksgas.authendication.DealerRequest;
import ks.com.ksgas.common.ActivityManager;

/**
 * Created by jaga on 10/6/16.
 */

public class Feedback extends ActivityManager implements View.OnClickListener{

    EditText emailTxt,phoneTxt,msgTxt;
    Button submit;
    TextView msgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.dealerrequest);
        emailTxt = (EditText)findViewById(R.id.your_email);
        emailTxt.setText("moorthy@kshomegas.com");
        phoneTxt = (EditText) findViewById(R.id.your_phone);
        msgTxt = (EditText)findViewById(R.id.your_msg);
        msgTxt.setHint("Write Your Feedback");
        msgView = (TextView)findViewById(R.id.msgview);
        msgView.setText("Feedback");
        submit = (Button)findViewById(R.id.email_submit);
        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_submit:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailTxt.getText().toString().trim()});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                i.putExtra(Intent.EXTRA_TEXT   , "Phone: "+phoneTxt.getText().toString().trim()+"\n"+msgTxt.getText().toString().trim());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                    finish();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Feedback.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
