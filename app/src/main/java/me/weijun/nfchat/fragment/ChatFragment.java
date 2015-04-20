package me.weijun.nfchat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;

import me.weijun.nfchat.ChatClient;
import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.activity.MainActivity;

/**
 * Created by WeijunDeng on 2015/4/19.
 */
public class ChatFragment extends BaseFragment {

    private ListView listView;

    private AVIMConversation avimConversation;

    public void setAvimConversation(AVIMConversation avimConversation) {
        this.avimConversation = avimConversation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        listView = (ListView) rootView.findViewById(R.id.chat_listView);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 20;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    if (position % 6 == 0) {
                        convertView = View.inflate(getActivity(), R.layout.chat_item_text_right, null);
                    } else {
                        convertView = View.inflate(getActivity(), R.layout.chat_item_text_left, null);
                    }
                }
                ((TextView) convertView.findViewById(R.id.name_textView)).setText(position + " gggerd lj");
                if (position % 4 == 0) {
                    ((TextView) convertView.findViewById(R.id.message_textView)).setText(position + "jg qjgoiewgreag er grea hetrgregregrefewfwefeo pgjorei jgop oklj vclzkjv kjkldavj oirjv oiejr e d ");
                } else {
                    ((TextView) convertView.findViewById(R.id.message_textView)).setText(position + "fwefdrea ");
                }

                return convertView;
            }
        });
        final EditText messageEditText = (EditText) rootView.findViewById(R.id.edit_message_editText);
        rootView.findViewById(R.id.send_message_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatClient.sendMessage(messageEditText.getText().toString(), avimConversation, new AVIMConversationCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            MyUtils.Toast("发送成功");
                        } else {
                            MyUtils.Toast("发送失败");
                        }
                    }
                });
            }
        });
        return rootView;
    }


}
