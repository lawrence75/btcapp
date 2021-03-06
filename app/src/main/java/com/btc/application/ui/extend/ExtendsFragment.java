package com.btc.application.ui.extend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.btc.application.myapplication.R;
import com.btc.application.ui.login.LoginActivity;

public class ExtendsFragment extends Fragment {

    private ExtendsViewModel extendsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        extendsViewModel = new ViewModelProvider(this).get(ExtendsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_extends, container, false);
        final Button exitBtn = root.findViewById(R.id.exit);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(container.getContext(), LoginActivity.class));
            }
        });

        extendsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        return root;
    }
}