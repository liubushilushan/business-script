package com.liuapi.dingtalk.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuapi.dingtalk.api.model.MarkdownDingMsg;
import com.liuapi.http.httpclient4.HttpClients;

import java.io.IOException;
import java.util.Collections;

public class DingMsgSender {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void sendMarkdownMsg(String title,String markdown,String ... webHooks) {
        if (webHooks == null) {
            return;
        }
        // 构造消息对象
        MarkdownDingMsg msg = new MarkdownDingMsg();
        msg.setMarkdown(new MarkdownDingMsg.Markdown(title,markdown));
        try {
            // 序列化消息对象
            String value = objectMapper.writeValueAsString(msg);
            // 迭代发送消息
            for (String webHook : webHooks) {
                HttpClients.doPostWithJson(webHook, value, null);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
