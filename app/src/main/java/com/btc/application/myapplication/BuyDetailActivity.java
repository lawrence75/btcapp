package com.btc.application.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.btc.application.ui.dashboard.BuyFragment;
import com.btc.application.ui.dashboard.SellFragment;
import com.btc.application.util.Constant;
import com.btc.application.util.FileUtils;
import com.btc.application.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class BuyDetailActivity extends AppCompatActivity {

    String TAG = BuyDetailActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // B类接收数据
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
// 接收参数，参数类型可以是八大基本类型、String、八大基本类型的数组
        Object id = bundle.getString(SellFragment.ID);
//        Object userId = bundle.getString(SellFragment.USER_ID);
        Object num = bundle.getString(SellFragment.NUM);
        Object min = bundle.getString(SellFragment.MIN);
        Object price = bundle.getString(SellFragment.PRICE);

        final TextView inputNum = findViewById(R.id.buy_num);
        final TextView inputMin = findViewById(R.id.buy_min_price);
        final TextView inputPrice = findViewById(R.id.buy_unit_price);
        final ImageView receiveCodeImage = findViewById(R.id.receive_code_image);
        final Button buyBtn = findViewById(R.id.cbt_buy);

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

        // 获取SharedPreference
        SharedPreferences preference = getWindow().getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
        // 获取存在SharedPreference中的用户名
        id = preference.getInt("id", 0);

        String method = "user/getUser/";
        String result = HttpUtils.getJsonByInternet(method+id);
        Log.d("debugTest",result);

        JSONObject data = new JSONObject();
        Integer defaultPay = 0;
        String imageUrl = "";

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

        String wechatAddress = null;
        String alipayAddress = null;
        String bankCard = null;
        try {
            wechatAddress = data.getString("wechatAddress");
            alipayAddress = data.getString("alipayAddress");
            bankCard = data.getString("bankCard");
            defaultPay = data.getInt("defaultPay");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch(defaultPay)
        {
            case 1:
            {
                imageUrl = wechatAddress;
                break;
            }
            case 2:
            {
                imageUrl = alipayAddress;
                break;
            }
            case 3:
            {
                imageUrl = bankCard;
                break;
            }
        }

        Bitmap bitmap = FileUtils.url2bitmap(Constant.APP_URL + Constant.FILE_PREFIX + imageUrl);
        receiveCodeImage.setImageBitmap(bitmap);
    }
}