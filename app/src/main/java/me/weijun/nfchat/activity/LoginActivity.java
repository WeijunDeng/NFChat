package me.weijun.nfchat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.avos.avoscloud.AVAnalytics;

import me.weijun.nfchat.fragment.LoginFragment;
import me.weijun.nfchat.model.NFUser;
import me.weijun.nfchat.R;


public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         /*set it to be no title*/
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        AVAnalytics.trackAppOpened(getIntent());

        NFUser currentUser = NFUser.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else
        {
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();
            }
        }

    }

}
