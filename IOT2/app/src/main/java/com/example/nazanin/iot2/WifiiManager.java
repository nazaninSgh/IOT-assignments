package com.example.nazanin.iot2;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class WifiiManager {
    Context context;
    public WifiiManager(Context context){
        this.context = context;
    }
    public int ConnectToNetworkWPA( String networkSSID, String password )
    {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain SSID in quotes

            conf.preSharedKey = "\"" + password + "\"";

            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

            Log.d("connecting", conf.SSID + " " + conf.preSharedKey);

            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int see = wifiManager.addNetwork(conf);
            Toast.makeText(context,String.valueOf(see),Toast.LENGTH_SHORT).show();
            Log.d("after connecting", conf.SSID + " " + conf.preSharedKey);



            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration i : list ) {
                if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    Log.d("re connecting", i.SSID + " " + conf.preSharedKey);

                    break;
                }
            }

            return see;
            //WiFi Connection success, return true

        } catch (Exception ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));

        }
        return -1;
    }
}
