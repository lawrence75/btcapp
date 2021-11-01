package com.btc.application.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import com.btc.application.myapplication.R;

    public class DashboardFragment extends Fragment {

        private FragmentTabHost tabHost;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view  = inflater.inflate(R.layout.fragment_dashboard, null);
            tabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
            tabHost.setup(getActivity(),getChildFragmentManager(), android.R.id.tabcontent);
            tabHost.addTab(tabHost.newTabSpec("sell").setIndicator("我要卖", null), SellFragment
                    .class, null);
            tabHost.addTab(tabHost.newTabSpec("buy").setIndicator("我要买", null), BuyFragment
                            .class,
                    null);
            //逐个按钮添加特效
            for(int i=0;i<tabHost.getChildCount();i++){
                //换字体颜色
                TextView tv = (TextView)
                        tabHost.getChildAt(i).findViewById(android.R.id.title);
                tv.setTextColor(Color.rgb(255, 255, 255));
            }
            tabHost.setCurrentTab(0);
            return view;
        }
    }