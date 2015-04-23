package me.weijun.nfchat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.activity.MainActivity;
import me.weijun.nfchat.model.NFUser;

/**
 * Created by WeijunDeng on 2015/4/19.
 */
public class ChatFragment extends BaseFragment {

    private ListView listView;
    private BaseAdapter adapter;
    private AVIMConversation conversation;
    private List<AVIMMessage> messages;
    private BroadcastReceiver receiver;
    private Map<String, String> nameMap;

    public void setConversation(AVIMConversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().setTitle(conversation.getAttribute("creatorName").toString() + "的" + conversation.getName() + "(" + conversation.getMembers().size() + ")");
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        listView = (ListView) rootView.findViewById(R.id.chat_listView);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return messages == null ? 0 : messages.size();
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
                AVIMMessage message = messages.get(position);

//                if (convertView == null) {
                if (message.getFrom().equals(NFUser.getCurrentUser().getUserId() + "")) {
                    convertView = View.inflate(getActivity(), R.layout.chat_item_text_right, null);
                    ((TextView) convertView.findViewById(R.id.name_textView)).setText(NFUser.getCurrentUser().getNickName());
                } else {
                    convertView = View.inflate(getActivity(), R.layout.chat_item_text_left, null);
                    if (nameMap != null) {
                        ((TextView) convertView.findViewById(R.id.name_textView)).setText(nameMap.get(message.getFrom()));
                    }
                    else {
                        ((TextView) convertView.findViewById(R.id.name_textView)).setText(message.getFrom());
                    }
                }
//                }
                ((TextView) convertView.findViewById(R.id.message_textView)).setText(message.getContent());
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        final EditText messageEditText = (EditText) rootView.findViewById(R.id.edit_message_editText);
        rootView.findViewById(R.id.send_message_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageEditText.getText().toString().length() == 0) {
                    MyUtils.Toast("发送内容不能为空");
                    return;
                }
                final AVIMMessage imMessage = new AVIMMessage();
                imMessage.setContent(messageEditText.getText().toString());
                conversation.sendMessage(imMessage, new AVIMConversationCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            messages.add(imMessage);
                            adapter.notifyDataSetChanged();
                        } else {
                            MyUtils.Toast("发送失败" + e.getCode() + e.getMessage());
                        }
                    }
                });
                messageEditText.setText("");
            }
        });
        loadAllMessage();
        loadAllMembersNickName();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AVIMMessage message = intent.getParcelableExtra("message");
                if (message.getConversationId().equals(conversation.getConversationId())) {
                    messages.add(message);
                    adapter.notifyDataSetChanged();
                } else {
                    MyUtils.Toast(message.getFrom() + ":" + message.getContent());
                }
            }
        };
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter("receive_im_message"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    public void loadAllMessage() {
        conversation.queryMessages(new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> avimMessages, AVException e) {
                if (e == null) {
                    if (messages == null) {
                        messages = avimMessages;
                    } else {
                        messages.clear();
                        messages.addAll(avimMessages);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    MyUtils.Toast(e.getCode() + e.getMessage());
                }
            }
        });
    }

    public void loadAllMembersNickName() {
        NFUser.findAllUsers(new FindCallback<NFUser>() {
            @Override
            public void done(List<NFUser> nfUsers, AVException e) {
                if (e != null) {
                    MyUtils.Toast(e.getCode() + e.getMessage());
                    return;
                }
                if (nfUsers != null && nfUsers.size() > 0) {
                    for (NFUser user : nfUsers) {
                        nameMap = new HashMap<>();
                        nameMap.put(user.getUserId()+"", user.getNickName());
                    }
                    if (messages != null && adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

}
