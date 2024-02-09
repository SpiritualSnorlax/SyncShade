package com.example.mrsa;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class Start extends AppCompatActivity {

    protected TextView startScreen;
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
        startSignInBtn = findViewById(R.id.startSignInBtn);
        startSignUpBtn = findViewById(R.id.startSignUpBtn);
        gradientBackground = findViewById(R.id.gradientBackground);


        startSignInButton();
        startSignUpButton();
        startScreenAnimation();

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


    public void startScreenAnimation() {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        startScreen.startAnimation(fadeInAnimation);
        startSignInBtn.startAnimation(fadeInAnimation);
        startSignUpBtn.startAnimation(fadeInAnimation);

        AnimationDrawable animationDrawable = (AnimationDrawable) gradientBackground.getBackground();
        animationDrawable.setEnterFadeDuration(625);
        animationDrawable.setExitFadeDuration(1250);
        animationDrawable.start();

    }
}