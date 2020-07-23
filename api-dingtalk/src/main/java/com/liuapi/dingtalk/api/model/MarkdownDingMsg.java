package com.liuapi.dingtalk.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public final class MarkdownDingMsg {
    private final String msgtype = "markdown";
    private Markdown markdown;
    private At at;

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Markdown {
        private String title;
        private String text;
    }

    @Data
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class At {
        private boolean isAtAll;
        private List<String> atMobiles;
    }
}
