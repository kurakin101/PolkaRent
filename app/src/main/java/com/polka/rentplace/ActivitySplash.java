package com.polka.rentplace;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.race604.drawable.wave.WaveDrawable;

public class ActivitySplash extends Activity {
    ImageView shView;
    private WaveDrawable mWaveDrawable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            shView = (ImageView) findViewById(R.id.splView);

            Drawable mWaveDrawable = new WaveDrawable(this, R.drawable.logo_icon);

            shView.setImageDrawable(mWaveDrawable);

            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setDuration(4000);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.start();



//            View view = findViewById(R.id.view);
//            int color = getResources().getColor(R.color.colorAccent);
//            WaveDrawable colorWave = new WaveDrawable(new ColorDrawable(color));
//            view.setBackground(colorWave);
//            colorWave.setIndeterminate(true);



        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }
        Intent i = new Intent(ActivitySplash.this, MainActivity.class);
        startActivity(i);
        this.finish();
    }



}