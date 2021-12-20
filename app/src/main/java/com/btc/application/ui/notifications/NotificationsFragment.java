package com.btc.application.ui.notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.btc.application.MainActivity;
import com.btc.application.myapplication.R;
import com.btc.application.myapplication.RechargeActivity;
import com.btc.application.myapplication.WithdrawActivity;
import com.btc.application.ui.login.LoginActivity;
import com.btc.application.ui.register.RegisterActivity;
import com.btc.application.util.HttpUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static android.content.Context.MODE_PRIVATE;

import cn.jpush.android.api.JPushInterface;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private View root;
    private ViewGroup container;

    private TextView areaView1;
    private TextView areaView2;
    private TextView areaView3;

    private TextView labelView1;
    private TextView labelView2;
    private TextView labelView3;

    private TextView noDataView;
    private TextView registrationIDView;

    private TextView gcmView;
    private TextView cnyView;

    private SharedPreferences preference;
    private Integer id;
    private Integer bit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        root = inflater.inflate(R.layout.fragment_notifications, container, false);
        this.container = container;
        final Button rechargeBtn = root.findViewById(R.id.recharge);
        final Button withdrawBtn = root.findViewById(R.id.withdraw);
//        rechargeBtn.getBackground().setAlpha(0);//0~255透明度值

        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), RechargeActivity.class));
            }
        });

        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), WithdrawActivity.class));
            }
        });

        initData();

        refreshData(root , this.container);

        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    public void initData()
    {
        areaView1 = root.findViewById(R.id.areaView1);
        areaView2 = root.findViewById(R.id.areaView2);
        areaView3 = root.findViewById(R.id.areaView3);

        labelView1 = root.findViewById(R.id.labelView1);
        labelView2 = root.findViewById(R.id.labelView2);
        labelView3 = root.findViewById(R.id.labelView3);

        noDataView = root.findViewById(R.id.noDataView);

        gcmView = root.findViewById(R.id.gcmView);
        cnyView = root.findViewById(R.id.cnyView);

        // 获取SharedPreference
        preference = container.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
        // 获取存在SharedPreference中的用户名
        id = preference.getInt("id", 0);
        bit = preference.getInt("bit", 0);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //进入到当前Fragment
        if (enter){
//            refreshData(root , container);
            //进行刷新操作
        }else {

        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onPause() {
        super.onPause();
        //refreshData(root , container);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData(root , container);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(110 == requestCode) {
            refreshData(root , container);
        }
    }

    public void refreshData(View root , ViewGroup container)
    {

        String method = "user/getUserBlocks/";
        String result = HttpUtils.getJsonByInternet(method+id);
        Log.d("debugTest",result);
        gcmView.setText(bit+"");
        cnyView.setText(bit+"");
        try {
            JSONObject jsonObject = new JSONObject(result);
            String code = jsonObject.getString("code");
            if ("000000".equals(code))
            {
                JSONArray data = jsonObject.getJSONArray("data");
                int length = data.length();
                switch(length)
                {
                    case 0:
                        labelView1.setVisibility(View.INVISIBLE);
                        labelView2.setVisibility(View.INVISIBLE);
                        labelView3.setVisibility(View.INVISIBLE);
                        areaView1.setVisibility(View.INVISIBLE);
                        areaView2.setVisibility(View.INVISIBLE);
                        areaView3.setVisibility(View.INVISIBLE);
                        noDataView.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        JSONObject obj1 = (JSONObject) data.get(0);
                        areaView1.setText(obj1.getInt("bit")+"");
                        labelView2.setVisibility(View.INVISIBLE);
                        labelView3.setVisibility(View.INVISIBLE);
                        areaView2.setVisibility(View.INVISIBLE);
                        areaView3.setVisibility(View.INVISIBLE);
                        noDataView.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        JSONObject obj21 = (JSONObject) data.get(0);
                        areaView1.setText(obj21.getInt("bit")+"");
                        JSONObject obj2 = (JSONObject) data.get(1);
                        areaView2.setText(obj2.getInt("bit")+"");
                        labelView3.setVisibility(View.INVISIBLE);
                        areaView3.setVisibility(View.INVISIBLE);
                        noDataView.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        JSONObject obj31 = (JSONObject) data.get(0);
                        areaView1.setText(obj31.getInt("bit")+"");
                        JSONObject obj32 = (JSONObject) data.get(1);
                        areaView2.setText(obj32.getInt("bit")+"");
                        JSONObject obj3 = (JSONObject) data.get(2);
                        areaView3.setText(obj3.getInt("bit")+"");
                        noDataView.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        method = "user/getUser/";
        result = HttpUtils.getJsonByInternet(method+id);
        Log.d("debugTest",result);

        try {
            JSONObject jsonObject1 = new JSONObject(result);
            String code = jsonObject1.getString("code");
            if ("000000".equals(code))
            {
                JSONObject data = jsonObject1.getJSONObject("data");
                gcmView.setText(data.getInt("bit") + "");
                cnyView.setText(data.getInt("bit") + "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}