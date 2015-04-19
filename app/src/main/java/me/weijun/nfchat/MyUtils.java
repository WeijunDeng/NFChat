package me.weijun.nfchat;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by WeijunDeng on 2015/3/9.
 *
 */
public class MyUtils {
    public static void Log(String message) {
        Log.e("DWJ", message);
    }

    public static void Toast(String message) {
        Log.e("DWJ", message);
       Toast.makeText(MyApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
