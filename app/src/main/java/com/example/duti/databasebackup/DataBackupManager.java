package com.example.duti.databasebackup;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

public class DataBackupManager {

    public static void setBackUp(Context context, String message, String packageName, String databaseName) throws IOException {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dataDirectory = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        String currentDBPath = "//data//"+ packageName +"//databases//"+databaseName;
        String backupDBPath = databaseName+"Backup";
        File currentDB = new File(dataDirectory, currentDBPath);
        File backupDB = new File(externalStorageDirectory, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            Toast.makeText(context, "Great!"+"\n"+"Database Copied with permission", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("duti", e.getMessage()+ "\n" + e.toString());
            Toast.makeText(context, "Sorry!"+"\n"+"You are doing out of permission", Toast.LENGTH_LONG).show();
        } finally {
            try {
                if (source != null) source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (destination != null) destination.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setEmail(context, message, databaseName);
    }

    public static void setEmail(Context context, String message, String databaseName) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        final File file = new File(Environment.getExternalStorageDirectory(), databaseName);
        Uri uri = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(emailIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"suchana.mis.2017@gmail.com", "anindyadutidhar@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suchana Database Backup");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "User Name: " + message);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }


}
