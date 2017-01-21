package rws.sm3.app.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;
import org.json.JSONStringer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.GetAudioPath;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utilies;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomEditText;
import rws.sm3.app.CustomWidget.CustomTextView;
import rws.sm3.app.R;

/**
 * Created by Android-2 on 10/16/2015.
 */
public class AddSongFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    View rootView;
    Uri mImageUri;
    private Handler mHandler = new Handler();
    Context appContext;
    private MediaPlayer mp;
    AQuery aq;
    int REQUESTCODE_AUDIOFILE = 1;
    int position_for_submit = 0;
    protected static final int REQ_CODE_PICK_IMAGE = 3;
    int REQUESTCODE_RECORDING = 2;
    public Utilies util;
    private SeekBar songProgressBar;
    String other_cat_name;
    String recoded = "";
    private Handler mHandler1 = new Handler();
    String selected_audio_path = "";
    Bitmap book_pic = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_song, container, false);
        appContext = getActivity();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                Initialization();
            }
        }, 500);
        return rootView;
    }

    private void Initialization() {
        aq=new AQuery(appContext);
        (rootView.findViewById(R.id.select_aud_file)).setOnClickListener(this);
        (rootView.findViewById(R.id.btn_ply_song)).setOnClickListener(this);
        (rootView.findViewById(R.id.start_recording)).setOnClickListener(this);
        (rootView.findViewById(R.id.select_book_pic)).setOnClickListener(this);
        (rootView.findViewById(R.id.btn_upload)).setOnClickListener(this);
        ((Spinner) rootView.findViewById(R.id.cat_spin)).setOnItemSelectedListener(this);
        songProgressBar = (SeekBar) rootView.findViewById(R.id.song_prog);
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp = new MediaPlayer();
        util = new Utilies();
        mp.setOnCompletionListener(this);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        int idx;
        if(contentUri.getPath().startsWith("/external/image") || contentUri.getPath().startsWith("/internal/image")) {
            idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        }
        else if(contentUri.getPath().startsWith("/external/video") || contentUri.getPath().startsWith("/internal/video")) {
            idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        }
        else if(contentUri.getPath().startsWith("/external/audio") || contentUri.getPath().startsWith("/internal/audio")) {
            idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        }
        else{
            return contentUri.getPath();
        }
        if(cursor != null && cursor.moveToFirst()) {
            return cursor.getString(idx);
        }
        return null;
    }
    private String _getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_AUDIOFILE) {
            if (resultCode == -1) {
                //the selected audio.
                Uri uri = data.getData();
                System.out.println("uri is"+uri);
                    selected_audio_path = GetAudioPath.getPath(appContext, uri);
                String filename=selected_audio_path.substring(selected_audio_path.lastIndexOf("/")+1);
//                    selected_audio_path="file://"+selected_audio_path
                    recoded = "";
                if(selected_audio_path==null){
                    selected_audio_path=getRealPathFromURI(appContext,uri);
                }
                selected_audio_path = _getRealPathFromURI(appContext, data.getData());
                System.out.println("uri is"+filename);
                ((CustomTextView)rootView.findViewById(R.id.textname)).setText(filename);
//                if (Build.VERSION.SDK_INT < 11)
//                    selected_audio_path = RealPathUtil.getRealPathFromURI_BelowAPI11(appContext, data.getData());
//
//                    // SDK >= 11 && SDK < 19
//                else if (Build.VERSION.SDK_INT < 19)
//                    selected_audio_path = RealPathUtil.getRealPathFromURI_API11to18(appContext, data.getData());
//
//                    // SDK > 19 (Android 4.4)
//                else
//                    selected_audio_path = RealPathUtil.getRealPathFromURI_API19(appContext, data.getData());
                    loadSong(selected_audio_path);
//                selected_audio_path=uri.toString();
//                System.out.println("uri is" + selected_audio_path);
            }
        }
        if (requestCode == REQUESTCODE_RECORDING) {
            if (resultCode == -1) {
                Uri audioUri = data.getData();
                final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                if (isKitKat) {
                    selected_audio_path = GetAudioPath.getPath(appContext, audioUri);
                    String filename=selected_audio_path.substring(selected_audio_path.lastIndexOf("/")+1);
                    ((CustomTextView)rootView.findViewById(R.id.textname)).setText(filename);
//                    selected_audio_path="file://"+selected_audio_path
                    recoded = "1";
                    loadSong(selected_audio_path);
                } else {
                    recoded = "1";
                    selected_audio_path = audioUri.toString();
                    String filename=selected_audio_path.substring(selected_audio_path.lastIndexOf("/")+1);
                    ((CustomTextView)rootView.findViewById(R.id.textname)).setText(filename);
                    loadSong(selected_audio_path);
                }
            }
        }
        if (requestCode == REQ_CODE_PICK_IMAGE) {
            if (resultCode == -1) {
                if (data != null) {
                    String path = getRealPathFromURI(appContext,mImageUri);
                    System.out.println("path is"+path);
                    Bundle extras = data.getExtras();
//                    Uri imge = data.getData();
                    String filename=path.substring(path.lastIndexOf("/") + 1);
                    ((CustomTextView)rootView.findViewById(R.id.textname123)).setText(filename);
                    book_pic = extras.getParcelable("data");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadSong(String songpath) {
        try {
            if (mp != null) {
                mp.release();
                mp = new MediaPlayer();
            }
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(songpath);
            mp.prepare();
            ((ImageView) rootView.findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.play_btn_main);
        } catch (Exception e) {
            Log.e("error", "prepare() failed");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_aud_file:
                selectAudio();
                break;
            case R.id.start_recording:
                Intent intent =
                        new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (isAvailable(appContext, intent)) {
                    startActivityForResult(intent,
                            REQUESTCODE_RECORDING);
                }
                break;
            case R.id.btn_ply_song:
                try {
                    if (mp != null) {
                        if (mp.isPlaying()) {
                            mp.pause();
                            // Changing button image to play button
                            ((ImageView) rootView.findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.play_btn_main);
                        } else {
                            mp.start();
                            // Changing button image to pause button
                            ((ImageView) rootView.findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.pause_btn_main);
                        }
                    } else {
                        Toast.makeText(appContext, getResources().getString(R.string.selection_error_msg), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(appContext, getResources().getString(R.string.selection_error_msg), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.select_book_pic:
                SelectAndCropPic();
                break;
            case R.id.btn_upload:
                String res = CheckValidation(((CustomEditText) rootView.findViewById(R.id.edt_book_name)).getText().toString(),
                        ((CustomEditText) rootView.findViewById(R.id.edt_auth_name)).getText().toString(),
                        ((CustomEditText) rootView.findViewById(R.id.edt_comment)).getText().toString());
                if (res.equals("suc")) {
                    CheckValidation2();
                } else {
                    Toast.makeText(appContext, res, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void CheckValidation2() {
        if (position_for_submit == 0) {
            Toast.makeText(appContext, getResources().getString(R.string.category_not_selected), Toast.LENGTH_SHORT).show();
        } else if (position_for_submit == 13) {
            if ((((CustomEditText) rootView.findViewById(R.id.type_cat_name)).getText().toString()).equals("")) {
                Toast.makeText(appContext, getResources().getString(R.string.other_cat_name), Toast.LENGTH_SHORT).show();
            } else {
                other_cat_name = ((CustomEditText) rootView.findViewById(R.id.type_cat_name)).getText().toString();
                CheckValidation_And_Submit();
            }
        } else {
            CheckValidation_And_Submit();
        }
    }

    private void CheckValidation_And_Submit() {
        if (selected_audio_path.equals("")) {
            Toast.makeText(appContext, getResources().getString(R.string.audio_not_select), Toast.LENGTH_SHORT).show();
        } else if (book_pic == null) {
            Toast.makeText(appContext, getResources().getString(R.string.picture_not_select), Toast.LENGTH_SHORT).show();
        } else {
            if (Utility.isConnectingToInternet(appContext)) {
//                async_post(new String[]{((CustomEditText) rootView.findViewById(R.id.edt_book_name)).getText().toString(),
//                        ((CustomEditText) rootView.findViewById(R.id.edt_auth_name)).getText().toString(),
//                        ((CustomEditText) rootView.findViewById(R.id.edt_comment)).getText().toString(), "add_audio"});
                Log.e("UserID",""+Utility.getSharedPreferences(appContext, Constant.ID));
                new AudioUploadOnServer1().execute(((CustomEditText) rootView.findViewById(R.id.edt_book_name)).getText().toString(),
                        ((CustomEditText) rootView.findViewById(R.id.edt_auth_name)).getText().toString(),
                        ((CustomEditText) rootView.findViewById(R.id.edt_comment)).getText().toString(),"add_audio");
            } else {
                Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String CheckValidation(String bookname, String authorname, String comment) {
        if (bookname.equals("") || authorname.equals("") || comment.equals("")) {
            return getResources().getString(R.string.emptyFiled);
        } else {
            return "suc";
        }
    }

    private void SelectAndCropPic() {
        mImageUri = appContext.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
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

    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void selectAudio() {
        final CharSequence[] items = {getResources().getString(R.string.file_selection), getResources().getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
//        builder.setTitle(getResources().getString(R.string.audio));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getResources().getString(R.string.file_selection))) {
//                    Intent intent_upload = new Intent();
//                    intent_upload.setType("audio/*");
//                    intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                    Intent intent_upload = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent_upload, REQUESTCODE_AUDIOFILE);
//                    startActivityForResult(intent_upload, REQUESTCODE_AUDIOFILE);
                } else if (items[item].equals(getResources().getString(R.string.record_audio))) {

                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        position_for_submit = position;
        if (position == 13) {
            (rootView.findViewById(R.id.type_cat_name)).setVisibility(View.VISIBLE);
        } else {
            (rootView.findViewById(R.id.type_cat_name)).setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler1.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        try {
            mHandler1.removeCallbacks(mUpdateTimeTask);
            int totalDuration = mp.getDuration();
            int currentPosition = util.progressToTimer(seekBar.getProgress(), totalDuration);
            // forward or backward to certain seconds
            mp.seekTo(currentPosition);
            // update timer progress again
            updateProgressBar();
        } catch (Exception e) {

            // update timer progress again
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        mp.release();
        songProgressBar.setProgress(0);
        ((ImageView) rootView.findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.play_btn_main);
    }

    public void updateProgressBar() {
        mHandler1.postDelayed(mUpdateTimeTask, 100);

    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();
                // Updating progress bar
                int progress = (int) (util.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                songProgressBar.setProgress(progress);
                // Running this thread after 100 milliseconds
                mHandler1.postDelayed(this, 100);
            } catch (Exception e) {

            }
        }
    };

    public class AudioUploadOnServer extends AsyncTask<String, String, String> {
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
            String encodedString = "";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            book_pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            byte[] song_data = doFileUpload(selected_audio_path);
            System.out.println("song" + encodedString);
            if (!song_data.equals("")&&song_data!=null) {
                try {
                    if (position_for_submit == 13) {
                        JSONStringer putParameters = new JSONStringer()
                                .object()
                                .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
                                .key(Constant.BOOK_NAME).value(URLEncoder.encode(params[0], "utf-8").trim())
                                .key(Constant.AUTHOR_NAME).value(URLEncoder.encode(params[1], "utf-8").trim())
                                .key(Constant.COMMENT).value(URLEncoder.encode(params[2], "utf-8").trim())
                                .key(Constant.METHOD).value(params[3])
                                .key(Constant.IMAGE_PATH).value(encodedString)
                                .key("recorded_audio").value(recoded)
                                .key(Constant.CAT_ID).value("0")
                                .key("other_category").value(URLEncoder.encode(other_cat_name, "utf-8").trim())
                                .key(Constant.SONG_PATH).value(song_data)
                                .endObject();
                        return putRequest.executePostRequest(putParameters);
                    } else {
                        JSONStringer putParameters = new JSONStringer()
                                .object()
                                .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
                                .key(Constant.BOOK_NAME).value(URLEncoder.encode(params[0], "utf-8").trim())
                                .key(Constant.AUTHOR_NAME).value(URLEncoder.encode(params[1], "utf-8").trim())
                                .key(Constant.COMMENT).value(URLEncoder.encode(params[2], "utf-8").trim())
                                .key(Constant.METHOD).value(params[3])
                                .key(Constant.IMAGE_PATH).value(encodedString)
                                .key("recorded_audio").value(recoded)
                                .key(Constant.CAT_ID).value(position_for_submit)
                                .key(Constant.SONG_PATH).value(song_data)
                                .endObject();
                        return putRequest.executePostRequest(putParameters);
                    }

                } catch (Exception e) {
                    return "";
                }
            } else {
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

    private byte[] doFileUpload(String selectedPath) {
        byte[] videoBytes;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(selectedPath));

            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            videoBytes = baos.toByteArray();
//            String video_str = Base64.encodeToString(videoBytes, Base64.DEFAULT);
            return videoBytes;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }
    public void async_post(String[]params){

//do a twiiter search with a http post
        String encodedString = "";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        book_pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        JSONObject putParameters1=null;
        encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);
        byte[] song_data = doFileUpload(selected_audio_path);
        if(song_data!=null){

        }
        System.out.println("song" + encodedString);
        Map<String, String> params1 = new HashMap<String, String>();
        if (!song_data.equals("")) {
            try {

                if (position_for_submit == 13) {
                    params1.put(Constant.METHOD, params[3]);
                    params1.put(Constant.ID, Utility.getSharedPreferences(appContext, Constant.ID));
                    params1.put(Constant.BOOK_NAME, URLEncoder.encode(params[0], "utf-8").trim());
                    params1.put(Constant.AUTHOR_NAME, URLEncoder.encode(params[1], "utf-8").trim());
                    params1.put(Constant.COMMENT, URLEncoder.encode(params[2], "utf-8").trim());
                    params1.put(Constant.IMAGE_PATH, encodedString);
                    params1.put("recorded_audio", "0");
                    params1.put(Constant.CAT_ID, "0");
                    params1.put("other_category", URLEncoder.encode(other_cat_name, "utf-8").trim());
                    params1.put(Constant.SONG_PATH,""+song_data );
//                     JSONStringer putParameters = new JSONStringer()
//                            .object()
//                            .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
//                            .key(Constant.BOOK_NAME).value(URLEncoder.encode(params[0], "utf-8").trim())
//                            .key(Constant.AUTHOR_NAME).value(URLEncoder.encode(params[1], "utf-8").trim())
//                            .key(Constant.COMMENT).value(URLEncoder.encode(params[2], "utf-8").trim())
//                            .key(Constant.METHOD).value(params[3])
//                            .key(Constant.IMAGE_PATH).value(encodedString)
//                            .key("recorded_audio").value(recoded)
//                            .key(Constant.CAT_ID).value("0")
//                            .key("other_category").value(URLEncoder.encode(other_cat_name, "utf-8").trim())
//                            .key(Constant.SONG_PATH).value(song_data)
//                            .endObject();
//                               aq.ajax(Constant.BASE_URL, putParameters, JSONObject.class, new AjaxCallback<JSONObject>() {
//
//                            @Override
//                            public void callback(String url, JSONObject json, AjaxStatus status) {
//
//                                networkResponse=json;
//
//                            }
//                        });
//                        networkResponse.toString();
                } else {
                    params1.put(Constant.METHOD, params[3]);
                    params1.put(Constant.ID, Utility.getSharedPreferences(appContext, Constant.ID));
                    params1.put(Constant.BOOK_NAME, URLEncoder.encode(params[0], "utf-8").trim());
                    params1.put(Constant.AUTHOR_NAME, URLEncoder.encode(params[1], "utf-8").trim());
                    params1.put(Constant.COMMENT, URLEncoder.encode(params[2], "utf-8").trim());
                    params1.put(Constant.IMAGE_PATH, encodedString);
                    params1.put("recorded_audio", recoded);
                    params1.put(Constant.CAT_ID, ""+position_for_submit);
                    params1.put(Constant.SONG_PATH,""+song_data);
//                    JSONStringer putParameters = new JSONStringer()
//                            .object()
//                            .key(Constant.ID).value(Utility.getSharedPreferences(appContext, Constant.ID))
//                            .key(Constant.BOOK_NAME).value(URLEncoder.encode(params[0], "utf-8").trim())
//                            .key(Constant.AUTHOR_NAME).value(URLEncoder.encode(params[1], "utf-8").trim())
//                            .key(Constant.COMMENT).value(URLEncoder.encode(params[2], "utf-8").trim())
//                            .key(Constant.METHOD).value(params[3])
//                            .key(Constant.IMAGE_PATH).value(encodedString)
//                            .key("recorded_audio").value(recoded)
//                            .key(Constant.CAT_ID).value(position_for_submit)
//                            .key(Constant.SONG_PATH).value(song_data)
//                            .endObject();
                }

            } catch (Exception e) {

            }
        } else {

        }

        aq.ajax("http://audioketab.com/web_services/api.php?",params1,JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                System.out.println("response json"+json);
            }
        });

    }
    public class AudioUploadOnServer1 extends AsyncTask<String, String, String> {
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
            String encodedString = "";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            book_pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byte_arr = stream.toByteArray();
            // Encode Image to String
            encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            byte[] song_data = doFileUpload(selected_audio_path);
            Log.e("selected_audio_path",""+selected_audio_path);
            Log.e("UserID",""+Utility.getSharedPreferences(appContext, Constant.ID));
            Log.e("Image",""+encodedString);
            Log.e("Song Path",""+selected_audio_path);
            File f;
            FileOutputStream fos;
            if (!selected_audio_path.equals("")&&selected_audio_path!=null) {
                try {
                    f = new File(selected_audio_path);
                    long length = f.length();
                    length = length/1024;
                    if(length<30000) {
//                    if (f.exists())
//                        f.delete();
//                    try{
//                        fos = new FileOutputStream(f);
//                        fos.write(song_data);
//                    }catch(Exception e){e.printStackTrace();}
                        if (position_for_submit == 13) {
                            MultipartEntity entity = new MultipartEntity();
                            try {
                                entity.addPart(Constant.ID, new StringBody(Utility.getSharedPreferences(appContext, Constant.ID)));
                                entity.addPart(Constant.BOOK_NAME, new StringBody(URLEncoder.encode(params[0], "utf-8").trim()));
                                entity.addPart(Constant.AUTHOR_NAME, new StringBody(URLEncoder.encode(params[1], "utf-8").trim()));
                                entity.addPart(Constant.COMMENT, new StringBody(URLEncoder.encode(params[2], "utf-8").trim()));
                                entity.addPart(Constant.METHOD, new StringBody(params[3]));
                                entity.addPart(Constant.IMAGE_PATH, new StringBody(encodedString));
                                entity.addPart("recorded_audio", new StringBody(recoded));
                                entity.addPart(Constant.CAT_ID, new StringBody("0"));
                                entity.addPart("other_category", new StringBody(URLEncoder.encode(other_cat_name, "utf-8").trim()));
                                entity.addPart(Constant.SONG_PATH, new FileBody(f));
                            }
                            catch (Exception e) {

                                e.printStackTrace();
                            }
                            Log.d("value of entity is :", "" + entity);
                            return Utility.multiPart("http://audioketab.com/web_services/upload.php?", entity);

                        } else {
                            MultipartEntity entity = new MultipartEntity();
                            try {
                                entity.addPart(Constant.ID, new StringBody(Utility.getSharedPreferences(appContext, Constant.ID)));
                                entity.addPart(Constant.BOOK_NAME, new StringBody(URLEncoder.encode(params[0], "utf-8").trim()));
                                entity.addPart(Constant.AUTHOR_NAME, new StringBody(URLEncoder.encode(params[1], "utf-8").trim()));
                                entity.addPart(Constant.COMMENT, new StringBody(URLEncoder.encode(params[2], "utf-8").trim()));
                                entity.addPart(Constant.METHOD, new StringBody(params[3]));
                                entity.addPart(Constant.IMAGE_PATH, new StringBody(encodedString));
                                entity.addPart("recorded_audio", new StringBody(recoded));
                                 entity.addPart(Constant.CAT_ID, new StringBody("" + position_for_submit));
                                entity.addPart(Constant.SONG_PATH, new FileBody(f));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d("value of entity is :", "" + entity);
                            return Utility.multiPart("http://audioketab.com/web_services/upload.php?", entity);
                        }
                    }else{
                        return "size";
                    }
                } catch (Exception e) {
                    Log.e("Exc",""+e);
                    return "";
                }
            } else {
                return "";

            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("AudioUpload Response",""+s.toString());
            if (progress != null) {
                progress.cancel();
            }
            if(s.equals("size")){
                Toast.makeText(appContext, getResources().getString(R.string.greaterfilesize), Toast.LENGTH_SHORT).show();
            }else {
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
                    Log.e("AudioUpload Exc",""+e);
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(s);
        }

    }
}
