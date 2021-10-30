package com.btc.application.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.btc.application.MainActivity;
import com.btc.application.myapplication.NoticeActivity;
import com.btc.application.myapplication.R;
import com.btc.application.util.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    String TAG = HomeFragment.class.getCanonicalName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final Button buynowBtn = root.findViewById(R.id.buynow);
        final TextView noticeView = root.findViewById(R.id.textView);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                imageView
            }
        });

        buynowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(container.getContext()).setTitle("确定用3000币兑换一个区块吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //点击确定触发的事件
                                // 获取SharedPreference
                                SharedPreferences preference = container.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
                                // 获取存在SharedPreference中的用户名
                                String userAccount = preference.getString("userAccount", "");
                                Integer id = preference.getInt("id", 0);


                                String method = "user/openBlock/";
                                String result = HttpUtils.getJsonByInternet(method + id);
                                Log.v(TAG, result);
                                try {
                                    JSONObject jsonObject1 = new JSONObject(result);
                                    String code = jsonObject1.getString("code");
                                    if ("000000".equals(code)) {
                                        Log.v(TAG, code);
                                        Toast.makeText(container.getContext(), "购买成功！", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.v(TAG, code);
                                        Toast.makeText(container.getContext(), jsonObject1.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivityForResult(intent, 110);
                            }
                        })
                        .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //点击取消触发的事件
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        noticeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), NoticeActivity.class));
            }
        });

        return root;
    }
}