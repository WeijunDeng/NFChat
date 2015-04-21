package me.weijun.nfchat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;

import java.util.List;

import me.weijun.nfchat.ChatClient;
import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.activity.MainActivity;

/**
 * Created by WeijunDeng on 2015/4/19.
 */
public class TagListFragment extends BaseFragment {

    private ListView listView;
    private List<AVObject> conversationObjects;

    private BroadcastReceiver receiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        View rootView = inflater.inflate(R.layout.fragment_taglist, container, false);
        listView = (ListView) rootView.findViewById(R.id.tag_listView);
        ChatClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (e == null) {
                    ChatClient.createConversation(new AVIMConversationCreatedCallback() {
                        @Override
                        public void done(AVIMConversation conversation, AVException e) {
                            if (e == null) {
                                ChatClient.findAllConversation(new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> avObjects, AVException e) {
                                        if (e == null) {
                                            conversationObjects = avObjects;
                                            listView.setAdapter(new BaseAdapter() {
                                                @Override
                                                public int getCount() {
                                                    return conversationObjects.size();
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
                                                        convertView = View.inflate(getActivity(), R.layout.taglist_item, null);
                                                    }
//                                                    ((TextView) convertView.findViewById(R.id.textView)).setText(imConversations.get(position).getCreator() + "");
                                                    ((TextView) convertView.findViewById(R.id.textView)).setText(conversationObjects.get(position).getString("c"));
                                                    return convertView;
                                                }
                                            });
                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    final AVIMConversation conversation = ChatClient.getConversation(conversationObjects.get(position).getObjectId());
                                                    ChatClient.joinConversation(conversation, new AVIMConversationCallback() {
                                                        @Override
                                                        public void done(AVException e) {
                                                            if (e == null) {
                                                                ChatFragment fragment = new ChatFragment();
                                                                fragment.setConversation(conversation);
                                                                pushFragment(fragment);
                                                            }
                                                            else {
                                                                MyUtils.Toast(e.getCode() + e.getMessage());
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    MyUtils.Toast("连接失败：" + e.getCode() + e.getMessage());
                }
            }
        });
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AVIMMessage message = intent.getParcelableExtra("message");
                MyUtils.Toast(message.getFrom() + ":" + message.getContent());
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter("receive_im_message"));
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

}
