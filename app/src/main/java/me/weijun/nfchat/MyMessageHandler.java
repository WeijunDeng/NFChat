package me.weijun.nfchat;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;

/**
 * Created by WeijunDeng on 2015/4/18.
 *
 */
public class MyMessageHandler extends AVIMMessageHandler {
    @Override
    public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client) {
        // 新消息到来了。在这里增加你自己的处理代码。
        MyUtils.Toast(message.getContent() + ":" + conversation.getConversationId());
    }
}