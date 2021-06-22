package com.mrash.instagramclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView alreadyHaveAccount;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: Started");

        init();
        alreadyHaveAccount();
        setBtnRegister();
    }

    //setButon REgister
    private void setBtnRegister() {
        Log.d(TAG, "setBtnRegister: register Button Set");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    String txtUsername = username.getText().toString().trim();
                    String txtName = name.getText().toString().trim();
                    String txtEmail = email.getText().toString().trim();
                    String txtPassword = password.getText().toString().trim();
                    // registering user in firebase database
                    Log.d(TAG, "onClick: Calling register User Function in register Button");
                    registerUser(txtUsername, txtName, txtEmail, txtPassword);
                }

            }
        });
    }

    private void registerUser(String username, String name, String email, String password) {
        Log.d(TAG, "registerUser: Registering User Called");

        progressDialog.setMessage("please wait");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "onSuccess: In registerUser() -> Create User Successfully " + authResult);
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("email", email);
                map.put("username", username);
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageurl", "default");
                mRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onComplete: User Added Successfully" + mAuth.getCurrentUser().getUid());
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                }
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: Failed to add new User" + e.getMessage());
                Log.d(TAG, "onFailure: Failed to add new User" + mAuth.getCurrentUser().getUid());
            }
        });

    }

    //validation function for edit text
    private boolean validate() {
        Log.d(TAG, "validate: Going to validate editTexts");
        boolean flag = true;
        String txtUsername = username.getText().toString().trim();
        String txtName = name.getText().toString().trim();
        String txtEmail = email.getText().toString().trim();
        String txtPassword = password.getText().toString().trim();
        if (TextUtils.isEmpty(txtUsername)) {
            username.setError("Enter username");
            flag = false;
        }

        if (TextUtils.isEmpty(txtName)) {
            name.setError("Enter name");
            flag = false;

        }
        if (TextUtils.isEmpty(txtEmail)) {
            email.setError("Enter Email");
            flag = false;
        }
        if (TextUtils.isEmpty(txtPassword)) {
            password.setError("Enter password");
            flag = false;
        }
        if (txtPassword.length() < 8) {
            password.setError("Password must be at least 8 character long");
            flag = false;
        }

        return flag;
    }
    //Init View Method
    private void init() {
        Log.d(TAG, "init: Started");
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        alreadyHaveAccount = findViewById(R.id.already_have_account);
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
    }
    //Intent take action to Login Activity Screen
    private void alreadyHaveAccount() {
        Log.d(TAG, "alreadyHaveAccount: Already have and account clicked");
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, com.mrash.instagramclone.LoginActivity.class));
            }
        });
    }

}