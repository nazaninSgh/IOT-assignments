package com.example.nazanin.iot2;

import android.view.View;

public class ButtonClick implements View.OnClickListener {
    private int position;
    private ButtonClickListener buttonClickListener;
    public  ButtonClick(ButtonClickListener buttonClickListener,int position){
        this.buttonClickListener = buttonClickListener;
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        buttonClickListener.ConnectClickListener(v,position);
    }
}
