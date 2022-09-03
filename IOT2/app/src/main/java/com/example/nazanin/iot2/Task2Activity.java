package com.example.nazanin.iot2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Task2Activity extends AppCompatActivity {
    private Button scan;
    private ListView wlist;
    private ArrayAdapter adapter;
    private WifiManager wifiManager;
    private ArrayList<String> list = new ArrayList();
    private List<ScanResult> results;
    private List<ScanResult> conver;
    private Set<ScanResult> distincts = new HashSet<>();
    public static final String WPA2 = "WPA2";
    public static final String WPA = "WPA";
    public static final String WEP = "WEP";
    public static final String OPEN = "Open";
    /* For EAP Enterprise fields */
    public static final String WPA_EAP = "WPA-EAP";
    public static final String IEEE8021X = "IEEE8021X";
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);
        scan = findViewById(R.id.scanner);
        wlist = findViewById(R.id.wlist);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        wlist.setAdapter(adapter);
        scan();
    }

    private void scan(){
        list.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    public static String getScanResultSecurity(ScanResult scanResult) {
        final String cap = scanResult.capabilities;
        final String[] securityModes = { WEP, WPA, WPA2, WPA_EAP, IEEE8021X };
        for (int i = securityModes.length - 1; i >= 0; i--) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i];
            }
        }
        return OPEN;
    }
    public void scanWifi(View view) {
        scan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(),"hi", Toast.LENGTH_SHORT).show();
            results = wifiManager.getScanResults();
            for (ScanResult scanResult: results){
                distincts.add(scanResult);
            }
            conver = new ArrayList<>(distincts);
            Collections.sort(conver, new Comparator<ScanResult>() {

                @Override
                public int compare(ScanResult o1, ScanResult o2) {
                    return o2.level-o1.level;
                }
            });
            unregisterReceiver(this);
            for (ScanResult scanResult : conver) {
                if(i<4){
                    i++;
                }
                else {
                    break;
                }
                list.add("SSID: "+scanResult.SSID + " - BSSID: " + scanResult.BSSID+" - signal strength: "
                        +scanResult.level+" - mode: "+getScanResultSecurity(scanResult));
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(getApplicationContext(), String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
        }
    };
}
