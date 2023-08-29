package io.storytailor.central.chat.vo;

import java.util.List;

import lombok.Data;

@Data
public class ChatResponseVO {
    private String msgNum;
    private String msgType;
    private String sessionId;
    private String text;
    private String status;
    private List<String> keyword;
    private List<String> recoKeyword;
}
