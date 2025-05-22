package com.codekroy.tap2winbtc;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class NetworkUtil {

    // Callback Interface
    public interface InternetCheckCallback {
        void onInternetAvailable();
    }
    public static void checkInternetConnectionWithCallback(final Context context, final InternetCheckCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean[] connected = {false};

                if (!isNetworkAvailable(context) || !hasActiveInternetConnection()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle("No Internet Connection")
                                    .setMessage("Checking for an active internet connection...")
                                    .setCancelable(false)
                                    .show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (!connected[0]) {
                                        if (isNetworkAvailable(context) && hasActiveInternetConnection()) {
                                            connected[0] = true;
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                    callback.onInternetAvailable();
                                                }
                                            });
                                        } else {
                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }).start();
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onInternetAvailable();
                        }
                    });
                }
            }
        }).start();
    }

    public static boolean hasActiveInternetConnection() {

   try {
           InetAddress address = InetAddress.getByName("www.google.com");
           return !address.equals("");
           } catch (UnknownHostException e) {
           Log.e("NetworkUtil", "Internet connection check failed", e);
           }
           return false;
        }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



