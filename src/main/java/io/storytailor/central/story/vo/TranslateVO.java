package io.storytailor.central.story.vo;

import java.util.List;
import lombok.Data;

@Data
public class TranslateVO {

  private List<String> story;
  private String lang;
}
