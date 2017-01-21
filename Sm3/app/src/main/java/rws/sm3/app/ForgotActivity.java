package rws.sm3.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomEditText;

/**
 * Created by Android-2 on 10/14/2015.
 */
public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {
    Context appContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        appContext = this;
        Init();
    }

    private void Init() {
        ((findViewById(R.id.btn_submit))).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String res = CheckValidation(((CustomEditText) findViewById(R.id.edt_email_forgot)).getText().toString());
                if (res.equals("suc")) {
                    if (Utility.isConnectingToInternet(appContext)) {
                        new ForGetPassword().execute(((CustomEditText) findViewById(R.id.edt_email_forgot)).getText().toString().trim(), "forgotpassword");
                    } else {
                        Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(appContext, res, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(appContext, MainActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    private String CheckValidation(String email) {
        if (email.equals("")) {
            return getResources().getString(R.string.emptyFiled);
        } else {
            if (new Utility().isEmailAddressValid(email) == false) {
                return getResources().getString(R.string.emailinvalid);
            } else {
                return "suc";
            }
        }
    }
    public class ForGetPassword extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;
        ProgressHUD progress;
        @Override
        protected void onPreExecute() {
            progress = ProgressHUD.show(appContext, getResources().getString(R.string.wait), true,
                    false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.EMAIL).value(params[0])
                        .key(Constant.METHOD).value(params[1])
                        .endObject();
                return putRequest.executePostRequest(putParameters);
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
               // Log.e("Forgot Response",""+networkResponse);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                       if(networkResponse.getString("responseMessage").equals("An Email sent to your registered email address")){
                           Toast.makeText(appContext, getResources().getString(R.string.sent_pass), Toast.LENGTH_SHORT).show();
                       }
                    }else{
                        Toast.makeText(appContext, getResources().getString(R.string.email_not_available), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                //Log.e("Exception",""+e);
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }
}
