package io.storytailor.central.chat.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.storytailor.central.code.ChatProgressCode;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatVO {
    private Integer chatId;
    private Integer msgNum;
    private String msgType;
    private ChatProgressCode progress;
    private String text;
    private Integer sessionId;
    private Date createdAt;

}
