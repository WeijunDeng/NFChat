package me.weijun.nfchat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import java.util.List;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.fragment.ChatFragment;
import me.weijun.nfchat.fragment.TagListFragment;
import me.weijun.nfchat.model.ChatClient;
import me.weijun.nfchat.model.NFUser;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AVIMMessageManager.registerDefaultMessageHandler(new MyMessageHandler());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TagListFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setTitle("修改昵称");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_name:
                editNickName(item);
                return true;
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        MyUtils.ShowConfirmDialog(this, "确定退出?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NFUser.logOut(MainActivity.this);
            }
        });
    }


    private void editNickName(final MenuItem item) {
        final EditText editText = new EditText(this);
        editText.setText(NFUser.getCurrentUser().getNickName());
        MyUtils.ShowInputDialog(this, "修改昵称", editText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = editText.getText().toString();
                if (name.length() == 0) {
                    MyUtils.Toast("不能为空");
                    return;
                }
                NFUser user = NFUser.getCurrentUser();
                user.setNickName(name);
                MyUtils.instance.showProgressDialog(MainActivity.this);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        MyUtils.instance.dismissProgressDialog();
                        if (e != null) {
                            MyUtils.Toast(e.getCode() + " " + e.getMessage());
                        } else {
                            MyUtils.Toast("修改成功");
                            item.setTitle(name);
                            reloadFragment();
                        }
                    }
                });
            }
        });
    }

    public void handleNfcIntent(final String tagId) {
        MyUtils.instance.showProgressDialog(this);
        AVIMConversationQuery conversationQuery = ChatClient.instance.getQuery();
        conversationQuery.whereEqualTo("attr.tagId", tagId);
        conversationQuery.setLimit(1);
        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> avimConversations, AVException e) {
                MyUtils.instance.dismissProgressDialog();
                if (e != null) {
                    MyUtils.Toast(e.getCode() + e.getMessage());
                    return;
                }
                if (avimConversations != null && avimConversations.size() > 0) {
                    final AVIMConversation conversation = avimConversations.get(0);
                    final String tagName = conversation.getAttribute("creatorName").toString() + "的" + conversation.getName();
                    if (conversation.getMembers().contains(NFUser.getCurrentUser().getUserId() + "")) {
                        MyUtils.Toast("已经收集了[" + tagName + "]");
                        return;
                    }
                    MyUtils.ShowConfirmDialog(MainActivity.this, "确定收集[" + tagName + "]吗?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyUtils.instance.showProgressDialog(MainActivity.this);
                            conversation.join(new AVIMConversationCallback() {
                                @Override
                                public void done(AVException e) {
                                    MyUtils.instance.dismissProgressDialog();
                                    if (e != null) {
                                        MyUtils.Toast(e.getCode() + e.getMessage());
                                        return;
                                    }
                                    MyUtils.Toast("收集[" + tagName + "]成功");
                                    reloadFragment();
                                }
                            });
                        }
                    });


                } else if (avimConversations != null && avimConversations.size() == 0) {
                    MyUtils.ShowConfirmDialog(MainActivity.this, "确定收集这张新卡吗?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyUtils.instance.showProgressDialog(MainActivity.this);
                            ChatClient.instance.createConversationByTagId(tagId, new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(final AVIMConversation conversation, AVException e) {
                                    MyUtils.instance.dismissProgressDialog();
                                    if (e != null) {
                                        MyUtils.Toast(e.getCode() + e.getMessage());
                                        return;
                                    }
                                    if (conversation != null) {
                                        MyUtils.Toast("收集成功");
                                        final EditText editText = new EditText(MainActivity.this);
                                        editText.setText("卡卡");
                                        MyUtils.ShowInputDialog(MainActivity.this, "新卡名字：", editText, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final String name = editText.getText().toString();
                                                if (name.length() == 0) {
                                                    MyUtils.Toast("不能为空");
                                                    return;
                                                }
                                                conversation.setName(name);
                                                conversation.updateInfoInBackground(new AVIMConversationCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e != null) {
                                                            MyUtils.Toast(e.getCode() + e.getMessage());
                                                            return;
                                                        }
                                                        MyUtils.Toast("修改名字成功");
                                                        reloadFragment();
                                                    }
                                                });
                                            }

                                        });
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });


    }

    public void reloadFragment() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList == null) {
            return;
        }
        if (fragmentList.size() >= 2 && fragmentList.get(1) instanceof ChatFragment && fragmentList.get(1).isVisible()) {
            ChatFragment fragment = (ChatFragment) fragmentList.get(1);
            fragment.loadAllMessage();
        } else if (fragmentList.size() >= 1 && fragmentList.get(0) instanceof TagListFragment && fragmentList.get(0).isVisible()) {
            TagListFragment fragment = (TagListFragment) fragmentList.get(0);
            fragment.loadConversations();
        }
    }

    public class MyMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
            Intent intent = new Intent("receive_im_message");
            intent.putExtra("message", message);
            sendBroadcast(intent);
        }
    }
}
