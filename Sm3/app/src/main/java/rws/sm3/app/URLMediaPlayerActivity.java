package rws.sm3.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.EMAudioPlayer;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.NetworkHelper;
import rws.sm3.app.CommonUtilites.ProgressHUD;
import rws.sm3.app.CommonUtilites.Utility;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by RWS 6 on 11/22/2016.
 */
public class URLMediaPlayerActivity extends Activity {

    private EMAudioPlayer mediaPlayer;
    private SeekBar seekBar;

    private JSONObject song_data_json;
    private JSONArray jsonArray;
    int mDuration = 0;
    private String url = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // remove title and go full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
setContentView(R.layout.activity_media_player);
        if (Utility.isConnectingToInternet(URLMediaPlayerActivity.this)) {
            try {
                jsonArray = new JSONArray(Utility.getSharedPreferences(URLMediaPlayerActivity.this, Constant.PLAY_LIST));
//                new CheckLike().execute("get_likestatus", song_data_json.getString(Constant.SONG_ID), Utility.getSharedPreferences(appContext, Constant.ID),"getSongById");
                SetData(getIntent().getIntExtra("song_object", 0));
            } catch (Exception e) {

            }
        } else {
            Toast.makeText(URLMediaPlayerActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void playMedia(String audioFile) {
        // get data from main activity intent
        //   final String coverImage = intent.getStringExtra(MainActivity.IMG_URL);
        url = audioFile;
        // create a media player
        mediaPlayer = new EMAudioPlayer(URLMediaPlayerActivity.this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // try to load data and play
        try {

            // give data to mediaPlayer
            mediaPlayer.setDataSource(URLMediaPlayerActivity.this, Uri.parse(audioFile));
            // media player asynchronous preparation
            mediaPlayer.prepareAsync();

            // create a progress dialog (waiting media player preparation)
            final ProgressDialog dialog = new ProgressDialog(URLMediaPlayerActivity.this);

            // set message of the dialog
            dialog.setMessage("loading...");

            // prevent dialog to be canceled by back button press
            dialog.setCancelable(false);

            // show dialog at the bottom
            dialog.getWindow().setGravity(Gravity.CENTER);

            // show dialog
            dialog.show();

          /*  mediaPlayer.setOnPreparedListener(new com.devbrackets.android.exomedia.listener.OnPreparedListener() {
                @Override
                public void onPrepared() {

                }
            });*/

            // execute this code at the end of asynchronous media player preparation
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    //start media player
                    mediaPlayer.start();

                    // link seekbar to bar view
                    seekBar = (SeekBar) findViewById(R.id.seekBar);

                    FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                    mmr.setDataSource(url);
                    mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                    mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                    mDuration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
                    seekBar.setMax(mDuration);
                    //update total time text view
                    TextView totalTime = (TextView) findViewById(R.id.totalTime);
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


    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) {

                //set max value
                // int mDuration = mediaPlayer.getDuration();
                //set progress to current position
                int mCurrentPosition = mediaPlayer.getCurrentPosition();

                seekBar.setProgress(mCurrentPosition);

                //update current time text view
                TextView currentTime = (TextView) findViewById(R.id.currentTime);
                currentTime.setText(getTimeString(mCurrentPosition));

                //handle drag on seekbar
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (mediaPlayer != null) {
                            int a = seekBar.getProgress();
                            mediaPlayer.seekTo(a);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void play(View view) {

        mediaPlayer.start();
    }


    public void pause(View view) {

        mediaPlayer.pause();

    }

    public void stop(View view) {

        mediaPlayer.seekTo(0);
        mediaPlayer.pause();

    }


    public void seekForward(View view) {

        //set seek time
        int seekForwardTime = 5000;

        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + seekForwardTime <= mDuration) {
            // forward song
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        } else {
            // forward to end position
            mediaPlayer.seekTo(mDuration);
        }

    }

    public void seekBackward(View view) {

        //set seek time
        int seekBackwardTime = 5000;

        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if (currentPosition - seekBackwardTime >= 0) {
            // forward song
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        } else {
            // backward to starting position
            mediaPlayer.seekTo(0);
        }

    }


    public void onBackPressed() {
        super.onBackPressed();

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        finish();
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

    private void SetData(int song_data) {
        try {
            song_data_json = jsonArray.getJSONObject(song_data);
            if (Utility.isConnectingToInternet(URLMediaPlayerActivity.this)) {
                new CheckLike().execute("get_likestatus", song_data_json.getString(Constant.SONG_ID), Utility.getSharedPreferences(URLMediaPlayerActivity.this, Constant.ID), "getSongById");
            } else {
                Toast.makeText(URLMediaPlayerActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
        }
    }

    public class CheckLike extends AsyncTask<String, String, String> {
        NetworkHelper putRequest = new NetworkHelper(Constant.BASE_URL);
        JSONObject networkResponse = null;
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(URLMediaPlayerActivity.this, "", "Loading...");
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
            if (progress != null) {
                progress.cancel();
            }
            try {
                networkResponse = new JSONObject(s);
                if (networkResponse.equals(null) || networkResponse.equals("")) {

                    Toast.makeText(URLMediaPlayerActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                } else {
                    if (networkResponse.getString("result").equals("success")) {
                        playMedia(Constant.SERVER_IMG_URL + song_data_json.getString(Constant.SONG_PATH));

                    }
                }
            } catch (Exception e) {
                Toast.makeText(URLMediaPlayerActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(s);
        }
    }
}
