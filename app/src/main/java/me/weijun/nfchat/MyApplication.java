package me.weijun.nfchat;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by mac on 15/4/10.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static String currentTagId = "";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initLeanCloud();
    }

    public void initLeanCloud() {
        String appId = "5ynhb2wheoybb3tynx5wlb6kk111uiiui53ufkw9s93ok2kk";
        String appKey = "3iefx05olbwzd07uwhynav00v9c6gf78r09vsuyh0gyu2wg0";
        AVOSCloud.initialize(this, appId, appKey);
    }


    public static Context getContext(){
        return instance.getApplicationContext();
    }

}
