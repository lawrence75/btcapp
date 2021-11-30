package com.btc.application.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.btc.application.MainActivity;
import com.btc.application.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PublishOrderActivity extends AppCompatActivity {

    String TAG = PublishOrderActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set2ndpwd);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView pwdView = findViewById(R.id.pwd);
        final TextView confirmpwdView = findViewById(R.id.confirmpwd);
        final Button setpwdBtn = findViewById(R.id.setpwd);

        setpwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取SharedPreference
                SharedPreferences preference = v.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
                // 获取存在SharedPreference中的用户名
                Integer id = preference.getInt("id", 0);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("secondPwd", pwdView.getText().toString());
                    jsonObject.put("confirmSecondPwd", confirmpwdView.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String method = "user/set2ndPwd";
                String result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "POST");
                Log.v(TAG , result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if ("000000".equals(code))
                    {
                        Log.v(TAG , code);
                        Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PublishOrderActivity.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}