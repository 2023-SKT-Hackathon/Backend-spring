package io.storytailor.central.story.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryVO {

  private Integer id;
  private Integer sessionId;
  private String title;
  private String lang;
  private String creater;
  private String coverImg;
  private List<PageVO> pages;
}
