package com.example.user.downloadmanagerdemo.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.EnvironmentCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static List<File> getAllAvailableExternalStorages(Context ctx, boolean preferExternal) {
        String path;
        String state;
        File file;
        List<File> storages = new ArrayList();
        File primaryExternal = Environment.getExternalStorageDirectory();
        boolean primaryEmulated = Environment.isExternalStorageEmulated();
        String internalStorageString = System.getenv("EXTERNAL_STORAGE");
        if (!preferExternal) {
            storages.add(primaryExternal);
            if (!primaryExternal.getAbsolutePath().equals(internalStorageString)) {
                storages.add(new File(internalStorageString));
            }
        }
        if (VERSION.SDK_INT >= 19) {
            for (File f : ctx.getExternalFilesDirs("")) {
                if (f != null) {
                    path = f.getAbsolutePath();
                    int androidFolderIndex = path.indexOf("Android");
                    if (androidFolderIndex > 0) {
                        File storageRootFile = new File(path.substring(0, androidFolderIndex));
                        state = EnvironmentCompat.getStorageState(storageRootFile);
                        if (state.equals("mounted") || state.equals("mounted_ro")) {
                            storages.add(storageRootFile);
                        }
                    }
                }
            }
        }
        String secondaryStorageString = System.getenv("SECONDARY_STORAGE");
        if (secondaryStorageString == null || TextUtils.isEmpty(secondaryStorageString)) {
            secondaryStorageString = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        if (secondaryStorageString != null) {
            for (String path2 : secondaryStorageString.split(":")) {
                file = new File(path2);
                state = EnvironmentCompat.getStorageState(file);
                if (state.equals("mounted") || state.equals("mounted_ro") || state.equals(EnvironmentCompat.MEDIA_UNKNOWN)) {
                    storages.add(file);
                }
            }
        }
        for (String path22 : new String[]{"/storage/sdcard1"}) {
            file = new File(path22);
            state = EnvironmentCompat.getStorageState(file);
            if (state.equals("mounted") || state.equals("mounted_ro")) {
                storages.add(file);
            }
        }
        if (preferExternal) {
            storages.add(primaryExternal);
            if (!primaryExternal.getAbsolutePath().equals(internalStorageString)) {
                storages.add(new File(internalStorageString));
            }
        }
        return storages;
    }

    public static File getExternalResource(Context ctx, String externalBasePath, String filename) {
        File resource = null;
        List<File> storages = getAllAvailableExternalStorages(ctx, false);
        for (int i = 0; i < storages.size() && resource == null; i++) {
            File resourcePath = new File((File) storages.get(i), externalBasePath + File.separatorChar + filename);
            Log.d("Checking %s", resourcePath.getAbsolutePath());
            if (resourcePath.exists() && resourcePath.canRead()) {
                resource = resourcePath;
            }
        }
        return resource;
    }
    public static boolean checkPermission(Context mContext, String permission) {
        if (ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }
   public static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }
}
