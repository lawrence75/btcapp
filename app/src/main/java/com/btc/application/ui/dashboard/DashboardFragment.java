package com.btc.application.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import com.btc.application.myapplication.R;
import com.btc.application.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentTabHost tabHost;
    String TAG = DashboardFragment.class.getCanonicalName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        tabHost = view.findViewById(android.R.id.tabhost);
        //获取导航按钮控件
        //tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        //要切换显示的所有Fragment
        List<Class> list = new ArrayList<Class>();
        list.add(SellFragment.class);
        list.add(BuyFragment.class);
        //选项卡的标识
        List<String> tagLables = Arrays.asList(
                "sell", "buy");
        List<String> tags = Arrays.asList(
                "我要卖", "我要买");
        //选项卡使用View，layouts是这些3个选项卡样式各自的布局文件
        //设置Fragment将在哪里显示（
        //在id为tabcontent的FrameLayout中显示）
        tabHost.setup(getActivity(),
                getChildFragmentManager(),
                android.R.id.tabcontent);
        for (int i = 0; i < tagLables.size(); i++) {
            //获得第i个选项卡的View
            //创建第i个选项卡，该选项卡的标识为tags.get(i)，
            //该选项卡的样式为view
            //建立选项卡和Fragment的对应关系，
            //当点击该选项卡时在FrameLayout中
            //显示list中第i个Fragment
//          tabHost.addTab(tab, list.get(i), null);
            tabHost.addTab(tabHost.newTabSpec(tagLables.get(i)).setIndicator(tags.get(i), null), list.get(i), null);
            //其它设置 选项卡之间没有分割线
            tabHost.getTabWidget().setDividerDrawable(null);
        }

        /*tabHost.addTab(tabHost.newTabSpec("sell").setIndicator("我要卖", null), SellFragment
                .class, null);
        tabHost.addTab(tabHost.newTabSpec("buy").setIndicator("我要买", null), BuyFragment
                        .class,null);
        //逐个按钮添加特效
        for (int i = 0; i < tabHost.getChildCount(); i++) {
            //换字体颜色
            TextView tv = tabHost.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.rgb(255, 255, 255));
        }
        tabHost.setCurrentTab(0);*/
        return view;
    }

}