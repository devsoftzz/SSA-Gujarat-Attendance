package com.devsoftzz.onlineattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    public static final String PREFERENCE_NAME = "attendance_pref";
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";

    private TextInputLayout mUsername, mPassword;
    private MaterialButton mEnter;
    private SharedPreferences mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = findViewById(R.id.username_field);
        mPassword = findViewById(R.id.password_field);
        mEnter = findViewById(R.id.enter_btn);

        mPreference = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);

        mUsername.getEditText().setText(mPreference.getString(USERNAME_KEY, ""));
        mPassword.getEditText().setText(mPreference.getString(PASSWORD_KEY, ""));

        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getEditText().getText().toString().trim();
                String password = mPassword.getEditText().getText().toString().trim();

                SharedPreferences.Editor editor = mPreference.edit();
                editor.putString(USERNAME_KEY, username);
                editor.putString(PASSWORD_KEY, password);
                editor.apply();

                startActivity(new Intent(MainActivity.this, WebActivity.class));
            }
        });
    }
}
