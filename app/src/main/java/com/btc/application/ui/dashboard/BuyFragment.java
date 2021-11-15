package com.btc.application.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.btc.application.myapplication.R;

public class BuyFragment extends Fragment {

//    private ExtendsViewModel extendsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_buy, container, false);
        final Button buttonFirst = root.findViewById(R.id.button_first);
        buttonFirst.setTextColor(Color.rgb(0, 0, 0));


        return root;
    }
}