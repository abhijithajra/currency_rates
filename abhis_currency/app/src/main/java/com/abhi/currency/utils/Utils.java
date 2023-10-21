package com.abhi.currency.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Utils {
    public static void handledException(Exception e) {
        e.printStackTrace();
    }

    public static boolean isNotEmpty(String value) {
        try {
            return value != null && value.trim().length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isConnected(Context context) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                boolean isConnected = false;
                final android.net.Network n = cm.getActiveNetwork();
                if (n != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(n);
                    if (nc != null) {
                        isConnected = (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                                || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                                || nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                                || nc.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
                    }
                }
                return isConnected;
            }
        } catch (Exception e) {
            handledException(e);
        }
        return false;
    }

    public static void showNoInternetDialog(AppCompatActivity context, String title, String msg, boolean isFinish) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(title)
                            .setMessage(msg)
                            .setPositiveButton(isFinish ? "Yes" : "Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (isFinish) {
                                        context.finishAffinity();
                                    }
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert);
                    if (isFinish) {
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                    }
                    builder.show();
                } catch (Exception e) {
                    handledException(e);
                }
            }
        });

    }
}
