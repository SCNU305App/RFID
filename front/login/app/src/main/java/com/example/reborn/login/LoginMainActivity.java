package com.example.reborn.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.CheckBox;
import android.content.SharedPreferences;

public class LoginMainActivity extends AppCompatActivity {
    private EditText username_login;
    private EditText password_login;
    private CheckBox rememberpassword_login;
    private CheckBox auto_login;
    private Button button_login;
    private SharedPreferences sp;
    private String idvalue;
    private String passwordvalue;
    private static final int PASSWORD_MIWEN = 0x81;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        //找到相应的布局及控件
        username_login=(EditText) findViewById(R.id.username);
        password_login=(EditText) findViewById(R.id.password);
        rememberpassword_login=(CheckBox) findViewById(R.id.login_rememberpassword);
        auto_login=(CheckBox) findViewById(R.id.login_autologin);
        button_login=(Button) findViewById(R.id.login);
//      设置按钮功能实现跳转
        Button buttonLogin = (Button) findViewById(R.id.login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginMainActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });
    }
}
