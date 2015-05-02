package me.weijun.nfchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by WeijunDeng on 2015/3/9.
 */
public enum MyUtils {

    instance;

    public static void Log(String message) {
        Log.e("DWJ", message);
    }

    public static void Toast(String message) {
        Log.e("DWJ", message);
        Toast.makeText(MyApplication.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void ShowInputDialog(Context context, String title, EditText editText, DialogInterface.OnClickListener onClickListener) {
        editText.setSelection(editText.getText().length());
        new AlertDialog.Builder(context)
                .setTitle(title)
//                .setIcon(R.drawable.icon180)
                .setView(editText)
                .setPositiveButton("确定", onClickListener)
                .show();
    }

    public static void ShowConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage(message)
//                .setIcon(R.drawable.icon180)
                .setPositiveButton("确定", onClickListener)
                .show();
    }

    private ProgressDialog progressDialog;
    private int showingProgress = 0;

    public void showProgressDialog(Context context) {
        MyUtils.Log(context.toString());
        if (showingProgress == 0) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在加载网络数据...");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        showingProgress++;
        MyUtils.Log("p "+showingProgress);
    }

    public void dismissProgressDialog() {
        showingProgress--;
        MyUtils.Log("p "+showingProgress);
        if (progressDialog != null && showingProgress == 0) {
            progressDialog.dismiss();
        }
    }

    public static String getDateString(long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeStamp));
    }

}
