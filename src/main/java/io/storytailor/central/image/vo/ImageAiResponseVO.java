package io.storytailor.central.image.vo;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class ImageAiResponseVO {

  private Integer created;
  private List<Map<String, String>> data;
}
