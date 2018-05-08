package com.example.user.downloadmanagerdemo.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 *
 * Created by Utkarsh Sundaram on 7/5/18.
 */

public class DownloadManagerUtil
{
    private Context mContext;
    private String urlString;
    private long downloadReference=-1;
    private SharedPreferences sharedpreferences;
    private DownloadManager dManager;
    private SharedPreferences.Editor editor;
    private static final String downloadPreference = "downloadPreference" ;
    private static final String url = "urlKey";
    private static final String downloadRef = "referenceKey";
    public static final String Email = "emailKey";

    public DownloadManagerUtil(Context mContext, String urlString) {
        this.mContext = mContext;
        this.urlString = urlString;
        dManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        sharedpreferences = mContext.getSharedPreferences(downloadPreference, Context.MODE_PRIVATE);
        downloadFile();
    }
    public void downloadFile() {
        if (!urlString.equals("") && (ConnectivityUtils.isNetworkEnabled(mContext))) {
            try {
                // Get file name from the url
                String fileName = urlString.substring(urlString.lastIndexOf("/") + 1);

                // Do something else.
                // Create Download Request object
                android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse((urlString)));
                // Display download progress and status message in notification bar
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                // Set description to display in notification
                request.setDescription("Download " + fileName + " from " + urlString);
                // Set title
                request.setTitle("DownloadManagerDemo");
                // Set destination location for the downloaded file
                request.setDestinationUri(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/" + fileName));
                downloadReference = dManager.enqueue(request);
                editor = sharedpreferences.edit();
                editor.putString(url,urlString);
                editor.putLong(downloadRef,downloadReference);
                editor.commit();
              /*  File file = new File("file://" + Environment.getExternalStorageDirectory() + "/" + fileName);
                if(file.exists()){

                }else {
                    downloadReference = dManager.enqueue(request);

                }
//Do something*/
// Do something else.
                // Download the file if the Download manager is ready
                android.app.DownloadManager.Query ImageDownloadQuery = new android.app.DownloadManager.Query();
                //set the query filter to our previously Enqueued download
                ImageDownloadQuery.setFilterById(downloadReference);

                //Query the download manager about downloads that have been requested.
                Cursor cursor = dManager.query(ImageDownloadQuery);
                if (cursor.moveToFirst())
                {
                    Log.d("DOWNLOADING STATUS",checkStatus(cursor));

                }
                //Check_Image_Status(downloadReference);
                // checkDownloadProgress(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private String checkStatus(Cursor cursor) {

        //column for status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }


        Toast toast = Toast.makeText(mContext,
                statusText + "\n" +
                        reasonText,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();
        return statusText;

    }

}
