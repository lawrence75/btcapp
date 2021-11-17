package com.btc.application.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.btc.application.myapplication.R;
import com.btc.application.ui.extend.ExtendsViewModel;
import com.btc.application.ui.login.LoginActivity;
import com.btc.application.util.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellFragment extends Fragment {

    private TextView floatRatio;
    private TextView currentRrice;
    private TextView currentAveragePrice;
    private TextView currentHighPrice;

    List<String> list;
    List<String> list1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sell, container, false);
        floatRatio = root.findViewById(R.id.float_ratio);
        currentRrice = root.findViewById(R.id.current_price);
        currentAveragePrice = root.findViewById(R.id.current_average_price);
        currentHighPrice = root.findViewById(R.id.current_high_price);

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


        ListView listView = (ListView) root.findViewById(R.id.listView);

        //获取到集合数据
        //名字列表，之后可以动态加入数据即可，这里只是数据例子
        list = new ArrayList<>();
        list.add("小明");
        list.add("李华");
        list.add("张三");

        list1 = new ArrayList<>();
        list1.add("男");
        list1.add("男");
        list1.add("女");

        List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        for(int i = 0; i < list .size(); i++){
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("name", list.get(i));
            item.put("sex", list1.get(i));
            data.add(item);
        }
        //创建SimpleAdapter适配器将数据绑定到item显示控件上
        SimpleAdapter adapter = new SimpleAdapter(root.getContext(), data, R.layout.item,
                new String[]{"name", "sex"}, new int[]{R.id.name, R.id.sex});
        //实现列表的显示
        listView.setAdapter(adapter);

        return root;
    }
}