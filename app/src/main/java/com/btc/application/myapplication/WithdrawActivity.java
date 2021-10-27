package com.btc.application.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.btc.application.MainActivity;
import com.btc.application.util.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WithdrawActivity extends AppCompatActivity {

    String TAG = WithdrawActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView addressView = findViewById(R.id.address);
        final TextView countView = findViewById(R.id.count);
        final TextView passwordView = findViewById(R.id.password);
        final Button submitButton = findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String count = countView.getText().toString();
                String address = addressView.getText().toString();
                String pwd = passwordView.getText().toString();
                if(address.trim().length()==0)
                {
                    Toast.makeText(getApplicationContext(), "地址不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                if(count.trim().length()==0)
                {
                    Toast.makeText(getApplicationContext(), "转账币数不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                if(pwd.trim().length()==0)
                {
                    Toast.makeText(getApplicationContext(), "密码不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }
                double sub = Integer.parseInt(count) * 0.95;
                double charge = Integer.parseInt(count) * 0.05;
                //转账金额为"+count+"个币，
                AlertDialog dialog = new AlertDialog.Builder(view.getContext()).setTitle("转账"+count+"个币，实际到账"+sub+"个币，手续费"+charge+"个币，确定转账吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //点击确定触发的事件
                                // 获取SharedPreference
                                SharedPreferences preference = view.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
                                // 获取存在SharedPreference中的用户名
                                Integer id = preference.getInt("id", 0);


                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("id", id);
                                    jsonObject.put("address", addressView.getText().toString());
                                    jsonObject.put("bit", count);
                                    jsonObject.put("secondPwd", passwordView.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String method = "user/transferAccount";
                                String result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "POST");
                                Log.v(TAG , result);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(result);
                                    String code = jsonObject1.getString("code");
                                    if ("000000".equals(code))
                                    {
                                        Log.v(TAG , code);
                                        Toast.makeText(getApplicationContext(), "转账成功，请到资产页面查看！", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(WithdrawActivity.this, MainActivity.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //点击取消触发的事件
                                dialog.cancel();
                            }
                        }).create();
                dialog.show();
                //设置窗口的大小
//                dialog.getWindow().setLayout(1000, 800);
//                showTip();
            }
        });
    }

    private void showTip() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setPositiveButton("确定", (dialog1, which) -> dialog1.dismiss());
        AlertDialog alertDialog = dialog.create();
        TextView title = new TextView(this);
        title.setText("提示");
        title.setPadding(10, 30, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        alertDialog.setCustomTitle(title);
        alertDialog.setMessage("更多博客请联系博主，查看xiayiye5博客，地址：https://blog.csdn.net/xiayiye5");
        alertDialog.setCancelable(false);
        alertDialog.show();
        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams cancelBtnPara = (LinearLayout.LayoutParams) button.getLayoutParams();
        //设置按钮的大小
        cancelBtnPara.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        cancelBtnPara.width = LinearLayout.LayoutParams.MATCH_PARENT;
        //设置文字居中
        cancelBtnPara.gravity = Gravity.CENTER;
        //设置按钮左上右下的距离
        cancelBtnPara.setMargins(100, 20, 100, 20);
        button.setLayoutParams(cancelBtnPara);
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.background));
        button.setTextColor(ContextCompat.getColor(this, R.color.white));
        button.setTextSize(16);
    }
}