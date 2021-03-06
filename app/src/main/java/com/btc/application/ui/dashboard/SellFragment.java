package com.btc.application.ui.dashboard;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.btc.application.MainActivity;
import com.btc.application.myapplication.OrderListActivity;
import com.btc.application.myapplication.R;
import com.btc.application.myapplication.SellDetailActivity;
import com.btc.application.util.Constant;
import com.btc.application.util.HttpUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SellFragment extends Fragment implements AbsListView.OnScrollListener {

    private static final int LOAD_DATA_FINISH = 1;

    public static final String ID = "ID";

    public static final String USER_ID = "USER_ID";

    public static final String CUSTOMER_ID = "CUSTOMER_ID";

    public static final String NUM = "num";

    public static final String MIN = "min";

    public static final String PRICE = "price";

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
    private int currentPage = 1;
    private int requestSize = 10;
    private int maxLenth = 0;

    private ListView mListView;

    private JSONArray jsonArray = new JSONArray();

    private JSONObject jsonObject = new JSONObject();

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

        mListView = root.findViewById(R.id.list_sell);

        mAdapter = new DataAdapter(container.getContext());

        init();

        mListView.addFooterView(moreView);

        mListView.setAdapter(mAdapter);

        setListener();

        return root;
    }

    private void init()
    {
        currentPage = 1;
        startIndex = 0;
        dataList.clear();

        try {
            jsonObject.put("current", currentPage);
            jsonObject.put("size", requestSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String method = "user/getSellList";
        JSONObject data = new JSONObject();
        String result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "POST");
        Log.v(TAG , result);
        try {
            JSONObject jsonObject1 = new JSONObject(result);
            String code = jsonObject1.getString("code");
            if ("000000".equals(code))
            {
                data = jsonObject1.getJSONObject("data");
                jsonArray = data.getJSONArray("list");
                maxLenth = data.getInt("totalCount");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject myJsonObject = jsonArray.getJSONObject(i);
                    dataList.add(myJsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (maxLenth >= requestSize)
        {
            tv_load_more.setText(R.string.load_more_data);
            pb_load_progress.setVisibility(View.GONE);
        }
        else
        {
            tv_load_more.setText(R.string.no_more_data);
            pb_load_progress.setVisibility(View.GONE);
        }
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

                if (startIndex < maxLenth) {
                    try {
                        jsonObject.put("current", ++currentPage);
                        jsonObject.put("size", requestSize);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String method = "user/getSellList";
                    JSONObject data = new JSONObject();
                    String result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "POST");
                    Log.v(TAG , result);
                    try {
                        JSONObject jsonObject1 = new JSONObject(result);
                        String code = jsonObject1.getString("code");
                        if ("000000".equals(code))
                        {
                            data = jsonObject1.getJSONObject("data");
                            jsonArray = data.getJSONArray("list");
                            maxLenth = data.getInt("totalCount");
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject myJsonObject = jsonArray.getJSONObject(i);
                                dataList.add(myJsonObject);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                convertView = inflater.inflate(R.layout.item_sell_list, null);

                holder = new ViewHolder();
                holder.tv_user_id = (TextView) convertView
                        .findViewById(R.id.sell_user_id);
                holder.tv_sell_count = (TextView) convertView
                        .findViewById(R.id.sell_count);
                holder.tv_min_limit = (TextView) convertView
                        .findViewById(R.id.sell_min_limit);
                holder.iv_pay_method = (ImageView) convertView
                        .findViewById(R.id.sell_pay_method);
                holder.tv_label_price = (TextView) convertView
                        .findViewById(R.id.sell_label);
                holder.tv_price = (TextView) convertView
                        .findViewById(R.id.sell_price);
                holder.bt_sell = (Button) convertView
                        .findViewById(R.id.bt_sell);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            JSONObject dn = dataList.get(position);

            Random r = new Random();
            int ran = r.nextInt(3);
            try {
                holder.tv_user_id.setText(Constant.LABEL_USER_ID + dn.getString("userId"));
                holder.tv_sell_count.setText(Constant.LABEL_NUM + dn.getString("num") + Constant.BLANK + Constant.GCM);
                holder.tv_min_limit.setText(Constant.LABEL_MIN + dn.getString("min") + Constant.BLANK + Constant.GCM);
                switch(ran)
                {
                    case 0:
                    {
                        holder.iv_pay_method.setImageResource(R.drawable.wechat);
                        break;
                    }
                    case 1:
                    {
                        holder.iv_pay_method.setImageResource(R.drawable.alipay_logo);
                        break;
                    }
                    case 2:
                    {
                        holder.iv_pay_method.setImageResource(R.drawable.bank_card);
                        break;
                    }
                }
                holder.tv_label_price.setText(Constant.LABEL_PRICE);
                holder.tv_price.setText(dn.getString("price") + Constant.BLANK + Constant.CNY);
                holder.bt_sell.setText("??????");

                /*holder.bt_sell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mListView.getContext(), SellDetailActivity.class);
                        try {
                            intent.putExtra(ID, dn.getString("id"));
                            intent.putExtra(USER_ID, dn.getString("userId"));
                            intent.putExtra(NUM, dn.getString("num"));
                            intent.putExtra(MIN, dn.getString("min"));
                            intent.putExtra(PRICE, dn.getString("price"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });*/
                Integer id = dn.getInt("id");
                holder.bt_sell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog = new AlertDialog.Builder(view.getContext()).setTitle("????????????????")
                                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //???????????????????????????
                                        // ??????SharedPreference
                                        SharedPreferences preference = view.getContext().getSharedPreferences("userinfo", MODE_PRIVATE);
                                        // ????????????SharedPreference???????????????
                                        Integer userId = preference.getInt("id", 0);
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("id", id);
                                            jsonObject.put("buyerId", userId);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        String method = "user/sell";
                                        String result = HttpUtils.sendJsonPost(jsonObject.toString(), method , "POST");
                                        Log.d("debugTest",result);

                                        try {
                                            JSONObject jsonObject1 = new JSONObject(result);
                                            String code = jsonObject1.getString("code");
                                            if ("000000".equals(code))
                                            {
                                                Log.v(TAG , code);
                                                Toast.makeText(view.getContext(), "???????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        init();
                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //???????????????????????????
                                        dialog.cancel();
                                    }
                                }).create();
                        dialog.show();
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    static class ViewHolder {
        public TextView tv_user_id;
        public TextView tv_sell_count;
        public TextView tv_min_limit;
        public ImageView iv_pay_method;
        public TextView tv_label_price;
        public TextView tv_price;
        public Button bt_sell;
    }
}