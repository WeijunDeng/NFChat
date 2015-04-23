package me.weijun.nfchat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import java.util.List;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.activity.MainActivity;
import me.weijun.nfchat.model.ChatClient;

/**
 * Created by WeijunDeng on 2015/4/19.
 */
public class TagListFragment extends BaseFragment {

    private ListView listView;
    private BaseAdapter adapter;
    private List<AVIMConversation> conversations;

    private BroadcastReceiver receiver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().setTitle(getString(R.string.app_name) + " 群组列表");
        View rootView = inflater.inflate(R.layout.fragment_taglist, container, false);
        listView = (ListView) rootView.findViewById(R.id.tag_listView);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return conversations==null?0:conversations.size();
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
                String creator = conversations.get(position).getAttribute("creatorName").toString();
                String tag = conversations.get(position).getName();
                ((TextView) convertView.findViewById(R.id.textView)).setText(creator + "的" + tag);
                return convertView;
            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ChatFragment fragment = new ChatFragment();
                fragment.setConversation(conversations.get(position));
                pushFragment(fragment);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AVIMConversation conversation = conversations.get(position);
                final EditText editText = new EditText(getActivity());
                editText.setText(conversation.getName());
                MyUtils.ShowInputDialog(getActivity(), "修改卡名", editText, new DialogInterface.OnClickListener() {
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
                                    MyUtils.Toast(e.getCode() + " " + e.getMessage());
                                } else {
                                    MyUtils.Toast("修改成功");
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
                return true;
            }
        });
        ChatClient.instance.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (e == null) {
                    loadConversations();
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

    public void loadConversations() {
        ChatClient.instance.findMyConversations(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> avimConversations, AVException e) {
                conversations = avimConversations;
                listView.setAdapter(adapter);
            }
        });
    }

}
