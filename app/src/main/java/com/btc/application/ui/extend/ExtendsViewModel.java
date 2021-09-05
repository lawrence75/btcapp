package com.btc.application.ui.extend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExtendsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ExtendsViewModel() {
        mText = new MutableLiveData<>();
//        mText.setValue("111111");
    }

    public LiveData<String> getText() {
        return mText;
    }

}