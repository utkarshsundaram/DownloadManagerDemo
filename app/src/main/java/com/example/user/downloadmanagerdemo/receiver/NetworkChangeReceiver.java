package com.example.user.downloadmanagerdemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.user.downloadmanagerdemo.utils.ConnectivityUtils;

/**
 * Created by user on 23/4/18.
 */

public class NetworkChangeReceiver extends BroadcastReceiver
{
    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if(ConnectivityUtils.isNetworkEnabled(mContext)){
            Toast.makeText(mContext,"The network is present",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(mContext,"The network is not present",Toast.LENGTH_LONG).show();
        }
    }
}
