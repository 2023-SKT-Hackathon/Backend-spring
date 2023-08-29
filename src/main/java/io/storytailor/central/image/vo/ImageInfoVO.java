package io.storytailor.central.image.vo;

import java.util.Date;

import lombok.Data;

@Data
public class ImageInfoVO {
    private Integer id;
    private String originFilePath;
    private String expandFilePath;
    private Integer sessionId;
    private Date createdAt;
}
