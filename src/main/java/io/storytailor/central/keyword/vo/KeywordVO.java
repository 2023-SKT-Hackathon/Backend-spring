package io.storytailor.central.keyword.vo;

import lombok.Data;

@Data
public class KeywordVO {

  private String id;
  private Integer sessionId;
  private String type;
  private String keyword;
  private String storyId;
}
