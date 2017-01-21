package rws.sm3.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import rws.sm3.app.CommonUtilites.Constant;
import rws.sm3.app.CommonUtilites.Utility;

/**
 * Created by Android-2 on 10/22/2015.
 */
public class SplashActivity extends AppCompatActivity {
    private Context appContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appContext=this;
        Init();

    }
    private void Init() {

        TranslateAnimation tAnimation = new TranslateAnimation(0, 0, 0, -250);
        tAnimation.setDuration(2000);
        tAnimation.setRepeatCount(0);
        tAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        tAnimation.setFillAfter(true);
        tAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                if (Utility.getSharedPreferences(appContext, Constant.ID).equals("")) {
                    startActivity(new Intent(appContext, MainActivity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                } else {
                    startActivity(new Intent(appContext, Home_Activity.class));
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    finish();
                }
            }
        });

        ((ImageView) findViewById(R.id.logo)).startAnimation(tAnimation);
    }

}
