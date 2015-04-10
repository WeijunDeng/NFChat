package me.weijun.nfchat;

import android.app.Application;
import android.content.Context;

/**
 * Created by mac on 15/4/10.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    public static Context getContext(){
        return instance.getApplicationContext();
    }

}
