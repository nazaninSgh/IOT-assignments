package com.example.nazanin.iot2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class WifiListview extends BaseAdapter  {

    private WifiClickListener wifiClickListener;
    private ButtonClickListener buttonClickListener;
    private Context context;
    private ArrayList<String> items;
    public WifiListview(Context context, ArrayList<String> items,WifiClickListener wifiClickListener,ButtonClickListener buttonClickListener) {
        this.context = context;
        this.items = items;
        this.wifiClickListener = wifiClickListener;
        this.buttonClickListener = buttonClickListener;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button connectBtn;
        EditText passTxt;
        View listviewItem = null;
        if(convertView == null){
            listviewItem = LayoutInflater.from(context).inflate(R.layout.row,parent,false);
            TextView infoText = listviewItem.findViewById(R.id.info);
            connectBtn = listviewItem.findViewById(R.id.connect);
            passTxt = listviewItem.findViewById(R.id.pass);
            infoText.setText(items.get(position));
            connectBtn.setOnClickListener(new ButtonClick(buttonClickListener,position));
        }
        else {
            listviewItem = convertView;
        }
        listviewItem.setOnClickListener(new WifiClick(wifiClickListener,position));
        return listviewItem;
    }

}
