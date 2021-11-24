package com.btc.application.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.Map;

public class SellFragment extends Fragment implements AbsListView.OnScrollListener {

    private static final int LOAD_DATA_FINISH = 1;

    private static final String TAG = SellFragment.class.getName();

    private List<JSONObject> dataList = new ArrayList<JSONObject>();

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOAD_DATA_FINISH:
                    tv_load_more.setText(R.string.load_more_data);
                    pb_load_progress.setVisibility(View.GONE);

                    if(mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }

                    break;

                default:
                    break;
            }
        };
    };

    private int startIndex = 0;
    private int requestSize = 10;

    private ListView mListView;

    private View moreView;

    private DataAdapter mAdapter;

    private int lastItem;

    private TextView tv_load_more;

    private ProgressBar pb_load_progress;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sell, container, false);

        moreView = inflater.inflate(R.layout.footer_more, null);

        tv_load_more = (TextView) moreView.findViewById(R.id.tv_load_more);
        pb_load_progress = (ProgressBar) moreView.findViewById(R.id.pb_load_progress);

        mListView = root.findViewById(R.id.lv_expense);

        for (int i = 0; i < 10; i++) {
            JSONObject dn = new JSONObject();
            try {
                dn.put("num" , "第 " + i + " 行标题");
                dn.put("price" , "" + i + " 行内容");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dataList.add(dn);
        }
        mListView.addFooterView(moreView);
        mAdapter = new DataAdapter(container.getContext());
        mListView.setAdapter(mAdapter);

        /*List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>(); //存储数据的数组列表
//写死的数据，用于测试
        int[] item1 = new int[]{R.drawable.wechat
                , R.drawable.alipay_logo
                , R.drawable.alipay_logo
                , R.drawable.alipay_logo
                , R.drawable.alipay_logo
                , R.drawable.alipay_logo
                , R.drawable.alipay_logo
                , R.drawable.alipay_logo}; //存储图片
        String[] item2 = new String[] {"发工资1", "买衣服2" ,"发工资3", "买衣服4","发工资5",
                "买衣服6","发工资7", "买衣服8"};
        String[] item3 = new String[] {"1.00", "2.00","3.00", "4.00",
                "5.00", "6.00","7.00", "8.00"};
        String[] item4 = new String[] {"aaa1", "aaa2.00","aaa3.00", "a4.00",
                "a5.00", "a6.00","a7.00", "a8.00"};
        String[] item5 = new String[] {"b1.00", "b2.00","b3.00", "b4.00",
                "b5.00", "b6.00","b7.00", "b8.00"};
        String[] item6 = new String[] {"c1.00", "c2.00","c3.00", "c4.00",
                "c5.00", "c6.00","c7.00", "c8.00"};
        String[] item7 = new String[] {"d1.00", "d2.00","d3.00", "d4.00",
                "d5.00", "d6.00","d7.00", "d8.00"};
        String[] item8 = new String[] {"e1.00", "e2.00","e3.00", "e4.00",
                "e5.00", "e6.00","e7.00", "e8.00"};
        for (int i = 0; i < item1.length; i++)
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item1", item1[i]);
            map.put("item2", item2[i]);
            map.put("item3", item3[i]);
            map.put("item4", item4[i]);
            map.put("item5", item5[i]);
            map.put("item6", item6[i]);
            map.put("item7", item7[i]);
            map.put("item8", item8[i]);
            listitem.add(map);
        }

//创建适配器
// 第一个参数是上下文对象
// 第二个是listitem
// 第三个是指定每个列表项的布局文件
// 第四个是指定Map对象中定义的两个键（这里通过字符串数组来指定）
// 第五个是用于指定在布局文件中定义的id（也是用数组来指定）
        SimpleAdapter adapter = new SimpleAdapter(getActivity()
                , listitem
                , R.layout.item
                , new String[]{"item1", "item2", "item3", "item4"
                , "item5", "item6", "item7", "item8"}
                , new int[]{R.id.the_first_number, R.id.the_option, R.id.the_second_number
                , R.id.the_equal, R.id.surplus, R.id.root, R.id.clear, R.id.delete});*/

//        mListView.setAdapter(adapter);

        setListener();
        return root;
    }

    private void setListener() {
        mListView.setOnScrollListener(this);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (lastItem == mAdapter.getCount()
                && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

            Log.e(TAG, "load more");

            startIndex += requestSize;

            loadMoreData();
        }
    }

    private void loadMoreData() {

        tv_load_more.setText(R.string.loading_data);
        pb_load_progress.setVisibility(View.VISIBLE);

        new Thread(){

            public void run() {

                if (startIndex + requestSize <50) {

                    for (int i = startIndex; i < startIndex + requestSize; i++) {

                        JSONObject dn = new JSONObject();
                        try {
                            dn.put("price" , "增加 第 " + i + " 行标题");
                            dn.put("num" , "增加 第 " + i + " 行内容");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dataList.add(dn);
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessage(LOAD_DATA_FINISH);

                }else{
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            tv_load_more.setText(R.string.no_more_data);
                            pb_load_progress.setVisibility(View.GONE);

                        }
                    }, 1000);
                }

            };
        }.start();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItem = firstVisibleItem + visibleItemCount - 1;
    }

    private class DataAdapter extends BaseAdapter {
        private Context ctx;
        private LayoutInflater inflater;

        public DataAdapter(Context ctx) {
            this.ctx = ctx;
            inflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {

            return dataList == null ? 0 : dataList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item, null);

                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView
                        .findViewById(R.id.the_first_number);
                holder.tv_content = (TextView) convertView
                        .findViewById(R.id.the_option);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            JSONObject dn = dataList.get(position);

            try {
                holder.tv_title.setText(dn.getString("num"));
                holder.tv_content.setText(dn.getString("price"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    static class ViewHolder {
        public TextView tv_title;
        public TextView tv_content;
    }
}