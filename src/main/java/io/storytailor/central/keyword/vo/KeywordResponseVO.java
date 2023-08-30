package io.storytailor.central.keyword.vo;

import java.util.List;
import lombok.Data;

@Data
public class KeywordResponseVO {

  private Integer sessionId;
  private List<String> extractKeyword;
  private List<String> recommandKeyword;
}
