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
import com.btc.application.ui.dashboard.SellFragment;
import com.btc.application.util.Constant;
import com.btc.application.util.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SellDetailActivity extends AppCompatActivity {

    String TAG = SellDetailActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // B类接收数据
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
// 接收参数，参数类型可以是八大基本类型、String、八大基本类型的数组
        Object userId = bundle.getString(SellFragment.USER_ID);
        Object customerId = bundle.getString(SellFragment.CUSTOMER_ID);
        Object num = bundle.getString(SellFragment.NUM);
        Object min = bundle.getString(SellFragment.MIN);
        Object price = bundle.getString(SellFragment.PRICE);

        final TextView inputNum = findViewById(R.id.sell_num);
        final TextView inputMin = findViewById(R.id.sell_min_price);
        final TextView inputPrice = findViewById(R.id.sell_unit_price);
        final TextView passwordTV = findViewById(R.id.password);
        final Button sellBtn = findViewById(R.id.cbt_sell);

        if (null != num)
        {
            String numStr = num.toString();
            inputNum.setText(Constant.LABEL_NUM + numStr + Constant.BLANK + Constant.GCM);
        }
        if (null != min)
        {
            String minStr = min.toString();
            inputMin.setText(Constant.LABEL_MIN + minStr + Constant.BLANK + Constant.GCM);
        }
        if (null != price)
        {
            String priceStr = price.toString();
            inputPrice.setText(Constant.LABEL_PRICE + priceStr + Constant.BLANK + Constant.CNY);
        }

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取SharedPreference
                SharedPreferences preference = toolbar.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
                // 获取存在SharedPreference中的用户名
                Integer id = preference.getInt("id", 0);

                String method = "user/getUser/";
                String result = HttpUtils.getJsonByInternet(method+userId);
                Log.d("debugTest",result);

                JSONObject data = new JSONObject();

                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if ("000000".equals(code))
                    {
                        data = jsonObject1.getJSONObject("data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("address", data.getString("address"));
                    jsonObject.put("bit", Float.parseFloat(num.toString())
                            * Float.parseFloat(price.toString()) );
                    jsonObject.put("secondPwd", passwordTV.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                method = "user/transferAccount";
                result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "POST");
                Log.v(TAG , result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String code = jsonObject1.getString("code");
                    if ("000000".equals(code))
                    {
                        Log.v(TAG , code);
                        Toast.makeText(getApplicationContext(), "卖出成功！", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(toolbar.getContext(), MainActivity.class);
                        startActivity(intent);
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