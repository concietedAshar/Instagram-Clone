package com.mrash.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private ImageView iconImage;
    private LinearLayout linearLayout;
    private Button login;
    private Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        init();
        setAnimation();
        setButtons();

    }

    // Init View Method
    private void init() {
        iconImage = findViewById(R.id.icon_image);
        linearLayout = findViewById(R.id.linear_layout);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        iconImage.setVisibility(View.VISIBLE);
    }

    // This method set Animation
    private void setAnimation() {
        linearLayout.animate().alpha(0f).setDuration(1);

        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListner());
        iconImage.setAnimation(animation);
    }

    //setButtons to Activate Intents to login and register Activity
    private void setButtons() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, com.mrash.instagramclone.LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, com.mrash.instagramclone.RegisterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //for that user don't have to login again and again.
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(StartActivity.this, com.mrash.instagramclone.MainActivity.class));
            finish();
        }
    }

    //class for Animation
    private class MyAnimationListner implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        //after instagram logo animate to top Linear Layout Appear
        @Override
        public void onAnimationEnd(Animation animation) {
            iconImage.clearAnimation();
            linearLayout.setVisibility(View.VISIBLE);
            iconImage.setVisibility(View.GONE);
            linearLayout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


}