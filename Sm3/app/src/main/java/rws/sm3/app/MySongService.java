package rws.sm3.app;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Android-2 on 12/2/2015.
 */
public class MySongService extends Service {
    private MediaPlayer mp;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
//        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
        mp = new MediaPlayer();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Perform your long running operations here.
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
