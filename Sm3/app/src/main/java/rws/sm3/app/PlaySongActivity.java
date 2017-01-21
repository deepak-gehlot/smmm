package rws.sm3.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.EMAudioPlayer;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rws.sm3.app.Adapters.AdapterForComment;
import rws.sm3.app.Adapters.AdapterForUsers;
import rws.sm3.app.Adapters.PlayListAdapter;
import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utilies;
import rws.sm3.app.CommonUtilites.Utility;
import rws.sm3.app.CustomWidget.CustomEditText;
import rws.sm3.app.CustomWidget.CustomTextView;
import rws.sm3.app.SqureImageViewPack.RoundedImageView;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Android-2 on 10/23/2015.
 */
public class PlaySongActivity extends AppCompatActivity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener, OnPreparedListener {
    JSONObject song_data_json;
    JSONArray jsonArray;
    private Context appContext;
    ProgressHUD progress;

    @Override
    public void onCompletion() {
        if (song_data < jsonArray.length()) {
            song_data = song_data + 1;
            SetData(song_data);
        }
    }

    @Override
    public void onPrepared() {

    }

    Toolbar toolbar;
    int commentCounter, likecounter, listenor_count;
    private SeekBar songProgressBar;
    private EMAudioPlayer mp;
    public Utilies util;
    int song_data;
    String[] cats;
    String getsong_response;
    private int mDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        appContext = this;
        cats = getResources().getStringArray(R.array.Categories);
        Init();
    }

    private void Init() {
        (findViewById(R.id.like_img)).setOnClickListener(this);
        (findViewById(R.id.no_likes)).setOnClickListener(this);
        (findViewById(R.id.share_img)).setOnClickListener(this);
        (findViewById(R.id.comment_img)).setOnClickListener(this);
        (findViewById(R.id.no_comment)).setOnClickListener(this);
        (findViewById(R.id.add_playlist)).setOnClickListener(this);
        mp = new EMAudioPlayer(PlaySongActivity.this);
        util = new Utilies();
        songProgressBar = (SeekBar) findViewById(R.id.song_prog);
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this);
        song_data = getIntent().getIntExtra("song_object", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        (toolbar.findViewById(R.id.img_back)).setOnClickListener(this);
        if (Utility.isConnectingToInternet(appContext)) {
            try {
                jsonArray = new JSONArray(Utility.getSharedPreferences(appContext, Constant.PLAY_LIST));
//                new CheckLike().execute("get_likestatus", song_data_json.getString(Constant.SONG_ID), Utility.getSharedPreferences(appContext, Constant.ID),"getSongById");
                SetData(song_data);
            } catch (JSONException e) {

            }
        } else {
            Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void SetData(int song_data) {
        listenor_count = 0;
        likecounter = 0;
        commentCounter = 0;
        getsong_response = "";
        try {
            song_data_json = jsonArray.getJSONObject(song_data);
            if (Utility.isConnectingToInternet(appContext)) {
                new CheckLike().execute("get_likestatus", song_data_json.getString(Constant.SONG_ID), Utility.getSharedPreferences(appContext, Constant.ID), "getSongById");
            } else {
                Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
        }
    }

    private void SetRealData() {
        try {
            Picasso.with(appContext).load(Constant.PRO_PIC_IMG_URL + song_data_json.getString(Constant.USER_PROFILE_PIC)).centerCrop().fit()
                    .placeholder(R.drawable.user_default).error(R.drawable.user_default)
                    .into(((ImageView) toolbar.findViewById(R.id.img_audio)));
            ((CustomTextView) toolbar.findViewById(R.id.post_by)).setText("Posted By : " + song_data_json.getString(Constant.FIRST_NAME_POSTED_USER) + " " + song_data_json.getString(Constant.LAST_NAME_POSTED_USER));
            (toolbar.findViewById(R.id.post_by)).setOnClickListener(this);
            if (song_data_json.getString(Constant.CAT_ID).equals("0")) {
                ((CustomTextView) toolbar.findViewById(R.id.cat_name)).setText(cats[13]);
            } else {
                ((CustomTextView) toolbar.findViewById(R.id.cat_name)).setText(cats[Integer.parseInt((song_data_json.getString(Constant.CAT_ID)).replaceAll("[\\D]", "")) - 1]);
            }
            commentCounter = Integer.parseInt(song_data_json.getString(Constant.COUNT_COMMENT));
            likecounter = Integer.parseInt(song_data_json.getString(Constant.COUNT_LIKE));
            ((CustomTextView) findViewById(R.id.description)).setText("Description : " + song_data_json.getString(Constant.COMMENT));
            ((CustomTextView) findViewById(R.id.no_likes)).setText("" + likecounter);
            ((CustomTextView) findViewById(R.id.no_comment)).setText("" + commentCounter);
            ((CustomTextView) findViewById(R.id.no_playlist)).setText("" + listenor_count);
            Picasso.with(appContext).load(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.IMAGE_PATH)).centerCrop().fit()
                    .placeholder(R.drawable.default_track).error(R.drawable.default_track)
                    .fit().into(((ImageView) findViewById(R.id.track_pic)));
            ((CustomTextView) findViewById(R.id.auth_name)).setText("Author Name : " + song_data_json.getString(Constant.AUTHOR_NAME));
            ((CustomTextView) findViewById(R.id.book_name)).setText("Book Name : " + song_data_json.getString(Constant.BOOK_NAME));
        } catch (JSONException e) {

        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mRunnable, 100);
    }

   /* private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                long currentDuration = mp.getCurrentPosition();
//                System.out.println("song play"+ String.format("%02d:,%02d:,%02d",
//                        TimeUnit.MILLISECONDS.toHours(currentDuration),
//                        TimeUnit.MILLISECONDS.toMinutes(currentDuration) -
//                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentDuration)),
//                        TimeUnit.MILLISECONDS.toSeconds(currentDuration) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentDuration))
//                ));
                ((CustomTextView) findViewById(R.id.remaintime)).setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(currentDuration),
                        TimeUnit.MILLISECONDS.toMinutes(currentDuration) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currentDuration)),
                        TimeUnit.MILLISECONDS.toSeconds(currentDuration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentDuration))
                ));
                // Updating progress bar
                int progress = (int) (util.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                //       songProgressBar.setProgress(progress);


                int mediaPos_new = mp.getCurrentPosition();


                songProgressBar.setMax(mDuration);
                songProgressBar.setProgress(mediaPos_new);


                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            } catch (Exception e) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                updateProgressBar();
            }

        }
    };*/

   /* @Override
    public void onCompletion(EMAudioPlayer mp) {
        if (song_data < jsonArray.length()) {
            song_data = song_data + 1;
            SetData(song_data);
        }
    }*/

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mRunnable);
        try {
            int totalDuration = mp.getDuration();
            int currentPosition = util.progressToTimer(seekBar.getProgress(), totalDuration);
            // forward or backward to certain seconds
            mp.seekTo(currentPosition);
            // update timer progress again
            updateProgressBar();
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(mRunnable);
        try {
            if (mp.isPlaying()) {
                mp.pause();
//            mp.release();
            }
            if (mp != null) {
                mp.release();
            }
        } catch (NullPointerException e) {

        }
        Utility.setSharedPreference(appContext, "ACTFOR", "HOME");
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_by:
                try {
                    Utility.setSharedPreference(appContext, "USERIS", song_data_json.getString("user_id"));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constant.LOG_U_PRO_PIC, song_data_json.getString(Constant.LOG_U_PRO_PIC));
                    jsonObject.put(Constant.FIRST_NAME, song_data_json.getString(Constant.FIRST_NAME));
                    jsonObject.put(Constant.LAST_NAME, song_data_json.getString(Constant.LAST_NAME));
                    jsonObject.put(Constant.STATUS, song_data_json.getString(Constant.STATUS));
                    jsonObject.put("u_id", song_data_json.getString("user_id"));
                    Utility.setSharedPreference(appContext, "ACTFOR", "PRO");
                    Utility.setSharedPreference(appContext, "USERIS2", "" + jsonObject);
                    mHandler.removeCallbacks(mRunnable);
                    try {

                        if (mp.isPlaying()) {
                            mp.pause();
//            mp.release();
                        }
                        if (mp != null) {
                            mp.release();
                        }
                    } catch (NullPointerException e) {

                    }
                    finish();
                } catch (JSONException e) {

                }
                break;
            case R.id.btn_ply_song:
                try {
                    if (mp.isPlaying()) {
                        if (mp != null) {
                            mp.pause();
                            // Changing button image to play button
                            ((ImageView) findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.play_btn_main);
                        }
                    } else {
                        // Resume song
                        if (mp != null) {
                            try {
                                mp.start();
                                ((ImageView) findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.pause_btn_main);
                                songProgressBar.setMax(mp.getDuration());
                            } catch (Exception e) {
//                            Toast.makeText(appContext,"Song Not Available",Toast.LENGTH_SHORT).show();
                            }
                            // Changing button image to pause button

                        }
                    }
                } catch (Exception e) {

                }

                break;
            case R.id.btn_prev_song:
                if (song_data > 0) {
                    song_data = song_data - 1;
                    SetData(song_data);
                }
                break;
            case R.id.btn_next_song:
                if (song_data < jsonArray.length()) {
                    song_data = song_data + 1;
                    SetData(song_data);
                }

                break;
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.no_likes:
                if (Utility.isConnectingToInternet(appContext)) {
                    try {
                        new getLikeUsers().execute("like_data", song_data_json.getString(Constant.SONG_ID));
                    } catch (JSONException e) {
//                        System.out.println("noytfind");
                    }
                } else {
                    Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.like_img:
                if (Utility.isConnectingToInternet(appContext)) {
                    try {
                        if (!getsong_response.equals("")) {
                            new LikeUnlike().execute("like_song", song_data_json.getString(Constant.SONG_ID), Utility.getSharedPreferences(appContext, Constant.ID), song_data_json.getString(Constant.ID));
                        }
//                        System.out.println("dgghjjj");
                    } catch (JSONException e) {
//                        System.out.println("noytfind");
                    }
                } else {
                    Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.share_img:
                try {
                    ShowShareDialog(song_data_json.getString(Constant.SONG_ID));
                } catch (JSONException e) {
                }
                break;
            case R.id.comment_img:
                try {
                    ShowSendComment(song_data_json.getString(Constant.SONG_ID));
                } catch (JSONException e) {
                }
                break;
            case R.id.no_comment:
                if (Utility.isConnectingToInternet(appContext)) {
                    try {
                        new getCommentUsers().execute("comment_data", song_data_json.getString(Constant.SONG_ID));
                    } catch (JSONException e) {
//                        System.out.println("noytfind");
                    }
                } else {
                    Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.add_playlist:
                if (Utility.isConnectingToInternet(appContext)) {
                    new getPlayList().execute("show_userplaylist", Utility.getSharedPreferences(appContext, Constant.ID));
                } else {
                    Toast.makeText(appContext, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public class CheckLike extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;

        @Override
        protected void onPreExecute() {
            progress = ProgressHUD.show(appContext, getResources().getString(R.string.wait), true,
                    false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONStringer putParameters1 = new JSONStringer()
                        .object()
                        .key(Constant.METHOD).value(params[3])
                        .key("song_id").value(params[1])
                        .endObject();
                song_data_json = new JSONObject(putRequest.executePostRequest(putParameters1)).getJSONArray("tracklist").getJSONObject(0);
                JSONStringer putParameters2 = new JSONStringer()
                        .object()
                        .key(Constant.METHOD).value("count_visiter")
                        .key("song_id").value(params[1])
                        .endObject();
                putRequest.executePostRequest(putParameters2);
//                System.out.println("song detail"+song_data_json);
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.SONG_ID).value(params[1])
                        .key(Constant.ID).value(params[2])
                        .endObject();
                return putRequest.executePostRequest(putParameters);
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                if (progress != null) {
                    progress.cancel();
                }
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    if (progress != null) {
                        progress.cancel();
                    }
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        listenor_count = Integer.parseInt(networkResponse.getString("count_listioner"));
                        SetRealData();
                        if (networkResponse.getString("responseMessage").equals("1")) {
                            ((ImageView) findViewById(R.id.like_img)).setImageResource(R.drawable.like_fill_icon);
                            getsong_response = "1";
                            //new song().execute(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.SONG_PATH));
                            playMedia(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.SONG_PATH));
//                           Play(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.SONG_PATH));
                        } else {
                            ((ImageView) findViewById(R.id.like_img)).setImageResource(R.drawable.like_icon);
                            getsong_response = "0";
                            playMedia(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.SONG_PATH));
                            //new song().execute(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.SONG_PATH));
//                            Play(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.SONG_PATH));
                        }
                    }
                }
            } catch (Exception e) {
                if (progress != null) {
                    progress.cancel();
                }
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    public class LikeUnlike extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;

        @Override
        protected void onPreExecute() {
            progress = ProgressHUD.show(appContext, getResources().getString(R.string.wait), true,
                    false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String status = "";
            if (getsong_response.equals("0")) {
                status = "1";
            } else {
                status = "0";
            }
            try {
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.SONG_ID).value(params[1])
                        .key(Constant.ID).value(params[2])
                        .key("songuser_id").value(params[3])
                        .key("status").value(status)
                        .endObject();
//                System.out.println("send " + putParameters);
                return putRequest.executePostRequest(putParameters);
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
//            System.out.println("response is liked" + s);
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        if (networkResponse.getString("responseMessage").equals("Song Like successfully")) {
                            ((ImageView) findViewById(R.id.like_img)).setImageResource(R.drawable.like_fill_icon);
                            likecounter = likecounter + 1;
                            ((CustomTextView) findViewById(R.id.no_likes)).setText("" + likecounter);
                            getsong_response = "1";
                        } else {
                            ((ImageView) findViewById(R.id.like_img)).setImageResource(R.drawable.like_icon);
                            likecounter = likecounter - 1;
                            ((CustomTextView) findViewById(R.id.no_likes)).setText("" + likecounter);
                            getsong_response = "0";
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }

    public class getLikeUsers extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;

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
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.SONG_ID).value(params[1])
                        .endObject();
                return putRequest.executePostRequest(putParameters);
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
//            System.out.println("response is" + s);
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        if (networkResponse.getJSONArray("tracklist").length() > 0) {
                            ArrayList<JSONObject> j1 = new ArrayList<>();
                            for (int i = 0; i < networkResponse.getJSONArray("tracklist").length(); i++) {
                                j1.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                            }
                            if (j1.size() > 0) {
                                ShowlikeDialog(j1);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }

    private void ShowlikeDialog(ArrayList<JSONObject> arr) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);
        LayoutInflater li = (LayoutInflater) appContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = li.inflate(R.layout.dialog_list, null);
        alertDialogBuilder.setView(convertView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setTitle(getResources().getString(R.string.likelist));
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        AdapterForUsers adapterForUsers = new AdapterForUsers(appContext, arr);
        lv.setAdapter(adapterForUsers);
        alertDialog.show();
    }

    Intent twitterIntent, facebookIntent;
    String url;

    private void ShowShareDialog(String id) {
        url = Constant.SHARE_URL + id;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);
        LayoutInflater li = (LayoutInflater) appContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = li.inflate(R.layout.dialog_share, null);
        alertDialogBuilder.setView(convertView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setTitle(getResources().getString(R.string.share_txt));
        (convertView.findViewById(R.id.sh_fb)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookIntent = new Intent(Intent.ACTION_SEND);
                facebookIntent.setType("text/plain");
                facebookIntent.putExtra(Intent.EXTRA_TEXT, url);
                boolean facebookAppFound = false;
                List<ResolveInfo> matches = getPackageManager()
                        .queryIntentActivities(facebookIntent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.facebook")) {
                        facebookIntent.setPackage(info.activityInfo.packageName);
                        facebookAppFound = true;
                        break;
                    }
                }

                if (facebookAppFound) {
                    startActivity(facebookIntent);
                } else {
                    Toast.makeText(appContext, getResources().getString(R.string.fb_app_n_a), Toast.LENGTH_SHORT).show();
                }
            }
        });
        (convertView.findViewById(R.id.sh_tw)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterIntent = new Intent(Intent.ACTION_SEND);
                twitterIntent.setType("text/plain");
                twitterIntent.putExtra(Intent.EXTRA_TEXT, url);
                boolean twitterAppFound = false;
                List<ResolveInfo> matches1 = getPackageManager()
                        .queryIntentActivities(twitterIntent, 0);
                for (ResolveInfo info : matches1) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith(
                            "com.twitter")) {
                        twitterIntent.setPackage(info.activityInfo.packageName);
                        twitterAppFound = true;
                        break;
                    }
                }

                if (twitterAppFound) {
                    startActivity(twitterIntent);
                } else {
                    Toast.makeText(appContext, getResources().getString(R.string.tw_app_n_a), Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    public class getCommentUsers extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;

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
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.SONG_ID).value(params[1])
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
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        if (networkResponse.getJSONArray("tracklist").length() > 0) {
                            ArrayList<JSONObject> j1 = new ArrayList<>();
                            for (int i = 0; i < networkResponse.getJSONArray("tracklist").length(); i++) {
                                j1.add(networkResponse.getJSONArray("tracklist").getJSONObject(i));
                            }
                            if (j1.size() > 0) {
                                ShowCommentDialog(j1);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }

    public class getPlayList extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;

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
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.ID).value(params[1])
                        .endObject();
                return putRequest.executePostRequest(putParameters);
            } catch (JSONException e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("getPlayList response", "" + s);
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        if (networkResponse.getJSONArray("playlist").length() > 0) {
                            ArrayList<JSONObject> j1 = new ArrayList<>();
                            for (int i = 0; i < networkResponse.getJSONArray("playlist").length(); i++) {
                                j1.add(networkResponse.getJSONArray("playlist").getJSONObject(i));
                            }
                            if (j1.size() > 0) {
                                ShowPlayListDialog(j1);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }

    }

    private void ShowPlayListDialog(final ArrayList<JSONObject> arr) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);
        LayoutInflater li = (LayoutInflater) appContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = li.inflate(R.layout.dialog_for_playlist, null);
        alertDialogBuilder.setView(convertView);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setTitle(getResources().getString(R.string.add_playlist));
        ListView lv = (ListView) convertView.findViewById(R.id.playlistlist);
        Utility.setSharedPreference(appContext, "cat_id_for_send", "");
        PlayListAdapter adapterForUsers = new PlayListAdapter(appContext, arr);
        lv.setAdapter(adapterForUsers);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setTextFilterEnabled(true);
        (convertView.findViewById(R.id.edt_for_name_plylist)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (Utility.getSharedPreferences(appContext, "cat_id_for_send").equals("")) {
                    return false;
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    (convertView.findViewById(R.id.edt_for_name_plylist)).setVisibility(View.GONE);
                    Toast.makeText(appContext, getResources().getString(R.string.play_list_selected), Toast.LENGTH_SHORT).show();
                    return false;
                }

            }
        });
        (convertView.findViewById(R.id.submit_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.getSharedPreferences(appContext, "cat_id_for_send").equals("") && (((CustomEditText) convertView.findViewById(R.id.edt_for_name_plylist)).getText().toString()).equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.select_playlistortype), Toast.LENGTH_SHORT).show();
                } else {
                    if (Utility.getSharedPreferences(appContext, "cat_id_for_send").equals("")) {
                        try {
                            alertDialog.cancel();
                            new AddToPlayList().execute("addto_playlist", Utility.getSharedPreferences(appContext, Constant.ID), ((CustomEditText) convertView.findViewById(R.id.edt_for_name_plylist)).getText().toString(), song_data_json.getString(Constant.SONG_ID), "1");
                        } catch (JSONException e) {

                        }
                    } else {
                        try {
                            alertDialog.cancel();
                            new AddToPlayList().execute("addto_playlist", Utility.getSharedPreferences(appContext, Constant.ID), Utility.getSharedPreferences(appContext, "cat_id_for_send"), song_data_json.getString(Constant.SONG_ID), "0");
                        } catch (JSONException e) {

                        }
                    }
                }
            }
        });
        (convertView.findViewById(R.id.cancel_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void ShowCommentDialog(ArrayList<JSONObject> arr) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);
        LayoutInflater li = (LayoutInflater) appContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = li.inflate(R.layout.dialog_list, null);
        alertDialogBuilder.setView(convertView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setTitle(getResources().getString(R.string.comment_list));
        ListView lv = (ListView) convertView.findViewById(R.id.lv);
        AdapterForComment adapterForUsers = new AdapterForComment(appContext, arr);
        lv.setAdapter(adapterForUsers);
        alertDialog.show();
    }

    private void ShowSendComment(final String id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appContext);
        LayoutInflater li = (LayoutInflater) appContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = li.inflate(R.layout.dialog_comment, null);
        alertDialogBuilder.setView(convertView);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.setTitle(getResources().getString(R.string.send_comment));
        (convertView.findViewById(R.id.submit_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((((CustomEditText) convertView.findViewById(R.id.edt_for_com)).getText().toString()).equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.emptyFiled), Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.cancel();
                    new SendCommentServer().execute("comment_song", id, Utility.getSharedPreferences(appContext, Constant.ID), ((CustomEditText) convertView.findViewById(R.id.edt_for_com)).getText().toString());
                }
            }
        });
        (convertView.findViewById(R.id.cancel_comment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    public class SendCommentServer extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;

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
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.SONG_ID).value(params[1])
                        .key(Constant.ID).value(params[2])
                        .key("comment").value(URLEncoder.encode(params[3], "utf-8").trim())
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
                    commentCounter = commentCounter + 1;
                    ((CustomTextView) findViewById(R.id.no_comment)).setText("" + commentCounter);
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    public class AddToPlayList extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;

        @Override
        protected void onPreExecute() {
            progress = ProgressHUD.show(appContext, getResources().getString(R.string.wait), true,
                    false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            //    {"method":"addto_playlist","user_id":"14","playlist":"test by lakhan","song_id":"10","status":"1"}
            try {
                JSONStringer putParameters = new JSONStringer()
                        .object()
                        .key(Constant.METHOD).value(params[0])
                        .key(Constant.ID).value(params[1])
                        .key("playlist").value(URLEncoder.encode(params[2], "utf-8").trim())
                        .key(Constant.SONG_ID).value(params[3])
                        .key("status").value(params[4])
                        .endObject();
//                System.out.println("parameters" + putParameters);
                return putRequest.executePostRequest(putParameters);
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("Add to playlist response is" + s);
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {
                    Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        Toast.makeText(appContext, getResources().getString(R.string.added_suc), Toast.LENGTH_SHORT).show();
                    } else {
                        if (networkResponse.getString("responseMessage").equals("Song Already Added")) {
                            Toast.makeText(appContext, getResources().getString(R.string.prev_added), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(appContext, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }

    public class song extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
//            System.out.println("song pathis" + params[0]);
            try {
                try {
                    if (mp.isPlaying()) {
                        mp.pause();
//            mp.release();
                    }
                } catch (Exception e) {

                }

                if (mp != null) {
                    mp.release();
                }

                mp = new EMAudioPlayer(PlaySongActivity.this);
                mp.setDataSource(appContext, Uri.parse(params[0]));
                mp.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        mp.start();
                        ((ImageView) findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.pause_btn_main);
                        updateProgressBar();
                    }
                });
//                mp.reset();
//                mp.setDataSource(params[0]);
//                mp.prepare();
                return "suc";
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", "prepare() failed");
                return "fail";
//                return "suc";
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(String s) {
            if (progress != null) {
                progress.cancel();
            }
            if (s.equals("suc")) {
                startSong("suc");
            } else {
                startSong("fail");
            }
            super.onPostExecute(s);
        }

    }


    private void playMedia(String audioFile) {
        // get data from main activity intent
        //   final String coverImage = intent.getStringExtra(MainActivity.IMG_URL);
        url = audioFile;

        if (mp != null) {
            mp.stopPlayback();
            mp.release();
        }
        // create a media player
        mp = new EMAudioPlayer(PlaySongActivity.this);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // try to load data and play
        try {
            //  EMAudioPlayer
            // give data to mediaPlayer
            mp.setDataSource(PlaySongActivity.this, Uri.parse(audioFile));
            // media player asynchronous preparation
            mp.prepareAsync();

            // create a progress dialog (waiting media player preparation)
            final ProgressDialog dialog = new ProgressDialog(PlaySongActivity.this);

            // set message of the dialog
            dialog.setMessage("loading...");

            // prevent dialog to be canceled by back button press
            dialog.setCancelable(false);

            // show dialog at the bottom
            dialog.getWindow().setGravity(Gravity.CENTER);

            // show dialog
            dialog.show();

            // execute this code at the end of asynchronous media player preparation
            mp.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared() {
                    //start media player
                    mp.start();

                    startSong("suc");
                    ((ImageView) findViewById(R.id.btn_ply_song)).setImageResource(R.drawable.pause_btn_main);

                    /*FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                    mmr.setDataSource(url);
                    mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                    mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);*/
                    mDuration = mp.getDuration();
                    songProgressBar.setMax(mDuration);
                    //update total time text view
                    CustomTextView totalTime = (CustomTextView) findViewById(R.id.remaintime);
                    totalTime.setText(getTimeString(mDuration));
                    //update seekbar
                    mRunnable.run();

                    //dismiss dialog
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Activity a = this;
            a.finish();
            Toast.makeText(this, "FIle not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mp != null) {

                //set max value
                // int mDuration = mediaPlayer.getDuration();
                //set progress to current position
                int mCurrentPosition = mp.getCurrentPosition();

                songProgressBar.setProgress(mCurrentPosition);

                //update current time text view
                TextView currentTime = (TextView) findViewById(R.id.remaintime);
                currentTime.setText(getTimeString(mCurrentPosition));

                //handle drag on seekbar
                songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mp != null) {
                            int a = seekBar.getProgress();
                            mp.seekTo(a);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }
                });
            }
            //repeat above code every second
            mHandler.postDelayed(this, 10);
        }
    };

    public void startSong(String res) {
        if (res.equals("suc")) {
            (findViewById(R.id.btn_ply_song)).setOnClickListener(this);
//            updateProgressBar();
        } else {
            Toast.makeText(appContext, getResources().getString(R.string.play_faild), Toast.LENGTH_SHORT).show();
        }
        (findViewById(R.id.btn_next_song)).setOnClickListener(this);
        (findViewById(R.id.btn_prev_song)).setOnClickListener(this);
        (toolbar.findViewById(R.id.img_audio)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
