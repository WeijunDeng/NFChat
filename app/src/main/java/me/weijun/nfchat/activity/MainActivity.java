package me.weijun.nfchat.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.fragment.TagListFragment;
import me.weijun.nfchat.model.NFUser;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new TagListFragment())
                    .commit();
        }
    }

//    @Override
//    public void onBackPressed() {
//        MyUtils.Toast("返回键");
//        if (getSupportFragmentManager().getFragments().size() > 1) {
//            getSupportFragmentManager().popBackStack();
//        }
//        else {
//            super.onBackPressed();
//        }
//    }

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
                MyUtils.Toast("返回");
                onBackPressed();
                return true;
            case R.id.action_logout:
                MyUtils.Toast("退出");
                NFUser.logOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
