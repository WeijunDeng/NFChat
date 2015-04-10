package me.weijun.nfchat;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;

/**
 * Created by mac on 15/4/10.
 */
public class NfcHelper {

    private static NfcAdapter mNfcAdapter;

    private static NfcHelper mInstance;

    public static synchronized NfcHelper getInstance() {
        if (mInstance == null) {
            mInstance = new NfcHelper();
        }
        return mInstance;
    }

    private NfcHelper() {

    }

    public void onCreateInActivity(Activity activity) {
        /**
         * checkNFC
         */
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            MyUtils.Toast("设备不支持NFC！");
            activity.finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            MyUtils.Toast("请在系统设置中先启用NFC功能！");
            activity.finish();
            return;
        }

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

    public void onResumeInActivity (Activity activity) {
        Intent intent = new Intent(activity,
                activity.getClass()).addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                activity, 0, intent, 0);
        mNfcAdapter.enableForegroundDispatch(
                activity, pendingIntent, null, new String[][] {
                        new String[] { NfcA.class.getName() } });
        if (activity.getIntent() != null) {
            onNewIntentInActivity(activity.getIntent());
        }
    }

    public void onPauseInActivity (Activity activity) {
        mNfcAdapter.disableForegroundDispatch(activity);
    }

    public void onNewIntentInActivity (Intent intent) {
        if ((NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TECH_DISCOVERED
                .equals(intent.getAction()))) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            final String id = NfcHelper.byte2HexString(tag.getId());
            MyUtils.Toast(id);
        }
    }

}

