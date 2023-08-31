package io.storytailor.central.story.vo;

import java.util.List;
import lombok.Data;

@Data
public class StoryAiResponseVO {

  private List<String> story;
  private List<String> imgPrompt;
}
