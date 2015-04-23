package me.weijun.nfchat.model;

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

    private AVIMClient getImClient() {
        if (imClient == null) {
            imClient = AVIMClient.getInstance(NFUser.getCurrentUser().getUserId() + "");
        }
        return imClient;
    }

    public void open(final AVIMClientCallback imClientCallback) {
        getImClient().open(imClientCallback);
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

        Map<String, Object> attr = new HashMap<String, Object>();
        attr.put("creatorName", NFUser.getCurrentUser().getNickName());
        attr.put("tagId", tagId);
        getImClient().createConversation(clientIds, "新卡卡", attr, conversationCreatedCallback);

    }

    public void close(AVIMClientCallback avimClientCallback) {
        getImClient().close(avimClientCallback);
    }
}
