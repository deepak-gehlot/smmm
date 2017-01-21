package rws.sm3.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
Context appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext=this;
        Init();
    }
    private void Init(){
        (findViewById(R.id.btn_login)).setOnClickListener(this);
        (findViewById(R.id.btn_fb)).setOnClickListener(this);
        (findViewById(R.id.btn_g_plus)).setOnClickListener(this);
        (findViewById(R.id.btn_forgot)).setOnClickListener(this);
        (findViewById(R.id.btn_signup)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
     switch (v.getId()){
         case R.id.btn_signup:
             startActivity(new Intent(appContext,SignUpActivity.class));
             overridePendingTransition(R.anim.right_in, R.anim.left_out);
             finish();
             break;
         case R.id.btn_forgot:
             startActivity(new Intent(appContext,ForgotActivity.class));
             overridePendingTransition(R.anim.right_in, R.anim.left_out);
             finish();
             break;
         case R.id.btn_login:
             String res = CheckValidation(((CustomEditText) findViewById(R.id.edt_email)).getText().toString(),
                     ((CustomEditText) findViewById(R.id.edt_pass)).getText().toString());
             if (res.equals("suc")) {
                     if (Utility.isConnectingToInternet(appContext)) {
                         new LoginOnServer().execute(((CustomEditText) findViewById(R.id.edt_email)).getText().toString().trim(),
                                 ((CustomEditText) findViewById(R.id.edt_pass)).getText().toString(),"login");
                     } else {
                         Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                     }
             } else {
                 Toast.makeText(appContext, res, Toast.LENGTH_SHORT).show();
             }
             break;
      }
    }
    private String CheckValidation(String email, String pass) {
        if (email.equals("") || pass.equals("")) {
            return getResources().getString(R.string.emptyFiled);
        } else {
            if (new Utility().isEmailAddressValid(email) == false) {
                return getResources().getString(R.string.emailinvalid);
            } else {
                return "suc";
            }
        }
    }
    public class LoginOnServer extends AsyncTask<String, String, String> {
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
                        .key(Constant.PASSWORD).value(params[1])
                        .key(Constant.METHOD).value(params[2])
                        .endObject();
                return putRequest.executePostRequest(putParameters);
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Login Response",""+s);
 //{"result":"success","user_email":"abc@mailinator.com","first_name":"abc","last_name":"abc","user_id":"100","profile_pic":"886549.jpg","from":"","description":"Contrary to popular beliefs igcxuttc, Lorem Ipsum is not simply random text. It has roots in a piece of Classical Latin"}
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        Utility.setSharedPreference(appContext, Constant.ID, networkResponse.getString(Constant.ID));
                        Utility.setSharedPreference(appContext, Constant.FIRST_NAME, networkResponse.getString(Constant.FIRST_NAME));
                        Utility.setSharedPreference(appContext, Constant.EMAIL, networkResponse.getString(Constant.EMAIL));
                        Utility.setSharedPreference(appContext, Constant.LAST_NAME, networkResponse.getString(Constant.LAST_NAME));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_PRO_PIC, networkResponse.getString(Constant.LOG_U_PRO_PIC));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_LOCATION, networkResponse.getString(Constant.LOG_U_LOCATION));
                        Utility.setSharedPreference(appContext, Constant.STATUS, networkResponse.getString(Constant.STATUS));
                        SwitchActivity();
                    } else if(networkResponse.getString("result").equals("failed")){
                        if(networkResponse.getString("responseMessage").equals("Incorrect Email or Password")) {
                            Toast.makeText(appContext, getResources().getString(R.string.incorrect_cre), Toast.LENGTH_SHORT).show();
                        }
                        else if(networkResponse.getString("responseMessage").equals("not_verified")) {
                            alertBoxShowSuccessLogin(appContext,getResources().getString(R.string.email_not_verified));
                        }
                        else{
                            Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("Login Exc",""+e);
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }
    private void SwitchActivity(){
        startActivity(new Intent(appContext,Home_Activity.class));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    void alertBoxShowSuccessLogin(Context context, String msg) {
        // set dialog for user's current location set for searching theaters.
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(getResources().getString(R.string.not_verify));
        dialog.setMessage(msg);
        dialog.setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
               /* startActivity(new Intent(appContext, MainActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();*/
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}
