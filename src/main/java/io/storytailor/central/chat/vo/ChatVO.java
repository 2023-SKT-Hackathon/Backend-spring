package io.storytailor.central.chat.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatVO {

  private Integer id;
  private Integer msgNum;
  private String msgType;
  private String progress;
  private String text;
  private Integer sessionId;
  private Date createdAt;
}
