package com.example.nazanin.iot2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiClickListener,ButtonClickListener{
    private Button scan;
    private ListView wlist;
    private WifiListview wifiListview;
    private WifiManager wifiManager;
    private ArrayList<String> list = new ArrayList();
    private List<ScanResult> results;
    private EditText passTxt;
    private AlertDialog.Builder pop;
    private ArrayList<String> permissionsNeeded = new ArrayList<>();
    private ArrayList<String> permissionsAvailable = new ArrayList<>();
    public static final String WPA2 = "WPA2";
    public static final String WPA = "WPA";
    public static final String WEP = "WEP";
    public static final String OPEN = "Open";
    /* For EAP Enterprise fields */
    public static final String WPA_EAP = "WPA-EAP";
    public static final String IEEE8021X = "IEEE8021X";
    public static final int GROUP_PERMISSION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = findViewById(R.id.scanner);
        wlist = findViewById(R.id.wlist);
        permissionsAvailable.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsAvailable.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsAvailable.add(Manifest.permission.ACCESS_WIFI_STATE);
        permissionsAvailable.add(Manifest.permission.CHANGE_WIFI_STATE);
        for (String permission : permissionsAvailable){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                permissionsNeeded.add(permission);
            }
        }
        //permission
        if(permissionsNeeded.size()>0){
            RequestPermission(permissionsNeeded);
        }

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        wifiListview = new WifiListview(getApplicationContext(),list,this, (ButtonClickListener) this);
        wlist.setAdapter(wifiListview);
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
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            for (ScanResult scanResult : results) {
                list.add("SSID: "+scanResult.SSID + " \n BSSID: " + scanResult.BSSID+" \n signal strength: "
                +scanResult.level+" \n security mode: "+getScanResultSecurity(scanResult));
                wifiListview.notifyDataSetChanged();
            }
            Toast.makeText(getApplicationContext(), String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
        }
    };

    public void gototask2(View view) {
        Intent intent = new Intent(this,Task2Activity.class);
        startActivity(intent);
    }


    @Override
    public void wifiClickListener(View view, int pos) {
        WifiiManager manager = new WifiiManager(getApplicationContext());
//        Button connectBtn = view.findViewById(R.id.connect);
//        EditText passTxt = view.findViewById(R.id.pass);
//        connectBtn.setVisibility(View.VISIBLE);
//        passTxt.setVisibility(View.VISIBLE);
      //  if(pass dash va pass null bud){
            Button connectBtn = view.findViewById(R.id.connect);
            passTxt = view.findViewById(R.id.pass);
            connectBtn.setVisibility(View.VISIBLE);
            passTxt.setVisibility(View.VISIBLE);
//            Boolean con = manager.ConnectToNetworkWPA(results.get(pos).SSID,passTxt.toString());
      //  }
//        else if pass null nabud{
        // connect
//          else if pass nadash{
      //  connect
  //  }
//        }

    }

    private void RequestPermission(ArrayList<String> permissions){
        String[] permissionList = new String[permissions.size()];
        permissions.toArray(permissionList);
        ActivityCompat.requestPermissions(this,permissionList,GROUP_PERMISSION);
    }

    @Override
    public void ConnectClickListener(View view, int pos) {
        WifiiManager manager = new WifiiManager(getApplicationContext());
        if(manager.ConnectToNetworkWPA(results.get(pos).SSID,passTxt.getText().toString())== -1){
            Toast.makeText(getApplicationContext(), "authentication problem", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_SHORT).show();
            pop = new AlertDialog.Builder(this);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo!=null){
                int ipAddress = wifiInfo.getIpAddress();
                @SuppressLint("DefaultLocale") final String formattedIpAddress = String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff));
                Toast.makeText(getApplicationContext(), formattedIpAddress, Toast.LENGTH_SHORT).show();
                pop.setMessage("IP: "+formattedIpAddress+"\n ESSID: "+wifiInfo.getSSID());
                AlertDialog alertDialog = pop.create();
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
            }

        }
    }
}
