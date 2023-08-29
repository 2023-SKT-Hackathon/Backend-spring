package io.storytailor.central.chat.vo;

import lombok.Data;

@Data
public class ChatHistoryVO {
    private String msgNum;
    private String msgType;
    private String msg;
}
