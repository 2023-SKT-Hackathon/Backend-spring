package io.storytailor.central.story.vo;

import java.util.List;

import io.storytailor.central.chat.vo.ChatVO;
import io.storytailor.central.keyword.vo.KeywordResponseVO;
import lombok.Data;

@Data
public class StoryChatVO {
  private Integer id;
  private Integer sessionId;
  private String title;
  private String lang;
  private String creater;
  private String coverImg;
  private KeywordResponseVO keyword;
  private List<ChatVO> chat;
}
