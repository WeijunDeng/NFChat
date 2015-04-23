package me.weijun.nfchat.model;

import android.app.Activity;
import android.content.Intent;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;

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

    public String getNickName() {
        return this.getString("nickname");
    }

    public void setNickName(String nickName) {
        this.put("nickname", nickName);
    }

    public static void findUserByUserIdInBackground(int userId, final NFUserCallBack userCallBack) {
        AVQuery<NFUser> query = NFUser.getUserQuery(NFUser.class);
        query.whereEqualTo("userId", userId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<NFUser>() {
            @Override
            public void done(List<NFUser> users, AVException e) {
                if (e == null) {
                    if (users != null && users.size() > 0) {
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

    public static void findUserByTagId(String tagId, final NFUserCallBack userCallBack) {
        AVQuery<AVObject> query = new AVQuery<>("_Conversation");
        query.whereEqualTo("attr.tagId", tagId);
        query.setLimit(1);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if (e != null) {
                    MyUtils.Toast(e.getCode() + e.getMessage());
                    return;
                }
                if (avObjects != null && avObjects.size() > 0) {
                    String userIdString = avObjects.get(0).getString("c");
                    int userId = 0;
                    try {
                        userId = Integer.parseInt(userIdString);
                    } catch (NumberFormatException e1) {
                        e1.printStackTrace();
                    }
                    NFUser.findUserByUserIdInBackground(userId, userCallBack);
                }
            }
        });

    }

    public static void findAllUsers(FindCallback<NFUser> userFindCallback) {
        AVQuery<NFUser> query = NFUser.getUserQuery(NFUser.class);
        query.findInBackground(userFindCallback);
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
        NFUser.findUserByUserIdInBackground(userId, new NFUserCallBack() {
            @Override
            public void succeed(NFUser user) {
                if (user != null) {
                    login(user.getUsername(), password, userCallBack);
                }
                else {
                    userCallBack.succeed(null);
                }
            }

            @Override
            public void fail(AVException e) {
                userCallBack.internalFail(e);
            }
        });
    }

    public static NFUser getCurrentUser() {
        return AVUser.getCurrentUser(NFUser.class);
    }

    public static void logOut(final Activity activity){
        logOut();
        ChatClient.instance.close(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (e == null) {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                    activity.finish();
                }
                else {
                    MyUtils.Toast("" + e.getCode() + e.getMessage());
                }
            }
        });

    }

    public static abstract class NFUserCallBack {
        public abstract void succeed(NFUser user);
        public abstract void fail(AVException e);
        protected void internalFail(AVException e) {
            MyUtils.Log("失败" + e.getCode() + e.getMessage());
            fail(e);
        }
    }
}
