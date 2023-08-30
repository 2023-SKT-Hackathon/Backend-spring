package io.storytailor.central.chat.vo;

import java.util.List;
import lombok.Data;

@Data
public class ChatHistoryVO {

  private List<String> userChat;
  private List<String> aiChat;
}
