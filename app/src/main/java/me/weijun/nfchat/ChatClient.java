package me.weijun.nfchat;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.weijun.nfchat.model.NFUser;

/**
 * Created by WeijunDeng on 2015/4/20.
 * 使用枚举实现单例模式
 */
public enum ChatClient {

    instance;

    private static AVIMClient imClient;

    public static void open(final AVIMClientCallback imClientCallback) {
        imClient = AVIMClient.getInstance(NFUser.getCurrentUser().getUserId() + "");
        imClient.open(imClientCallback);
    }

    public static void findAllConversation(final FindCallback<AVObject>  findCallback) {
        AVQuery<AVObject> query = new AVQuery<>("_Conversation");
        query.findInBackground(findCallback);
    }

    public static AVIMConversation getConversation(String objectId) {
        return imClient.getConversation(objectId);
    }

    public static void createConversation(final AVIMConversationCreatedCallback imConversationCreatedCallback) {
        final List<String> clientIds = new ArrayList<>();
        clientIds.add(NFUser.getCurrentUser().getUserId() + "");

        AVIMConversationQuery conversationQuery = imClient.getQuery();
        conversationQuery.whereEqualTo("c", NFUser.getCurrentUser().getUserId() + "");
        conversationQuery.setLimit(1);

        conversationQuery.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> imConversations, AVException e) {
                if (e == null) {
                    if (imConversations.size() == 0) {
                        Map<String, Object> ConversationTypeGroup = new HashMap<>();
                        ConversationTypeGroup.put("type", 1);

                        imClient.createConversation(clientIds, ConversationTypeGroup, imConversationCreatedCallback);
                    } else {
                        imConversationCreatedCallback.done(imConversations.get(0), null);
                    }
                }
            }
        });
    }

    public static void joinConversation(final AVIMConversation conversation, final AVIMConversationCallback imConversationCallback) {
        conversation.join(imConversationCallback);
    }

//    public static void joinTagIdConversation(final String tagId, final AVIMConversationCallback imConversationCallback) {
//        AVQuery<NFUser> query = NFUser.getUserQuery(NFUser.class);
//        query.whereEqualTo("username", tagId);
//        query.setLimit(1);
//        query.findInBackground(new FindCallback<NFUser>() {
//            @Override
//            public void done(List<NFUser> nfUsers, AVException e) {
//                if (e == null) {
//
//                }
//                else {
//
//                }
//            }
//        });
//    }

    public static void sendMessage(final String message, final AVIMConversation conversation, final AVIMConversationCallback imConversationCallback) {
        AVIMMessage imMessage = new AVIMMessage();
        imMessage.setContent(message);
        conversation.sendMessage(imMessage, imConversationCallback);
    }

    public static void close(AVIMClientCallback avimClientCallback) {
        imClient.close(avimClientCallback);
    }
}
