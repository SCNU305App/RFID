package com.example.reborn.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class LoginMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        Button buttonOne = (Button) findViewById(R.id.login);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginMainActivity.this, SuccessMainActivity.class);
                startActivity(intent);
            }
        });
    }
}
