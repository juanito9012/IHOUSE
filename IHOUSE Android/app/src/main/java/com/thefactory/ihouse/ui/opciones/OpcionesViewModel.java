package com.thefactory.ihouse.ui.opciones;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OpcionesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public OpcionesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Coming Soon ;-) <3");
    }

    public LiveData<String> getText() {
        return mText;
    }
}