package com.btc.application.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.btc.application.ui.dashboard.SellFragment;
import com.btc.application.util.Constant;

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
        Object num = bundle.getString(SellFragment.NUM);
        Object min = bundle.getString(SellFragment.MIN);
        Object price = bundle.getString(SellFragment.PRICE);

        final TextView inputNum = findViewById(R.id.sell_num);
        final TextView inputMin = findViewById(R.id.sell_min_price);
        final TextView inputPrice = findViewById(R.id.sell_unit_price);

        if (null != num)
        {
            String numStr = num.toString();
            inputNum.setText(Constant.LABEL_NUM + numStr);
        }
        if (null != min)
        {
            String minStr = min.toString();
            inputMin.setText(Constant.LABEL_MIN + minStr);
        }
        if (null != price)
        {
            String priceStr = price.toString();
            inputPrice.setText(Constant.LABEL_PRICE + priceStr);
        }
    }
}