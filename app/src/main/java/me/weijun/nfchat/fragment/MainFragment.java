package me.weijun.nfchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.activity.LoginActivity;

/**
 * Created by mac on 15/4/16.
 *
 */
public class MainFragment extends Fragment {
    EditText mUsernameEditText;
    EditText mMessageEditText;
    Button mSendButton;
    TextView mContentTextView;
    Button mLogoutButton;

    AVIMClient imClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mUsernameEditText = (EditText)rootView.findViewById(R.id.username_edittext);
        mMessageEditText = (EditText)rootView.findViewById(R.id.message_edittext);
        mSendButton = (Button)rootView.findViewById(R.id.send_button);
        mContentTextView = (TextView)rootView.findViewById(R.id.content_textview);
        mLogoutButton = (Button)rootView.findViewById(R.id.logout_button);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imClient != null) {
                    List<String> clientIds = new ArrayList<String>();
                    clientIds.add(AVUser.getCurrentUser().getObjectId());
                    clientIds.add("55321334e4b09058c2201734");
                    Map<String, Object> attr = new HashMap<String, Object>();
                    attr.put("type", 0);
                    imClient.createConversation(clientIds, attr, new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation conversation, AVException e) {
                            if (conversation != null) {
                                MyUtils.Toast("创建对话成功");
                                final AVIMMessage message = new AVIMMessage();
                                message.setContent("123456");
                                conversation.sendMessage(message, new AVIMConversationCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (null != e) {
                                            // 出错了。。。
                                            e.printStackTrace();
                                        } else {
                                            MyUtils.Toast("发送成功，msgId=" + message.getMessageId());
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AVUser.getCurrentUser().logOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        imClient = AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId());
        imClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (e != null) {
                    e.printStackTrace();
                }
                else {
                    MyUtils.Toast("连接成功");
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String myUserName = AVUser.getCurrentUser().getUsername();
        mContentTextView.setText(myUserName);
    }
}
