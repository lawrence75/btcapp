package com.btc.application.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.appcompat.widget.AppCompatButton;
import com.btc.application.myapplication.R;

public class CustomBtn extends AppCompatButton {

    // 圆角半径
    private float radius = getResources().getDimension(R.dimen.activity_horizontal_margin);
    // 按钮正常时的背景颜色
    private int normalColor;

    private GradientDrawable gradientDrawable;

    public CustomBtn(Context context) {
        this(context, null, 0);
    }

    public CustomBtn(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttris(attrs);
        init();
    }

    private void init() {

        setGravity(Gravity.CENTER);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColor(normalColor);
        // 这句话一定不要忘记，不然没效果
        setBackground(gradientDrawable);
    }

    /**
     * 属行初始化
     * @param attrs
     */
    private void initAttris(AttributeSet attrs) {
        if (attrs != null) {  // 获取自定义属性
            TypedArray typedArray  = getContext().obtainStyledAttributes(attrs, R.styleable.CustomBtn);
            radius = (int) typedArray .getDimension(R.styleable.CustomBtn_radius, getResources().getDimension(R.dimen.x6));
            int defaultColor = getResources().getColor(R.color.purple_500);
            normalColor = typedArray.getColor(R.styleable.CustomBtn_normal_color, defaultColor);
            typedArray.recycle();
        }
    }
}