package com.example.mrsa;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Start extends AppCompatActivity {

    protected TextView startScreen;
    protected ImageView rsOpenLogo;
    protected ImageView rsCloseLogo;
    private boolean isShowingFirstImage = true;
    private Handler handler = new Handler();
    private final int animationDuration = 1000; // Animation duration in milliseconds
    private final int animationDelay = 3000; // Delay between animations in milliseconds
    protected Button startSignInBtn;
    protected Button startSignUpBtn;
    protected ConstraintLayout gradientBackground;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);


        startScreen = findViewById(R.id.startScreen);
        rsOpenLogo = findViewById(R.id.rsOpenLogo);
        rsCloseLogo = findViewById(R.id.rsCloseLogo);
        startSignInBtn = findViewById(R.id.startSignInBtn);
        startSignUpBtn = findViewById(R.id.startSignUpBtn);
        gradientBackground = findViewById(R.id.gradientBackground);

        enableNotifications();
        startSignInButton();
        startSignUpButton();
        backgroundAnimation();
        handler.post(animationRunnable);
    }


    public void startSignInButton() {
        startSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Start.this, Login.class));
                finish();
            }
        });
    }

    public void startSignUpButton() {
        startSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Start.this, Register.class));
                finish();
            }
        });
    }


    public void backgroundAnimation() {
        AnimationDrawable animationDrawable = (AnimationDrawable) gradientBackground.getBackground();
        animationDrawable.setEnterFadeDuration(625);
        animationDrawable.setExitFadeDuration(1250);
        animationDrawable.start();
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShowingFirstImage) {
                // Fade out imageView1
                fadeOut(rsOpenLogo);
                // Fade in imageView2 after the fade out animation completes
                handler.postDelayed(() -> fadeIn(rsCloseLogo), animationDuration);
            } else {
                // Fade out imageView2
                fadeOut(rsCloseLogo);
                // Fade in imageView1 after the fade out animation completes
                handler.postDelayed(() -> fadeIn(rsOpenLogo), animationDuration);
            }
            // Toggle the flag to alternate between image views
            isShowingFirstImage = !isShowingFirstImage;
            // Schedule the next animation loop after a delay
            handler.postDelayed(this, animationDelay);
        }
    };

    private void fadeOut(final ImageView imageView) {
        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(animationDuration);
        fadeOutAnimation.setFillAfter(true); // Ensure the view stays invisible after animation
        imageView.startAnimation(fadeOutAnimation);
    }

    private void fadeIn(final ImageView imageView) {
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeInAnimation.setDuration(animationDuration);
        fadeInAnimation.setFillAfter(true); // Ensure the view stays visible after animation
        imageView.startAnimation(fadeInAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }

    private void enableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(Start.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(Start.this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }
}