package me.weijun.nfchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.avos.avoscloud.AVAnalytics;


public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NfcHelper.instance.onCreateInActivity(this);
        AVAnalytics.trackAppOpened(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        NfcHelper.instance.onResumeInActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcHelper.instance.onPauseInActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        NfcHelper.instance.onNewIntentInActivity(this, intent);
    }
}
