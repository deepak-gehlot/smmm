package rws.sm3.app.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.net.URLEncoder;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomEditText;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 11/3/2015.
 */
public class ContactUsFragment extends Fragment implements View.OnClickListener {
    View rootView;
    Context appContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        appContext = getActivity();
        Init();
        return rootView;
    }

    private void Init() {
        (rootView.findViewById(R.id.c_us_submit)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.c_us_submit:
                String res = CheckValidation(((CustomEditText) rootView.findViewById(R.id.c_us_name)).getText().toString(),
                        ((CustomEditText) rootView.findViewById(R.id.c_us_email)).getText().toString(),
                        ((CustomEditText) rootView.findViewById(R.id.c_us_sub)).getText().toString(),
                        ((CustomEditText) rootView.findViewById(R.id.c_us_msg)).getText().toString());
                if (res.equals("suc")) {
                    if (Utility.isConnectingToInternet(appContext)) {
                        new ContactUs().execute(((CustomEditText) rootView.findViewById(R.id.c_us_name)).getText().toString(),
                                ((CustomEditText) rootView.findViewById(R.id.c_us_email)).getText().toString(),
                                ((CustomEditText) rootView.findViewById(R.id.c_us_sub)).getText().toString(),
                                ((CustomEditText) rootView.findViewById(R.id.c_us_msg)).getText().toString(), "contact_us");
                    } else {
                        Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(appContext, res, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
private String CheckValidation(String name,String email,String sub,String msg){
    if (name.equals("") || sub.equals("") || email.equals("") || msg.equals("")) {
        return getResources().getString(R.string.emptyFiled);
    } else {
        if (new Utility().isEmailAddressValid(email) == false) {
            return getResources().getString(R.string.emailinvalid);
        } else {
            return "suc";
        }
    }
}
    public class ContactUs extends AsyncTask<String, String, String> {
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
//{"method":"contact_us", "subject":"test", "email":"user@gmail.com", "name":"name", "message":"hello url"}
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key("subject").value(URLEncoder.encode(params[2], "utf-8").trim())
                        .key("email").value(params[1])
                        .key("name").value(URLEncoder.encode(params[0], "utf-8").trim())
                        .key("message").value(URLEncoder.encode(params[3], "utf-8").trim())
                        .key(Constant.METHOD).value(params[4])
//                        .key("subject").value(params[2])
//                        .key("email").value(params[1])
//                        .key("name").value(params[0])
//                        .key("message").value(params[3])
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
            System.out.println("result is"+s);
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        Toast.makeText(appContext, getResources().getString(R.string.sucees_msg), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(appContext, getResources().getString(R.string.failed_msg), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }
}
