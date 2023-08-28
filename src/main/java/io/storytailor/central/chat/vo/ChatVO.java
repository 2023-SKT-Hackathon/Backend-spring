package io.storytailor.central.chat.vo;

import java.util.Date;

import lombok.Data;

@Data
public class ChatVO {
    private Integer chatId;
    private Integer msgNum;
    private String msgType;
    private String text;
    private Integer sessionId;
    private Date createdAt;

}
