package com.btc.application.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.btc.application.MainActivity;
import com.btc.application.ui.dashboard.BuyFragment;
import com.btc.application.ui.dashboard.SellFragment;
import com.btc.application.util.Constant;
import com.btc.application.util.FileUtils;
import com.btc.application.util.HttpUtils;
import com.btc.application.util.NotificationChannels;

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
        Object customerId = bundle.getString(SellFragment.CUSTOMER_ID);
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

        Object finalId = id;
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                 String channelId = "app1";
                                 if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                                         NotificationChannel channel = new NotificationChannel(channelId,"app1",NotificationManager.IMPORTANCE_DEFAULT);
                                         manager.createNotificationChannel(channel);
                                     }

                                 Notification notification =
                                         new NotificationCompat.Builder(toolbar.getContext(),channelId)
                                         .setContentTitle("通知标题")
                                         .setContentText("通知正文")
                                         .setWhen(System.currentTimeMillis())
                                         .setSmallIcon(R.mipmap.ic_launcher)
                                         .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                                         .build();
                                 manager.notify(1,notification);*/

                String title = "123";
                String content = "456";

                String method = "msg/send?title=" + title + "&content=" + content + "&id=" + customerId;

                String result = HttpUtils.getJsonByInternet(method);
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

                Intent intent = new Intent(toolbar.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendSimpleNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,BuyDetailActivity.class);
        intent.setAction("ACTION_SIMPLE");
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建删除通知时发送的广播
        Intent deleteIntent = new Intent(context,BuyDetailActivity.class);
        deleteIntent.setAction("ACTION_DELETE");
        PendingIntent deletePendingIntent = PendingIntent.getService(context,0,deleteIntent,0);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context, NotificationChannels.LOW)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知标题
                .setContentTitle("Simple notification")
                //设置通知内容
                .setContentText("Demo for simple notification !")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置通知右侧的大图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_round))
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                //设置删除通知时的响应事件
                .setDeleteIntent(deletePendingIntent);
        //发送通知
        nm.notify(1,nb.build());
    }
}