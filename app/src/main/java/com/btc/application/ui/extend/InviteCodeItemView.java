package com.btc.application.ui.extend;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.btc.application.myapplication.NoticeActivity;
import com.btc.application.myapplication.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * 自定义个人中心选项控件
 */
public class InviteCodeItemView extends RelativeLayout {
    private TextView data;

    public InviteCodeItemView(final Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_personal_menu, this);
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PersonaltemView);

        ImageView icon = findViewById(R.id.icon);
        ImageView more = findViewById(R.id.more);
        ImageView line = findViewById(R.id.line);
        TextView name = findViewById(R.id.name);
        TextView dataView = findViewById(R.id.data);

        // 获取SharedPreference
        SharedPreferences preference = context.getSharedPreferences("userinfo", MODE_PRIVATE);
        // 获取存在SharedPreference中的用户名
        String inviteCode = preference.getString("inviteCode", "");
        dataView.setText(inviteCode);

        icon.setImageDrawable(typedArray.getDrawable(R.styleable.PersonaltemView_icon));
        name.setText(typedArray.getText(R.styleable.PersonaltemView_name));

        if (typedArray.getBoolean(R.styleable.PersonaltemView_show_more, false)){
            more.setVisibility(VISIBLE);
        }
        if (typedArray.getBoolean(R.styleable.PersonaltemView_show_line, false)){
            line.setVisibility(VISIBLE);
        }
        typedArray.recycle();

        dataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(inviteCode);
                Toast.makeText(context, "复制成功！", Toast.LENGTH_LONG).show();
            }
        });
    }


    // 提供设置控件的描述数据
    public void setData(String data){
        this.data.setText(data);
    }
}