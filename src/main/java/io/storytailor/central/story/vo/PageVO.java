package io.storytailor.central.story.vo;

import lombok.Data;

@Data
public class PageVO {
    private Integer id;
    private Integer sessionId;
    private Integer storyId;
    private Integer pageNo;
    private String pageImg;
    private String pageContent;
}
