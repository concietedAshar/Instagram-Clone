package com.mrash.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText email;
    private EditText password;
    private Button login;
    private TextView registerNow;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started");
        init();
        setLogin();
        goRegisterYourself();
    }
    //init for view connection
    private void init() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        registerNow = findViewById(R.id.register_now);
        mAuth = FirebaseAuth.getInstance();
    }
    // if user is not register then goto register activity and signUp First
    private void goRegisterYourself() {
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, com.mrash.instagramclone.RegisterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });

    }

    //set Login Button
    private void setLogin() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String txtEmail = email.getText().toString().trim();
                    String txtPassword = password.getText().toString().trim();
                    loginUser(txtEmail, txtPassword);
                }
            }
        });
    }

    //login user called in set login to check email and password with firebase auth
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: User Logged in Successfully" + mAuth.getCurrentUser().getUid());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to login" + e.getMessage());
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Validate func() used in setlogin() to validate edit text fields
    private boolean validate() {
        String txtEmail = email.getText().toString().trim();
        String txtPassword = password.getText().toString().trim();
        boolean flag = true;
        if (TextUtils.isEmpty(txtEmail)) {
            email.setError("Enter Email");
            flag = false;
        }
        if (TextUtils.isEmpty(txtPassword)) {
            password.setError("Enter Password");
            flag = false;
        }

        return flag;
    }

}