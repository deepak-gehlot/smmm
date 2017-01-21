package rws.sm3.app;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.Calendar;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomEditText;
import rws.sm3.app.CustomWidget.CustomTextView;
import rws.sm3.app.SqureImageViewPack.RoundedImageView;

/**
 * Created by Android-2 on 10/30/2015.
 */
public class Edit_Profile_Activity extends AppCompatActivity implements View.OnClickListener {
    private Context appContext;
    Toolbar toolbar;
    private DatePicker datePicker;
    private Calendar calendar;
    String dob;
    private int year, month, day;
    Bitmap book_pic = null;
    protected static final int REQ_CODE_PICK_IMAGE = 3;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        appContext = this;
        Init();

    }

    private void Init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        (toolbar.findViewById(R.id.toggle_btn)).setOnClickListener(this);
        if (Utility.isConnectingToInternet(appContext)) {
            new getProfileInfo().execute("show_profile", Utility.getSharedPreferences(appContext, Constant.ID));
        } else {
            Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
        (findViewById(R.id.change_pro_pic)).setOnClickListener(this);
        (findViewById(R.id.update_profile)).setOnClickListener(this);
        (findViewById(R.id.txt_dob)).setOnClickListener(this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        String month1,day1;
        if(month<10){
            month1="0"+month;
        }else{
            month1=""+month;
        }
        if(day<10){
            day1="0"+day;
        }else{
            day1=""+day;
        }
        ((CustomTextView)findViewById(R.id.txt_dob)).setText(new StringBuilder().append(year).append("-")
                .append(month1).append("-").append(day1));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_btn:
                finish();
                break;
            case R.id.txt_dob:
                showDialog(999);
                break;
            case R.id.change_pro_pic:
                SelectAndCropPic();
                break;
            case R.id.update_profile:
                String res = CheckValidation(((CustomEditText) findViewById(R.id.edt_fname)).getText().toString(),
                        ((CustomEditText) findViewById(R.id.edt_lname)).getText().toString(),
                        ((CustomEditText) findViewById(R.id.edt_from)).getText().toString(),
                        ((CustomTextView) findViewById(R.id.txt_dob)).getText().toString(),
                        ((CustomEditText) findViewById(R.id.edt_des)).getText().toString());

                if (res.equals("suc")) {
                    if (Utility.isConnectingToInternet(appContext)) {
                        new UpdateProfile().execute(((CustomEditText) findViewById(R.id.edt_fname)).getText().toString(),
                                ((CustomEditText) findViewById(R.id.edt_lname)).getText().toString(),
                                ((CustomEditText) findViewById(R.id.edt_from)).getText().toString(),
                                ((CustomTextView) findViewById(R.id.txt_dob)).getText().toString(),
                                ((CustomEditText) findViewById(R.id.edt_des)).getText().toString(), "update_profile");
                    } else {
                        Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(appContext, res, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void SelectAndCropPic() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("outputX", 200);
        photoPickerIntent.putExtra("outputY", 200);
        photoPickerIntent.putExtra("aspectX", 1);
        photoPickerIntent.putExtra("aspectY", 1);
        photoPickerIntent.putExtra("scale", true);
        photoPickerIntent.putExtra("return-data", true);
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(photoPickerIntent, REQ_CODE_PICK_IMAGE);
    }

    private String CheckValidation(String f_name, String email, String ph_no, String pass, String des) {
        if (f_name.equals("") || ph_no.equals("") || email.equals("") || pass.equals("") || des.equals("")) {
            return getResources().getString(R.string.emptyFiled);
        } else {

            return "suc";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    book_pic = extras.getParcelable("data");
                    ((RoundedImageView) findViewById(R.id.u_pic)).setImageBitmap(book_pic);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class UpdateProfile extends AsyncTask<String, String, String> {
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
            String encodedString = "";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            if (book_pic!=null) {
                book_pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            }
//            {"method":"update_profile", "user_id":"1", "first_name":"first_name", "last_name":"last_name", "from":"from", "dob":"dob", "description":"description", "profile_pic":"profile_pic"}
            try {
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
                        .key(Constant.METHOD).value(params[5])
                        .key(Constant.FIRST_NAME).value(URLEncoder.encode(params[0], "utf-8").trim())
                        .key(Constant.LAST_NAME).value(URLEncoder.encode(params[1], "utf-8").trim())
                        .key(Constant.LOG_U_LOCATION).value(URLEncoder.encode(params[2], "utf-8").trim())
                        .key(Constant.DOB).value(params[3])
                        .key(Constant.STATUS).value(URLEncoder.encode(params[4], "utf-8").trim())
                        .key(Constant.LOG_U_PRO_PIC).value(encodedString)
                        .endObject();
                System.out.println("param"+putParameters);
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
            System.out.println("response is" + s);
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("update profile successfully")) {
                        JSONObject j = networkResponse.getJSONObject("users_details");
                        Utility.setSharedPreference(appContext, Constant.FIRST_NAME, j.getString(Constant.FIRST_NAME));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_PRO_PIC, j.getString(Constant.USER_PROFILE_PIC));
                        Utility.setSharedPreference(appContext, Constant.LAST_NAME, j.getString(Constant.LAST_NAME));
                        Utility.setSharedPreference(appContext, Constant.STATUS, j.getString(Constant.STATUS));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_LOCATION, j.getString(Constant.LOG_U_LOCATION));
                        Utility.setSharedPreference(appContext, Constant.DOB, j.getString(Constant.DOB));
                        setData();
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

    private void setData() {
        Picasso.with(appContext)
                .load(Constant.PRO_PIC_IMG_URL + Utility.getSharedPreferences(appContext, Constant.LOG_U_PRO_PIC))
                .placeholder(R.drawable.user_default).error(R.drawable.user_default)
                .into(((RoundedImageView) findViewById(R.id.u_pic)));
        ((CustomEditText) findViewById(R.id.edt_fname)).setText(Utility.getSharedPreferences(appContext, Constant.FIRST_NAME));
        ((CustomEditText) findViewById(R.id.edt_lname)).setText(Utility.getSharedPreferences(appContext, Constant.LAST_NAME));
        ((CustomEditText) findViewById(R.id.edt_from)).setText(Utility.getSharedPreferences(appContext, Constant.LOG_U_LOCATION));
        ((CustomTextView) findViewById(R.id.txt_dob)).setText(Utility.getSharedPreferences(appContext, Constant.DOB));
        ((CustomEditText) findViewById(R.id.edt_des)).setText(Utility.getSharedPreferences(appContext, Constant.STATUS));
        ((CustomEditText) findViewById(R.id.edt_email)).setText(Utility.getSharedPreferences(appContext, Constant.EMAIL));
        String []date=Utility.getSharedPreferences(appContext, Constant.DOB).split("-");
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1])-1;
        day = Integer.parseInt(date[2]);
    }

    public class getProfileInfo extends AsyncTask<String, String, String> {
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
//            {"method":"add_audio", "user_id":"1", "book_name":"book name", "author_name":"author name", "category":"1", "comment":"comment test", "song_path":"song path test", "image_path":"image path"}
            try {
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.ID).value(params[1])
                        .key(Constant.METHOD).value(params[0])
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
            System.out.println("response is" + s);
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        JSONObject j = networkResponse.getJSONObject("users_details");
                        Utility.setSharedPreference(appContext, Constant.FIRST_NAME, j.getString(Constant.FIRST_NAME));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_PRO_PIC, j.getString(Constant.USER_PROFILE_PIC));
                        Utility.setSharedPreference(appContext, Constant.LAST_NAME, j.getString(Constant.LAST_NAME));
                        Utility.setSharedPreference(appContext, Constant.STATUS, j.getString(Constant.STATUS));
                        Utility.setSharedPreference(appContext, Constant.LOG_U_LOCATION, j.getString(Constant.LOG_U_LOCATION));
                        Utility.setSharedPreference(appContext, Constant.DOB, j.getString(Constant.DOB));
                        setData();
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
