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
import com.btc.application.ui.dashboard.DashboardFragment;
import com.btc.application.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class PublishOrderActivity extends AppCompatActivity {

    String TAG = PublishOrderActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_order);
        Toolbar toolbar = findViewById(R.id.toolbar);

        // B类接收数据
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
// 接收参数，参数类型可以是八大基本类型、String、八大基本类型的数组
        Object flag = bundle.getString(DashboardFragment.FLAG);

        String method = null;
        String successTip = null;

        if (null != flag)
        {
            String flagStr = flag.toString();
            if ("sell".equals(flagStr))
            {
                toolbar.setTitle("发布出售信息");
                successTip = "发布出售信息成功!";
                method = "user/publishSellInfo";
            }
            else if ("buy".equals(flagStr))
            {
                toolbar.setTitle("发布购买信息");
                successTip = "发布购买信息成功!";
                method = "user/publishBuyInfo";
            }
        }

        setSupportActionBar(toolbar);

        final TextView inputNum = findViewById(R.id.input_num);
        final TextView minPrice = findViewById(R.id.min_price);
        final TextView unitPrice = findViewById(R.id.unit_price);
        final Button cbtPublish = findViewById(R.id.cbt_publish);

        String finalMethod = method;
        String finalSuccessTip = successTip;
        cbtPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取SharedPreference
                SharedPreferences preference = v.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
                // 获取存在SharedPreference中的用户名
                Integer id = preference.getInt("id", 0);
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("num", inputNum.getText().toString());
                    jsonObject.put("min", minPrice.getText().toString());
                    jsonObject.put("price", unitPrice.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String result = HttpUtils.sendJsonPost(jsonObject.toString(), finalMethod, "POST");
                Log.v(TAG , result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if ("000000".equals(code))
                    {
                        Log.v(TAG , code);
                        Toast.makeText(getApplicationContext(), finalSuccessTip, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(v.getContext(), MainActivity.class));
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