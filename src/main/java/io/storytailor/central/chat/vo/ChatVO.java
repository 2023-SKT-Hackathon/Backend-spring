package io.storytailor.central.chat.vo;

import java.util.Date;

import io.storytailor.central.code.ChatProgressCode;
import io.storytailor.central.code.ChatTypeCode;
import lombok.Data;

@Data
public class ChatVO {
    private Integer chatId;
    private Integer msgNum;
    private ChatTypeCode msgType;
    private ChatProgressCode progress;
    private String text;
    private Integer sessionId;
    private Date createdAt;

}
