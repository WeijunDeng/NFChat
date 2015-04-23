package me.weijun.nfchat.model;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.activity.LoginActivity;
import me.weijun.nfchat.activity.MainActivity;
import me.weijun.nfchat.fragment.LoginFragment;

/**
 * Created by mac on 15/4/10.
 * 使用枚举实现单例模式
 */
public enum NfcHelper {

    instance;

    private NfcAdapter mNfcAdapter;


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
            String tagId = NfcHelper.byte2HexString(tag.getId());
            MyUtils.Toast(tagId);
            if (activity instanceof LoginActivity) {
                LoginFragment loginFragment = (LoginFragment)((LoginActivity) activity).getSupportFragmentManager().getFragments().get(0);
                loginFragment.showPasswordEditText(tagId);
            }
            else if (activity instanceof MainActivity) {
                ((MainActivity)activity).handleNfcIntent(tagId);
            }
        }
    }

    public boolean NfcEnabled (Activity activity) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            if (activity instanceof LoginActivity) {
                MyUtils.Toast("设备不支持NFC！");
//            activity.finish();
            }
            return false;
        }
        if (!mNfcAdapter.isEnabled()) {
            if (activity instanceof LoginActivity) {
                MyUtils.Toast("请在系统设置中先启用NFC功能！");
//            activity.finish();
            }
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

