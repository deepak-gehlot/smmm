package rws.sm3.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONStringer;
import java.net.URLEncoder;
import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomEditText;

/**
 * Created by Android-2 on 10/14/2015.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    Context appContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        appContext=this;
        Init();
    }
    private void Init(){
        (findViewById(R.id.btn_reg)).setOnClickListener(this);
        (findViewById(R.id.btn_alreac)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reg:
                String res = CheckValidation(((CustomEditText) findViewById(R.id.edt_fname)).getText().toString(),
                        ((CustomEditText) findViewById(R.id.edt_email_sign)).getText().toString(),
                        ((CustomEditText) findViewById(R.id.edt_u_name)).getText().toString(),
                        ((CustomEditText) findViewById(R.id.edt_pass_sign)).getText().toString());
                if (res.equals("suc")) {
                        if (Utility.isConnectingToInternet(appContext)) {
                            new RegistorOnServer().execute(((CustomEditText) findViewById(R.id.edt_fname)).getText().toString(),
                                    ((CustomEditText) findViewById(R.id.edt_email_sign)).getText().toString().trim(),
                                    ((CustomEditText) findViewById(R.id.edt_u_name)).getText().toString(),
                                    ((CustomEditText) findViewById(R.id.edt_pass_sign)).getText().toString(), "signup");
                        } else {
                            Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                else {
                    Toast.makeText(appContext, res, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_alreac:
                onBackPressed();
                break;
        }
    }
    private String CheckValidation(String f_name, String email, String ph_no, String pass) {
        if (f_name.equals("") || ph_no.equals("") || email.equals("") || pass.equals("")) {
            return getResources().getString(R.string.emptyFiled);
        } else {
            if (new Utility().isEmailAddressValid(email) == false) {
                return getResources().getString(R.string.emailinvalid);
            } else {
                    return "suc";
                }
            }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(appContext,MainActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    public class RegistorOnServer extends AsyncTask<String, String, String> {
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
                        .key(Constant.FIRST_NAME).value(URLEncoder.encode(params[0], "utf-8").trim())
                        .key(Constant.EMAIL).value(params[1])
                        .key(Constant.LAST_NAME).value(URLEncoder.encode(params[2], "utf-8").trim())
                        .key(Constant.PASSWORD).value(params[3])
                        .key(Constant.METHOD).value(params[4])
                        .endObject();
                return putRequest.executePostRequest(putParameters);
            } catch (Exception e) {
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
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                       /* Utility.setSharedPreference(appContext, Constant.ID, networkResponse.getString(Constant.ID));
                        Utility.setSharedPreference(appContext, Constant.FIRST_NAME, networkResponse.getString(Constant.FIRST_NAME));
                        Utility.setSharedPreference(appContext, Constant.EMAIL, networkResponse.getString(Constant.EMAIL));
                        Utility.setSharedPreference(appContext, Constant.LAST_NAME, networkResponse.getString(Constant.LAST_NAME));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_PRO_PIC, networkResponse.getString(Constant.LOG_U_PRO_PIC));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_LOCATION, networkResponse.getString(Constant.LOG_U_LOCATION));
                        Utility.setSharedPreference(appContext, Constant.STATUS, networkResponse.getString(Constant.STATUS));
                        SwitchActivity();*/
                        alertBoxShowSuccessReg(appContext,getResources().getString(R.string.reg_msg));
                    }  else if(networkResponse.getString("result").equals("failed")){
                    if(networkResponse.getString("responseMessage").equals("Email already Exist")) {
                        Toast.makeText(appContext, getResources().getString(R.string.existing_email), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
                }
            } catch (Exception e) {
                Toast.makeText(appContext,  getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }

     void alertBoxShowSuccessReg(Context context, String msg) {
        // set dialog for user's current location set for searching theaters.
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(getResources().getString(R.string.success));
        dialog.setMessage(msg);
        dialog.setPositiveButton(" Ok ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                startActivity(new Intent(appContext,MainActivity.class));
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void SwitchActivity(){
        startActivity(new Intent(appContext,Home_Activity.class));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }
}
