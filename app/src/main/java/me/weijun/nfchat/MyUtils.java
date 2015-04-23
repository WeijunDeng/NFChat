package me.weijun.nfchat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
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

    public static void ShowInputDialog(Context context, String title, EditText editText, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(editText)
                .setPositiveButton("确定", onClickListener)
                .show();
    }

    public static void ShowConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle("确认提示")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", onClickListener)
                .show();
    }
}
