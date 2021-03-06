package com.btc.application.ui.extend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.btc.application.myapplication.ModAlipayCodeActivity;
import com.btc.application.myapplication.ModBankAccountActivity;
import com.btc.application.myapplication.R;

/**
 * 自定义个人中心选项控件
 */
public class ModBankCardItemView extends RelativeLayout {
    private TextView data;

    public ModBankCardItemView(final Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_personal_menu, this);
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PersonaltemView);

        ImageView icon = findViewById(R.id.icon);
        ImageView more = findViewById(R.id.more);
        ImageView line = findViewById(R.id.line);
        TextView name = findViewById(R.id.name);

        icon.setImageDrawable(typedArray.getDrawable(R.styleable.PersonaltemView_icon));
        name.setText(typedArray.getText(R.styleable.PersonaltemView_name));

        if (typedArray.getBoolean(R.styleable.PersonaltemView_show_more, false)){
            more.setVisibility(VISIBLE);
        }
        if (typedArray.getBoolean(R.styleable.PersonaltemView_show_line, false)){
            line.setVisibility(VISIBLE);
        }
        typedArray.recycle();

        name.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ModBankAccountActivity.class));
            }
        });
    }

    // 提供设置控件的描述数据
    public void setData(String data){
        this.data.setText(data);
    }
}