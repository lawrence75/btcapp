package com.btc.application.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.btc.application.myapplication.R;
import com.btc.application.ui.extend.ExtendsViewModel;
import com.btc.application.ui.login.LoginActivity;

public class SellFragment extends Fragment {

//    private ExtendsViewModel extendsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        extendsViewModel = new ViewModelProvider(this).get(ExtendsViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_extends, container, false);
        View root = inflater.inflate(R.layout.fragment_sell, container, false);
        final Button buttonFirst = root.findViewById(R.id.button_first);
        buttonFirst.setTextColor(Color.rgb(0, 0, 0));

        return null;
    }
}