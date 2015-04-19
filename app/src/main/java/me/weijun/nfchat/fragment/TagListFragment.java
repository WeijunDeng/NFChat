package me.weijun.nfchat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.FindCallback;

import java.util.List;

import me.weijun.nfchat.MyUtils;
import me.weijun.nfchat.R;
import me.weijun.nfchat.model.NFUser;

/**
 * Created by WeijunDeng on 2015/4/19.
 *
 */
public class TagListFragment extends Fragment {

    private ListView listView;
    private List<NFUser> users;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_taglist, container, false);
        listView = (ListView)rootView.findViewById(R.id.tag_listView);
        NFUser.getAllUser(new FindCallback<NFUser>() {
            @Override
            public void done(List<NFUser> nfUsers, AVException e) {
                if (e == null && nfUsers != null && nfUsers.size() > 0) {
                    users = nfUsers;
                    listView.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return users.size();
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
                            ((TextView)convertView.findViewById(R.id.textView)).setText(users.get(position).getUserId() +"");
                            return convertView;
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MyUtils.Toast("点击"+position);
                        }
                    });
                }
                else if (e != null) {
                    MyUtils.Toast("获取列表失败" + e.getCode());
                }
            }
        });

        return rootView;
    }
}
