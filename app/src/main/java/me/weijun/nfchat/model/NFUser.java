package me.weijun.nfchat.model;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;

import java.util.List;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.activity.LoginActivity;

/**
 * Created by WeijunDeng on 2015/4/18.
 *
 */
public class NFUser extends AVUser{

    public int getUserId() {
        return this.getInt("userId");
    }

    public void setUserId(int userId) {
        this.put("userId", userId);
    }

    public static void findUserByTagIdInBackground(String tagId, final NFUserCallBack userCallBack) {
        AVQuery<NFUser> query = NFUser.getUserQuery(NFUser.class);
        query.whereEqualTo("username", tagId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<NFUser>() {
            @Override
            public void done(List<NFUser> users, AVException e) {
                if (e == null) {
                    if (users != null && users.size() > 0) {
                        MyUtils.Toast(users.get(0).getUserId() + "");
                        userCallBack.succeed(users.get(0));
                    }
                    else {
                        userCallBack.succeed(null);
                    }
                }
                else {
                    userCallBack.internalFail(e);
                }
            }
        });
    }

    public static void register(final String tagId, final String password, final NFUserCallBack userCallBack) {
        AVUser newUser = new AVUser();
        newUser.setUsername(tagId);
        newUser.setPassword(password);
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    login(tagId, password, userCallBack);
                }
                else {
                    userCallBack.internalFail(e);
                }
            }
        });
    }

    public static void login(String tagId, String password, final NFUserCallBack userCallBack) {
        AVUser.logInInBackground(tagId, password, new LogInCallback<NFUser>() {
            @Override
            public void done(NFUser user, AVException e) {
                if (e == null && user != null) {
                    userCallBack.succeed(user);
                }
                else {
                    userCallBack.internalFail(e);
                }
            }
        }, NFUser.class);
    }

    public static void login(int userId, final String password, final NFUserCallBack userCallBack) {
        AVQuery<NFUser> query = NFUser.getUserQuery(NFUser.class);
        query.whereEqualTo("userId", userId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<NFUser>() {
            @Override
            public void done(List<NFUser> users, AVException e) {
                if (e == null) {
                    if (users != null && users.size() > 0) {
                        login(users.get(0).getUsername(), password, userCallBack);
                    }
                    else {
                        userCallBack.succeed(null);
                    }
                }
                else {
                    userCallBack.internalFail(e);
                }
            }
        });
    }

    public static NFUser getCurrentUser() {
        return AVUser.getCurrentUser(NFUser.class);
    }

    public static void getAllUser(FindCallback<NFUser> userFindCallback) {
        AVQuery<NFUser> query = NFUser.getUserQuery(NFUser.class);
        query.whereNotEqualTo("userId", getCurrentUser().getUserId());
        query.findInBackground(userFindCallback);
    }

    public static void logOut(Activity activity){
        logOut();
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public static abstract class NFUserCallBack {
        public abstract void succeed(NFUser user);
        public abstract void fail(AVException e);
        protected void internalFail(AVException e) {
            MyUtils.Log("失败" + e.getCode());
            fail(e);
        }
    }
}
