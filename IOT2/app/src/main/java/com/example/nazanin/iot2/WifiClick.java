package com.example.nazanin.iot2;

import android.view.View;

public class WifiClick implements View.OnClickListener {
    private int position;
    private WifiClickListener wifiClickListener;
    public  WifiClick(WifiClickListener wifiClickListener,int position){
        this.wifiClickListener = wifiClickListener;
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        wifiClickListener.wifiClickListener(v,position);
    }
}
