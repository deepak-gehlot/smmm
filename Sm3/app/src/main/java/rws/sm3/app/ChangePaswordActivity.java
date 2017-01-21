package rws.sm3.app;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONStringer;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomEditText;
import rws.sm3.app.CustomWidget.CustomTextView;

/**
 * Created by Android Developer-1 on 06-05-2016.
 */
public class ChangePaswordActivity extends AppCompatActivity implements View.OnClickListener{

    Context ctxChangePwd;
    String user_id;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        ctxChangePwd=this;
        ((CustomTextView)findViewById(R.id.btn_change_pwd)).setOnClickListener(this);
        user_id=Utility.getSharedPreferences(ctxChangePwd,Constant.ID);
        InitToolbar();
    }

    void InitToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        (toolbar.findViewById(R.id.img_back)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btn_change_pwd:
                if (!Utility.isStringNullOrBlank(((CustomEditText) findViewById(R.id.edt_current_pwd)).getText().toString())
                   && !Utility.isStringNullOrBlank(((CustomEditText) findViewById(R.id.edt_new_pwd)).getText().toString())
                        && !Utility.isStringNullOrBlank(((CustomEditText) findViewById(R.id.edt_confirm_pwd)).getText().toString())) {
                    if (Utility.isConnectingToInternet(ctxChangePwd)) {
                        new ChangePwdOnServer().execute(((CustomEditText) findViewById(R.id.edt_current_pwd)).getText().toString().trim(),
                                ((CustomEditText) findViewById(R.id.edt_new_pwd)).getText().toString().trim(),
                                ((CustomEditText) findViewById(R.id.edt_confirm_pwd)).getText().toString().trim());
                    } else {
                        Toast.makeText(ctxChangePwd, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(ctxChangePwd, getResources().getString(R.string.emptyFiled), Toast.LENGTH_SHORT).show();
                }
                                    break;
            case R.id.img_back:
                                onBackPressed();
                                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class ChangePwdOnServer extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;
        ProgressHUD progress;
        @Override
        protected void onPreExecute() {
            progress = ProgressHUD.show(ctxChangePwd, getResources().getString(R.string.wait), true,
                    false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key("current_password").value(params[0])
                        .key("new_password").value(params[1])
                        .key("confirm_password").value(params[2])
                        .key(Constant.METHOD).value("change_password")
                        .key(Constant.ID).value(user_id)
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
                    Toast.makeText(ctxChangePwd, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        Toast.makeText(ctxChangePwd, getResources().getString(R.string.pwd_update_success), Toast.LENGTH_SHORT).show();
                        finish();
                    }  else if(networkResponse.getString("result").equals("failed")){
                        if(networkResponse.getString("responseMessage").equals("Current password is not valid!")) {
                            Toast.makeText(ctxChangePwd, getResources().getString(R.string.pwd_not_valid), Toast.LENGTH_SHORT).show();
                        }
                        else if(networkResponse.getString("responseMessage").equals("New password and confirm password does not matched!")) {
                            Toast.makeText(ctxChangePwd, getResources().getString(R.string.pwd_and_confirmpwd_not_match), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ctxChangePwd, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ctxChangePwd, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(ctxChangePwd,  getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }
}
