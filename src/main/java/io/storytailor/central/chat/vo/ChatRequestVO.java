package io.storytailor.central.chat.vo;

import lombok.Data;

@Data
public class ChatRequestVO {

  private Integer sessionId;
  private Integer msgNum;
  private String msgType;
  private String text;
  private ChatHistoryVO history;
}
