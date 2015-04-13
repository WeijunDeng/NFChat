package me.weijun.nfchat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;

/**
 * Created by mac on 15/4/10.
 * 使用枚举实现单例模式
 */
public enum NfcHelper {

    instance;

    private static NfcAdapter mNfcAdapter;

    public void onCreateInActivity(Activity activity) {
        NfcEnabled(activity);
    }

    public void onResumeInActivity (Activity activity) {
        if (!NfcEnabled(activity)) {
            return;
        }
        Intent intent = new Intent(activity,
                activity.getClass()).addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                activity, 0, intent, 0);
        mNfcAdapter.enableForegroundDispatch(
                activity, pendingIntent, null, new String[][] {
                        new String[] { NfcA.class.getName() } });
        if (activity.getIntent() != null) {
            onNewIntentInActivity(activity, activity.getIntent());
        }
    }

    public void onPauseInActivity (Activity activity) {
        if (!NfcEnabled(activity)) {
            return;
        }
        mNfcAdapter.disableForegroundDispatch(activity);
    }

    public void onNewIntentInActivity (Activity activity, Intent intent) {
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED
                .equals(intent.getAction()))) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            final String id = NfcHelper.byte2HexString(tag.getId());
            MyUtils.Toast(id);
            AVObject testObject = new AVObject("Tag");
            testObject.put("tag_id", id);
            testObject.saveInBackground();

            if (activity instanceof LoginActivity) {
                ((TextView)activity.findViewById(R.id.login_textView)).setText("你将成为这张卡的主人");
                activity.findViewById(R.id.login_imageView).setVisibility(View.GONE);
                EditText passwordEditText = (EditText)activity.findViewById(R.id.password_editText);
                passwordEditText.setVisibility(View.VISIBLE);
                passwordEditText.setFocusable(true);
                passwordEditText.setFocusableInTouchMode(true);
                passwordEditText.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager)passwordEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(passwordEditText, 0);
            }
        }
    }

    public boolean NfcEnabled (Activity activity) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            MyUtils.Toast("设备不支持NFC！");
            activity.finish();
            return false;
        }
        if (!mNfcAdapter.isEnabled()) {
            MyUtils.Toast("请在系统设置中先启用NFC功能！");
            activity.finish();
            return false;
        }
        return true;
    }

    public static String byte2HexString(byte[] bytes) {
        String ret = "";
        if (bytes != null) {
            for (Byte b : bytes) {
                ret += String.format("%02X", b.intValue() & 0xFF);
            }
        }
        return ret;
    }

}

