package me.weijun.nfchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;

import me.weijun.nfchat.R;
import me.weijun.nfchat.fragment.TagListFragment;
import me.weijun.nfchat.model.NFUser;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                NFUser.logOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

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
