package com.btc.application.ui.dashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;

import com.btc.application.MainActivity;
import com.btc.application.myapplication.PublishOrderActivity;
import com.btc.application.myapplication.R;
import com.btc.application.myapplication.Set2ndpwdActivity;
import com.btc.application.util.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentTabHost tabHost;
    private TextView floatRatio;
    private TextView currentRrice;
    private TextView currentAveragePrice;
    private TextView currentHighPrice;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container,false);
        tabHost = view.findViewById(android.R.id.tabhost);

        //要切换显示的所有Fragment
        List<Class> list = new ArrayList<Class>();
        list.add(SellFragment.class);
        list.add(BuyFragment.class);
        //选项卡的标识
        tabHost.setup(getActivity(),
                getChildFragmentManager(),
                android.R.id.tabcontent);

        tabHost.addTab(tabHost.newTabSpec("sell").setIndicator("我要卖", null), SellFragment
                .class, null);
        tabHost.addTab(tabHost.newTabSpec("buy").setIndicator("我要买", null), BuyFragment
                        .class,null);

        floatRatio = view.findViewById(R.id.float_ratio);
        currentRrice = view.findViewById(R.id.current_price);
        currentAveragePrice = view.findViewById(R.id.current_average_price);
        currentHighPrice = view.findViewById(R.id.current_high_price);

        String method = "user/getConstant/";
        String result = HttpUtils.getJsonByInternet(method);
        Log.d("debugTest",result);

        try {
            JSONObject jsonObject1 = new JSONObject(result);
            String code = jsonObject1.getString("code");
            if ("000000".equals(code))
            {
                JSONObject data = jsonObject1.getJSONObject("data");
                floatRatio.setText(data.getString("floatRatio") + "%");
                currentRrice.setText(data.getString("currentRrice") );
                currentAveragePrice.setText(data.getString("currentAveragePrice") );
                currentHighPrice.setText(data.getString("currentHighPrice") );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Button publishBtn = view.findViewById(R.id.bt_publish);

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), PublishOrderActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tabHost = null;
    }
}