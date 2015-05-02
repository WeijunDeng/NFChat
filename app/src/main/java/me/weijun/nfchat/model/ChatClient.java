package me.weijun.nfchat.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WeijunDeng on 2015/4/20.
 * 使用枚举实现单例模式
 */
public enum ChatClient {

    instance;

    private AVIMClient imClient;

    private boolean isOpened = false;

    public boolean hasOpened() {
        return isOpened;
    }

    private AVIMClient getImClient() {
        if (imClient == null) {
            imClient = AVIMClient.getInstance(NFUser.getCurrentUser().getUserId() + "");
        }
        return imClient;
    }

    public void open(final AVIMClientCallback avimClientCallback) {
        getImClient().open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVException e) {
                if (e == null) {
                    isOpened = true;
                }
                avimClientCallback.done(avimClient,e);
            }
        });
    }

    public void findMyConversations(AVIMConversationQueryCallback conversationQueryCallback) {
        final List<String> clientIds = new ArrayList<>();
        clientIds.add(NFUser.getCurrentUser().getUserId() + "");

        AVIMConversationQuery conversationQuery = getImClient().getQuery();
        conversationQuery.containsMembers(clientIds);
        conversationQuery.findInBackground(conversationQueryCallback);
    }

    public AVIMConversationQuery getQuery() {
        return getImClient().getQuery();
    }

    public void createConversationByTagId(String tagId, AVIMConversationCreatedCallback conversationCreatedCallback) {
        if (getImClient() == null) {
            return;
        }
        final List<String> clientIds = new ArrayList<>();
        clientIds.add(NFUser.getCurrentUser().getUserId() + "");

        Map<String, Object> attr = new HashMap<>();
        attr.put("creatorName", NFUser.getCurrentUser().getNickName());
        attr.put("tagId", tagId);
        getImClient().createConversation(clientIds, attr, conversationCreatedCallback);

    }

    public void close(AVIMClientCallback avimClientCallback) {
        isOpened = false;
        getImClient().close(avimClientCallback);
    }
}
