package com.btc.application.myapplication;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RechargeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView addressView = findViewById(R.id.address);
        final Button copyButton = findViewById(R.id.copy);
        // 获取SharedPreference
        SharedPreferences preference = addressView.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
        // 获取存在SharedPreference中的用户名
        String address = preference.getString("address", "");
        addressView.setText(address);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) addressView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(address);
                Toast.makeText(addressView.getContext(), "邀请码复制成功！", Toast.LENGTH_LONG).show();
            }
        });
    }
}