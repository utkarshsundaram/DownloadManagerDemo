package com.example.user.downloadmanagerdemo.view;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.downloadmanagerdemo.utils.ConnectivityUtils;
import com.example.user.downloadmanagerdemo.utils.Constants;
import com.example.user.downloadmanagerdemo.R;
import com.example.user.downloadmanagerdemo.utils.DownloadManagerUtil;
import com.example.user.downloadmanagerdemo.utils.Util;

import static com.example.user.downloadmanagerdemo.utils.Constants.PERMISSION_CALLBACK_CONSTANT;
import static com.example.user.downloadmanagerdemo.utils.Constants.downloadPreference;

public class DownloadingActivity extends AppCompatActivity {

    private TextView tvMessage;
    private EditText urlInput;
    private Button btnDownload;
    private ProgressBar mProgressBar;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloading);
        tvMessage = findViewById(R.id.txtmessage);
        urlInput = findViewById(R.id.txturl);
        sharedpreferences = getSharedPreferences(downloadPreference, Context.MODE_PRIVATE);
        btnDownload = findViewById(R.id.btdownload);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        if(Build.VERSION.SDK_INT>23){
            checkRequiredPermission();
        }else{
                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(urlInput.getText().toString()!=null||!(urlInput.getText().toString().equalsIgnoreCase(""))){
                            new DownloadManagerUtil(DownloadingActivity.this, urlInput.getText().toString());

                        }else{
                            Toast.makeText(DownloadingActivity.this,"please enter the url",Toast.LENGTH_LONG).show();
                        }
                    }
                });


        }

       /* IntentFilter filter = new IntentFilter(DownloadManagerUtil.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);*/

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkRequiredPermission() {
        if (!Util.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && !Util.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_CALLBACK_CONSTANT);
        } else {
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(urlInput.getText().toString()!=null||!(urlInput.getText().toString().equalsIgnoreCase(""))){
                        new DownloadManagerUtil(DownloadingActivity.this, urlInput.getText().toString());

                    }else{
                        Toast.makeText(DownloadingActivity.this,"please enter the url",Toast.LENGTH_LONG).show();
                    }
                }
            });


        }

        }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CALLBACK_CONSTANT: {
                int permissionGrantCount = 0;
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        permissionGrantCount++;
                    } else {
                        break;
                    }
                }
                if (permissionGrantCount == 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    new DownloadManagerUtil(this, urlInput.getText().toString());
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_CALLBACK_CONSTANT);
                }
                break;
            }
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = manager.query(query);
                if (cursor.moveToFirst()) {
                    if (cursor.getCount() > 0) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            String file = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                            Toast.makeText(context, "the download has successfully completed" + file, Toast.LENGTH_LONG).show();
                            // So something here on success
                        } else {
                            int message = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                            // So something here on failed.
                            try {
                                int bytes_downloaded = cursor.getInt(cursor
                                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
                }
            }
        }


    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReceiver);
    }
}
