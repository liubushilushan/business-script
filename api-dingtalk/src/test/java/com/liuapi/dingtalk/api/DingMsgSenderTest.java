package com.liuapi.dingtalk.api;

import com.liuapi.dingtalk.api.model.MarkdownDingMsg;
import com.liuapi.property.PropertyReader;
import org.junit.jupiter.api.Test;

class DingMsgSenderTest {
    @Test
    void testSendMsg() {
        String webhook = PropertyReader.get("ding.webHook");
        // 获取一个webhook
        String title = "GPS数据质量监控报告";
        StringBuilder text = new StringBuilder();
        text.append("#### 商户\n")
                .append("- 运行环境: 生产环境\n")
                .append("- 调度厂商: 海信\n");
        DingMsgSender.sendMarkdownMsg(title, text.toString(), webhook);
    }
}